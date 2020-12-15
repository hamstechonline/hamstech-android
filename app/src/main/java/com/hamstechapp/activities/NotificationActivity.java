package com.hamstechapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hamstechapp.R;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.fragment.NavigationFragment;
import com.hamstechapp.fragment.SearchFragment;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.UIUtils;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    NavigationFragment navigationFragment;
    BottomNavigationView bottom_navigation;
    DrawerLayout drawer;
    ImageView imgDiscover;

    RecyclerView notificationList;
    NotificationListAdapter notificationListAdapter;
    HocLoadingDialog hocLoadingDialog;
    ArrayList<AffiliationDataModel> arrayData = new ArrayList<>();
    ArrayList<AffiliationDataModel> notificationDetail = new ArrayList<>();
    LogEventsActivity logEventsActivity;
    String lessonLog,ActivityLog,PagenameLog;
    CheckBox imgSearch;
    TextView txtHeaderTitle;
    RelativeLayout searchParent;
    SearchFragment searchFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.notifications_activity);

        initView();
    }

    private void initView(){

        bottom_navigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        notificationList = findViewById(R.id.notificationList);
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

        hocLoadingDialog = new HocLoadingDialog(this);
        logEventsActivity = new LogEventsActivity();

        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counsellingIntent = new Intent(NotificationActivity.this, CounsellingActivity.class);
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
        getNotifications(this);

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
                Intent intentHome = new Intent(NotificationActivity.this, HomeActivity.class);
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

        return true;
    }

    public void ChatUs(View view){
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Notification Page";
        getLogEvent(NotificationActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }

    public void getNotifications(Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metaData = new JSONObject();
        try {
            params.put("appname","Hamstech");
            params.put("page","notification");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("lang","en");
            metaData.put("metadata", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hocLoadingDialog.showLoadingDialog();
        final String mRequestBody = metaData.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getNotifications, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    arrayData.clear();
                    if (jo.isNull("list")){
                        Toast.makeText(NotificationActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray jsonArray = jo.getJSONArray("list");

                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            AffiliationDataModel dataModel = new AffiliationDataModel();

                            dataModel.setId(object.getString("termid"));
                            dataModel.setAffiliationTitle(object.getString("title"));
                            dataModel.setAffiliationDescription(object.getString("description"));
                            dataModel.setDate(object.getString("date"));

                            arrayData.add(dataModel);
                        }
                        notificationListAdapter = new NotificationListAdapter(NotificationActivity.this,arrayData);
                        notificationList.setLayoutManager(new LinearLayoutManager(NotificationActivity.this, LinearLayoutManager.VERTICAL, false));
                        notificationList.setAdapter(notificationListAdapter);
                        hocLoadingDialog.hideDialog();
                        if (getIntent().getStringExtra("notificationId")!= null){
                            showNotifications(NotificationActivity.this,Integer.parseInt(getIntent().getStringExtra("notificationId")));
                        }
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
                    Toast.makeText(NotificationActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(7000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

        Context context;
        ArrayList<AffiliationDataModel> arrayData;
        public NotificationListAdapter(Context context, ArrayList<AffiliationDataModel> arrayData){
            this.context = context;
            this.arrayData = arrayData;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v = inflater.inflate(R.layout.notification_list_adapter, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            try {
                if ( (position+1) % 2 == 0 ) {
                    holder.checkbox.setChecked(true);
                    holder.txtTitle.setTextColor(context.getResources().getColor(R.color.gray));
                    holder.txtDescription.setTextColor(context.getResources().getColor(R.color.gray));
                    holder.txtTime.setTextColor(context.getResources().getColor(R.color.gray));
                    holder.txtTitle.setText(arrayData.get(position).getAffiliationTitle());
                    holder.txtDescription.setText(arrayData.get(position).getAffiliationDescription());
                    holder.txtTime.setText(arrayData.get(position).getDate());
                    holder.cardParent.setCardBackgroundColor(Color.parseColor("#E1E1E1"));
                    holder.checkbox.setButtonDrawable(R.drawable.ic_notification_gray);
                } else {
                    holder.checkbox.setChecked(false);
                    holder.cardParent.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.txtTitle.setText(arrayData.get(position).getAffiliationTitle());
                    holder.txtDescription.setText(arrayData.get(position).getAffiliationDescription());
                    holder.txtTime.setText(arrayData.get(position).getDate());
                    holder.checkbox.setButtonDrawable(R.drawable.ic_notification);
                }
                holder.cardParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lessonLog = arrayData.get(position).getAffiliationTitle();
                        ActivityLog = "Clicked on notification";
                        PagenameLog = "Notification page";
                        getLogEvent(context);
                        showNotifications(context,Integer.parseInt(arrayData.get(position).getId()));
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return arrayData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            CheckBox checkbox;
            CardView cardParent;
            TextView txtTitle,txtDescription,txtTime;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtTitle = itemView.findViewById(R.id.txtTitle);
                txtDescription = itemView.findViewById(R.id.txtDescription);
                txtTime = itemView.findViewById(R.id.txtTime);
                checkbox = itemView.findViewById(R.id.checkbox);
                cardParent = itemView.findViewById(R.id.cardParent);
            }
        }
    }

    public void showNotifications(final Context context, final int place){

        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metaData = new JSONObject();
        try {
            params.put("appname","Hamstech");
            params.put("page","notificationdetail");
            params.put("apikey",context.getResources().getString(R.string.lblApiKey));
            params.put("termid",place);
            params.put("lang","en");
            metaData.put("metadata", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hocLoadingDialog.showLoadingDialog();
        final String mRequestBody = metaData.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getNotifications, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    notificationDetail.clear();
                    if (jo.getString("status").equals("ok")){
                        JSONArray jsonArray = jo.getJSONArray("details");
                        if (jsonArray.length() !=0){
                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                AffiliationDataModel dataModel = new AffiliationDataModel();

                                dataModel.setId(object.getString("postid"));
                                dataModel.setAffiliationTitle(object.getString("title"));
                                dataModel.setAffiliationDescription(object.getString("description"));
                                dataModel.setAffiliationImage(object.getString("image"));

                                notificationDetail.add(dataModel);
                                //PagenameLog = object.getString("title");
                            }
                            hocLoadingDialog.hideDialog();
                            showNotification(notificationDetail);
                            //imageView.setImage(ImageSource.uri(notificationDetail.get(0).lesson_image_url));
                        }

                    } else {
                        Toast.makeText(context, "Invalid Request", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    public void showNotification(ArrayList<AffiliationDataModel> notificationDetail){
        final Dialog dialog = new Dialog(NotificationActivity.this);
        dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.notification_item_dialoge);
        dialog.setCancelable(true);
        int itemPosition;
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        TextView txtDescription = dialog.findViewById(R.id.txtDescription);
        ImageView imgCancel = dialog.findViewById(R.id.imgCancel);
        ImageView imageView = dialog.findViewById(R.id.imageView);
        itemPosition = 0;
        if (!notificationDetail.get(itemPosition).getAffiliationImage().equals("")){
            imageView.setVisibility(View.VISIBLE);
            Glide.with(NotificationActivity.this)
                    .asBitmap()
                    .load(notificationDetail.get(itemPosition).getAffiliationImage())
                    //.placeholder(R.drawable.duser1)
                    .into(UIUtils.getRoundedImageTarget(NotificationActivity.this, imageView, 0));
        }
        txtTitle.setText(notificationDetail.get(itemPosition).getAffiliationTitle());
        txtDescription.setText(notificationDetail.get(itemPosition).getAffiliationDescription());

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
            data.put("appname","Hamstech");
            data.put("mobile", UserDataConstants.userMobile);
            data.put("fullname",UserDataConstants.userName);
            data.put("email","");
            data.put("category","");
            data.put("course","");
            data.put("lesson",lessonLog);
            data.put("activity",ActivityLog);
            data.put("pagename",PagenameLog);
            boolean logevent = logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
