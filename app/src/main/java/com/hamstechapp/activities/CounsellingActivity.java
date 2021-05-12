package com.hamstechapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.hamstechapp.R;
import com.hamstechapp.common.DeveloperKey;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.CounsellingDataModel;
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
import java.util.ArrayList;

public class CounsellingActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    NavigationFragment navigationFragment;
    BottomNavigationView bottom_navigation;
    DrawerLayout drawer;
    RecyclerView counsellingList;
    LinearLayout videoLayout;
    ImageView imgCancel,imgDiscover;
    Button btnOnline, btnOnCampus;
    CounsellingListAdapter counsellingListAdapter;
    CheckBox imgSearch;
    TextView txtHeaderTitle;
    RelativeLayout searchParent;
    SearchFragment searchFragment;

    YouTubePlayerFragment youtubeFragment;
    private YouTubePlayer player;
    private StringBuilder logString;
    private MyPlaybackEventListener playbackEventListener;
    String mp4URL,courseName,lessonLog,ActivityLog,PagenameLog,CourseLog;
    LogEventsActivity logEventsActivity;
    HocLoadingDialog hocLoadingDialog;
    ArrayList<CounsellingDataModel> counsellingData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.counselling_activity);

        initView();
    }

    private void initView(){

        bottom_navigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        counsellingList = findViewById(R.id.counsellingList);
        videoLayout = findViewById(R.id.videoLayout);
        imgCancel = findViewById(R.id.imgCancel);
        imgDiscover = findViewById(R.id.imgDiscover);
        imgSearch = findViewById(R.id.imgSearch);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchParent = findViewById(R.id.searchParent);
        btnOnline = findViewById(R.id.btnOnline);
        btnOnCampus = findViewById(R.id.btnOnCampus);

        bottom_navigation.setOnNavigationItemSelectedListener(this);
        bottom_navigation.getMenu().findItem(R.id.navigation_enrol).setChecked(true);

        navigationFragment = NavigationFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.navSideMenu, navigationFragment, "")
                .commit();
        logEventsActivity = new LogEventsActivity();
        hocLoadingDialog = new HocLoadingDialog(this);
        youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);
        logString = new StringBuilder();
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
        getCounselling(this);
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();
                videoLayout.setVisibility(View.GONE);
            }
        });
        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counsellingIntent = new Intent(CounsellingActivity.this, CounsellingActivity.class);
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
    protected void onStop() {
        drawer.closeDrawers();
        super.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                courseName = "";
                lessonLog = "";
                PagenameLog = "Home page";
                ActivityLog = "Counselling Page";
                getLogEvent(CounsellingActivity.this);
                Intent intentHome = new Intent(CounsellingActivity.this, HomeActivity.class);
                startActivity(intentHome);
                return true;
            case R.id.navigation_courses:
                courseName = "";
                lessonLog = "";
                PagenameLog = "Course us";
                ActivityLog = "Counselling Page";
                getLogEvent(CounsellingActivity.this);
                Intent intentCourses = new Intent(this, HomeActivity.class);
                intentCourses.putExtra("isCoursePage","Course");
                startActivity(intentCourses);
                return true;
            case R.id.navigation_enrol:

                return true;
            case R.id.navigation_chat:
                courseName = "";
                lessonLog = "";
                PagenameLog = "Chat with us";
                ActivityLog = "Counselling Page";
                getLogEvent(CounsellingActivity.this);
                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
                startActivity(myIntent);
                return true;
            case R.id.navigation_contact:
                courseName = "";
                lessonLog = "";
                PagenameLog = "Contact us";
                ActivityLog = "Counselling Page";
                getLogEvent(CounsellingActivity.this);
                Intent contactIntent = new Intent(this, ContactUsActivity.class);
                startActivity(contactIntent);
                return true;
        }

        return true;
    }

    public void ChatUs(View view){
        CourseLog = "";
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Online Counselling Page";
        getLogEvent(CounsellingActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }

    public class CounsellingListAdapter extends RecyclerView.Adapter<CounsellingListAdapter.ViewHolder> {
        Context context;
        ArrayList<CounsellingDataModel> counsellingData;
        public CounsellingListAdapter(Context context,ArrayList<CounsellingDataModel> counsellingData){
            this.context = context;
            this.counsellingData = counsellingData;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v = inflater.inflate(R.layout.counselling_list_adapter, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            try {

            Glide.with(context)
                    .asBitmap()
                    .load(counsellingData.get(position).getCounsellingImage())
                    //.placeholder(R.drawable.duser1)
                    .into(UIUtils.getRoundedImageTarget(context, holder.videoImage, 30));
            holder.txtTitle.setText(counsellingData.get(position).getCounsellingTitle());
            holder.videoPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    courseName = "";
                    lessonLog = "";
                    PagenameLog = counsellingData.get(position).getCounsellingTitle();
                    ActivityLog = "Counselling Page";
                    getLogEvent(CounsellingActivity.this);
                    mp4URL = counsellingData.get(position).getCounsellingVideo();
                    lessonLog = "";
                    courseName = counsellingData.get(position).getCounsellingTitle();
                    videoLayout.setVisibility(View.VISIBLE);
                    player.loadVideo(mp4URL);
                }
            });
            holder.btnFindCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lessonLog = "";
                    courseName = counsellingData.get(position).getCounsellingTitle();
                    PagenameLog = "Clicked on mobile number";
                    ActivityLog = "Online Counselling Page";
                    getLogEvent(CounsellingActivity.this);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+getResources().getString(R.string.mobileNumber)));
                    startActivity(intent);
                }
            });
            holder.btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lessonLog = "";
                    courseName = counsellingData.get(position).getCounsellingTitle();
                    PagenameLog = "Clicked on Register";
                    ActivityLog = "Online Counselling Page";
                    new SocialMediaEventsHelper(CounsellingActivity.this).EventRegisterCourse();
                    getLogEvent(CounsellingActivity.this);
                    Intent intent = new Intent(CounsellingActivity.this, RegisterCourseActivity.class);
                    startActivity(intent);
                }
            });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return counsellingData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView imgBanner,videoImage,videoPlay;
            Button btnFindCenter,btnRegister;
            TextView txtTitle;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgBanner = itemView.findViewById(R.id.imgBanner);
                videoImage = itemView.findViewById(R.id.videoImage);
                videoPlay = itemView.findViewById(R.id.videoPlay);
                txtTitle = itemView.findViewById(R.id.txtTitle);
                btnFindCenter = itemView.findViewById(R.id.btnFindCenter);
                btnRegister = itemView.findViewById(R.id.btnRegister);
            }
        }
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {
        String playbackState = "NOT_PLAYING";
        String bufferingState = "";
        @Override
        public void onPlaying() {
            playbackState = "PLAYING";
            PagenameLog = "Video playing";
            ActivityLog = "Online Counselling Page";
            getLogEvent(CounsellingActivity.this);
            log("\tPLAYING " + getTimesText());
        }

        @Override
        public void onBuffering(boolean isBuffering) {
            bufferingState = isBuffering ? "(BUFFERING)" : "";
            PagenameLog = "Video buffering";
            ActivityLog = "Online Counselling Page";
            getLogEvent(CounsellingActivity.this);
            log("\t\t" + (isBuffering ? "BUFFERING " : "NOT BUFFERING ") + getTimesText());
        }

        @Override
        public void onStopped() {
            playbackState = "STOPPED";
            PagenameLog = "Video stopped";
            ActivityLog = "Online Counselling Page";
            getLogEvent(CounsellingActivity.this);
            log("\tSTOPPED");
        }

        @Override
        public void onPaused() {
            playbackState = "PAUSED";
            PagenameLog = "Video paused";
            ActivityLog = "Online Counselling Page";
            getLogEvent(CounsellingActivity.this);
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

    public void getCounselling(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metadata = new JSONObject();
        try {
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("appname","Hamstech");
            params.put("page","hamstech_counseling_page");
            metadata.put("metadata",params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = metadata.toString();
        hocLoadingDialog.showLoadingDialog();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getCounselling, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    if (jo.getString("status").equals("ok")){
                        JSONArray jsonArray = jo.getJSONArray("counseling_details");
                        counsellingData.clear();
                        for (int i = 0; i<jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            CounsellingDataModel dataModel = new CounsellingDataModel();
                            dataModel.setCounsellingId(object.getString("counseling_id"));
                            dataModel.setCounsellingTitle(object.getString("counseling_name"));
                            dataModel.setCounsellingVideo(object.getString("video_url"));
                            dataModel.setCounsellingImage(object.getString("image_url"));
                            counsellingData.add(dataModel);
                        }
                        counsellingListAdapter = new CounsellingListAdapter(CounsellingActivity.this,counsellingData);
                        counsellingList.setLayoutManager(new LinearLayoutManager(CounsellingActivity.this, LinearLayoutManager.VERTICAL, false));
                        counsellingList.setAdapter(counsellingListAdapter);

                        hocLoadingDialog.hideDialog();
                    } else {
                        hocLoadingDialog.hideDialog();
                        Toast.makeText(CounsellingActivity.this, "Try again", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CounsellingActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };queue.add(sr);
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
            data.put("course",courseName);
            data.put("lesson",lessonLog);
            data.put("activity",ActivityLog);
            data.put("pagename",PagenameLog);
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
