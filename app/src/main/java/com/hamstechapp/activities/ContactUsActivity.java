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
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hamstechapp.R;
import com.hamstechapp.common.GetNearestBranch;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.fragment.NavigationFragment;
import com.hamstechapp.fragment.SearchFragment;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.UserDataConstants;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class ContactUsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    NavigationFragment navigationFragment;
    BottomNavigationView bottom_navigation;
    DrawerLayout drawer;
    ImageView imgDiscover;
    GetNearestBranch getNearestBranch;
    TextView txtNearestBranch,txtMobileNumber;
    HocLoadingDialog hocLoadingDialog;
    UserDataConstants userDataConstants;
    LinearLayout requestCallBack;
    String ActivityLog,PagenameLog,lessonLog,CourseLog;
    LogEventsActivity logEventsActivity;
    LinearLayout mapOnclick;
    private SupportMapFragment mSupportMapFragment;
    private LocationManager locationManager;
    Criteria criteria;
    String provider;
    Location location;
    GoogleMap Map;
    CheckBox imgSearch;
    TextView txtHeaderTitle;
    RelativeLayout searchParent;
    SearchFragment searchFragment;
    double branchLats[] = {17.409634,17.436826,17.438766,17.450836,17.490768,17.442345,17.370879};
    double branchLongs[] = {78.485407,78.453033,78.409774,78.489081,78.389206,78.369605,78.543669};
    String[] branchNames = {"HIMAYATNAGAR","Punjagutta","Jubilee Hills","Secunderabad","Kukatpally","Gachibowli","Kothapet"};
    double lat,lng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.contact_activity);

        initView();
    }

    private void initView(){

        bottom_navigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        txtNearestBranch = findViewById(R.id.txtNearestBranch);
        txtMobileNumber = findViewById(R.id.txtMobileNumber);
        requestCallBack = findViewById(R.id.requestCallBack);
        mapOnclick = findViewById(R.id.mapOnclick);
        imgDiscover = findViewById(R.id.imgDiscover);
        imgSearch = findViewById(R.id.imgSearch);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchParent = findViewById(R.id.searchParent);

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);
        bottom_navigation.setOnNavigationItemSelectedListener(this);
        bottom_navigation.getMenu().findItem(R.id.navigation_contact).setChecked(true);

        navigationFragment = NavigationFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.navSideMenu, navigationFragment, "")
                .commit();

        txtMobileNumber.setText(R.string.mobileNumber);
        hocLoadingDialog = new HocLoadingDialog(this);
        userDataConstants = new UserDataConstants();
        logEventsActivity = new LogEventsActivity();

        getNearestBranch = new GetNearestBranch(this);
        takeLocationPermission();

        txtMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lessonLog = "";
                ActivityLog = "Clicked on mobile number";
                PagenameLog = "Contact Us page";
                getLogEvent(ContactUsActivity.this);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+getResources().getString(R.string.mobileNumber)));
                startActivity(intent);
            }
        });

        requestCallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lessonLog = "";
                ActivityLog = "Request callback";
                PagenameLog = "Contact Us page";
                getLogEvent(ContactUsActivity.this);
                getRequestCall(ContactUsActivity.this);
            }
        });
        mapOnclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String map = "http://maps.google.co.in/maps?q="+UserDataConstants.resultLat+","+UserDataConstants.resultLong+"("+"Hamstech"+")";
                String geoUri = "http://maps.google.com/maps?q=" + UserDataConstants.resultLat + "," + UserDataConstants.resultLong + " (" + "Hamstech" + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(intent);
            }
        });
        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counsellingIntent = new Intent(ContactUsActivity.this, CounsellingActivity.class);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                Intent intentHome = new Intent(ContactUsActivity.this, HomeActivity.class);
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

                return true;
        }

        return true;
    }

    @Override
    protected void onStop() {
        drawer.closeDrawers();
        super.onStop();
    }

    public void ChatUs(View view){
        CourseLog = "";
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Contact us Page";
        getLogEvent(ContactUsActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }

   /* @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;
        LatLng sydney = new LatLng(UserDataConstants.resultLat, UserDataConstants.resultLong);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Hamstech Online Services"));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String map = "http://maps.google.co.in/maps?q="+UserDataConstants.resultLat+","+UserDataConstants.resultLong+"("+"Hamstech"+")";
                String geoUri = "http://maps.google.com/maps?q=" + UserDataConstants.resultLat + "," + UserDataConstants.resultLong + " (" + "Hamstech" + ")";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(intent);
            }
        });
    }*/
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
   }

    public void getRequestCall(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        hocLoadingDialog.showLoadingDialog();
        JSONObject params = new JSONObject();

        try {
            params.put("leadid",UserDataConstants.LeadId);
            params.put("appname","Hamstech");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = params.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getRequestCallBack, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    JSONObject object = jo.getJSONObject("status");

                    if (object.getInt("status")==200){
                        hocLoadingDialog.hideDialog();
                        RequestCallBack();
                    } else {
                        Toast.makeText(ContactUsActivity.this, ""+jo.getString("messsage"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ContactUsActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };

        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        sr.setRetryPolicy(policy);

        queue.add(sr);
    }
    public void RequestCallBack(){
        LayoutInflater li = getLayoutInflater();
        View layout = li.inflate(R.layout.request_dialogue,(ViewGroup) findViewById(R.id.custom_toast_layout));
        Toast toast = new Toast(getApplicationContext());
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
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
                            mSupportMapFragment.getMapAsync(ContactUsActivity.this);
                            txtNearestBranch.setText(UserDataConstants.branchName);
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            takeLocationPermission();
            return;
        } else {
            try {

                getNearestBranch.start();
                txtNearestBranch.setText(UserDataConstants.branchName);

                provider = locationManager.getBestProvider(criteria, true);
                location = locationManager.getLastKnownLocation(provider);
                UserDataConstants.getLatitude = location.getLatitude();
                UserDataConstants.getLongitude = location.getLongitude();
                mSupportMapFragment.getMapAsync(this);

            } catch (Exception e) {
                e.printStackTrace();
            }

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
            data.put("lesson",lessonLog);
            data.put("activity",ActivityLog);
            data.put("pagename",PagenameLog);
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
