package com.hamstechapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hamstechapp.R;
import com.hamstechapp.adapter.CareerOptionsListAdapter;
import com.hamstechapp.adapter.CareerOptionsPopupListAdapter;
import com.hamstechapp.adapter.CoursesListAdapter;
import com.hamstechapp.adapter.PlacementsHomeListAdapter;
import com.hamstechapp.adapter.SliderCardPagerAdapter;
import com.hamstechapp.adapter.WhyHamstechHomeListAdapter;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.datamodel.CourseDataModel;
import com.hamstechapp.datamodel.HomePageDatamodel;
import com.hamstechapp.fragment.NavigationFragment;
import com.hamstechapp.fragment.SearchFragment;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.GridSpacingItemDecoration;
import com.hamstechapp.utils.UserDataConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CoursesActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    RecyclerView courseList,whyHamstechList,careerOptionsList,placementsList;
    CoursesListAdapter coursesListAdapter;
    WhyHamstechHomeListAdapter whyHamstechHomeListAdapter;
    CareerOptionsListAdapter careerOptionsListAdapter;
    CareerOptionsPopupListAdapter careerOptionsPopupListAdapter;
    ImageView imgDiscover,placementImage;
    TextView txtCategoryName,txtHeaderTitle;
    CheckBox imgReadMore;

    NavigationFragment navigationFragment;
    BottomNavigationView bottomNavigation;
    DrawerLayout drawer;
    ViewPager mViewPager;

    YouTubePlayer player;
    YouTubePlayerView youTubePlayerView;
    SliderCardPagerAdapter mCardAdapter;
    Timer timer;
    int currentPage;
    String mp4URL;
    ArrayList<HomePageDatamodel> placementsData = new ArrayList<>();
    ArrayList<HomePageDatamodel> placementsDataDown = new ArrayList<>();
    ArrayList<AffiliationDataModel> hamstechData = new ArrayList<>();
    ArrayList<CourseDataModel> careerData = new ArrayList<>();
    ArrayList<CourseDataModel> courseData = new ArrayList<>();
    ArrayList<HomePageDatamodel> testimonialsData = new ArrayList<>();
    Handler handler;
    Runnable update;
    HocLoadingDialog hocLoadingDialog;
    PlacementsHomeListAdapter placementsHomeListAdapter;
    CheckBox imgSearch;
    RelativeLayout searchParent;
    SearchFragment searchFragment;
    String ActivityLog,PagenameLog,lessonLog,CourseLog;
    LogEventsActivity logEventsActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.courses_activity);
        initView();
    }
    private void initView(){

        courseList = findViewById(R.id.courseList);
        whyHamstechList = findViewById(R.id.whyHamstechList);
        careerOptionsList = findViewById(R.id.careerOptionsList);
        mViewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        imgDiscover = findViewById(R.id.imgDiscover);
        placementImage = findViewById(R.id.placementImage);
        txtCategoryName = findViewById(R.id.txtCategoryName);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        placementsList = findViewById(R.id.placementsList);
        imgSearch = findViewById(R.id.imgSearch);
        searchParent = findViewById(R.id.searchParent);
        imgReadMore = findViewById(R.id.imgReadMore);
        youTubePlayerView = findViewById(R.id.youtube_player_view);

        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.getMenu().findItem(R.id.navigation_courses).setChecked(true);

        hocLoadingDialog = new HocLoadingDialog(this);
        logEventsActivity = new LogEventsActivity();
        timer = new Timer();
        handler = new Handler();

        navigationFragment = NavigationFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.navSideMenu, navigationFragment, "")
                .commit();

        getCourseData(this);
        txtHeaderTitle.setText(UserDataConstants.categoryName);
        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counsellingIntent = new Intent(CoursesActivity.this, CounsellingActivity.class);
                startActivity(counsellingIntent);
            }
        });
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
                    careerOptionsListAdapter = new CareerOptionsListAdapter(CoursesActivity.this,careerData,careerData.size());
                    careerOptionsList.setLayoutManager(new LinearLayoutManager(CoursesActivity.this, LinearLayoutManager.VERTICAL, false));
                    careerOptionsList.setAdapter(careerOptionsListAdapter);
                } else {
                    careerOptionsListAdapter = new CareerOptionsListAdapter(CoursesActivity.this,careerData,4);
                    careerOptionsList.setLayoutManager(new LinearLayoutManager(CoursesActivity.this, LinearLayoutManager.VERTICAL, false));
                    careerOptionsList.setAdapter(careerOptionsListAdapter);
                }
            }
        });
    }

    public void sideMenu(View view){
        drawer.openDrawer(Gravity.RIGHT);
    }

    @Override
    protected void onStop() {
        timer.cancel();
        drawer.closeDrawers();
        super.onStop();
        if (player!=null) player.pause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (player!=null) player.play();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player!=null) player.pause();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        drawer.closeDrawers();
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                Intent intentHome = new Intent(CoursesActivity.this, HomeActivity.class);
                startActivity(intentHome);
                return true;
            case R.id.navigation_courses:
                return true;
            case R.id.navigation_enrol:

                return true;
            case R.id.navigation_chat:
                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
                startActivity(myIntent);
                return true;
            case R.id.navigation_contact:
                Intent contactIntent = new Intent(this, ContactUsActivity.class);
                startActivity(contactIntent);
                return true;
        }

        return true;
    }

    public void ChatUs(View view){
        CourseLog = "";
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Course Page";
        getLogEvent(CoursesActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }

    public void getCourseData(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        hocLoadingDialog.showLoadingDialog();
        JSONObject params = new JSONObject();
        JSONObject metadata = new JSONObject();

        try {
            params.put("appname","Hamstech");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("page","hamstech_career_page");
            params.put("categoryId",UserDataConstants.categoryId);
            metadata.put("metadata",params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = metadata.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getCourseData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    if (jo.getString("status").equals("ok")){
                        careerData.clear();
                        courseData.clear();
                        placementsData.clear();
                        testimonialsData.clear();
                        hamstechData.clear();
                        placementsDataDown.clear();
                        JSONArray jsonArray = jo.getJSONArray("career_options");
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject dataObject = jsonArray.getJSONObject(i);
                            CourseDataModel dataModel = new CourseDataModel();
                            dataModel.setCatId(dataObject.getString("category_id"));
                            dataModel.setCareerId(dataObject.getString("career_id"));
                            dataModel.setCareerOption(dataObject.getString("career_options"));
                            careerData.add(dataModel);
                        }
                        JSONArray courseArray = jo.getJSONArray("course_list");
                        for (int j = 0; j<courseArray.length(); j++){
                            JSONObject courseObject = courseArray.getJSONObject(j);
                            CourseDataModel courseDataModel = new CourseDataModel();
                            courseDataModel.setCatId(courseObject.getString("categoryId"));
                            courseDataModel.setCourseId(courseObject.getString("courseId"));
                            courseDataModel.setCourseName(courseObject.getString("course_name"));
                            courseData.add(courseDataModel);
                        }
                        JSONArray placementsArray = jo.getJSONArray("placements");
                        for (int p = 0; p<placementsArray.length(); p++){
                            JSONObject placementsObject = placementsArray.getJSONObject(p);
                            HomePageDatamodel placementsModel = new HomePageDatamodel();
                            HomePageDatamodel placementsModelDown = new HomePageDatamodel();
                            if ((p+1) % 2 == 0){
                                placementsModel.setPlacementImage(placementsObject.getString("plagement_images"));
                                placementsData.add(placementsModel);
                            } else {
                                placementsModelDown.setPlacementImage(placementsObject.getString("plagement_images"));
                                placementsDataDown.add(placementsModelDown);
                            }
                        }
                        JSONArray testimonialsDataArray = jo.getJSONArray("testimonials");
                        for (int t = 0; t<testimonialsDataArray.length(); t++){
                            JSONObject testimonialsObject = testimonialsDataArray.getJSONObject(t);
                            HomePageDatamodel testimonialsModel = new HomePageDatamodel();
                            testimonialsModel.setTestimonialTitle(testimonialsObject.getString("add_title"));
                            testimonialsModel.setTestimonialDescription(testimonialsObject.getString("add_description"));
                            testimonialsModel.setTestimonialImage(testimonialsObject.getString("add_image"));
                            testimonialsData.add(testimonialsModel);
                        }
                        JSONArray hamstechArray = jo.getJSONArray("why_hamstech");
                        for (int h = 0; h<hamstechArray.length(); h++){
                            JSONObject hamstechObject = hamstechArray.getJSONObject(h);
                            AffiliationDataModel hamstechModel = new AffiliationDataModel();
                            hamstechModel.setAffiliationImage(hamstechObject.getString("upload_images"));
                            hamstechData.add(hamstechModel);
                        }

                        txtCategoryName.setText("Programmes in "+UserDataConstants.categoryName);

                        mp4URL = jo.getString("course_video");
                        if (careerData.size()>4) imgReadMore.setVisibility(View.VISIBLE);
                        else imgReadMore.setVisibility(View.GONE);

                        careerOptionsListAdapter = new CareerOptionsListAdapter(CoursesActivity.this,careerData,4);
                        careerOptionsList.setLayoutManager(new LinearLayoutManager(CoursesActivity.this, LinearLayoutManager.VERTICAL, false));
                        careerOptionsList.setAdapter(careerOptionsListAdapter);

                        coursesListAdapter = new CoursesListAdapter(CoursesActivity.this,courseData);
                        courseList.setLayoutManager(new LinearLayoutManager(CoursesActivity.this, LinearLayoutManager.VERTICAL, false));
                        courseList.setAdapter(coursesListAdapter);

                        placementsHomeListAdapter =new PlacementsHomeListAdapter(CoursesActivity.this,placementsData,placementsDataDown);
                        placementsList.setLayoutManager(new LinearLayoutManager(CoursesActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        placementsList.setAdapter(placementsHomeListAdapter);

                        mCardAdapter = new SliderCardPagerAdapter(CoursesActivity.this,testimonialsData);
                        mViewPager.setAdapter(mCardAdapter);
                        mViewPager.setOffscreenPageLimit(3);

                        whyHamstechHomeListAdapter = new WhyHamstechHomeListAdapter(CoursesActivity.this,hamstechData);
                        whyHamstechList.setLayoutManager(new GridLayoutManager(CoursesActivity.this, 2));
                        whyHamstechList.addItemDecoration(new GridSpacingItemDecoration(2,0,false));
                        whyHamstechList.setAdapter(whyHamstechHomeListAdapter);
                        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                                //String videoId = UserDataConstants.videoUrl;
                                player = youTubePlayer;
                                player.loadVideo(mp4URL,0);
                            }
                            @Override
                            public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
                                super.onStateChange(youTubePlayer, state);
                                CourseLog = UserDataConstants.categoryName;
                                ActivityLog = "Course page";
                                if (state.toString().equals("PLAYING")){
                                    PagenameLog = "Video start";
                                    getLogEvent(CoursesActivity.this);
                                } else if (state.toString().equals("PAUSED")){
                                    PagenameLog = "Video paused";
                                    getLogEvent(CoursesActivity.this);
                                } else if (state.toString().equals("STOPPED")){
                                    PagenameLog = "Video stopped";
                                    getLogEvent(CoursesActivity.this);
                                }
                            }

                        });
                        if (careerData.size() >= 4){
                            imgReadMore.setVisibility(View.VISIBLE);
                        } else {
                            imgReadMore.setVisibility(View.GONE);
                        }

                        update = new Runnable() {
                            public void run() {
                                if (mCardAdapter.getCount() == currentPage) {
                                    currentPage = 0;
                                }
                                mViewPager.setCurrentItem(currentPage++, true);
                            }
                        };

                        timer.schedule(new TimerTask() {

                            @Override
                            public void run()
                            {
                                handler.post(update);
                            }
                        }, 1000, 3000);

                        hocLoadingDialog.hideDialog();
                    } else {
                        Toast.makeText(CoursesActivity.this, ""+jo.getString("messsage"), Toast.LENGTH_SHORT).show();
                    }

                } catch(JSONException e) {
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
                    Toast.makeText(CoursesActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return mRequestBody.getBytes();
                }
            }

        };

        int socketTimeout = 6000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        sr.setRetryPolicy(policy);

        queue.add(sr);
    }
    public void detailsPopup(ArrayList<CourseDataModel> arrayData){
        final Dialog dialog = new Dialog(CoursesActivity.this);
        dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.careeroption_popup);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imgCancel = dialog.findViewById(R.id.imgCancel);
        RecyclerView careerOptionsList = dialog.findViewById(R.id.careerOptionsList);

        careerOptionsPopupListAdapter = new CareerOptionsPopupListAdapter(CoursesActivity.this,arrayData);
        careerOptionsList.setLayoutManager(new LinearLayoutManager(CoursesActivity.this, LinearLayoutManager.VERTICAL, false));
        careerOptionsList.setAdapter(careerOptionsPopupListAdapter);

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
}
