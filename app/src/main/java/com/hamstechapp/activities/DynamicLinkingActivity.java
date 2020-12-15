package com.hamstechapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.hamstechapp.R;
import com.hamstechapp.database.User;
import com.hamstechapp.database.UserDatabase;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DynamicLinkingActivity extends AppCompatActivity {

    String courseId, language;
    String langPref = "Language",notiTitle="";
    SharedPreferences prefs;
    UserDatabase userDatabase;
    ArrayList<User> userDetails = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.loading_progress);

        prefs = getSharedPreferences("Hindi", Activity.MODE_PRIVATE);
        //prefs = PreferenceManager.getDefaultSharedPreferences(this);
        langPref = prefs.getString("Language", "en");
        userDatabase = Room.databaseBuilder(this,
                UserDatabase.class, "database-name").allowMainThreadQueries().addCallback(callback).build();
        new getUserDetails().execute();

        if (getIntent().getStringExtra("notificationId")!=null){
            courseId = getIntent().getStringExtra("notificationId");
            notiTitle = getIntent().getStringExtra("notiTitle");
            language = "english";
            new getUserDetails().execute();
        } else {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
                                String split = deepLink.toString().split("utm_content")[1];
                                split = split.toString().split("\\%")[1];
                                String result = "";
                                result = split.substring(2);
                                courseId = result;

                                language = "english";
                                new getUserDetails().execute();

                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
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

    public void DbCheck(){
        if(userDetails.size() == 0) {
            startActivity(new Intent(DynamicLinkingActivity.this,HomeActivity.class));
        } else {
            startActivity(new Intent(DynamicLinkingActivity.this,HomeActivity.class));
        }
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

    public void getLessons(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metaData = new JSONObject();
        try {
            params.put("appname","Hamstech");
            params.put("page","course");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("courseId",courseId);
            params.put("language",language);
            params.put("lang",langPref);
            metaData.put("metadata", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = metaData.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getAboutData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);

                    if (jo.getString("status").equals("ok")) {
                        JSONObject object = new JSONObject(jo.getString("detail"));
                        Intent intent = new Intent(DynamicLinkingActivity.this, HomeActivity.class);
                        intent.putExtra("CategoryId",object.getString("courseId"));
                        intent.putExtra("CategoryName",object.getString("categoryname"));
                        intent.putExtra("CourseName",object.getString("course_title"));
                        intent.putExtra("description",object.getString("course_description"));
                        intent.putExtra("language",language);
                        intent.putExtra("VideoUrl",object.getString("video_url"));
                        intent.putExtra("statusNSDC",object.getString("nsdc_status"));
                        intent.putExtra("notiTitle",notiTitle);
                        intent.putExtra("notificationId",courseId);
                        startActivity(intent);
                        finish();
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    Toast.makeText(DynamicLinkingActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };
        queue.add(sr);
    }

}
