package com.hamstechapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hamstechapp.R;
import com.hamstechapp.activities.CourseDetailsActivity;
import com.hamstechapp.activities.CoursesActivity;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.CourseDataModel;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CoursesListAdapter extends RecyclerView.Adapter<CoursesListAdapter.ViewHolder> {

    Context context;
    ArrayList<CourseDataModel> courseData;
    LogEventsActivity logEventsActivity;
    String ActivityLog,PagenameLog,lessonLog,CourseLog;
    public CoursesListAdapter(Context context,ArrayList<CourseDataModel> courseData){
        this.context = context;
        this.courseData = courseData;
        logEventsActivity = new LogEventsActivity();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.courses_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            holder.txtTitle.setText(courseData.get(position).getCourseName());
            holder.linearParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CourseLog = courseData.get(position).getCourseName();
                    ActivityLog = "Course Page";
                    getLogEvent(context);
                    UserDataConstants.courseId = courseData.get(position).getCourseId();
                    UserDataConstants.courseName = courseData.get(position).getCourseName();
                    Intent detailsIntent = new Intent(context, CourseDetailsActivity.class);
                    context.startActivity(detailsIntent);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return courseData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout linearParent;
        TextView txtTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearParent = itemView.findViewById(R.id.linearParent);
            txtTitle = itemView.findViewById(R.id.txtTitle);
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
            data.put("category",UserDataConstants.categoryName);
            data.put("course",CourseLog);
            data.put("lesson","");
            data.put("activity",ActivityLog);
            data.put("pagename","");
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
