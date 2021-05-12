package com.hamstechapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hamstechapp.R;
import com.hamstechapp.adapter.EventsDetailsListAdapter;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.fragment.NavigationFragment;
import com.hamstechapp.fragment.SearchFragment;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.UserDataConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    NavigationFragment navigationFragment;
    BottomNavigationView bottom_navigation;
    DrawerLayout drawer;
    ImageView imgDiscover;
    CheckBox imgSearch;
    RelativeLayout searchParent,headerLayout;
    SearchFragment searchFragment;
    TextView txtHeaderTitle,eventDiscription;

    String lessonLog,ActivityLog,PagenameLog,CourseLog,mp4URL;
    ArrayList<AffiliationDataModel> arrayEventsData = new ArrayList<>();
    EventsDetailsListAdapter eventsDetailsListAdapter;
    LogEventsActivity logEventsActivity;
    HocLoadingDialog hocLoadingDialog;
    YouTubePlayer player;
    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.event_details_activity);
        initView();
    }

    private void initView(){
        bottom_navigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        imgDiscover = findViewById(R.id.imgDiscover);
        imgSearch = findViewById(R.id.imgSearch);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchParent = findViewById(R.id.searchParent);
        headerLayout = findViewById(R.id.headerLayout);
        eventDiscription = findViewById(R.id.eventDiscription);
        youTubePlayerView = findViewById(R.id.youtube_player_view);

        bottom_navigation.setOnNavigationItemSelectedListener(this);
        bottom_navigation.getMenu().findItem(R.id.navigation_enrol).setChecked(true);

        navigationFragment = NavigationFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.navSideMenu, navigationFragment, "")
                .commit();
        logEventsActivity = new LogEventsActivity();
        hocLoadingDialog = new HocLoadingDialog(this);

        txtHeaderTitle.setText(UserDataConstants.EventHeaderTitle);
        lessonLog = UserDataConstants.EventHeaderTitle;
        getData(this);


        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lessonLog = "";
                PagenameLog = "Contact us";
                ActivityLog = "Event details Page";
                getLogEvent(EventDetailsActivity.this);
                Intent counsellingIntent = new Intent(EventDetailsActivity.this, CounsellingActivity.class);
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
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        youTubePlayerView.getPlayerUiController().getMenu().dismiss();
        if (newConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            headerLayout.setVisibility(View.GONE);
            eventDiscription.setVisibility(View.GONE);
            bottom_navigation.setVisibility(View.GONE);
            imgDiscover.setVisibility(View.GONE);
        } else {
            headerLayout.setVisibility(View.VISIBLE);
            eventDiscription.setVisibility(View.VISIBLE);
            bottom_navigation.setVisibility(View.VISIBLE);
            imgDiscover.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                lessonLog = "";
                PagenameLog = "Home page";
                ActivityLog = "Event details Page";
                getLogEvent(EventDetailsActivity.this);
                Intent intentHome = new Intent(EventDetailsActivity.this, HomeActivity.class);
                startActivity(intentHome);
                return true;
            case R.id.navigation_courses:
                lessonLog = "";
                PagenameLog = "Course page";
                ActivityLog = "Event details Page";
                getLogEvent(EventDetailsActivity.this);
                Intent intentCourses = new Intent(this, HomeActivity.class);
                intentCourses.putExtra("isCoursePage","Course");
                startActivity(intentCourses);
                return true;
            case R.id.navigation_enrol:

                return true;
            case R.id.navigation_chat:
                lessonLog = "";
                PagenameLog = "Chat with us";
                ActivityLog = "Event details Page";
                getLogEvent(EventDetailsActivity.this);
                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
                startActivity(myIntent);
                return true;
            case R.id.navigation_contact:
                lessonLog = "";
                PagenameLog = "Contact us";
                ActivityLog = "Event details Page";
                getLogEvent(EventDetailsActivity.this);
                Intent contactIntent = new Intent(this, ContactUsActivity.class);
                startActivity(contactIntent);
                return true;
        }

        return true;
    }
    @Override
    protected void onStop() {
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
        drawer.closeDrawers();
        super.onDestroy();
    }
    public void ChatUs(View view){
        CourseLog = "";
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Life at Hamstech Page";
        getLogEvent(EventDetailsActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }

    public void getData(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metaData = new JSONObject();
        try {
            params.put("appname", "Hamstech");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("page","life_at_hamstech_events");
            params.put("lang","en");
            params.put("event_id",UserDataConstants.EventId);
            metaData.put("metadata", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = metaData.toString();
        hocLoadingDialog.showLoadingDialog();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.lifeatHamstechEvents, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    if (jo.getString("status").equals("ok")){

                        JSONArray imagesJsonArray = jo.getJSONArray("life_at_hamstech_events");
                        arrayEventsData.clear();
                        eventDiscription.setText(imagesJsonArray.getJSONObject(0).getString("description"));
                        mp4URL = imagesJsonArray.getJSONObject(0).getString("video_url");
                        /*for (int j = 0; j<imagesJsonArray.length(); j++){
                            AffiliationDataModel dataModel = new AffiliationDataModel();
                            dataModel.setEvent_id(imagesJsonArray.getJSONObject(j).getString("id"));
                            dataModel.setAffiliationDescription(imagesJsonArray.getJSONObject(j).getString("description"));

                            arrayEventsData.add(dataModel);
                        }*/

                        hocLoadingDialog.hideDialog();
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
                                    getLogEvent(EventDetailsActivity.this);
                                } else if (state.toString().equals("PAUSED")){
                                    PagenameLog = "Video paused";
                                    getLogEvent(EventDetailsActivity.this);
                                } else if (state.toString().equals("STOPPED")){
                                    PagenameLog = "Video stopped";
                                    getLogEvent(EventDetailsActivity.this);
                                }
                            }

                        });
                    } else {
                        hocLoadingDialog.hideDialog();
                        Toast.makeText(EventDetailsActivity.this, ""+jo.getString("messsage"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EventDetailsActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
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
            data.put("lesson",lessonLog);
            data.put("activity",ActivityLog);
            data.put("pagename",PagenameLog);
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
