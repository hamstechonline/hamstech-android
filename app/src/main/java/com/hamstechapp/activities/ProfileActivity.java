package com.hamstechapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
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
import com.hamstechapp.fragment.NavigationFragment;
import com.hamstechapp.fragment.SearchFragment;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.UIUtils;
import com.hamstechapp.utils.UserDataConstants;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    NavigationFragment navigationFragment;
    BottomNavigationView bottom_navigation;
    DrawerLayout drawer;
    ImageView imgDiscover;

    private static final int FILE_SELECT_CODE = 0;
    Bitmap mBitmap, resized;

    String imagePathData = "",type_image,picturePath,imgString;
    String ActivityLog,PagenameLog,lessonLog,CourseLog;
    ImageView imgBanner;
    CircleImageView imgProfile;
    TextView txtCenter,txtUserMobile,txtEdit;
    EditText txtUserName,txtAddress;
    Button btnSave;
    HocLoadingDialog hocLoadingDialog;
    CheckBox imgSearch;
    TextView txtHeaderTitle;
    RelativeLayout searchParent;
    SearchFragment searchFragment;
    LogEventsActivity logEventsActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.profile_activity);

        initView();
    }

    private void initView(){

        bottom_navigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        imgBanner = findViewById(R.id.imgBanner);
        imgProfile = findViewById(R.id.imgProfile);
        txtCenter = findViewById(R.id.txtCenter);
        txtUserName = findViewById(R.id.txtUserName);
        txtEdit = findViewById(R.id.txtEdit);
        txtUserMobile = findViewById(R.id.txtUserMobile);
        txtAddress = findViewById(R.id.txtAddress);
        btnSave = findViewById(R.id.btnSave);
        imgDiscover = findViewById(R.id.imgDiscover);
        imgSearch = findViewById(R.id.imgSearch);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchParent = findViewById(R.id.searchParent);

        bottom_navigation.setOnNavigationItemSelectedListener(this);
        bottom_navigation.getMenu().findItem(R.id.navigation_enrol).setChecked(true);

        txtCenter.setText(UserDataConstants.branchName);
        txtUserName.setEnabled(false);
        txtAddress.setEnabled(false);

        navigationFragment = NavigationFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.navSideMenu, navigationFragment, "")
                .commit();

        hocLoadingDialog = new HocLoadingDialog(this);
        logEventsActivity = new LogEventsActivity();
        Glide.with(ProfileActivity.this)
                .asBitmap()
                .load(R.drawable.profile_banner)
                .into(UIUtils.getRoundedImageTarget(ProfileActivity.this, imgBanner, 20));

        getProfile(this);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStoragePermissionGranted();
            }
        });
        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUserName.setEnabled(true);
                txtAddress.setEnabled(true);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ValidateInputs()) {

                } else {
                    getUpdateProfile(ProfileActivity.this);
                }
            }
        });
        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counsellingIntent = new Intent(ProfileActivity.this, CounsellingActivity.class);
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

    @Override
    protected void onStop() {
        drawer.closeDrawers();
        super.onStop();
    }

    private boolean ValidateInputs() {

        boolean result = IsValid(txtUserName, "Enter Name") &&
                IsValid(txtAddress, "Enter Address");
        return result;
    }

    private boolean IsValid(EditText txtText, String validationMessage) {
        if (txtText.getText().toString().trim().equals("")) {
            txtText.setError(validationMessage);
            return false;
        }
        return true;
    }

    public void sideMenu(View view){
        drawer.openDrawer(Gravity.RIGHT);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                Intent intentHome = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intentHome);
                return true;
            case R.id.navigation_courses:
                Intent intentCourses = new Intent(this, CoursesActivity.class);
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
        CourseLog = "";
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Profile Page";
        getLogEvent(ProfileActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }

    public void isStoragePermissionGranted(){
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showFileChooser();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                   PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showFileChooser() {

        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(i, FILE_SELECT_CODE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE) {

            if (resultCode == Activity.RESULT_OK)
            {
                if (data != null)
                {
                    Uri selectedImage = data.getData();

                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        Log.e("resized",selectedImage+""+mBitmap.getHeight());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    resized = Bitmap.createScaledBitmap(mBitmap,(int)(mBitmap.getWidth()*0.3), (int)(mBitmap.getHeight()*0.3), true);
                    imgProfile.setImageBitmap(resized);
                    if (resized != null) {
                        imagePathData = getEncoded64ImageStringFromBitmap(resized);
                        uploadImage(this);
                        System.out.println(""+imagePathData);
                    }
                    Log.e("resized",resized.getWidth()+""+resized.getHeight());
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);

                    type_image = getContentResolver().getType(selectedImage);

                    if (type_image.equals(null)) type_image = "images/jpeg";

                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    try{
                        File file = new File(picturePath);
                        long length = file.length();
                        length = length/1024;
                        Log.e("File_Path : " , file.getPath() + ", File size : " + length +" KB");
                    } catch(Exception e) {
                        System.out.println("File not found : " + e.getMessage() + e);
                    }

                    if (picturePath.isEmpty() || picturePath == null) {
                        Toast.makeText(this, "Unable to capture the image..Please try again", Toast.LENGTH_LONG).show();
                    } else {

                    }
                } else {
                    Toast.makeText(this,
                            "Image Loading Failed", Toast.LENGTH_LONG)
                            .show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {

                Toast.makeText(this,
                        "User cancelled file upload", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(this,
                        "Sorry! Failed to load file", Toast.LENGTH_LONG)
                        .show();
            }
        }

    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    public void getProfile(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        try {
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("page","profile");
            params.put("phone",UserDataConstants.userMobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = params.toString();
        hocLoadingDialog.showLoadingDialog();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getProfile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    JSONObject object = jo.getJSONObject("status");
                    if (object.getInt("status") == 200){
                        JSONObject jsonObject = jo.getJSONObject("data");
                        txtUserName.setText(jsonObject.getString("prospectname"));
                        UserDataConstants.LeadId = jsonObject.getString("prospectname");
                        txtUserMobile.setText("+91 "+jsonObject.getString("phone"));
                        txtAddress.setText(jsonObject.getString("address"));
                        Glide.with(ProfileActivity.this)
                                .load(jsonObject.getString("profilepic"))
                                .placeholder(R.drawable.ic_userprofile)
                                .error(R.drawable.ic_userprofile)
                                .into(imgProfile);
                        hocLoadingDialog.hideDialog();
                    } else {
                        hocLoadingDialog.hideDialog();
                        Toast.makeText(ProfileActivity.this, "Try again", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ProfileActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return mRequestBody.getBytes();
                }
            }

        };queue.add(sr);
    }

    public void uploadImage(Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();

        try {
            params.put("page","profile");
            params.put("phone",UserDataConstants.userMobile);
            params.put("imagedata",imagePathData);
            Log.e("Params","270    "+params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = params.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.uploadImage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    JSONObject object = jo.getJSONObject("status");
                    if (object.getString("messsage").equals("Profile Pic updated Successfully")){
                        Toast.makeText(ProfileActivity.this, ""+object.getString("messsage"), Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ProfileActivity.this,ProfileActivity.class));
                    } else {
                        Toast.makeText(ProfileActivity.this, ""+jo.getString("messsage"), Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e)
                {
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
                    Toast.makeText(ProfileActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };
        queue.add(sr);
    }

    public void getUpdateProfile(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        try {
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("page","profile");
            params.put("phone",UserDataConstants.userMobile);
            params.put("email",UserDataConstants.userMail);
            params.put("prospectname",txtUserName.getText().toString().trim());
            params.put("city",UserDataConstants.userCity);
            params.put("address",txtAddress.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = params.toString();
        hocLoadingDialog.showLoadingDialog();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.updateprofile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    JSONObject object = jo.getJSONObject("status");
                    if (object.getString("messsage").equals("Profile Data Updated Successfully")){
                        JSONObject jsonObject = jo.getJSONObject("data");
                        txtUserName.setText(jsonObject.getString("name"));
                        txtAddress.setText(jsonObject.getString("address"));
                        txtUserName.setEnabled(false);
                        txtAddress.setEnabled(false);
                        hocLoadingDialog.hideDialog();
                    } else {
                        hocLoadingDialog.hideDialog();
                        Toast.makeText(ProfileActivity.this, "Try again", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ProfileActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
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
