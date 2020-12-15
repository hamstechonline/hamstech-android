package com.hamstechapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hamstechapp.R;
import com.hamstechapp.adapter.CustomPageAdapter;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.OnBoardingDataModel;
import com.hamstechapp.utils.Constants;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.setAdvertiserIDCollectionEnabled;

public class OnBoardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    CustomPageAdapter mCustomPagerAdapter;
    ArrayList<OnBoardingDataModel> boardingList = new ArrayList<>();
    CirclePageIndicator indicator;
    HocLoadingDialog hocLoadingDialog;
    Button btnSkip;
    String activityLog,gcm_id;
    LogEventsActivity logEventsActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.onboarding_activity);
        initView();
    }

    public void initView(){
        viewPager = findViewById(R.id.viewpager);
        indicator = findViewById(R.id.indicator);
        btnSkip = findViewById(R.id.btnSkip);

        hocLoadingDialog = new HocLoadingDialog(this);
        logEventsActivity = new LogEventsActivity();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( OnBoardingActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                gcm_id = newToken;
            }
        });

        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        setAdvertiserIDCollectionEnabled(true);
        FacebookSdk.sdkInitialize(this);

        btnSkip.setVisibility(View.GONE);
    }

    public void skip(View view){

        Intent intent = new Intent(OnBoardingActivity.this, RegistrationActivity.class);
        activityLog = "OnBoarding Page";
        getLogEvent(OnBoardingActivity.this);
        intent.putExtra("pageName","1");
        startActivity(intent);
        OnBoardingActivity.this.finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        getContent(this);
    }
    public void getContent(final Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metaData = new JSONObject();
        try {
            params.put("appname","Hamstech");
            params.put("page","boardingpage");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            metaData.put("metadata", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hocLoadingDialog.showLoadingDialog();
        final String mRequestBody = metaData.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.getOnBoarding,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boardingList.clear();
                            if (jsonObject.getString("broadingpage")!= null && !jsonObject.getString("broadingpage").equals("null")) {
                                btnSkip.setVisibility(View.VISIBLE);
                                JSONArray jsonArray = jsonObject.getJSONArray("broadingpage");

                                for (int i = 0; i<jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    OnBoardingDataModel datamodel = new OnBoardingDataModel();
                                    datamodel.setCat_image_url(object.getString("upload_images"));
                                    datamodel.setStatus("2");
                                    boardingList.add(datamodel);
                                }
                                hocLoadingDialog.hideDialog();
                                mCustomPagerAdapter = new CustomPageAdapter(OnBoardingActivity.this,boardingList);
                                viewPager.setAdapter(mCustomPagerAdapter);
                                indicator.setViewPager(viewPager);

                                indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                    }

                                    @Override
                                    public void onPageSelected(int position) {
                                        if (position == (boardingList.size())-1){
                                            btnSkip.setText("NEXT");
                                        } else {
                                            btnSkip.setText("SKIP");
                                        }

                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

                                    }
                                });

                            } else {
                                Intent intent = new Intent(OnBoardingActivity.this, LoginActivity.class);
                                intent.putExtra("pageName","1");
                                startActivity(intent);
                                OnBoardingActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OnBoardingActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    Toast.makeText(OnBoardingActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return mRequestBody.getBytes();
                }
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(7000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }

    public void getLogEvent(Context context){
        JSONObject data = new JSONObject();
        try {
            data.put("apikey",context.getResources().getString(R.string.lblApiKey));
            data.put("appname","Dashboard");
            data.put("mobile", "");
            data.put("fullname",gcm_id);
            data.put("email","");
            data.put("category","");
            data.put("course","");
            data.put("lesson","");
            data.put("activity",activityLog);
            data.put("pagename","");
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
