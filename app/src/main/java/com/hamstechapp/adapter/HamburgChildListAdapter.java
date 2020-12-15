package com.hamstechapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hamstechapp.R;
import com.hamstechapp.activities.AboutUsActivity;
import com.hamstechapp.activities.AffiliationsActivity;
import com.hamstechapp.activities.MentorsActivity;

import java.util.List;

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
                        Intent intentAbout = new Intent(context, AboutUsActivity.class);
                        context.startActivity(intentAbout);
                    } else if (position == 1){
                        Intent intentAbout = new Intent(context, AffiliationsActivity.class);
                        context.startActivity(intentAbout);
                    } else if (position == 2){
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
