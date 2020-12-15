package com.hamstechapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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
import com.hamstechapp.adapter.CustomAutoCompleteAdapter;
import com.hamstechapp.common.HocLoadingDialog;
import com.hamstechapp.datamodel.HomePageDatamodel;
import com.hamstechapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchFragment extends Fragment {

    ArrayList<HomePageDatamodel> materialDataModel = new ArrayList<>();
    HashMap<String, ArrayList<HomePageDatamodel>> autoTextViewData = new HashMap<>();
    ArrayList<String> stringNames= new ArrayList<String>();
    //String[] stringNames;
    AutoCompleteTextView autoSearch;
    HocLoadingDialog hocLoadingDialog;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout,
                container, false);
        autoSearch = view.findViewById(R.id.autoSearch);

        hocLoadingDialog = new HocLoadingDialog(getActivity());

        getSearchList(getActivity());

        return view;
    }

    private void getSearchList(final Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        JSONObject metaData = new JSONObject();
        try {
            params.put("appname","Hamstech");
            params.put("page","search");
            params.put("apikey",getResources().getString(R.string.lblApiKey));
            metaData.put("metadata", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hocLoadingDialog.showLoadingDialog();
        final String mRequestBody = metaData.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.getSearch,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            autoTextViewData.clear();
                            materialDataModel.clear();
                            stringNames.clear();
                            JSONArray jsonArray = jsonObject.getJSONArray("course_list");
                            for (int i = 0; i<jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                HomePageDatamodel datamodel = new HomePageDatamodel();
                                datamodel.setCatId(object.getString("categoryId"));
                                datamodel.setCourseId(object.getString("courseId"));
                                datamodel.setCatName(object.getString("categoryname"));
                                datamodel.setCourseName(object.getString("course_name"));
                                datamodel.setCourseImage(object.getString("image_url"));
                                datamodel.setCatVideo(object.getString("video_url"));

                                materialDataModel.add(datamodel);
                                autoTextViewData.put(object.getString("categoryname")+" - "+object.getString("course_name"), materialDataModel);
                                stringNames.add(object.getString("categoryname")+"-"+object.getString("course_name"));
                                //stringNames.add(object.getString("lesson_title"));
                            }
                            //ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.select_dialog_item, stringNames);
                            CustomAutoCompleteAdapter adapter = new CustomAutoCompleteAdapter(getActivity(),materialDataModel, stringNames);
                            autoSearch.setThreshold(2);
                            autoSearch.setAdapter(adapter);
                            hocLoadingDialog.hideDialog();
                        } catch (JSONException e) {
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
                    Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        };stringRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

}
