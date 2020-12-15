package com.hamstechapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hamstechapp.R;
import com.hamstechapp.ccAvenueActivities.WebViewActivity;
import com.hamstechapp.ccAvenueUtils.AvenuesParams;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.HomePageDatamodel;
import com.hamstechapp.fragment.NavigationFragment;
import com.hamstechapp.fragment.SearchFragment;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;

public class RegisterCourseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    NavigationFragment navigationFragment;
    BottomNavigationView bottom_navigation;
    DrawerLayout drawer;
    ImageView imgDiscover,imgSelectedImage;
    HocLoadingDialog hocLoadingDialog;
    UserDataConstants userDataConstants;
    String ActivityLog,PagenameLog,lessonLog,CourseLog,categoryLog;
    LogEventsActivity logEventsActivity;
    RecyclerView optionsList;
    Button btnPayNow,btnSubmit;
    EditText txtFirstName,txtLastName,txtEmail,txtMobile,txtAddress,txtDistrict,txtCity,txtState,txtPincode;
    NestedScrollView scrollPayment,scrollBillingAddress;
    PaymentOptionsListAdapter paymentOptionsListAdapter;
    String[] names = {"CREDIT CARD","DEBIT CARD","INTERNET BANKING","UPI/ WALLETS"};
    int[] optionImgs = {R.drawable.creditcard_checkbox,R.drawable.debittcard_checkbox,
            R.drawable.internetbank_checkbox,R.drawable.upiwallet_checkbox};
    int optionSelected = 100, groupOptionSelected = 100;
    float amount;
    boolean addressLayout = false;
    CheckBox spinnerArrow;
    ImageView selectedCourseCheckbox;
    CardView txtChooseCourse;
    RecyclerView listExpandable;
    CourseListExpandableAdapter courseListExpandableAdapter;
    RelativeLayout selectedLayout;
    LinearLayout courseListParent;
    TextView txtSelectedCourseName,txtSelectedCatName,txtScholarship;
    long orderID;
    ArrayList<HomePageDatamodel> courseData = new ArrayList<>();
    ArrayList<HomePageDatamodel> categoryData = new ArrayList<>();
    ArrayList<HomePageDatamodel> childListData = new ArrayList<>();
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
        setContentView(R.layout.register_course);

        initView();
    }

    private void initView(){
        bottom_navigation = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        optionsList = findViewById(R.id.optionsList);
        btnPayNow = findViewById(R.id.btnPayNow);
        btnSubmit = findViewById(R.id.btnSubmit);
        scrollPayment = findViewById(R.id.scrollPayment);
        scrollBillingAddress = findViewById(R.id.scrollBillingAddress);
        spinnerArrow = findViewById(R.id.spinnerArrow);
        txtChooseCourse = findViewById(R.id.txtChooseCourse);
        listExpandable = findViewById(R.id.listExpandable);
        selectedLayout = findViewById(R.id.selectedLayout);
        courseListParent = findViewById(R.id.courseListParent);
        selectedCourseCheckbox = findViewById(R.id.selectedCourseCheckbox);
        txtSelectedCourseName = findViewById(R.id.txtSelectedCourseName);
        txtSelectedCatName = findViewById(R.id.txtSelectedCatName);
        imgDiscover = findViewById(R.id.imgDiscover);
        txtScholarship = findViewById(R.id.txtScholarship);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtEmail = findViewById(R.id.txtEmail);
        txtMobile = findViewById(R.id.txtMobile);
        txtAddress = findViewById(R.id.txtAddress);
        txtDistrict = findViewById(R.id.txtDistrict);
        txtCity = findViewById(R.id.txtCity);
        txtState = findViewById(R.id.txtState);
        txtPincode = findViewById(R.id.txtPincode);
        imgSelectedImage = findViewById(R.id.imgSelectedImage);
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
        userDataConstants = new UserDataConstants();
        logEventsActivity = new LogEventsActivity();
        UserDataConstants.courseId = ""+0;

        paymentOptionsListAdapter = new PaymentOptionsListAdapter(this);
        optionsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        optionsList.setAdapter(paymentOptionsListAdapter);

        spinnerArrow.setButtonDrawable(R.drawable.spinner_checkbox);
        listExpandable.setVisibility(View.GONE);

        selectedLayout.setVisibility(View.GONE);
        txtMobile.setEnabled(false);
        getCategoryData(this);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserDataConstants.courseId.equals("0")){
                    Toast.makeText(RegisterCourseActivity.this, "Please select any course", Toast.LENGTH_SHORT).show();
                } else if(optionSelected == 100) {
                    Toast.makeText(RegisterCourseActivity.this, "Select any payment method", Toast.LENGTH_SHORT).show();
                } else {
                    addressLayout = true;
                    scrollPayment.setVisibility(View.GONE);
                    scrollBillingAddress.setVisibility(View.VISIBLE);
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ValidateInputs()){
                    Toast.makeText(RegisterCourseActivity.this, "Please check errors", Toast.LENGTH_SHORT).show();
                } else if (isMailValid() == false){
                    Toast.makeText(RegisterCourseActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                } else if (isPincodeValid() == false){
                    Toast.makeText(RegisterCourseActivity.this, "Invalid pincode", Toast.LENGTH_SHORT).show();
                }
                else {
                    addressLayout = false;
                    scrollPayment.setVisibility(View.VISIBLE);
                    scrollBillingAddress.setVisibility(View.GONE);
                    orderID = System.currentTimeMillis();
                    PayNow();
                }

            }
        });
        txtChooseCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinnerArrow.isChecked()){
                    spinnerArrow.setChecked(false);
                    listExpandable.setVisibility(View.GONE);
                } else {
                    spinnerArrow.setChecked(true);
                    listExpandable.setVisibility(View.VISIBLE);
                }
            }
        });
        selectedCourseCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedLayout.setVisibility(View.GONE);
                courseListParent.setVisibility(View.VISIBLE);
            }
        });
        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counsellingIntent = new Intent(RegisterCourseActivity.this, CounsellingActivity.class);
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
                Intent intentHome = new Intent(RegisterCourseActivity.this, HomeActivity.class);
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
    protected void onStop() {
        drawer.closeDrawers();
        super.onStop();
    }
    public void ChatUs(View view){
        categoryLog = "";
        CourseLog = "";
        PagenameLog = "chat with whatsapp";
        ActivityLog = "Register for Course Page";
        getLogEvent(RegisterCourseActivity.this);
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
        startActivity(myIntent);
    }
    private boolean ValidateInputs() {
        boolean result = IsValid(txtFirstName, "Enter First Name") &&
                IsValid(txtLastName, "Enter Last Name") &&
                IsValid(txtEmail, "Enter Email") &&
                IsValid(txtMobile, "Enter Mobile Number") &&
                IsValid(txtAddress, "Enter Address") &&
                IsValid(txtDistrict, "Enter District") &&
                IsValid(txtCity, "Enter City") &&
                IsValid(txtState, "Enter State") &&
                IsValid(txtPincode, "Enter Pincode");
        return result;
    }

    private boolean IsValid(EditText txtText, String validationMessage) {
        if (txtText.getText().toString().trim().equals("")) {
            txtText.setError(validationMessage);
            return false;
        }
        return true;
    }
    public boolean isMailValid() {
        String email = txtEmail.getText().toString().trim();
        boolean returnValue;
        String emailPattern = "[a-zA-Z0-9.]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        return returnValue;
    }
    public boolean isPincodeValid(){
        boolean returnValue;
        String pincode = txtPincode.getText().toString().trim();
        if (pincode.length()<6){
            returnValue = false;
        } else {
            returnValue = true;
        }
        return returnValue;
    }

    public void PayNow(){
        String vAmount = "1";
        String vAccessCode = "AVVS92HF95BT57SVTB";
        String vMerchantId = "258349";
        String vCurrency = "INR";

        if(!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")){
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, vAccessCode);
            intent.putExtra(AvenuesParams.MERCHANT_ID, vMerchantId);
            intent.putExtra(AvenuesParams.ORDER_ID, String.valueOf(orderID));
            intent.putExtra(AvenuesParams.CURRENCY, vCurrency);
            //intent.putExtra(AvenuesParams.AMOUNT, vAmount);
            intent.putExtra(AvenuesParams.AMOUNT, "1");
            intent.putExtra(AvenuesParams.BILLING_NAME, txtFirstName.getText().toString().trim()+
                    " "+txtLastName.getText().toString().trim());
            intent.putExtra(AvenuesParams.BILLING_ADDRESS, txtAddress.getText().toString().trim());
            intent.putExtra(AvenuesParams.BILLING_CITY, txtCity.getText().toString().trim());
            intent.putExtra(AvenuesParams.BILLING_TEL, UserDataConstants.userMobile);
            intent.putExtra(AvenuesParams.BILLING_EMAIL, txtEmail.getText().toString().trim());
            intent.putExtra(AvenuesParams.BILLING_STATE, txtState.getText().toString().trim());
            intent.putExtra(AvenuesParams.BILLING_ZIP, txtPincode.getText().toString().trim());

            //intent.putExtra(AvenuesParams.REDIRECT_URL, "http://www.hamstechonline.com/merchant/ccavResponseHandler.jsp");
            intent.putExtra(AvenuesParams.REDIRECT_URL, Constants.redirectUrl);
            intent.putExtra(AvenuesParams.CANCEL_URL, Constants.redirectUrl);
            intent.putExtra(AvenuesParams.RSA_KEY_URL, Constants.getRsaKey+String.valueOf(orderID));
            //intent.putExtra("mRequestBody",mRequestBody);
            startActivity(intent);
        }else{
            Toast.makeText(this, "All parameters are mandatory.", Toast.LENGTH_SHORT).show();
        }
    }

    public class PaymentOptionsListAdapter extends RecyclerView.Adapter<PaymentOptionsListAdapter.ViewHolder> {

        Context context;

        public PaymentOptionsListAdapter(Context context){
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v = inflater.inflate(R.layout.payment_options, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            try {
                holder.txtTitle.setText(names[position]);
                holder.imgBanner.setButtonDrawable(optionImgs[position]);
                holder.linearParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionSelected = position;
                        notifyDataSetChanged();
                    }
                });
                if (optionSelected == position){
                    holder.imgBanner.setChecked(true);
                    holder.linearParent.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    holder.txtTitle.setTextColor(getResources().getColor(R.color.white));
                } else {
                    holder.imgBanner.setChecked(false);
                    holder.linearParent.setBackgroundColor(getResources().getColor(R.color.white));
                    holder.txtTitle.setTextColor(getResources().getColor(R.color.gray));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return names.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            RelativeLayout linearParent;
            TextView txtTitle;
            CheckBox imgBanner;
            CardView parentCardview;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                linearParent = itemView.findViewById(R.id.linearParent);
                txtTitle = itemView.findViewById(R.id.txtTitle);
                imgBanner = itemView.findViewById(R.id.imgBanner);
                parentCardview = itemView.findViewById(R.id.parentCardview);
            }
        }
    }

    public class CourseListExpandableAdapter extends RecyclerView.Adapter<CourseListExpandableAdapter.ViewHolder> {

        Context context;
        ArrayList<HomePageDatamodel> courseData;
        ArrayList<HomePageDatamodel> categoryData;
        ArrayList<HomePageDatamodel> childListData;
        ChildListAdapter childListAdapter;

        public CourseListExpandableAdapter(Context context,
                                           ArrayList<HomePageDatamodel> categoryData,
                                           ArrayList<HomePageDatamodel> courseData) {
            this.context = context;
            this.categoryData = categoryData;
            this.courseData = courseData;
            childListData = new ArrayList<>();
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v = inflater.inflate(R.layout.rigistercourse_groupadapter, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            try {
                holder.txtGroupName.setText(categoryData.get(position).getCatName());
                holder.groupArrow.setButtonDrawable(R.drawable.spinner_checkbox);
                holder.childItemList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                holder.groupLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        childListData.clear();
                        for (int cat = 0; cat < courseData.size(); cat ++) {
                            if (categoryData.get(position).getCatId().equals(courseData.get(cat).getCatId())) {
                                childListData.add(courseData.get(cat));
                            }
                            if ((cat+1) == courseData.size()) {
                                if (holder.groupArrow.isChecked()) {
                                    holder.groupArrow.setChecked(false);
                                    holder.childItemList.setVisibility(View.GONE);
                                } else {
                                    groupOptionSelected = position;
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
                if (groupOptionSelected == position){
                    holder.groupArrow.setChecked(true);
                    holder.childItemList.setVisibility(View.VISIBLE);
                    childListAdapter = new ChildListAdapter(context,childListData);
                    holder.childItemList.setAdapter(childListAdapter);
                } else {
                    holder.groupArrow.setChecked(false);
                    holder.childItemList.setVisibility(View.GONE);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return categoryData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView txtGroupName;
            CheckBox groupArrow;
            RelativeLayout groupLayout;
            RecyclerView childItemList;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtGroupName = itemView.findViewById(R.id.txtGroupName);
                groupLayout = itemView.findViewById(R.id.groupLayout);
                groupArrow = itemView.findViewById(R.id.groupArrow);
                childItemList = itemView.findViewById(R.id.childItemList);
            }
        }

        public class ChildListAdapter extends RecyclerView.Adapter<ChildListAdapter.ViewHolder> {

            Context context;
            ArrayList<HomePageDatamodel> childListData;

            public ChildListAdapter(Context context,
                                    ArrayList<HomePageDatamodel> childListData) {
                this.context = context;
                this.childListData = childListData;
            }
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(
                        parent.getContext());
                View v = inflater.inflate(R.layout.rigistercourse_childadapter, parent, false);
                ViewHolder vh = new ViewHolder(v);
                return vh;
            }

            @Override
            public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
                try {
                    holder.txtChildName.setText(childListData.get(position).getCourseName());
                    holder.txtChildName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            courseListParent.setVisibility(View.GONE);
                            selectedLayout.setVisibility(View.VISIBLE);
                            amount = Float.parseFloat(childListData.get(position).getCourseAmount());
                            txtScholarship.setText("Get Scholarships and discounts by registering now for a refundable amount of Rs. "+
                                    NumberFormat.getInstance().format(amount)+"/- and confirm your admission.");
                            txtMobile.setText(UserDataConstants.userMobile);
                            txtSelectedCourseName.setText(childListData.get(position).getCourseName());
                            txtSelectedCatName.setText(childListData.get(position).getCatName());
                            UserDataConstants.courseId = childListData.get(position).getCourseId();
                            Glide.with(RegisterCourseActivity.this)
                                    .asBitmap()
                                    .load(childListData.get(position).getCourseImage())
                                    //.placeholder(R.drawable.duser1)
                                    .into(imgSelectedImage);
                        }
                    });
                    selectedCourseCheckbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserDataConstants.courseId = ""+0;
                            selectedLayout.setVisibility(View.GONE);
                            courseListParent.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public int getItemCount() {
                return childListData.size();
            }

            public class ViewHolder extends RecyclerView.ViewHolder{
                TextView txtChildName;

                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    txtChildName = itemView.findViewById(R.id.txtChildName);
                }
            }
        }
    }

    public void getCourses(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metadata = new JSONObject();
        try {
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            params.put("appname","Hamstech");
            params.put("page","search");
            metadata.put("metadata",params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = metadata.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, Constants.getSearch, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    if (jo.getString("status").equals("ok")){
                        JSONArray jsonArray = jo.getJSONArray("course_list");
                        courseData.clear();
                        for (int i = 0; i<jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            HomePageDatamodel homePageDatamodel = new HomePageDatamodel();
                            homePageDatamodel.setCatId(object.getString("categoryId"));
                            homePageDatamodel.setCourseId(object.getString("courseId"));
                            homePageDatamodel.setCatName(object.getString("categoryname"));
                            homePageDatamodel.setCourseName(object.getString("course_name"));
                            homePageDatamodel.setCourseAmount(object.getString("amount"));
                            homePageDatamodel.setCourseImage(object.getString("image_url"));
                            courseData.add(homePageDatamodel);
                        }

                        courseListExpandableAdapter = new CourseListExpandableAdapter(RegisterCourseActivity.this,categoryData,courseData);
                        listExpandable.setLayoutManager(new LinearLayoutManager(RegisterCourseActivity.this, LinearLayoutManager.VERTICAL, false));
                        listExpandable.setAdapter(courseListExpandableAdapter);
                    } else {
                        Toast.makeText(RegisterCourseActivity.this, "Try again", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RegisterCourseActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };queue.add(sr);
    }

    public void getCategoryData(Context context){

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
                    categoryData.clear();
                    JSONArray courseArray = jo.getJSONArray("course_details");
                    for (int j = 0; j<courseArray.length(); j++){
                        JSONObject courseObject = courseArray.getJSONObject(j);
                        HomePageDatamodel courseModel = new HomePageDatamodel();
                        courseModel.setCatId(courseObject.getString("categoryId"));
                        courseModel.setCatName(courseObject.getString("categoryname"));
                        courseModel.setCatImage(courseObject.getString("cat_image_url"));
                        categoryData.add(courseModel);
                    }
                    getCourses(RegisterCourseActivity.this);
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
                    Toast.makeText(RegisterCourseActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        if (addressLayout == true){
            addressLayout = false;
            scrollPayment.setVisibility(View.VISIBLE);
            scrollBillingAddress.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
