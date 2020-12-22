package com.hamstechapp.adapter;

import android.content.Context;
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
import com.hamstechapp.datamodel.AffiliationDataModel;

import java.util.ArrayList;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    Context context;
    ArrayList<AffiliationDataModel> arrayEventsData;
    int eventSize;

    public EventsListAdapter(Context context, ArrayList<AffiliationDataModel> arrayEventsData,int eventSize){
        this.context = context;
        this.arrayEventsData = arrayEventsData;
        this.eventSize = eventSize;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.txtLabel.setText(arrayEventsData.get(position).getAffiliationTitle());
            Glide.with(context)
                    .asBitmap()
                    .load(arrayEventsData.get(position).getAffiliationImage())
                    //.placeholder(R.drawable.duser1)
                    .into(holder.imgBanner);
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return eventSize;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearParent;
        ImageView imgBanner;
        TextView txtLabel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            linearParent = itemView.findViewById(R.id.linearParent);
        }
    }
}
