package com.hamstechapp.common;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hamstechapp.R;
import com.hamstechapp.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LogEventsActivity {

    Context context;
    JSONObject jsonObject;
    String langPref = "en";

    public boolean LogEventsActivity(Context context, JSONObject jsonObject){
        this.context = context;
        this.jsonObject = jsonObject;
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metaData = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            params.put("apikey",context.getResources().getString(R.string.lblApiKey));
            params.put("appname","Hamstech");
            data.put("mobile",jsonObject.getString("mobile"));
            data.put("fullname",jsonObject.getString("fullname"));
            data.put("email",jsonObject.getString("email"));
            data.put("category",jsonObject.getString("category"));
            data.put("course",jsonObject.getString("course"));
            data.put("lesson",jsonObject.getString("lesson"));
            data.put("activity",jsonObject.getString("activity"));
            data.put("pagename",jsonObject.getString("pagename"));
            data.put("lang",langPref);
            metaData.put("metadata", params);
            metaData.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String mRequestBody = metaData.toString();
        Log.e("logevent","63    "+mRequestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.getLogevent,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        return ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return mRequestBody.getBytes();
                }
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

        return true;
    }
}
