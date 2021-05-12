package com.hamstechapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.hamstechapp.R;
import com.hamstechapp.adapter.EventsListAdapter;
import com.hamstechapp.adapter.GalleryListAdapter;
import com.hamstechapp.common.CounsellingPopup;
import com.hamstechapp.common.DeveloperKey;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.fragment.NavigationFragment;
import com.hamstechapp.fragment.SearchFragment;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.GridSpacingItemDecoration;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LifeatHamstechActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    NavigationFragment navigationFragment;
    BottomNavigationView bottom_navigation;
    DrawerLayout drawer;
    RecyclerView galleryList,eventsList;
    ImageView imgDiscover,imgEventMore;

    RelativeLayout playerFrameLayout;
    YouTubePlayerFragment youtubeFragment;
    private YouTubePlayer player;
    private StringBuilder logString;
    private MyPlaybackEventListener playbackEventListener;

    GalleryListAdapter galleryListAdapter;
    EventsListAdapter eventsListAdapter;
    private SupportMapFragment mSupportMapFragment;
    double branchLats[] = {17.409634,17.436826,17.438766,17.450836,17.490768,17.442345,17.370879};
    double branchLongs[] = {78.485407,78.453033,78.409774,78.489081,78.389206,78.369605,78.543669};
    String[] branchNames = {"HIMAYATNAGAR","Punjagutta","Jubilee Hills","Secunderabad","Kukatpally","Gachibowli","Kothapet"};
    double lat,lng;
    HocLoadingDialog hocLoadingDialog;
    CounsellingPopup counsellingPopup;
    String mp4URL;
    ArrayList<AffiliationDataModel> arrayEventsData = new ArrayList<>();
    ArrayList<AffiliationDataModel> arrayGalleryData = new ArrayList<>();
    TextView txtHeaderTitle;
    CheckBox imgSearch;
    RelativeLayout searchParent,headerLayout;
    LinearLayout mailContent;
    SearchFragment searchFragment;
    String ActivityLog,PagenameLog,lessonLog,CourseLog;
    LogEventsActivity logEventsActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.lifeathamstech_activity);

        initView();
    }

    private void initView(){

        bottom_navigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        galleryList = findViewById(R.id.galleryList);
        eventsList = findViewById(R.id.eventsList);
        imgDiscover = findViewById(R.id.imgDiscover);
        imgSearch = findViewById(R.id.imgSearch);
        imgEventMore = findViewById(R.id.imgEventMore);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchParent = findViewById(R.id.searchParent);
        headerLayout = findViewById(R.id.headerLayout);
        mailContent = findViewById(R.id.mailContent);

        bottom_navigation.setOnNavigationItemSelectedListener(this);
        bottom_navigation.getMenu().findItem(R.id.navigation_enrol).setChecked(true);

        navigationFragment = NavigationFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.navSideMenu, navigationFragment, "")
                .commit();

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);

        playerFrameLayout = findViewById(R.id.player_frame_layout);

        youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);
        logString = new StringBuilder();

        playerFrameLayout.setVisibility(View.VISIBLE);
        hocLoadingDialog = new HocLoadingDialog(this);
        counsellingPopup = new CounsellingPopup(this);
        logEventsActivity = new LogEventsActivity();

        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagenameLog = "Counselling";
                ActivityLog = "LifeatHamstech Page";
                getLogEvent(LifeatHamstechActivity.this);
                Intent intentAbout = new Intent(LifeatHamstechActivity.this, CounsellingActivity.class);
                startActivity(intentAbout);
            }
        });

        imgSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PagenameLog = "Search";
                    ActivityLog = "LifeatHamstech Page";
                    getLogEvent(LifeatHamstechActivity.this);
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

        getData(this);
        imgEventMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsListAdapter = new EventsListAdapter(LifeatHamstechActivity.this,arrayEventsData,arrayEventsData.size());
                eventsList.setLayoutManager(new GridLayoutManager(LifeatHamstechActivity.this, 2));
                eventsList.addItemDecoration(new GridSpacingItemDecoration(2,20,false));
                eventsList.setAdapter(eventsListAdapter);
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

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        if (newConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            headerLayout.setVisibility(View.GONE);
            mailContent.setVisibility(View.GONE);
            bottom_navigation.setVisibility(View.GONE);
            imgDiscover.setVisibility(View.GONE);
        } else {
            headerLayout.setVisibility(View.VISIBLE);
            mailContent.setVisibility(View.VISIBLE);
            bottom_navigation.setVisibility(View.VISIBLE);
            imgDiscover.setVisibility(View.VISIBLE);
        }
    }

    public void sideMenu(View view){
        drawer.openDrawer(Gravity.RIGHT);
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
    protected void onStop() {
        super.onStop();
        drawer.closeDrawers();
        if (player!=null) player.pause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MarkerOptions markerOptions;
        Marker marker;
        for(int i=0;i<branchLats.length;i++){
            lat = branchLats[i];
            lng = branchLongs[i];
            Log.e("latlong","155  "+lat);
            LatLng sydney = new LatLng(lat,lng);
            markerOptions = new MarkerOptions()
                    .position(sydney)
                    .title(branchNames[i]);
            markerOptions.visible(true);
            marker = googleMap.addMarker(markerOptions);
            marker.showInfoWindow();
            marker.setVisible(true);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(10).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        loadYouTube();
    }

    public void loadYouTube(){
        playbackEventListener = new MyPlaybackEventListener();
        youtubeFragment.initialize(DeveloperKey.DEVELOPER_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
                        // do any work here to cue video, play video, etc.
                        player = youTubePlayer;
                        if (!wasRestored) {
                            player.setPlaybackEventListener(playbackEventListener);
                            //player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                            //player.loadVideo(mp4URL);
                        }
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                PagenameLog = "Home page";
                ActivityLog = "LifeatHamstech Page";
                getLogEvent(LifeatHamstechActivity.this);
                Intent intentHome = new Intent(LifeatHamstechActivity.this, HomeActivity.class);
                startActivity(intentHome);
                return true;
            case R.id.navigation_courses:
                PagenameLog = "Course page";
                ActivityLog = "LifeatHamstech Page";
                getLogEvent(LifeatHamstechActivity.this);
                Intent intentCourses = new Intent(this, HomeActivity.class);
                intentCourses.putExtra("isCoursePage","Course");
                startActivity(intentCourses);
                return true;
            case R.id.navigation_enrol:

                return true;
            case R.id.navigation_chat:
                PagenameLog = "Chat with us";
                ActivityLog = "LifeatHamstech Page";
                getLogEvent(LifeatHamstechActivity.this);
                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
                startActivity(myIntent);
                return true;
            case R.id.navigation_contact:
                PagenameLog = "Contact us";
                ActivityLog = "LifeatHamstech Page";
                getLogEvent(LifeatHamstechActivity.this);
                Intent contactIntent = new Intent(this, ContactUsActivity.class);
                startActivity(contactIntent);
                return true;
        }

        return true;
    }

    public void ChatUs(View view){
        CourseLog = "";
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Life at Hamstech Page";
        getLogEvent(LifeatHamstechActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
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

    public void getData(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metaData = new JSONObject();
        try {
            params.put("appname", "Hamstech");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("page","life_at_hamstech");
            params.put("lang","en");
            metaData.put("metadata", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = metaData.toString();
        hocLoadingDialog.showLoadingDialog();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.lifeatHamstech, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    if (jo.getString("status").equals("ok")){
                        mp4URL = jo.getString("hamstech_videos");
                        //player.loadVideo(mp4URL);

                        JSONArray imagesJsonArray = jo.getJSONArray("hamstech_gallery");
                        arrayGalleryData.clear();
                        for (int j = 0; j<imagesJsonArray.length(); j++){
                            AffiliationDataModel dataModel = new AffiliationDataModel();
                            dataModel.setAffiliationImage(imagesJsonArray.getJSONObject(j).getString("add_image"));

                            arrayGalleryData.add(dataModel);
                        }

                        JSONArray jsonArray = jo.getJSONArray("hamstech_events");
                        arrayEventsData.clear();
                        for (int i = 0; i<jsonArray.length(); i++) {
                            AffiliationDataModel dataModel = new AffiliationDataModel();
                            dataModel.setAffiliationImage(jsonArray.getJSONObject(i).getString("add_image"));
                            dataModel.setAffiliationTitle(jsonArray.getJSONObject(i).getString("add_title"));
                            dataModel.setEvent_id(jsonArray.getJSONObject(i).getString("id"));

                            arrayEventsData.add(dataModel);
                        }
                        if (arrayEventsData.size()>4) imgEventMore.setVisibility(View.VISIBLE);
                        else imgEventMore.setVisibility(View.GONE);

                        eventsListAdapter = new EventsListAdapter(LifeatHamstechActivity.this,arrayEventsData,4);
                        eventsList.setLayoutManager(new GridLayoutManager(LifeatHamstechActivity.this, 2));
                        eventsList.addItemDecoration(new GridSpacingItemDecoration(2,20,false));
                        eventsList.setAdapter(eventsListAdapter);

                        galleryListAdapter = new GalleryListAdapter(LifeatHamstechActivity.this,arrayGalleryData);
                        galleryList.setLayoutManager(new LinearLayoutManager(LifeatHamstechActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        galleryList.setAdapter(galleryListAdapter);

                        hocLoadingDialog.hideDialog();
                        player.loadVideo(mp4URL);
                    } else {
                        hocLoadingDialog.hideDialog();
                        Toast.makeText(LifeatHamstechActivity.this, ""+jo.getString("messsage"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LifeatHamstechActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };

        queue.add(sr);
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
