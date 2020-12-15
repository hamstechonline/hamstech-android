package com.hamstechapp.common;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class PostResponse {
    Context context;
    JSONObject jsonObject;
    String url,mRequestBody,responseJson;

    public PostResponse(Context context, JSONObject jsonObject, String url){
        this.context = context;
        this.jsonObject = jsonObject;
        this.url = url;
    }
    public void getTopicsList() {
        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            mRequestBody = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //hocLoadingDialog.showLoadingDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            responseJson = response;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Internet connection error", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };

        queue.add(stringRequest);
    }

    public String finalResult(){
        getTopicsList();
        return responseJson;
    }
}
