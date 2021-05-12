package com.hamstechapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.hamstechapp.R;
import com.hamstechapp.activities.CoursesActivity;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.HomePageDatamodel;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BannerSliderCardPagerAdapter extends PagerAdapter {

    ArrayList<HomePageDatamodel> sliderData;
    Context context;
    String CourseLog,ActivityLog,PagenameLog;
    LogEventsActivity logEventsActivity;

    public BannerSliderCardPagerAdapter(Context context, ArrayList<HomePageDatamodel> sliderData) {
        this.context = context;
        this.sliderData = sliderData;
        logEventsActivity = new LogEventsActivity();
    }

    @Override
    public int getCount() {
        return sliderData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.banner_image_adapter, container, false);
        container.addView(view);
        bind(view,position);
        CardView cardView = view.findViewById(R.id.cardView);
        cardView.setMaxCardElevation(0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void bind(View view, final int position) {
        ImageView image = view.findViewById(R.id.imageview);

        Glide.with(context)
                .asBitmap()
                .load(sliderData.get(position).getSliderImage())
                //.placeholder(R.drawable.duser1)
                .into(image);
        /*image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLog = "Clicked on courses";
                PagenameLog = "Home Page";
                CourseLog = "Slider clicked";
                UserDataConstants.categoryId = ""+(position+1);
                UserDataConstants.categoryName = sliderData.get(position).getCatName();
                getLogEvent(context);
            }
        });*/
    }

    public void getLogEvent(Context context){
        JSONObject data = new JSONObject();
        try {
            data.put("apikey",context.getResources().getString(R.string.lblApiKey));
            data.put("appname","Dashboard");
            data.put("mobile", UserDataConstants.userMobile);
            data.put("fullname",UserDataConstants.userName);
            data.put("email","");
            data.put("category","");
            data.put("course",CourseLog);
            data.put("lesson","");
            data.put("activity",ActivityLog);
            data.put("pagename",PagenameLog);
            boolean logevent = logEventsActivity.LogEventsActivity(context,data);
            Intent courseIntent = new Intent(context, CoursesActivity.class);
            context.startActivity(courseIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
