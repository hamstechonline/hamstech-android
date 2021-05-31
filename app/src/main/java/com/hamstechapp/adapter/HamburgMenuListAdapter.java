package com.hamstechapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hamstechapp.R;
import com.hamstechapp.activities.AboutUsActivity;
import com.hamstechapp.activities.AffiliationsActivity;
import com.hamstechapp.activities.ContactUsActivity;
import com.hamstechapp.activities.CounsellingActivity;
import com.hamstechapp.activities.CourseDetailsActivity;
import com.hamstechapp.activities.CoursesActivity;
import com.hamstechapp.activities.LifeatHamstechActivity;
import com.hamstechapp.activities.MentorsActivity;
import com.hamstechapp.activities.NotificationActivity;
import com.hamstechapp.activities.ProfileActivity;
import com.hamstechapp.activities.RegisterCourseActivity;
import com.hamstechapp.common.CounsellingPopup;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.utils.SocialMediaEventsHelper;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class HamburgMenuListAdapter extends RecyclerView.Adapter<HamburgMenuListAdapter.ViewHolder> {
    Context context;
    private List<String> expandableListTitle;
    private List<String> expandableListDetail;
    int[] icons = {R.drawable.ic_profile,R.drawable.ic_notification,R.drawable.ic_down_arrow,R.drawable.ic_life,
            R.drawable.ic_online,R.drawable.ic_chat_pink,
            R.drawable.ic_contact_pink,R.drawable.ic_register_pink};
    HamburgChildListAdapter hamburgChildListAdapter;
    boolean isSelect;
    CounsellingPopup counsellingPopup;
    LogEventsActivity logEventsActivity;
    String ActivityLog,PagenameLog,lessonLog,CourseLog;

    public HamburgMenuListAdapter(Context context, List<String> expandableListTitle,
                                  List<String> expandableListDetail){
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        counsellingPopup = new CounsellingPopup(context);
        logEventsActivity = new LogEventsActivity();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.list_group, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.listTitle.setText(expandableListTitle.get(position));
            holder.imgGroupIcon.setImageResource(icons[position]);
            holder.linearParent.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.listChild.setVisibility(View.GONE);
            holder.listTitle.setTextColor(context.getResources().getColor(R.color.hamburgTextColor));
            holder.linearParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0){
                        setLogEvents("Profile");
                        holder.linearParent.setBackgroundColor(context.getResources().getColor(R.color.white));
                        holder.listChild.setVisibility(View.GONE);
                        holder.listTitle.setTextColor(context.getResources().getColor(R.color.hamburgTextColor));
                        Intent intentAbout = new Intent(context, ProfileActivity.class);
                        context.startActivity(intentAbout);
                    } else if (position == 1){
                        setLogEvents("Notifications page");
                        holder.linearParent.setBackgroundColor(context.getResources().getColor(R.color.white));
                        holder.listChild.setVisibility(View.GONE);
                        holder.listTitle.setTextColor(context.getResources().getColor(R.color.hamburgTextColor));
                        Intent intentAbout = new Intent(context, NotificationActivity.class);
                        context.startActivity(intentAbout);
                    } else if (position == 2) {
                        if (isSelect == false) {
                            isSelect = true;
                            holder.linearParent.setBackgroundColor(context.getResources().getColor(R.color.light_pink));
                            holder.listChild.setVisibility(View.VISIBLE);
                            holder.listTitle.setTextColor(context.getResources().getColor(R.color.white));
                            holder.imgGroupIcon.setImageResource(R.drawable.ic_down_white);
                            expandableListDetail = Arrays.asList(context.getResources().getStringArray(R.array.hamburgSubList));
                            hamburgChildListAdapter = new HamburgChildListAdapter(context, expandableListDetail);
                            holder.listChild.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                            holder.listChild.setAdapter(hamburgChildListAdapter);
                        } else {
                            isSelect = false;
                            holder.linearParent.setBackgroundColor(context.getResources().getColor(R.color.white));
                            holder.listChild.setVisibility(View.GONE);
                            holder.listTitle.setTextColor(context.getResources().getColor(R.color.hamburgTextColor));
                            holder.imgGroupIcon.setImageResource(R.drawable.ic_down_arrow);
                        }
                    } else if (position == 3){
                        setLogEvents("LifeatHamstech page");
                        holder.linearParent.setBackgroundColor(context.getResources().getColor(R.color.white));
                        holder.listChild.setVisibility(View.GONE);
                        holder.listTitle.setTextColor(context.getResources().getColor(R.color.hamburgTextColor));
                        Intent intentAbout = new Intent(context, LifeatHamstechActivity.class);
                        context.startActivity(intentAbout);
                    } else if (position == 4){
                        setLogEvents("Counselling page");
                        holder.linearParent.setBackgroundColor(context.getResources().getColor(R.color.white));
                        holder.listChild.setVisibility(View.GONE);
                        holder.listTitle.setTextColor(context.getResources().getColor(R.color.hamburgTextColor));
                        Intent intentAbout = new Intent(context, CounsellingActivity.class);
                        context.startActivity(intentAbout);
                    } else if (position == 5){
                        setLogEvents("Chat with us");
                        holder.linearParent.setBackgroundColor(context.getResources().getColor(R.color.white));
                        holder.listChild.setVisibility(View.GONE);
                        Intent myIntent = new Intent(Intent.ACTION_VIEW);
                        myIntent.setData(Uri.parse(context.getResources().getString(R.string.chatURL)));
                        context.startActivity(myIntent);
                    } else if (position == 6){
                        setLogEvents("Contact us");
                        holder.linearParent.setBackgroundColor(context.getResources().getColor(R.color.white));
                        holder.listChild.setVisibility(View.GONE);
                        holder.listTitle.setTextColor(context.getResources().getColor(R.color.hamburgTextColor));
                        Intent intentAbout = new Intent(context, ContactUsActivity.class);
                        context.startActivity(intentAbout);
                    } else if (position == 7){
                        setLogEvents("Register Courses");
                        new SocialMediaEventsHelper(context).EventRegisterCourse();
                        holder.linearParent.setBackgroundColor(context.getResources().getColor(R.color.white));
                        holder.listChild.setVisibility(View.GONE);
                        holder.listTitle.setTextColor(context.getResources().getColor(R.color.hamburgTextColor));
                        Intent intentRegister = new Intent(context, RegisterCourseActivity.class);
                        context.startActivity(intentRegister);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return expandableListTitle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView listTitle;
        ImageView imgGroupIcon;
        RecyclerView listChild;
        RelativeLayout linearParent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listTitle = itemView.findViewById(R.id.listTitle);
            imgGroupIcon = itemView.findViewById(R.id.imgGroupIcon);
            listChild = itemView.findViewById(R.id.listChild);
            linearParent = itemView.findViewById(R.id.linearParent);
        }
    }
    public class HamburgChildListAdapter extends RecyclerView.Adapter<HamburgChildListAdapter.ViewHolder> {

        Context context;
        private List<String> expandableListDetail;

        public HamburgChildListAdapter(Context context, List<String> expandableListDetail){
            this.context = context;
            this.expandableListDetail = expandableListDetail;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v = inflater.inflate(R.layout.list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            try {
                holder.expandedListItem.setText(expandableListDetail.get(position));
                holder.linearParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 0){
                            setLogEvents("About us page");
                            Intent intentAbout = new Intent(context, AboutUsActivity.class);
                            context.startActivity(intentAbout);
                        } else if (position == 1){
                            setLogEvents("Affiliations page");
                            Intent intentAbout = new Intent(context, AffiliationsActivity.class);
                            context.startActivity(intentAbout);
                        } else if (position == 2){
                            setLogEvents("Mentors page");
                            Intent intentAbout = new Intent(context, MentorsActivity.class);
                            context.startActivity(intentAbout);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return expandableListDetail.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView expandedListItem;
            RelativeLayout linearParent;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                expandedListItem = itemView.findViewById(R.id.expandedListItem);
                linearParent = itemView.findViewById(R.id.linearParent);
            }
        }
    }
    public void setLogEvents(String event){
        PagenameLog = event;
        ActivityLog = "Hamburg menu";
        getLogEvent(context);
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
            data.put("lesson","");
            data.put("activity",ActivityLog);
            data.put("pagename",PagenameLog);
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
