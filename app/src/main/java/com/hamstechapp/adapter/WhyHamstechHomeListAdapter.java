package com.hamstechapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hamstechapp.R;
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.utils.UIUtils;

import java.util.ArrayList;

public class WhyHamstechHomeListAdapter extends RecyclerView.Adapter<WhyHamstechHomeListAdapter.ViewHolder> {

    Context context;
    ArrayList<AffiliationDataModel> arrayData;

    public WhyHamstechHomeListAdapter(Context context,ArrayList<AffiliationDataModel> arrayData){
        this.context = context;
        this.arrayData = arrayData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.whyhamstech_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Glide.with(context)
                    .asBitmap()
                    .load(arrayData.get(position).getAffiliationImage())
                    //.placeholder(R.drawable.duser1)
                    .into(UIUtils.getRoundedImageTarget(context, holder.imgCourseIcon, 0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgCourseIcon;
        TextView txtTitle,txtDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCourseIcon = itemView.findViewById(R.id.imgCourseIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }
}
