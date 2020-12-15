package com.hamstechapp.ccAvenueActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.hamstechapp.R;
import com.hamstechapp.activities.HomeActivity;
import com.hamstechapp.activities.RegisterCourseActivity;
import com.hamstechapp.ccAvenueUtils.AvenuesParams;
import com.hamstechapp.utils.Constants;
import com.hamstechapp.ccAvenueUtils.LoadingDialog;
import com.hamstechapp.ccAvenueUtils.RSAUtility;
import com.hamstechapp.ccAvenueUtils.ServiceUtility;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends Activity {

	Intent mainIntent;
	String html, encVal,mRequestBody,tracking_id;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_webview);
		mainIntent = getIntent();
		Log.e("online","requset"+getIntent().getStringExtra("mRequestBody"));
		get_RSA_key(mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE),mainIntent.getStringExtra(AvenuesParams.ORDER_ID));
	}
	
	private class RenderView extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			LoadingDialog.showLoadingDialog(WebViewActivity.this, "Loading...");
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if(!ServiceUtility.chkNull(vResponse).equals("")
					&& ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR")==-1){
				StringBuffer vEncVal = new StringBuffer("");
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CVV, mainIntent.getStringExtra(AvenuesParams.CVV)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CARD_NUMBER, mainIntent.getStringExtra(AvenuesParams.CARD_NUMBER)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CUSTOMER_IDENTIFIER, mainIntent.getStringExtra(AvenuesParams.CUSTOMER_IDENTIFIER)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.EXPIRY_YEAR, mainIntent.getStringExtra(AvenuesParams.EXPIRY_YEAR)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.EXPIRY_MONTH, mainIntent.getStringExtra(AvenuesParams.EXPIRY_MONTH)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL, mainIntent.getStringExtra(AvenuesParams.REDIRECT_URL)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL, mainIntent.getStringExtra(AvenuesParams.CANCEL_URL)));

				encVal = RSAUtility.encrypt(vEncVal.substring(0,vEncVal.length()-1), vResponse);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			LoadingDialog.cancelLoading();
			
			@SuppressWarnings("unused")
			class MyJavaScriptInterface
			{
				@JavascriptInterface
			    public void processHTML(String html)
			    {
			    	String status = null;
			    	if(html.indexOf("Failure")!=-1){
			    		status = "Transaction Declined!";
			    	}else if(html.indexOf("Success")!=-1){
			    		status = "Transaction Successful!";
			    	}else if(html.indexOf("Aborted")!=-1){
			    		status = "Transaction Cancelled!";
			    	}else{
			    		status = "Status Not Known!";
			    	}
			    	Intent intent = new Intent(getApplicationContext(),StatusActivity.class);
					intent.putExtra("transStatus", status);
					startActivity(intent);
			    }
			}
			
			final WebView webview = (WebView) findViewById(R.id.webview);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.setWebViewClient(new WebViewClient(){
				@Override  
	    	    public void onPageFinished(WebView view, String url) {
	    	        super.onPageFinished(webview, url);
					LoadingDialog.cancelLoading();
	    	        if(url.indexOf("/ccavResponseHandler.jsp")!=-1){
	    	        	webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
	    	        }
	    	    }
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {

					Log.e("TheMainUrl", "223     "+ url);
					if (url.contains("https://app.hamstech.com/dev/api/list/transactions/")) {

						Intent intent = new Intent(WebViewActivity.this, RegisterCourseActivity.class);
						startActivity(intent);
					}

					return false;
				}
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					LoadingDialog.showLoadingDialog(WebViewActivity.this, "Loading...");
				}

			});
			
			StringBuffer params = new StringBuffer();
			params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE,mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID,mainIntent.getStringExtra(AvenuesParams.MERCHANT_ID)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID,mainIntent.getStringExtra(AvenuesParams.ORDER_ID)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL,mainIntent.getStringExtra(AvenuesParams.REDIRECT_URL)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL,mainIntent.getStringExtra(AvenuesParams.REDIRECT_URL)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.LANGUAGE,"EN"));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_NAME,mainIntent.getStringExtra(AvenuesParams.BILLING_NAME)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ADDRESS,mainIntent.getStringExtra(AvenuesParams.BILLING_ADDRESS)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_CITY,mainIntent.getStringExtra(AvenuesParams.BILLING_CITY)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_STATE,mainIntent.getStringExtra(AvenuesParams.BILLING_STATE)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ZIP,mainIntent.getStringExtra(AvenuesParams.BILLING_ZIP)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_COUNTRY,mainIntent.getStringExtra(AvenuesParams.BILLING_COUNTRY)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_TEL,mainIntent.getStringExtra(AvenuesParams.BILLING_TEL)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_EMAIL,mainIntent.getStringExtra(AvenuesParams.BILLING_EMAIL)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_NAME,mainIntent.getStringExtra(AvenuesParams.DELIVERY_NAME)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_ADDRESS,mainIntent.getStringExtra(AvenuesParams.DELIVERY_ADDRESS)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_CITY,mainIntent.getStringExtra(AvenuesParams.DELIVERY_CITY)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_STATE,mainIntent.getStringExtra(AvenuesParams.DELIVERY_STATE)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_ZIP,mainIntent.getStringExtra(AvenuesParams.DELIVERY_ZIP)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_COUNTRY,mainIntent.getStringExtra(AvenuesParams.DELIVERY_COUNTRY)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_TEL,mainIntent.getStringExtra(AvenuesParams.DELIVERY_TEL)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_PARAM1,"additional Info."));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_PARAM2,"additional Info."));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_PARAM3,"additional Info."));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_PARAM4,"additional Info."));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.PAYMENT_OPTION,mainIntent.getStringExtra(AvenuesParams.PAYMENT_OPTION)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.CARD_TYPE,mainIntent.getStringExtra(AvenuesParams.CARD_TYPE)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.CARD_NAME,mainIntent.getStringExtra(AvenuesParams.CARD_NAME)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.DATA_ACCEPTED_AT,mainIntent.getStringExtra(AvenuesParams.DATA_ACCEPTED_AT)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.ISSUING_BANK,mainIntent.getStringExtra(AvenuesParams.ISSUING_BANK)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL,URLEncoder.encode(encVal)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.EMI_PLAN_ID,mainIntent.getStringExtra(AvenuesParams.EMI_PLAN_ID)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.EMI_TENURE_ID,mainIntent.getStringExtra(AvenuesParams.EMI_TENURE_ID)));
			if(mainIntent.getStringExtra(AvenuesParams.SAVE_CARD)!=null)
				params.append(ServiceUtility.addToPostParams(AvenuesParams.SAVE_CARD,mainIntent.getStringExtra(AvenuesParams.SAVE_CARD)));
			
			String vPostParams = params.substring(0,params.length()-1);
			try {
				webview.postUrl(com.hamstechapp.ccAvenueUtils.Constants.TRANS_URL,  vPostParams.getBytes());
				webview.getSettings().setLoadWithOverviewMode(true);
				webview.getSettings().setJavaScriptEnabled(true);
				webview.getSettings().setJavaScriptEnabled(true);
				webview.getSettings().setLoadWithOverviewMode(true);
				webview.setHorizontalScrollBarEnabled(true);
				webview.setVerticalScrollBarEnabled(true);
				//wv.getSettings().setPluginState(WebSettings.PluginState.ON);
				webview.getSettings().setAllowFileAccess(true);
				webview.addJavascriptInterface(new MyJavascriptInterface(WebViewActivity.this), "Android");
			} catch (Exception e) {
				showToast("Exception occured while opening webview.");
			}
		}
	}
	
	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}


	public void get_RSA_key(final String ac, final String od) {
		LoadingDialog.showLoadingDialog(WebViewActivity.this, "Loading...");

		StringRequest stringRequest = new StringRequest(Request.Method.POST, mainIntent.getStringExtra(AvenuesParams.RSA_KEY_URL),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						//Toast.makeText(WebViewActivity.this,response,Toast.LENGTH_LONG).show();
						LoadingDialog.cancelLoading();
						vResponse = response;
						if (vResponse.contains("!ERROR!")) {
							show_alert(vResponse);
						} else {
							new RenderView().execute();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						LoadingDialog.cancelLoading();
						//Toast.makeText(WebViewActivity.this,error.toString(),Toast.LENGTH_LONG).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(AvenuesParams.ACCESS_CODE, ac);
				params.put(AvenuesParams.ORDER_ID, od);
				return params;
			}

		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(
				7000,
				0,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		requestQueue.add(stringRequest);
	}
	class MyJavascriptInterface {
		Context mContext;
		MyJavascriptInterface(Context c) {
			mContext = c;
		}
		@JavascriptInterface
		public void callmobile(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
			if(toast.contains("Success")){
				tracking_id = toast.substring(0,toast.length()-7);
				init_transactions(WebViewActivity.this);
			}else{
				Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
				PaymentErrorPopUp();
			}
		}
	}

	public void SuccessfulPopUp(){
		final Dialog dialog = new Dialog(this);
		dialog.getWindow();
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setGravity(Gravity.CENTER);
		dialog.setContentView(R.layout.successfull_layout);
		dialog.setCancelable(false);

		ImageView imgCancel = dialog.findViewById(R.id.imgCancel);
		ImageView progressBar = dialog.findViewById(R.id.progressBar);
		Glide.with(WebViewActivity.this)
				.asGif()
				.load(R.raw.payment_success)
				.into(progressBar);
		dialog.show();

		imgCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WebViewActivity.this, HomeActivity.class);
				dialog.dismiss();
				startActivity(intent);
			}
		});

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK &&
						event.getAction() == KeyEvent.ACTION_UP &&
						!event.isCanceled()) {
					Intent intent = new Intent(WebViewActivity.this, HomeActivity.class);
					startActivity(intent);
					return true;
				}
				return false;
			}
		});
	}
	public void PaymentErrorPopUp(){
		final Dialog dialog = new Dialog(this);
		dialog.getWindow();
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setGravity(Gravity.CENTER);
		dialog.setContentView(R.layout.successfull_layout);
		dialog.setCancelable(false);

		ImageView imgCancel = dialog.findViewById(R.id.imgCancel);
		ImageView progressBar = dialog.findViewById(R.id.progressBar);
		Glide.with(WebViewActivity.this)
				.asGif()
				.load(R.raw.payment_error)
				.into(progressBar);

		dialog.show();

		imgCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK &&
						event.getAction() == KeyEvent.ACTION_UP &&
						!event.isCanceled()) {
					finish();
					return true;
				}
				return false;
			}
		});
	}

	public void init_transactions(Context context){

		RequestQueue queue = Volley.newRequestQueue(context);
		JSONObject params = new JSONObject();

		try {
			params.put("apikey",getResources().getString(R.string.lblApiKey));
			params.put("appname","Hamstech");
			params.put("phone", UserDataConstants.userMobile);
			params.put("page","PaymentPage");
			params.put("courseid",UserDataConstants.courseId);
			//params.put("skill_id",new JSONArray(skillIds));
			params.put("course_language","en");
			params.put("amount",mainIntent.getStringExtra(AvenuesParams.AMOUNT));
			params.put("discount_percentage","0");
			params.put("gst_amount","0");
			params.put("discount_amount","0");
			params.put("course_type","");
			params.put("skill_set","");
			params.put("orderid",mainIntent.getStringExtra(AvenuesParams.ORDER_ID));
			params.put("ordertype",tracking_id);
			params.put("order_amount",mainIntent.getStringExtra(AvenuesParams.AMOUNT));
			params.put("billing_address",mainIntent.getStringExtra(AvenuesParams.BILLING_ADDRESS));
			params.put("billing_city",mainIntent.getStringExtra(AvenuesParams.BILLING_CITY));
			params.put("billing_email",mainIntent.getStringExtra(AvenuesParams.BILLING_EMAIL));
			params.put("billing_country",mainIntent.getStringExtra(AvenuesParams.BILLING_COUNTRY));
			params.put("billing_pincode",mainIntent.getStringExtra(AvenuesParams.BILLING_ZIP));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		final String mRequestBody = params.toString();
		Log.e("init_transactions","343   "+mRequestBody);

		StringRequest sr = new StringRequest(Request.Method.POST, Constants.init_transactions, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try{
					JSONObject jo = new JSONObject(response);
					JSONObject object = jo.getJSONObject("status");
					JSONObject jsonObject = object.getJSONObject("message");
					if (jsonObject.getString("status_message").equals("success")){
						SuccessfulPopUp();
					} else {
						Toast.makeText(WebViewActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(WebViewActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
					return null;
				}
			}

		};

		queue.add(sr);
	}

	public void postCodHoc(Context context){

		RequestQueue queue = Volley.newRequestQueue(context);
		JSONObject params;
		JSONObject metaData = new JSONObject();
		try {
			//mRequestBody = getIntent().getStringExtra("mRequestBody");
			params = new JSONObject(getIntent().getStringExtra("mRequestBody"));
			params.put("status","Success");
			params.put("order_id",mainIntent.getStringExtra(AvenuesParams.ORDER_ID));
			params.put("tracking_id",tracking_id);
			metaData.put("data", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mRequestBody = metaData.toString();

		Log.e("Online","336   "+mRequestBody);
		StringRequest sr = new StringRequest(Request.Method.POST, Constants.create_hoconlineorder, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try{
					JSONObject jo = new JSONObject(response);
					if (jo.getString("message").equals("success")){
						SuccessfulPopUp();
					} else {
						Toast.makeText(WebViewActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(WebViewActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
					return null;
				}
			}

		};
		queue.add(sr);
	}


	public void show_alert(String msg) {
		AlertDialog alertDialog = new AlertDialog.Builder(
				WebViewActivity.this).create();

		// Setting Dialog Title
		alertDialog.setTitle("Error!!!");

		// Setting Dialog Message
        if(msg.contains("\n"))
            msg=msg.replaceAll("\\\n","");
		alertDialog.setMessage(msg);


		// Setting OK Button
		alertDialog.setButton(Dialog.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
	String vResponse;

} 