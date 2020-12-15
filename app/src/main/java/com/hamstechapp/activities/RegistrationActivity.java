package com.hamstechapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.textview.MaterialTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hamstechapp.R;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.database.User;
import com.hamstechapp.database.UserDatabase;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.UserDataConstants;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    LinearLayout linOtpLayout;
    Button btnGetOtp,btnResend,btnVerify;
    MaterialTextView txtExpire,txtLogin;
    TextView otpMessage;
    EditText txtName,txtMobile,txtOTP;
    CountDownTimer countDownTimer;
    private LocationManager locationManager;
    Criteria criteria;
    private String provider,userName,cityName = "", getPincode = "", getAddress = "", getState = "", getCountryName = "",
            eventType,otphash, otptimestamp,resMobile,countrycode = "91",langPref = "en",gcm_id,networkType = "",lastNumber = "";
    int downSpeed, upSpeed;
    Location location;
    LogEventsActivity logEventsActivity;
    UserDatabase userDatabase;
    ArrayList<User> userDetails = new ArrayList<>();
    HocLoadingDialog hocLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.registration_activity);
        initView();
    }
    private void initView(){
        linOtpLayout = findViewById(R.id.linOtpLayout);
        btnGetOtp = findViewById(R.id.btnGetOtp);
        btnResend = findViewById(R.id.btnResend);
        btnVerify = findViewById(R.id.btnVerify);
        txtName = findViewById(R.id.txtName);
        txtMobile = findViewById(R.id.txtMobile);
        txtOTP = findViewById(R.id.txtOTP);
        txtExpire = findViewById(R.id.txtExpire);
        txtLogin = findViewById(R.id.txtLogin);
        otpMessage = findViewById(R.id.otpMessage);

        linOtpLayout.setVisibility(View.GONE);
        btnVerify.setVisibility(View.GONE);
        txtExpire.setVisibility(View.GONE);
        btnResend.setVisibility(View.GONE);

        logEventsActivity = new LogEventsActivity();
        hocLoadingDialog = new HocLoadingDialog(this);
        checkNetwork();
        userDatabase = Room.databaseBuilder(this,
                UserDatabase.class, "database-name").allowMainThreadQueries().addCallback(callback).build();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(RegistrationActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                gcm_id = newToken;
            }
        });
        btnGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtMobile.getText().toString().length() == 10) {
                    userName = txtName.getText().toString().trim();
                    GetOtp(RegistrationActivity.this);
                } else {
                    Toast.makeText(RegistrationActivity.this, "Enter valid 10 digit mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP();
            }
        });
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnResend.setVisibility(View.GONE);
                if (txtMobile.getText().toString().length() == 10) {
                    GetOtp(RegistrationActivity.this);
                } else {
                    Toast.makeText(RegistrationActivity.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        takeCameraPermission();

        countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtExpire.setVisibility(View.VISIBLE);
                long min = ((millisUntilFinished / 60000) % 60);
                long sec = ((millisUntilFinished / 1000) % 60);
                txtExpire.setText("OTP Expires in: " + String.format("%02d", min) + ":" + String.format("%02d", sec) + " Minutes");
            }

            @Override
            public void onFinish() {
                btnResend.setVisibility(View.VISIBLE);
                txtExpire.setVisibility(View.GONE);
                lastNumber = resMobile.substring(resMobile.length() - 4);
                txtOTP.setText(resMobile.substring(resMobile.length() - 4));
                otpMessage.setVisibility(View.VISIBLE);
                verifyOTP();
            }
        };
    }

    private boolean ValidateInputs() {
        boolean result = IsValid(txtName, "Enter Name") &&
                IsValid(txtMobile, "Enter Mobile Number");
        return result;
    }

    private boolean IsValid(EditText txtText, String validationMessage) {
        if (txtText.getText().toString().trim().equals("")) {
            txtText.setError(validationMessage);
            return false;
        }
        return true;
    }

    RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase database) {
            super.onCreate(database);
            Log.i("HomeActivity","onCreate oinvoked");
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase database) {
            super.onOpen(database);
            Log.i("HomeActivity","onOpen oinvoked");
        }
    };

    public void GetOtp(Context context) {
        if (!ValidateInputs()) {
            return;
        } else {
            postGetOtp(this);
        }
    }
    public void verifyOTP(){
        VerifyOtp(this);
    }
    public void takeCameraPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            requestLocation();
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
            takeCameraPermission();
            return;
        } else {
            try {
                provider = locationManager.getBestProvider(criteria, true);
                location = locationManager.getLastKnownLocation(provider);
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
                StringBuilder builder = new StringBuilder();
                try {
                    List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
                    int maxLines = address.get(0).getMaxAddressLineIndex();
                    for (int i=0; i<=maxLines; i++) {
                        String addressStr = address.get(0).getAddressLine(i);
                        builder.append(addressStr);
                        builder.append(" ");
                        Log.e("address","197   "+addressStr);
                    }

                    cityName = address.get(0).getLocality(); //This is the complete address.
                    getPincode = address.get(0).getPostalCode(); //This is the complete address.
                    getAddress = address.get(0).getAddressLine(0);
                    getState = address.get(0).getAdminArea(); //This is the complete address.
                    getCountryName = address.get(0).getCountryName(); //This is the complete address.
                    Log.e("address","197   "+address.get(0).getAddressLine(0));

                } catch (IOException e) {
                    // Handle IOException
                } catch (NullPointerException e) {
                    // Handle NullPointerException
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    public void postGetOtp(final Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        resMobile = txtMobile.getText().toString().trim();
        hocLoadingDialog.showLoadingDialog();
        try {
            jsonObject.put("page", "registration");
            jsonObject.put("phone", txtMobile.getText().toString().trim());
            jsonObject.put("countrycode", "91");
            jsonObject.put("lang", "en");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String mRequestBody = jsonObject.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.getOTP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject object = jsonObject.getJSONObject("status");
                            JSONObject objData = object.getJSONObject("metadata");
                            if (object.getString("data").equals("OTP Sent Successfully")) {
                                otptimestamp = String.valueOf(objData.getInt("timestamp"));
                                linOtpLayout.setVisibility(View.VISIBLE);
                                btnVerify.setVisibility(View.VISIBLE);
                                txtExpire.setVisibility(View.VISIBLE);
                                btnGetOtp.setVisibility(View.GONE);
                                txtMobile.setEnabled(false);
                                txtName.setEnabled(false);
                                hocLoadingDialog.hideDialog();
                                countDownTimer.start();
                                eventType = "Clicked on OTP";
                                getLogEvent(RegistrationActivity.this, txtMobile.getText().toString().trim());
                            } else {
                                Toast.makeText(RegistrationActivity.this, "" + object.getString("status"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                hocLoadingDialog.hideDialog();
                                if (jsonObject.getString("status").equals("Phone number is already register, Please login")){
                                    Toast.makeText(RegistrationActivity.this, ""+jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                    intent.putExtra("getMobile", txtMobile.getText().toString().trim());
                                    intent.putExtra("getCountryCode", countrycode);
                                    startActivity(intent);
                                    e.printStackTrace();
                                } else {
                                    Toast.makeText(RegistrationActivity.this, ""+jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex){

                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(RegistrationActivity.this, "No Internet Connection! Please try again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Toast.makeText(RegistrationActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(7000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
    public void VerifyOtp(Context context) {
        resMobile = txtMobile.getText().toString().trim();
        JSONObject jsonObject = new JSONObject();
        hocLoadingDialog.showLoadingDialog();
        try {
            jsonObject.put("appname", "Hamstech");
            jsonObject.put("apikey", getResources().getString(R.string.lblApiKey));
            jsonObject.put("page", "registration");
            jsonObject.put("name", txtName.getText().toString().trim());
            jsonObject.put("phone", resMobile);
            jsonObject.put("cityName", cityName);
            jsonObject.put("user_address", getAddress);
            jsonObject.put("user_state", getState);
            jsonObject.put("user_pincode", getPincode);
            jsonObject.put("user_country", getCountryName);
            jsonObject.put("otp", txtOTP.getText().toString().trim());
            jsonObject.put("otptimestamp", otptimestamp);
            jsonObject.put("device", "android");
            jsonObject.put("last_number", lastNumber);
            jsonObject.put("networkconnection", networkType);
            jsonObject.put("downSpeed", ""+downSpeed);
            jsonObject.put("upSpeed", ""+upSpeed);
            jsonObject.put("gcm_id", gcm_id);
            jsonObject.put("countrycode", countrycode);
            jsonObject.put("lang", langPref);

            postVerify(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postVerify(JSONObject jsonObject) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String mRequestBody = jsonObject.toString();
        Log.e("address","395   "+mRequestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.verifyOTP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject object = jsonObject.getJSONObject("status");
                            JSONObject objData = object.getJSONObject("data");
                            if (objData.getString("OTP").equals("OTP validated Successfully")) {
                                countDownTimer.cancel();
                                UserDataConstants.userName = objData.getString("name");
                                UserDataConstants.userMobile = objData.getString("phone");
                                userDatabase.userDao().insertAll(new User(0,
                                        objData.getString("name"), objData.getString("phone")));
                                new getUserDetails().execute();
                                hocLoadingDialog.hideDialog();
                                Intent intent = new Intent(RegistrationActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                                RegistrationActivity.this.finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(RegistrationActivity.this, "No Internet Connection! Please try again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Toast.makeText(RegistrationActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(7000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
    public void checkNetwork(){
        //https://www.codota.com/code/java/classes/android.net.NetworkInfo
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        Log.e("networkInfo","510   "+networkInfo.getTypeName());
        NetworkCapabilities nc = conManager.getNetworkCapabilities(conManager.getActiveNetwork());
        networkType = networkInfo.getTypeName();
        downSpeed = nc.getLinkDownstreamBandwidthKbps();
        upSpeed = nc.getLinkUpstreamBandwidthKbps();
    }
    private class getUserDetails extends AsyncTask<User,Void,Void> {

        @Override
        protected Void doInBackground(User... users) {
            userDetails.clear();
            userDetails.addAll(userDatabase.userDao().getAll());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void getLogEvent(Context context,String userMobile){
        JSONObject data = new JSONObject();
        try {
            data.put("apikey",context.getResources().getString(R.string.lblApiKey));
            data.put("appname","Dashboard");
            data.put("mobile", userMobile);
            data.put("fullname","");
            data.put("email","");
            data.put("category","");
            data.put("course","");
            data.put("lesson","");
            data.put("activity","New Register");
            data.put("pagename",eventType);
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
