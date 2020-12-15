package com.hamstechapp.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hamstechapp.R;
import com.hamstechapp.adapter.HamburgMenuListAdapter;
import com.hamstechapp.database.User;
import com.hamstechapp.database.UserDatabase;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationFragment extends Fragment {

    TextView txtUserName,txtUserMobile;
    RecyclerView expandableList;
    HamburgMenuListAdapter hamburgMenuListAdapter;
    List<String> expandableListTitle;
    List<String> expandableListDetail;
    UserDatabase userDatabase;
    ArrayList<User> userDetails = new ArrayList<>();
    CircleImageView profile_image;

    public static NavigationFragment newInstance() {
        return new NavigationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_layout,
                container, false);

        txtUserName = view.findViewById(R.id.txtUserName);
        txtUserMobile = view.findViewById(R.id.txtUserMobile);
        expandableList = view.findViewById(R.id.expandableList);
        profile_image = view.findViewById(R.id.profile_image);

        expandableListDetail = Arrays.asList(getActivity().getResources().getStringArray(R.array.hamburgSubList));
        expandableListTitle = Arrays.asList(getActivity().getResources().getStringArray(R.array.hamburgMenuList));
        hamburgMenuListAdapter = new HamburgMenuListAdapter(getActivity(), expandableListTitle, expandableListDetail);
        expandableList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        expandableList.setAdapter(hamburgMenuListAdapter);

        getProfile(getActivity());
        userDatabase = Room.databaseBuilder(getActivity(),
                UserDatabase.class, "database-name").allowMainThreadQueries().addCallback(callback).build();
        new getUserDetails().execute();

        return view;
    }

    private class getUserDetails extends AsyncTask<User,Void,Void> {

        @Override
        protected Void doInBackground(User... users) {
            userDetails.clear();
            userDetails.addAll(userDatabase.userDao().getAll());
            UserDataConstants.userMobile = userDetails.get(0).getPhone();
            UserDataConstants.userName = userDetails.get(0).getName();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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

    public void getProfile(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();

        try {
            params.put("phone",UserDataConstants.userMobile);
            params.put("page","profile");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = params.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getProfile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    JSONObject object = jo.getJSONObject("status");
                    JSONObject objData = jo.getJSONObject("data");
                    if (object.getInt("status") == 200){
                        txtUserMobile.setText("+91 "+objData.getString("phone"));
                        txtUserName.setText(objData.getString("prospectname"));
                        UserDataConstants.userMail = objData.getString("email");
                        UserDataConstants.userName = objData.getString("prospectname");
                        UserDataConstants.userMobile = objData.getString("phone");
                        UserDataConstants.userAddress = objData.getString("address");
                        UserDataConstants.userCity = objData.getString("city");
                        UserDataConstants.userPincode = objData.getString("pincode");
                        UserDataConstants.userState = objData.getString("state");
                        UserDataConstants.LeadId = objData.getString("state");
                        UserDataConstants.userCountryName = objData.getString("country");
                        UserDataConstants.lang = objData.getString("lang");
                        Glide.with(getActivity())

                                .load(objData.getString("profilepic"))
                                .placeholder(R.drawable.ic_userprofile)
                                .error(R.drawable.ic_userprofile)
                                .into(profile_image);
                    } else {
                        Toast.makeText(getActivity(), ""+jo.getString("messsage"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                    return mRequestBody.getBytes();
                }
            }

        };
        queue.add(sr);
    }
}
