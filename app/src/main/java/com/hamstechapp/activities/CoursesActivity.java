package com.hamstechapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.hamstechapp.R;
import com.hamstechapp.adapter.CareerOptionsListAdapter;
import com.hamstechapp.adapter.CareerOptionsPopupListAdapter;
import com.hamstechapp.adapter.CourseHighlightsAdapter;
import com.hamstechapp.adapter.CoursesListAdapter;
import com.hamstechapp.adapter.PlacementsHomeListAdapter;
import com.hamstechapp.adapter.SliderCardPagerAdapter;
import com.hamstechapp.adapter.WhyHamstechHomeListAdapter;
import com.hamstechapp.common.CounsellingPopup;
import com.hamstechapp.common.DeveloperKey;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.datamodel.CourseDataModel;
import com.hamstechapp.datamodel.HomePageDatamodel;
import com.hamstechapp.fragment.NavigationFragment;
import com.hamstechapp.fragment.SearchFragment;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.GridSpacingItemDecoration;
import com.hamstechapp.utils.SocialMediaEventsHelper;
import com.hamstechapp.utils.UIUtils;
import com.hamstechapp.utils.UserDataConstants;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CoursesActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    RecyclerView courseList,whyHamstechList,careerOptionsList,placementsList,listCourseHighlights;
    CoursesListAdapter coursesListAdapter;
    CourseHighlightsAdapter courseHighlightsAdapter;
    WhyHamstechHomeListAdapter whyHamstechHomeListAdapter;
    CareerOptionsListAdapter careerOptionsListAdapter;
    CareerOptionsPopupListAdapter careerOptionsPopupListAdapter;
    ImageView imgDiscover,placementImage,imgMentor;
    TextView txtCategoryName,txtHeaderTitle,mentorDescription,txtMentorName;
    CheckBox imgReadMore;
    LinearLayout layoutMentor;
    Button btnOurCounsellor;

    NavigationFragment navigationFragment;
    BottomNavigationView bottomNavigation;
    DrawerLayout drawer;
    ViewPager mViewPager;

    RelativeLayout playerFrameLayout;
    YouTubePlayerFragment youtubeFragment;
    private YouTubePlayer player;
    private StringBuilder logString;
    private MyPlaybackEventListener playbackEventListener;

    SliderCardPagerAdapter mCardAdapter;
    Timer timer;
    int currentPage;
    String mp4URL;
    ArrayList<HomePageDatamodel> placementsData = new ArrayList<>();
    ArrayList<HomePageDatamodel> placementsDataDown = new ArrayList<>();
    ArrayList<AffiliationDataModel> hamstechData = new ArrayList<>();
    ArrayList<CourseDataModel> careerData = new ArrayList<>();
    ArrayList<CourseDataModel> courseData = new ArrayList<>();
    ArrayList<CourseDataModel> courseHighlights = new ArrayList<>();
    ArrayList<HomePageDatamodel> testimonialsData = new ArrayList<>();
    Handler handler;
    Runnable update;
    HocLoadingDialog hocLoadingDialog;
    CounsellingPopup counsellingPopup;
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
        imgMentor = findViewById(R.id.imgMentor);
        listCourseHighlights = findViewById(R.id.listCourseHighlights);
        mentorDescription = findViewById(R.id.mentorDescription);
        txtMentorName = findViewById(R.id.txtMentorName);
        layoutMentor = findViewById(R.id.layoutMentor);
        btnOurCounsellor = findViewById(R.id.btnOurCounsellor);

        playerFrameLayout = findViewById(R.id.player_frame_layout);

        youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);
        logString = new StringBuilder();
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.getMenu().findItem(R.id.navigation_courses).setChecked(true);

        hocLoadingDialog = new HocLoadingDialog(this);
        counsellingPopup = new CounsellingPopup(this);
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
        CourseLog = UserDataConstants.categoryName;
        ActivityLog = "Course page";
        loadYouTube();
        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagenameLog = "Counselling";
                ActivityLog = "Course Page";
                getLogEvent(CoursesActivity.this);
                Intent intentAbout = new Intent(CoursesActivity.this, CounsellingActivity.class);
                startActivity(intentAbout);
            }
        });
        imgSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PagenameLog = "Search";
                    ActivityLog = "Course Page";
                    getLogEvent(CoursesActivity.this);
                    new SocialMediaEventsHelper(CoursesActivity.this).EventAccordion("Search");
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
                    imgReadMore.setText("read less");
                    careerOptionsListAdapter = new CareerOptionsListAdapter(CoursesActivity.this,careerData,careerData.size());
                    careerOptionsList.setLayoutManager(new LinearLayoutManager(CoursesActivity.this, LinearLayoutManager.VERTICAL, false));
                    careerOptionsList.setAdapter(careerOptionsListAdapter);
                } else {
                    imgReadMore.setText("read more");
                    careerOptionsListAdapter = new CareerOptionsListAdapter(CoursesActivity.this,careerData,4);
                    careerOptionsList.setLayoutManager(new LinearLayoutManager(CoursesActivity.this, LinearLayoutManager.VERTICAL, false));
                    careerOptionsList.setAdapter(careerOptionsListAdapter);
                }
            }
        });
        btnOurCounsellor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagenameLog = "Talk to counsellor";
                ActivityLog = "Course Page";
                getLogEvent(CoursesActivity.this);
                new SocialMediaEventsHelper(CoursesActivity.this).EventAccordion("Counsellor");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+getResources().getString(R.string.chatCounceller)));
                startActivity(intent);
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
                PagenameLog = "Home page";
                ActivityLog = "Course Page";
                getLogEvent(CoursesActivity.this);
                Intent intentHome = new Intent(CoursesActivity.this, HomeActivity.class);
                startActivity(intentHome);
                return true;
            case R.id.navigation_courses:
                return true;
            case R.id.navigation_enrol:

                return true;
            case R.id.navigation_chat:
                PagenameLog = "Chat with us";
                ActivityLog = "Course Page";
                getLogEvent(CoursesActivity.this);
                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
                startActivity(myIntent);
                return true;
            case R.id.navigation_contact:
                PagenameLog = "Contact us";
                ActivityLog = "Course Page";
                getLogEvent(CoursesActivity.this);
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
        new SocialMediaEventsHelper(CoursesActivity.this).EventAccordion("chat with whatsapp");
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }
    public void loadYouTube(){
        playbackEventListener = new MyPlaybackEventListener();
        youtubeFragment.initialize(DeveloperKey.DEVELOPER_KEY,
                new com.google.android.youtube.player.YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(com.google.android.youtube.player.YouTubePlayer.Provider provider,
                                                        com.google.android.youtube.player.YouTubePlayer youTubePlayer, boolean wasRestored) {
                        // do any work here to cue video, play video, etc.
                        player = youTubePlayer;
                        if (!wasRestored) {
                            player.setPlaybackEventListener(playbackEventListener);
                            //player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                            //player.loadVideo(mp4URL);
                        }
                    }
                    @Override
                    public void onInitializationFailure(com.google.android.youtube.player.YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {
        String playbackState = "NOT_PLAYING";
        String bufferingState = "";
        @Override
        public void onPlaying() {
            playbackState = "PLAYING";
            log("\tPLAYING " + getTimesText());
        }

        @Override
        public void onBuffering(boolean isBuffering) {
            bufferingState = isBuffering ? "(BUFFERING)" : "";
            log("\t\t" + (isBuffering ? "BUFFERING " : "NOT BUFFERING ") + getTimesText());
        }

        @Override
        public void onStopped() {
            playbackState = "STOPPED";
            log("\tSTOPPED");
        }

        @Override
        public void onPaused() {
            playbackState = "PAUSED";
            log("\tPAUSED " + getTimesText());
        }

        @Override
        public void onSeekTo(int endPositionMillis) {
            log(String.format("\tSEEKTO: (%s/%s)",
                    formatTime(endPositionMillis),
                    formatTime(player.getDurationMillis())));
        }
    }
    private void log(String message) {
        //Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
        Log.e("Log","191    "+message);
        logString.append(message + "\n");
    }

    private String getTimesText() {
        int currentTimeMillis = player.getCurrentTimeMillis();
        int durationMillis = player.getDurationMillis();
        return String.format("(%s/%s)", formatTime(currentTimeMillis), formatTime(durationMillis));
    }
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return (hours == 0 ? "" : hours + ":")
                + String.format("%02d:%02d", minutes % 60, seconds % 60);
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
                        courseHighlights.clear();
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
                        JSONArray arrayHighlights = jo.getJSONArray("highlights_list");
                        for (int g = 0; g<arrayHighlights.length(); g++) {
                            JSONObject objectHighlight = arrayHighlights.getJSONObject(g);
                            CourseDataModel courseDataModel = new CourseDataModel();
                            courseDataModel.setCourseHighlightId(objectHighlight.getString("highlight_id"));
                            courseDataModel.setCourseHighlight(objectHighlight.getString("highlight"));
                            courseHighlights.add(courseDataModel);
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

                        courseHighlightsAdapter = new CourseHighlightsAdapter(CoursesActivity.this,courseHighlights);
                        listCourseHighlights.setLayoutManager(new LinearLayoutManager(CoursesActivity.this, LinearLayoutManager.VERTICAL, false));
                        listCourseHighlights.setAdapter(courseHighlightsAdapter);

                        whyHamstechHomeListAdapter = new WhyHamstechHomeListAdapter(CoursesActivity.this,hamstechData);
                        whyHamstechList.setLayoutManager(new GridLayoutManager(CoursesActivity.this, 2));
                        whyHamstechList.addItemDecoration(new GridSpacingItemDecoration(2,0,false));
                        whyHamstechList.setAdapter(whyHamstechHomeListAdapter);

                        if (!jo.getJSONArray("mentors_list").getJSONObject(0).getString("mentors_title").equals("")){
                            txtMentorName.setText("Chief Mentor: "+jo.getJSONArray("mentors_list").getJSONObject(0).getString("mentors_title"));
                        } else layoutMentor.setVisibility(View.GONE);
                        Glide.with(CoursesActivity.this)
                                .asBitmap()
                                .load(jo.getJSONArray("mentors_list").getJSONObject(0).getString("mentor_image"))
                                //.placeholder(R.drawable.duser1)
                                .into(UIUtils.getRoundedImageTarget(CoursesActivity.this, imgMentor, 20));
                        mentorDescription.setText(jo.getJSONArray("mentors_list").getJSONObject(0).getString("mentorss_description"));

                        player.loadVideo(mp4URL);

                        if (careerData.size() >= 4){
                            imgReadMore.setVisibility(View.VISIBLE);
                        } else {
                            imgReadMore.setVisibility(View.GONE);
                        }

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
            data.put("course",CourseLog);
            data.put("lesson","");
            data.put("activity",ActivityLog);
            data.put("pagename",PagenameLog);
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
