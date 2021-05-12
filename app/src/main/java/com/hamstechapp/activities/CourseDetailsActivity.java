package com.hamstechapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hamstechapp.R;
import com.hamstechapp.adapter.CurriculumListAdapter;
import com.hamstechapp.common.CounsellingPopup;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.CourseDataModel;
import com.hamstechapp.datamodel.CourseDetailsData;
import com.hamstechapp.fragment.NavigationFragment;
import com.hamstechapp.fragment.SearchFragment;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.SocialMediaEventsHelper;
import com.hamstechapp.utils.UIUtils;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CourseDetailsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    NavigationFragment navigationFragment;
    BottomNavigationView bottomNavigation;
    DrawerLayout drawer;
    LinearLayout layoutRegister;
    ImageView imgCourseBanner,imgDiscover;
    Button btnEnroll;
    HocLoadingDialog hocLoadingDialog;
    CounsellingPopup counsellingPopup;
    TextView txtHeaderTitle,txtOverviewText,txtDuration,txtAwardName;
    CheckBox imgReadMore;
    CurriculumListAdapter curriculumListAdapter;
    RecyclerView curriculumList;
    ArrayList<CourseDataModel> curriculumData = new ArrayList<>();
    HashMap<String, ArrayList<CourseDetailsData>> courseDetailData = new HashMap<>();
    ArrayList<CourseDetailsData> semData = new ArrayList<>();
    CheckBox imgSearch;
    RelativeLayout searchParent;
    SearchFragment searchFragment;
    String ActivityLog,PagenameLog,lessonLog,CourseLog,overviewText;
    LogEventsActivity logEventsActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.course_details_activity);

        initView();
    }

    private void initView(){

        bottomNavigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        imgCourseBanner = findViewById(R.id.imgCourseBanner);
        imgDiscover = findViewById(R.id.imgDiscover);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        txtOverviewText = findViewById(R.id.txtOverviewText);
        txtDuration = findViewById(R.id.txtDuration);
        curriculumList = findViewById(R.id.curriculumList);
        imgSearch = findViewById(R.id.imgSearch);
        searchParent = findViewById(R.id.searchParent);
        imgReadMore = findViewById(R.id.imgReadMore);
        btnEnroll = findViewById(R.id.btnEnroll);
        txtAwardName = findViewById(R.id.txtAwardName);
        layoutRegister = findViewById(R.id.layoutRegister);

        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.getMenu().findItem(R.id.navigation_enrol).setChecked(true);

        navigationFragment = NavigationFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.navSideMenu, navigationFragment, "")
                .commit();

        hocLoadingDialog = new HocLoadingDialog(this);
        counsellingPopup = new CounsellingPopup(this);
        logEventsActivity = new LogEventsActivity();
        txtHeaderTitle.setText(UserDataConstants.courseName);
        imgSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtHeaderTitle.setVisibility(View.GONE);
                    searchParent.setVisibility(View.VISIBLE);
                    searchFragment = SearchFragment.newInstance();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.searchLayout, searchFragment, "")
                            .commit();
                } else {
                    txtHeaderTitle.setVisibility(View.VISIBLE);
                    searchParent.setVisibility(View.GONE);
                }
            }
        });
        imgReadMore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    txtOverviewText.setMaxLines(50);
                } else {
                    txtOverviewText.setMaxLines(4);
                }
            }
        });
        btnEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLog = "Enroll now";
                PagenameLog = "Course details page";
                new SocialMediaEventsHelper(CourseDetailsActivity.this).EventRegisterCourse();
                getLogEvent(CourseDetailsActivity.this);
                Intent intentRegister = new Intent(CourseDetailsActivity.this, RegisterCourseActivity.class);
                startActivity(intentRegister);
            }
        });
        layoutRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLog = "Register now";
                PagenameLog = "Course details page";
                new SocialMediaEventsHelper(CourseDetailsActivity.this).EventRegisterCourse();
                getLogEvent(CourseDetailsActivity.this);
                Intent intentRegister = new Intent(CourseDetailsActivity.this, RegisterCourseActivity.class);
                startActivity(intentRegister);
            }
        });
        getCourseDetails(this);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagenameLog = "Counselling Page";
                ActivityLog = "Counselling Page";
                getLogEvent(CourseDetailsActivity.this);
                Intent intentAbout = new Intent(CourseDetailsActivity.this, CounsellingActivity.class);
                startActivity(intentAbout);
            }
        });
    }

    public void sideMenu(View view){
        drawer.openDrawer(Gravity.RIGHT);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                PagenameLog = "Home page";
                ActivityLog = "Counselling Page";
                getLogEvent(CourseDetailsActivity.this);
                Intent intentHome = new Intent(CourseDetailsActivity.this, HomeActivity.class);
                startActivity(intentHome);
                return true;
            case R.id.navigation_courses:
                PagenameLog = "Course page";
                ActivityLog = "Counselling Page";
                getLogEvent(CourseDetailsActivity.this);
                Intent intentCourses = new Intent(this, HomeActivity.class);
                intentCourses.putExtra("isCoursePage","Course");
                startActivity(intentCourses);
                return true;
            case R.id.navigation_enrol:

                return true;
            case R.id.navigation_chat:
                PagenameLog = "Chat with us";
                ActivityLog = "Counselling Page";
                getLogEvent(CourseDetailsActivity.this);
                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
                startActivity(myIntent);
                return true;
            case R.id.navigation_contact:
                PagenameLog = "Contact us";
                ActivityLog = "Counselling Page";
                getLogEvent(CourseDetailsActivity.this);
                Intent intentContact = new Intent(this, ContactUsActivity.class);
                startActivity(intentContact);
                return true;
        }
        return true;
    }

    public void ChatUs(View view){
        CourseLog = "";
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Course Details Page";
        getLogEvent(CourseDetailsActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }

    public void getCourseDetails(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        hocLoadingDialog.showLoadingDialog();
        JSONObject params = new JSONObject();
        JSONObject metadata = new JSONObject();

        try {
            params.put("appname","Hamstech");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("page","hamstech_curriculum_page");
            params.put("categoryId", UserDataConstants.categoryId);
            params.put("courseId", UserDataConstants.courseId);
            metadata.put("metadata",params);
            Log.e("metadata","261    "+metadata.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = metadata.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getCourseDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    if (jo.getString("status").equals("ok")){

                        JSONArray jsonArray = jo.getJSONArray("curriculum_options");
                        JSONObject courseObject = jo.getJSONObject("course");

                        Glide.with(CourseDetailsActivity.this)
                                .asBitmap()
                                .load(courseObject.getString("image_url"))
                                .fitCenter()
                                .into(UIUtils.getRoundedImageTarget(CourseDetailsActivity.this, imgCourseBanner, 30));
                        overviewText = courseObject.getString("introduction");
                        txtOverviewText.setText(courseObject.getString("introduction"));
                        txtDuration.setText("Duration: "+courseObject.getString("duration") +"\n"+
                                "Eligibility: "+courseObject.getString("eligibility"));
                        txtAwardName.setText(courseObject.getString("university"));
                        curriculumData.clear();
                        courseDetailData.clear();
                        semData.clear();
                        for (int j = 0; j<jsonArray.length(); j++){
                            CourseDataModel courseDataModel = new CourseDataModel();
                            courseDataModel.setSemId(jsonArray.getJSONObject(j).getString("sem_id"));
                            courseDataModel.setSemName(jsonArray.getJSONObject(j).getString("sem_name"));
                            curriculumData.add(courseDataModel);
                            JSONArray childData = jsonArray.getJSONObject(j).getJSONArray("sem_details");
                            for (int k = 0; k <  childData.length(); k++) {
                                CourseDetailsData courseDetailsData = new CourseDetailsData();
                                courseDetailsData.setSem_id(childData.getJSONObject(k).getString("sem_id"));
                                courseDetailsData.setSem_name(childData.getJSONObject(k).getString("sem_name"));
                                courseDetailsData.setCurriculum(childData.getJSONObject(k).getString("curriculum"));
                                courseDetailsData.setCourseId(childData.getJSONObject(k).getString("courseId"));
                                courseDetailsData.setCategoryId(childData.getJSONObject(k).getString("categoryId"));

                                semData.add(courseDetailsData);
                            }

                            courseDetailData.put(jsonArray.getJSONObject(j).getString("sem_name"), semData);
                        }
                        /*JSONArray curriculumArray = jo.getJSONArray("curriculum_options");
                        for (int j = 0; j<curriculumArray.length(); j++){
                            JSONObject courseObject = curriculumArray.getJSONObject(j);
                            CourseDataModel courseDataModel = new CourseDataModel();
                            courseDataModel.setCatId(courseObject.getString("categoryId"));
                            courseDataModel.setCourseId(courseObject.getString("courseId"));
                            courseDataModel.setCurriculumId(courseObject.getString("curriculum_id"));
                            courseDataModel.setCourseName(courseObject.getString("curriculum"));
                            curriculumData.add(courseDataModel);
                        }
                        curriculumListAdapter = new CurriculumListAdapter(CourseDetailsActivity.this,curriculumData);
                        curriculumList.setLayoutManager(new LinearLayoutManager(CourseDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
                        curriculumList.setAdapter(curriculumListAdapter);*/
                        curriculumListAdapter = new CurriculumListAdapter(CourseDetailsActivity.this,curriculumData,semData);
                        curriculumList.setLayoutManager(new LinearLayoutManager(CourseDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
                        curriculumList.setAdapter(curriculumListAdapter);
                        hocLoadingDialog.hideDialog();
                    } else {
                        hocLoadingDialog.hideDialog();
                        Toast.makeText(CourseDetailsActivity.this, ""+jo.getString("messsage"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CourseDetailsActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return mRequestBody.getBytes();
                }
            }

        };

        int socketTimeout = 6000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        sr.setRetryPolicy(policy);

        queue.add(sr);
    }

    public void detailsPopup(String overviewText){
        final Dialog dialog = new Dialog(CourseDetailsActivity.this);
        dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.careeroption_popup);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imgCancel = dialog.findViewById(R.id.imgCancel);
        RecyclerView careerOptionsList = dialog.findViewById(R.id.careerOptionsList);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        ScrollView scrollText = dialog.findViewById(R.id.scrollText);

        careerOptionsList.setVisibility(View.GONE);
        txtTitle.setText(overviewText);
        txtTitle.setVisibility(View.VISIBLE);
        scrollText.setVisibility(View.VISIBLE);

        dialog.show();
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void getLogEvent(Context context){
        JSONObject data = new JSONObject();
        try {
            data.put("apikey",context.getResources().getString(R.string.lblApiKey));
            data.put("appname","Dashboard");
            data.put("mobile", UserDataConstants.userMobile);
            data.put("fullname",UserDataConstants.userName);
            data.put("email",UserDataConstants.userMail);
            data.put("category","");
            data.put("course","");
            data.put("lesson","");
            data.put("activity",ActivityLog);
            data.put("pagename",PagenameLog);
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        drawer.closeDrawers();
        super.onStop();
    }
}
