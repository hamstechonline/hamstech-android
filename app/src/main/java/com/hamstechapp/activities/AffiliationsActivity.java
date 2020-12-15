package com.hamstechapp.activities;

import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.hamstechapp.R;
import com.hamstechapp.adapter.AffiliationsHomeListAdapter;
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

public class AffiliationsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    NavigationFragment navigationFragment;
    BottomNavigationView bottom_navigation;
    DrawerLayout drawer;
    ImageView imgDiscover;
    CheckBox imgSearch;
    TextView txtHeaderTitle;
    RecyclerView affiliationsList;
    RelativeLayout searchParent;
    SearchFragment searchFragment;
    AffiliationsHomeListAdapter affiliationsHomeListAdapter;
    String ActivityLog,PagenameLog,CourseLog;
    RelativeLayout playerFrameLayout;
    YouTubePlayerFragment youtubeFragment;
    private YouTubePlayer player;
    private StringBuilder logString;
    private MyPlaybackEventListener playbackEventListener;
    HocLoadingDialog hocLoadingDialog;
    String mp4URL;
    ArrayList<AffiliationDataModel> arrayData = new ArrayList<>();
    LogEventsActivity logEventsActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.affiliations_activity);

        initView();
    }

    private void initView(){

        bottom_navigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        affiliationsList = findViewById(R.id.affiliationsList);
        imgDiscover = findViewById(R.id.imgDiscover);
        imgSearch = findViewById(R.id.imgSearch);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchParent = findViewById(R.id.searchParent);

        bottom_navigation.setOnNavigationItemSelectedListener(this);
        bottom_navigation.getMenu().findItem(R.id.navigation_enrol).setChecked(true);

        navigationFragment = NavigationFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.navSideMenu, navigationFragment, "")
                .commit();

        playerFrameLayout = findViewById(R.id.player_frame_layout);

        youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);
        logString = new StringBuilder();
        playbackEventListener = new MyPlaybackEventListener();
        hocLoadingDialog = new HocLoadingDialog(this);
        logEventsActivity = new LogEventsActivity();
        playerFrameLayout.setVisibility(View.VISIBLE);
        youtubeFragment.initialize(DeveloperKey.DEVELOPER_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
                        player = youTubePlayer;
                        if (!wasRestored) {
                            player.setPlaybackEventListener(playbackEventListener);
                        }
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });

        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counsellingIntent = new Intent(AffiliationsActivity.this, CounsellingActivity.class);
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

        getAffiliationData(this);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                Intent intentHome = new Intent(AffiliationsActivity.this, HomeActivity.class);
                startActivity(intentHome);
                return true;
            case R.id.navigation_courses:
                Intent intentCourses = new Intent(this, HomeActivity.class);
                intentCourses.putExtra("isCoursePage","Course");
                startActivity(intentCourses);
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

        return false;
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

    public void ChatUs(View view){
        CourseLog = "";
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Affiliation Page";
        getLogEvent(AffiliationsActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }

    public void getAffiliationData(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metaData = new JSONObject();
        try {
            params.put("appname", "Hamstech");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("page","affliations");
            params.put("lang","en");
            metaData.put("metadata", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = metaData.toString();
        hocLoadingDialog.showLoadingDialog();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getaffliations, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    if (jo.getString("status").equals("ok")){
                        mp4URL = jo.getString("affliation_video");
                        JSONArray jsonArray = jo.getJSONArray("affliation_data");
                        arrayData.clear();
                        for (int i = 0; i<jsonArray.length(); i++) {
                            AffiliationDataModel dataModel = new AffiliationDataModel();
                            dataModel.setId(jsonArray.getJSONObject(i).getString("affliation_id"));
                            dataModel.setAffiliationImage(jsonArray.getJSONObject(i).getString("affliation_Image"));
                            dataModel.setAffiliationDescription(jsonArray.getJSONObject(i).getString("affliations_descriptiom"));

                            arrayData.add(dataModel);
                        }
                        affiliationsHomeListAdapter = new AffiliationsHomeListAdapter(AffiliationsActivity.this,arrayData);
                        affiliationsList.setLayoutManager(new GridLayoutManager(AffiliationsActivity.this, 2));
                        affiliationsList.addItemDecoration(new GridSpacingItemDecoration(2,0,false));
                        affiliationsList.setAdapter(affiliationsHomeListAdapter);
                        hocLoadingDialog.hideDialog();
                        player.loadVideo(mp4URL);
                    } else {
                        hocLoadingDialog.hideDialog();
                        Toast.makeText(AffiliationsActivity.this, ""+jo.getString("messsage"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AffiliationsActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return mRequestBody.getBytes();
                }
            }

        };

        queue.add(sr);
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
        private void log(String message) {
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
