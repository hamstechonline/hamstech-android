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
import com.hamstechapp.datamodel.CourseDataModel;
import com.hamstechapp.utils.UserDataConstants;

import java.util.ArrayList;

public class CoursesListAdapter extends RecyclerView.Adapter<CoursesListAdapter.ViewHolder> {

    Context context;
    ArrayList<CourseDataModel> courseData;
    public CoursesListAdapter(Context context,ArrayList<CourseDataModel> courseData){
        this.context = context;
        this.courseData = courseData;
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
}
