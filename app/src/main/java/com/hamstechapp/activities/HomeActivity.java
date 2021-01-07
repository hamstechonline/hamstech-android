package com.hamstechapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
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
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hamstechapp.R;
import com.hamstechapp.adapter.AffiliationSliderCardPagerAdapter;
import com.hamstechapp.adapter.BannerSliderCardPagerAdapter;
import com.hamstechapp.adapter.HomeCoursesListAdapter;
import com.hamstechapp.adapter.MentorHomeListAdapter;
import com.hamstechapp.adapter.PlacementsHomeListAdapter;
import com.hamstechapp.adapter.PlacementsSliderCardPagerAdapter;
import com.hamstechapp.adapter.SliderCardPagerAdapter;
import com.hamstechapp.adapter.WhyHamstechHomeListAdapter;
import com.hamstechapp.common.GetNearestBranch;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.datamodel.HomePageDatamodel;
import com.hamstechapp.fragment.NavigationFragment;
import com.hamstechapp.fragment.SearchFragment;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.GridSpacingItemDecoration;
import com.hamstechapp.utils.UserDataConstants;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
View.OnClickListener{

    BottomNavigationView bottom_navigation;
    ImageView imgPrevious,imgNext,affiliationPrevious,affiliationNext,placementImage,imgDiscover;
    RelativeLayout searchParent;
    CheckBox imgSearch;
    View view;
    Button btnChat;
    TextView txtHeaderTitle,txtVideoContent,txtMentorContent,txtPlacementsContent,txtAffiliationContent,txtTestimonialsContent;
    NavigationFragment navigationFragment;
    NestedScrollView scrollParent;
    DrawerLayout drawer;
    LinearLayout linearParent;
    ViewPager mViewPager,sliderView,affiliationSlider,placementSlider;
    RecyclerView courseList,mentorsList,whyHamstechList,placementsList;
    HomeCoursesListAdapter homeCoursesListAdapter;
    MentorHomeListAdapter mentorHomeListAdapter;
    WhyHamstechHomeListAdapter whyHamstechHomeListAdapter;
    SliderCardPagerAdapter mCardAdapter;
    BannerSliderCardPagerAdapter bannerCardAdapter;
    AffiliationSliderCardPagerAdapter affiliationCardAdapter;
    PlacementsHomeListAdapter placementsHomeListAdapter;
    int currentPage,currentPageBanner,affiliationCurrentPage,placementsCurrentPage;
    CircleIndicator indicator,affilicationcircle,placementcircle;
    Handler handler,handlerBanner,affiliationHandler,placementsHandler;
    Timer timer,timerBanner,affiliationTimer,placementsTimer;
    GetNearestBranch getNearestBranch;
    SearchFragment searchFragment;
    private LocationManager locationManager;
    Criteria criteria;
    String provider,learningVideo;
    Location location;
    Runnable affiliationUpdate,updateBanner,update,placementsRunnable;
    ArrayList<HomePageDatamodel> sliderData = new ArrayList<>();
    ArrayList<HomePageDatamodel> courseData = new ArrayList<>();
    ArrayList<HomePageDatamodel> mentorsData = new ArrayList<>();
    ArrayList<AffiliationDataModel> hamstechData = new ArrayList<>();
    ArrayList<HomePageDatamodel> placementsData = new ArrayList<>();
    ArrayList<HomePageDatamodel> affiliationData = new ArrayList<>();
    ArrayList<HomePageDatamodel> testimonialsData = new ArrayList<>();
    LogEventsActivity logEventsActivity;
    String CourseLog,ActivityLog,PagenameLog;
    HocLoadingDialog hocLoadingDialog;
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_activity);

        initView();
    }

    private void initView(){
        bottom_navigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        mViewPager = findViewById(R.id.viewPager);
        sliderView = findViewById(R.id.imageSlider);
        imgPrevious = findViewById(R.id.imgPrevious);
        imgNext = findViewById(R.id.imgNext);
        indicator = findViewById(R.id.circle);
        courseList = findViewById(R.id.courseList);
        mentorsList = findViewById(R.id.mentorsList);
        whyHamstechList = findViewById(R.id.whyHamstechList);
        placementsList = findViewById(R.id.placementsList);
        affiliationSlider = findViewById(R.id.affiliationSlider);
        affiliationPrevious = findViewById(R.id.affiliationPrevious);
        affiliationNext = findViewById(R.id.affiliationNext);
        affilicationcircle = findViewById(R.id.affilicationcircle);
        imgSearch = findViewById(R.id.imgSearch);
        linearParent = findViewById(R.id.linearParent);
        placementImage = findViewById(R.id.placementImage);
        imgDiscover = findViewById(R.id.imgDiscover);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        view = findViewById(R.id.searchLayout);
        searchParent = findViewById(R.id.searchParent);
        txtVideoContent = findViewById(R.id.txtVideoContent);
        txtMentorContent = findViewById(R.id.txtMentorContent);
        txtPlacementsContent = findViewById(R.id.txtPlacementsContent);
        txtAffiliationContent = findViewById(R.id.txtAffiliationContent);
        txtTestimonialsContent = findViewById(R.id.txtTestimonialsContent);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        scrollParent = findViewById(R.id.scrollParent);
        btnChat = findViewById(R.id.btnChat);

        bottom_navigation.setOnNavigationItemSelectedListener(this);
        bottom_navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);

        handler = new Handler();
        handlerBanner = new Handler();
        affiliationHandler = new Handler();
        placementsHandler = new Handler();
        timer = new Timer();
        timerBanner = new Timer();
        affiliationTimer = new Timer();
        placementsTimer = new Timer();

        navigationFragment = NavigationFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.navSideMenu, navigationFragment, "")
                .commit();

        logEventsActivity = new LogEventsActivity();
        hocLoadingDialog = new HocLoadingDialog(this);

        imgPrevious.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        linearParent.setVisibility(View.GONE);

        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counsellingIntent = new Intent(HomeActivity.this, CounsellingActivity.class);
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

        getHomeData(this);
        affiliationPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (affiliationCurrentPage == 0){
                    affiliationCurrentPage = affiliationData.size();
                    affiliationSlider.setCurrentItem(affiliationCurrentPage--, true);
                } else {
                    affiliationSlider.setCurrentItem(affiliationCurrentPage--, true);
                }
            }
        });
        affiliationNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (affiliationData.size() == affiliationCurrentPage) {
                    affiliationCurrentPage = 0;
                    affiliationSlider.setCurrentItem(affiliationCurrentPage++, true);
                } else {
                    affiliationSlider.setCurrentItem(affiliationCurrentPage++, true);
                }
            }
        });
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
                startActivity(myIntent);
            }
        });

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
    }

    public void sideMenu(View view){
        drawer.openDrawer(Gravity.RIGHT);
    }

    public void ChatUs(View view){
        CourseLog = "";
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Home Page";
        getLogEvent(HomeActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }

    public void takeLocationPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            requestLocation();
                        } else {
                            getNearestBranch.ifDeniedLocationAccess();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                   PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            takeLocationPermission();
            return;
        } else {
            try {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, true);
                location = locationManager.getLastKnownLocation(provider);
                UserDataConstants.getLatitude = location.getLatitude();
                UserDataConstants.getLongitude = location.getLongitude();
                getNearestBranch.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                Intent homeIntent = new Intent(this, HomeActivity.class);
                startActivity(homeIntent);
                break;
            case R.id.navigation_courses:
                scrollParent.scrollTo(0,300);
                break;
            case R.id.navigation_enrol:
                break;
            case R.id.navigation_chat:
                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
                startActivity(myIntent);
                break;
            case R.id.navigation_contact:
                Intent contactIntent = new Intent(this, ContactUsActivity.class);
                startActivity(contactIntent);
                break;
        }
        return false;
    }

    @Override
    protected void onStop() {
        drawer.closeDrawers();
        timerBanner.cancel();
        affiliationTimer.cancel();
        placementsTimer.cancel();
        timer.cancel();
        super.onStop();
        if (player!=null) player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawer.closeDrawers();
        timerBanner.cancel();
        affiliationTimer.cancel();
        placementsTimer.cancel();
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player!=null) player.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player!=null) player.pause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgPrevious:
                if (currentPageBanner == 0){
                    currentPageBanner = sliderData.size();
                    sliderView.setCurrentItem(currentPageBanner--, true);
                } else {
                    sliderView.setCurrentItem(currentPageBanner--, true);
                }
                break;
            case R.id.imgNext:
                if (sliderData.size() == currentPageBanner) {
                    currentPageBanner = 0;
                    sliderView.setCurrentItem(currentPageBanner++, true);
                } else {
                    sliderView.setCurrentItem(currentPageBanner++, true);
                }
                break;
        }
    }

    public void getHomeData(Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metaData = new JSONObject();
        try {
            params.put("appname","Hamstech");
            params.put("page","hamstech_home_page");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("lang","en");
            metaData.put("metadata", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hocLoadingDialog.showLoadingDialog();
        final String mRequestBody = metaData.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.hamstech_home, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    sliderData.clear();
                    courseData.clear();
                    mentorsData.clear();
                    hamstechData.clear();
                    placementsData.clear();
                    affiliationData.clear();
                    testimonialsData.clear();
                    JSONArray jsonArray = jo.getJSONArray("Home_page_slider");
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        HomePageDatamodel SliderData = new HomePageDatamodel();
                        SliderData.setSliderImage(object.getString("slider_images"));
                        SliderData.setCatName(object.getString("slider_name"));
                        sliderData.add(SliderData);
                    }

                    JSONArray courseArray = jo.getJSONArray("course_details");
                    for (int j = 0; j<courseArray.length(); j++){
                        JSONObject courseObject = courseArray.getJSONObject(j);
                        HomePageDatamodel courseModel = new HomePageDatamodel();
                        courseModel.setCatId(courseObject.getString("categoryId"));
                        courseModel.setCourseName(courseObject.getString("categoryname"));
                        courseModel.setCourseImage(courseObject.getString("cat_image_url"));
                        courseData.add(courseModel);
                    }

                    JSONObject mentorsObj = jo.getJSONObject("mentors_details");
                    txtMentorContent.setText(mentorsObj.getString("post_content"));
                    JSONArray mentorsArray = mentorsObj.getJSONArray("mentors_details");
                    for (int k = 0; k<mentorsArray.length(); k++){
                        JSONObject mentorsObject = mentorsArray.getJSONObject(k);
                        HomePageDatamodel mentorsModel = new HomePageDatamodel();
                        mentorsModel.setMentorTitle(mentorsObject.getString("mentors_title"));
                        mentorsModel.setMentorDescription(mentorsObject.getString("mentorss_description"));
                        mentorsModel.setMentorImage(mentorsObject.getString("mentor_image"));
                        mentorsData.add(mentorsModel);
                    }

                    JSONArray hamstechArray = jo.getJSONArray("why_hamstech");
                    for (int h = 0; h<hamstechArray.length(); h++){
                        JSONObject hamstechObject = hamstechArray.getJSONObject(h);
                        AffiliationDataModel hamstechModel = new AffiliationDataModel();
                        hamstechModel.setAffiliationImage(hamstechObject.getString("upload_images"));
                        hamstechData.add(hamstechModel);
                    }

                    JSONObject placementsObj = jo.getJSONObject("placements");
                    txtPlacementsContent.setText(placementsObj.getString("placements_text"));
                    JSONArray placementsArray = placementsObj.getJSONArray("placements");
                    for (int p = 0; p<placementsArray.length(); p++){
                        JSONObject placementsObject = placementsArray.getJSONObject(p);
                        HomePageDatamodel placementsModel = new HomePageDatamodel();
                        placementsModel.setPlacementImage(placementsObject.getString("plagement_images"));
                        placementsData.add(placementsModel);
                    }

                    JSONObject affiliationObj = jo.getJSONObject("affliations_slider");
                    txtAffiliationContent.setText(affiliationObj.getString("post_content"));
                    JSONArray affiliationArray = affiliationObj.getJSONArray("affliations_details");
                    for (int a = 0; a<affiliationArray.length(); a++){
                        JSONObject affiliationObject = affiliationArray.getJSONObject(a);
                        HomePageDatamodel affiliationModel = new HomePageDatamodel();
                        affiliationModel.setAffiliationImage(affiliationObject.getString("affliation_Image"));
                        affiliationModel.setAffiliationDescription(affiliationObject.getString("affliations_descriptiom"));
                        affiliationData.add(affiliationModel);
                    }

                    JSONObject testimonialsObj = jo.getJSONObject("testimonials");
                    txtTestimonialsContent.setText(testimonialsObj.getString("testimonials_text"));
                    JSONArray testimonialsDataArray = testimonialsObj.getJSONArray("testimonials");
                    for (int t = 0; t<testimonialsDataArray.length(); t++){
                        JSONObject testimonialsObject = testimonialsDataArray.getJSONObject(t);
                        HomePageDatamodel testimonialsModel = new HomePageDatamodel();
                        testimonialsModel.setTestimonialTitle(testimonialsObject.getString("add_title"));
                        testimonialsModel.setTestimonialDescription(testimonialsObject.getString("add_description"));
                        testimonialsModel.setTestimonialImage(testimonialsObject.getString("add_image"));
                        testimonialsModel.setTestimonialPosition(testimonialsObject.getString("add_position"));
                        testimonialsData.add(testimonialsModel);
                    }
                    txtVideoContent.setText(jo.getJSONObject("online_learning").getString("learning_text"));
                    learningVideo = jo.getJSONObject("online_learning").getString("learning_video");
                    homeCoursesListAdapter = new HomeCoursesListAdapter(HomeActivity.this, courseData);
                    courseList.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));
                    courseList.setAdapter(homeCoursesListAdapter);

                    mentorHomeListAdapter = new MentorHomeListAdapter(HomeActivity.this,mentorsData);
                    mentorsList.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                    mentorsList.addItemDecoration(new GridSpacingItemDecoration(2,0,false));
                    mentorsList.setAdapter(mentorHomeListAdapter);

                    whyHamstechHomeListAdapter = new WhyHamstechHomeListAdapter(HomeActivity.this,hamstechData);
                    whyHamstechList.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                    whyHamstechList.addItemDecoration(new GridSpacingItemDecoration(2,0,false));
                    whyHamstechList.setAdapter(whyHamstechHomeListAdapter);

                    placementsHomeListAdapter =new PlacementsHomeListAdapter(HomeActivity.this,placementsData,placementsData);
                    placementsList.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    placementsList.setAdapter(placementsHomeListAdapter);

                    mCardAdapter = new SliderCardPagerAdapter(HomeActivity.this,testimonialsData);
                    mViewPager.setAdapter(mCardAdapter);

                    bannerCardAdapter = new BannerSliderCardPagerAdapter(HomeActivity.this,sliderData);
                    sliderView.setAdapter(bannerCardAdapter);
                    indicator.setViewPager(sliderView);

                    affiliationCardAdapter = new AffiliationSliderCardPagerAdapter(HomeActivity.this,affiliationData);
                    affiliationSlider.setAdapter(affiliationCardAdapter);
                    affilicationcircle.setViewPager(affiliationSlider);
                    if (getIntent().getStringExtra("isCoursePage")!=null) {
                        scrollParent.scrollTo(0,320);
                        bottom_navigation.setOnNavigationItemSelectedListener(HomeActivity.this);
                        bottom_navigation.getMenu().findItem(R.id.navigation_courses).setChecked(true);
                    }
                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                            //String videoId = UserDataConstants.videoUrl;
                            player = youTubePlayer;
                            player.loadVideo(learningVideo,0);
                            player.pause();
                        }
                        @Override
                        public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
                            super.onStateChange(youTubePlayer, state);
                            CourseLog = "";
                            ActivityLog = "How to use app";
                            if (state.toString().equals("PLAYING")){
                                PagenameLog = "Video start";
                                getLogEvent(HomeActivity.this);
                            } else if (state.toString().equals("PAUSED")){
                                PagenameLog = "Video paused";
                                getLogEvent(HomeActivity.this);
                            } else if (state.toString().equals("STOPPED")){
                                PagenameLog = "Video stopped";
                                getLogEvent(HomeActivity.this);
                            }
                        }

                    });
                    getNearestBranch = new GetNearestBranch(HomeActivity.this);
                    takeLocationPermission();
                    affiliationUpdate = new Runnable() {
                        public void run() {
                            if (affiliationCardAdapter.getCount() == affiliationCurrentPage) {
                                affiliationCurrentPage = 0;
                            }
                            affiliationSlider.setCurrentItem(affiliationCurrentPage++, true);
                        }
                    };

                    affiliationTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            affiliationHandler.post(affiliationUpdate);
                        }
                    }, 2000, 5000);

                    updateBanner = new Runnable() {
                        public void run() {
                            if (bannerCardAdapter.getCount() == currentPageBanner) {
                                currentPageBanner = 0;
                            }
                            sliderView.setCurrentItem(currentPageBanner++, true);
                        }
                    };

                    timerBanner.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            handlerBanner.post(updateBanner);
                        }
                    }, 2000, 5000);

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
                        public void run() {
                            handler.post(update);
                        }
                    }, 1000, 5000);
                    linearParent.setVisibility(View.VISIBLE);
                    hocLoadingDialog.hideDialog();
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
                    Toast.makeText(HomeActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return mRequestBody.getBytes();
                }
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(7000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }


    public void getLogEvent(Context context){
        JSONObject data = new JSONObject();
        try {
            data.put("apikey",context.getResources().getString(R.string.lblApiKey));
            data.put("appname","Dashboard");
            data.put("mobile", UserDataConstants.userMobile);
            data.put("fullname",UserDataConstants.userName);
            data.put("email","");
            data.put("category","");
            data.put("course",CourseLog);
            data.put("lesson","");
            data.put("activity",ActivityLog);
            data.put("pagename",PagenameLog);
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timerBanner.cancel();
        affiliationTimer.cancel();
        placementsTimer.cancel();
        timer.cancel();
        finishAffinity();
    }
}
