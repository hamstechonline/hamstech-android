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
import com.hamstechapp.activities.EventDetailsActivity;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    Context context;
    ArrayList<AffiliationDataModel> arrayEventsData;
    int eventSize;
    LogEventsActivity logEventsActivity;
    String ActivityLog,PagenameLog,lessonLog,CourseLog;

    public EventsListAdapter(Context context, ArrayList<AffiliationDataModel> arrayEventsData,int eventSize){
        this.context = context;
        this.arrayEventsData = arrayEventsData;
        this.eventSize = eventSize;
        logEventsActivity = new LogEventsActivity();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.events_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            holder.txtLabel.setText(arrayEventsData.get(position).getAffiliationTitle());
            Glide.with(context)
                    .asBitmap()
                    .load(arrayEventsData.get(position).getAffiliationImage())
                    //.placeholder(R.drawable.duser1)
                    .into(holder.imgBanner);
            holder.linearParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityLog = "Life at Hamstech Page";
                    PagenameLog = "Events";
                    lessonLog = arrayEventsData.get(position).getAffiliationTitle();
                    UserDataConstants.EventHeaderTitle = arrayEventsData.get(position).getAffiliationTitle();
                    UserDataConstants.EventId = arrayEventsData.get(position).getEvent_id();
                    Intent intent = new Intent(context, EventDetailsActivity.class);
                    context.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return eventSize;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearParent,layoutButton;
        ImageView imgBanner;
        TextView txtLabel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            linearParent = itemView.findViewById(R.id.linearParent);
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
}
