package com.hamstechapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hamstechapp.R;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.utils.UIUtils;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MentorsListAdapter extends RecyclerView.Adapter<MentorsListAdapter.ViewHolder> {

    Context context;
    ArrayList<AffiliationDataModel> arrayData;
    String lessonLog,ActivityLog,PagenameLog;
    LogEventsActivity logEventsActivity;
    public MentorsListAdapter(Context context, ArrayList<AffiliationDataModel> arrayData){
        this.context = context;
        this.arrayData = arrayData;
        logEventsActivity = new LogEventsActivity();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.mentors_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.txtTitle.setText(arrayData.get(position).getAffiliationTitle());
            holder.txtDescription.setText(arrayData.get(position).getAffiliationDescription());
            Glide.with(context)
                    .asBitmap()
                    .load(arrayData.get(position).getAffiliationImage())
                    //.placeholder(R.drawable.duser1)
                    .into(UIUtils.getRoundedImageTarget(context, holder.imgBanner, 20));

            holder.imgMore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        lessonLog = arrayData.get(position).getAffiliationTitle();
                        ActivityLog = "Clicked on mentors";
                        PagenameLog = "Mentors page";
                        getLogEvent(context);
                        holder.txtDescription.setMaxLines(50);
                    } else {
                        lessonLog = arrayData.get(position).getAffiliationTitle();
                        ActivityLog = "Clicked on mentors";
                        PagenameLog = "Mentors page";
                        getLogEvent(context);
                        holder.txtDescription.setMaxLines(2);
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgBanner;
        CheckBox imgMore;
        TextView txtTitle,txtDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            imgMore = itemView.findViewById(R.id.imgMore);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }

    public void detailsPopup(int itemPosition){
        final Dialog dialog = new Dialog(context);
        dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.notification_item_dialoge);
        dialog.setCancelable(true);

        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        TextView txtDescription = dialog.findViewById(R.id.txtDescription);
        ImageView imgCancel = dialog.findViewById(R.id.imgCancel);

        txtTitle.setText(arrayData.get(itemPosition).getAffiliationTitle());
        txtDescription.setText(arrayData.get(itemPosition).getAffiliationDescription());

        dialog.show();
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void getLogEvent(Context context){
        JSONObject data = new JSONObject();
        try {
            data.put("apikey",context.getResources().getString(R.string.lblApiKey));
            data.put("appname","Hamstech");
            data.put("mobile", UserDataConstants.userMobile);
            data.put("fullname",UserDataConstants.userName);
            data.put("email","");
            data.put("category","");
            data.put("course","");
            data.put("lesson",lessonLog);
            data.put("activity",ActivityLog);
            data.put("pagename",PagenameLog);
            boolean logevent = logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
