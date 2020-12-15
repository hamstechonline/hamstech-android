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
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.utils.UIUtils;

import java.util.ArrayList;

public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListAdapter.ViewHolder> {

    Context context;
    ArrayList<AffiliationDataModel> arrayGalleryData;

    public GalleryListAdapter(Context context,ArrayList<AffiliationDataModel> arrayGalleryData) {
        this.context = context;
        this.arrayGalleryData = arrayGalleryData;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.gallery_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Glide.with(context)
                    .asBitmap()
                    .load(arrayGalleryData.get(position).getAffiliationImage())
                    //.placeholder(R.drawable.duser1)
                    .into(UIUtils.getRoundedImageTarget(context, holder.imgBanner, 20));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayGalleryData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgBanner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
        }
    }
}
