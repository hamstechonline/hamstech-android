package com.hamstechapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hamstechapp.R;
import com.hamstechapp.datamodel.CourseDataModel;
import com.hamstechapp.datamodel.CourseDetailsData;

import java.util.ArrayList;

public class CurriculumChildListAdapter extends RecyclerView.Adapter<CurriculumChildListAdapter.ViewHolder> {

    Context context;
    ArrayList<CourseDetailsData> semData;

    public CurriculumChildListAdapter(Context context, ArrayList<CourseDetailsData> semData){
        this.context = context;
        this.semData = semData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.sem_child_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.txtTitle.setText(semData.get(position).getCurriculum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return semData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout linearParent;
        TextView txtTitle;
        ImageView imgBanner;
        RecyclerView childList;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearParent = itemView.findViewById(R.id.linearParent);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            childList = itemView.findViewById(R.id.childList);
        }
    }
}
