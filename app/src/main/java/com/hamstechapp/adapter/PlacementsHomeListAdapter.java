package com.hamstechapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hamstechapp.R;
import com.hamstechapp.datamodel.HomePageDatamodel;

import java.util.ArrayList;
import java.util.logging.Logger;

public class PlacementsHomeListAdapter extends RecyclerView.Adapter<PlacementsHomeListAdapter.ViewHolder> {

    Context context;
    ArrayList<HomePageDatamodel> placementsData;
    ArrayList<HomePageDatamodel> placementsDataDown;

    public PlacementsHomeListAdapter(Context context, ArrayList<HomePageDatamodel> placementsData,
                                     ArrayList<HomePageDatamodel> placementsDataDown){
        this.context = context;
        this.placementsData = placementsData;
        this.placementsDataDown = placementsDataDown;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.home_placements_listadapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Glide.with(context)
                    .asBitmap()
                    .load(placementsData.get(position).getPlacementImage())
                    //.placeholder(R.drawable.duser1)
                    .into(holder.mentorsImage);
        } catch (Exception e) {
            Logger.getLogger(e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return placementsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mentorsImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mentorsImage = itemView.findViewById(R.id.mentorsImage);
        }
    }
}
