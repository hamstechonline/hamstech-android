package com.hamstechapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hamstechapp.R;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.database.User;
import com.hamstechapp.database.UserDatabase;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.ForceUpdate;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    int version;
    HocLoadingDialog hocLoadingDialog;
    ForceUpdate forceUpdate;
    LogEventsActivity logEventsActivity;
    String gcm_id;
    UserDatabase userDatabase;
    ArrayList<User> userDetails = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        hocLoadingDialog = new HocLoadingDialog(this);
        forceUpdate = new ForceUpdate(this);
        logEventsActivity = new LogEventsActivity();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        userDatabase = Room.databaseBuilder(this,
                UserDatabase.class, "database-name").allowMainThreadQueries().addCallback(callback).build();
        new getUserDetails().execute();
    }

    public void DbCheck(){
        if(userDetails.size() == 0) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( SplashActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    gcm_id = newToken;
                }
            });
            getLogEvent(SplashActivity.this);
        } else {
            startActivity(new Intent(SplashActivity.this,HomeActivity.class));
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        getContent(SplashActivity.this);
    }

    private class getUserDetails extends AsyncTask<User,Void,Void> {

        @Override
        protected Void doInBackground(User... users) {
            userDetails.clear();
            userDetails.addAll(userDatabase.userDao().getAll());
            if (userDetails.size()!=0){
                UserDataConstants.userMobile = userDetails.get(0).getPhone();
                UserDataConstants.userName = userDetails.get(0).getName();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void getContent(final Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        try {
            params.put("apikey",getResources().getString(R.string.lblApiKey));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String mRequestBody = params.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.getversion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jobject = jsonObject.getJSONObject("status");
                            if (jobject.getString("status").equals("200")) {
                                float recommendedVersion = Integer.parseInt(jobject.getString("version"));
                                if (version >= recommendedVersion){
                                    DbCheck();
                                } else{
                                    forceUpdate.showLoadingDialog();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SplashActivity.this, "No Internet Connection! Please try again", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Toast.makeText(SplashActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return mRequestBody.getBytes();
                }
            }

        };
        queue.add(stringRequest);
    }

    RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase database) {
            super.onCreate(database);
            Log.i("HomeActivity","onCreate oinvoked");
            /*db.userDao().insertAll(new User(0,"name1","last1"));
            db.userDao().insertAll(new User(0,"name2","last2"));
            db.userDao().insertAll(new User(0,"name3","last3"));*/
            //new addNew().execute();

        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase database) {
            super.onOpen(database);
            Log.i("HomeActivity","onOpen oinvoked");
        }
    };

    public void getLogEvent(Context context){
        JSONObject data = new JSONObject();
        try {
            data.put("apikey",context.getResources().getString(R.string.lblApiKey));
            data.put("appname","Dashboard");
            data.put("mobile", "");
            data.put("fullname","");
            data.put("email","");
            data.put("category","");
            data.put("course","");
            data.put("lesson",gcm_id);
            data.put("activity","New User");
            data.put("pagename","GCM Id");
            logEventsActivity.LogEventsActivity(context,data);
            startActivity(new Intent(SplashActivity.this,OnBoardingActivity.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
