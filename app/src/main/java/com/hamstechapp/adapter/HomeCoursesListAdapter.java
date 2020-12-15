package com.hamstechapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hamstechapp.R;
import com.hamstechapp.activities.CoursesActivity;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.HomePageDatamodel;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeCoursesListAdapter extends RecyclerView.Adapter<HomeCoursesListAdapter.ViewHolder> {

    Context context;
    LogEventsActivity logEventsActivity;
    String CourseLog,ActivityLog,PagenameLog;
    ArrayList<HomePageDatamodel> courseData;

    public HomeCoursesListAdapter(Context context, ArrayList<HomePageDatamodel> courseData){
        logEventsActivity = new LogEventsActivity();
        this.context = context;
        this.courseData = courseData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.home_courses_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.txtTitle.setText(courseData.get(position).getCourseName());
            Glide.with(context)
                    .asBitmap()
                    .load(courseData.get(position).getCourseImage())
                    //.placeholder(R.drawable.duser1)
                    .into(holder.imgBanner);
            holder.txtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityLog = "Clicked on courses";
                    PagenameLog = "Home Page";
                    CourseLog = holder.txtTitle.getText().toString();
                    UserDataConstants.categoryId = courseData.get(position).getCatId();
                    UserDataConstants.categoryName = courseData.get(position).getCourseName();
                    getLogEvent(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return courseData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle;
        ImageView imgBanner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            imgBanner = itemView.findViewById(R.id.imgBanner);
        }
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
            logEventsActivity.LogEventsActivity(context,data);
            Intent courseIntent = new Intent(context, CoursesActivity.class);
            context.startActivity(courseIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
