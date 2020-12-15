package com.hamstechapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hamstechapp.R;
import com.hamstechapp.activities.MentorsActivity;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.HomePageDatamodel;
import com.hamstechapp.utils.UIUtils;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MentorHomeListAdapter extends RecyclerView.Adapter<MentorHomeListAdapter.ViewHolder> {

    Context context;
    LogEventsActivity logEventsActivity;
    String CourseLog,ActivityLog,PagenameLog;
    ArrayList<HomePageDatamodel> mentorsData;

    public MentorHomeListAdapter(Context context, ArrayList<HomePageDatamodel> mentorsData){
        logEventsActivity = new LogEventsActivity();
        this.context = context;
        this.mentorsData = mentorsData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.home_mentors_listadapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        try {
            holder.txtMentorText.setText(mentorsData.get(position).getMentorTitle());
            Glide.with(context)
                    .asBitmap()
                    .load(mentorsData.get(position).getMentorImage())
                    //.placeholder(R.drawable.duser1)
                    .into(UIUtils.getRoundedImageTarget(context, holder.mentorsImage, 20));

            holder.layoutParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityLog = "Clicked on mentors";
                    PagenameLog = "Home Page";
                    CourseLog = holder.txtMentorText.getText().toString();
                    getLogEvent(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mentorsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layoutParent;
        ImageView mentorsImage;
        TextView txtMentorText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mentorsImage = itemView.findViewById(R.id.mentorsImage);
            txtMentorText = itemView.findViewById(R.id.txtMentorText);
            layoutParent = itemView.findViewById(R.id.layoutParent);
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
            boolean logevent = logEventsActivity.LogEventsActivity(context,data);
            Intent mentorIntent = new Intent(context, MentorsActivity.class);
            context.startActivity(mentorIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
