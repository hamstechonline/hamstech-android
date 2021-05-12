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
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.utils.UserDataConstants;

import java.util.ArrayList;

public class EventsDetailsListAdapter extends RecyclerView.Adapter<EventsDetailsListAdapter.ViewHolder> {

    Context context;
    ArrayList<AffiliationDataModel> arrayEventsData;

    public EventsDetailsListAdapter(Context context, ArrayList<AffiliationDataModel> arrayEventsData){
        this.context = context;
        this.arrayEventsData = arrayEventsData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.events_details_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            holder.eventDiscription.setText(arrayEventsData.get(position).getAffiliationDescription());
            Glide.with(context)
                    .asBitmap()
                    .load(arrayEventsData.get(position).getAffiliationImage())
                    //.placeholder(R.drawable.duser1)
                    .into(holder.imgBanner);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayEventsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgBanner;
        TextView eventDiscription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            eventDiscription = itemView.findViewById(R.id.eventDiscription);
        }
    }
}
