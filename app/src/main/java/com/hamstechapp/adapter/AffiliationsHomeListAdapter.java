package com.hamstechapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hamstechapp.R;
import com.hamstechapp.datamodel.AffiliationDataModel;
import com.hamstechapp.utils.UIUtils;

import java.util.ArrayList;

public class AffiliationsHomeListAdapter extends RecyclerView.Adapter<AffiliationsHomeListAdapter.ViewHolder> {

    Context context;
    ArrayList<AffiliationDataModel> arrayData;

    public AffiliationsHomeListAdapter(Context context, ArrayList<AffiliationDataModel> arrayData){
        this.context = context;
        this.arrayData = arrayData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.affiliations_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            holder.txtTitle.setText(arrayData.get(position).getAffiliationDescription());
            Glide.with(context)
                    .asBitmap()
                    .load(arrayData.get(position).getAffiliationImage())
                    //.placeholder(R.drawable.duser1)
                    .into(UIUtils.getRoundedImageTarget(context, holder.imgBanner, 20));
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailsPopup(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void detailsPopup(int position){
        final Dialog dialog = new Dialog(context);
        dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.affiliations_popup);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imgBanner = dialog.findViewById(R.id.imgBanner);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);

        Glide.with(context)
                .asBitmap()
                .load(arrayData.get(position).getAffiliationImage())
                //.placeholder(R.drawable.duser1)
                .into(UIUtils.getRoundedImageTarget(context, imgBanner, 20));
        txtTitle.setText(arrayData.get(position).getAffiliationDescription());

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return arrayData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgBanner;
        TextView txtTitle;
        RelativeLayout mainLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
