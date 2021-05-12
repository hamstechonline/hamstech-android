package com.hamstechapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hamstechapp.R;
import com.hamstechapp.activities.CourseDetailsActivity;
import com.hamstechapp.datamodel.CourseDataModel;
import com.hamstechapp.datamodel.CourseDetailsData;

import java.util.ArrayList;

public class CurriculumListAdapter extends RecyclerView.Adapter<CurriculumListAdapter.ViewHolder> {

    Context context;
    ArrayList<CourseDataModel> careerData;
    ArrayList<CourseDetailsData> semData;
    ArrayList<CourseDetailsData> semChildData;
    CurriculumChildListAdapter curriculumChildListAdapter;

    public CurriculumListAdapter(Context context, ArrayList<CourseDataModel> careerData, ArrayList<CourseDetailsData> semData){
        this.context = context;
        this.careerData = careerData;
        this.semData = semData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.sem_parent_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.imgBanner.setVisibility(View.GONE);
            holder.txtTitle.setText(careerData.get(position).getSemName());
            semChildData = new ArrayList<>();
            semChildData.clear();
            for (int i = 0; i<semData.size(); i++) {
                if (semData.get(i).getSem_name().equals(careerData.get(position).getSemName())) {
                    semChildData.add(semData.get(i));
                }
            }
            Log.e("semChildData","58    "+semChildData.size());
            curriculumChildListAdapter = new CurriculumChildListAdapter(context,semChildData);
            holder.childList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            holder.childList.setAdapter(curriculumChildListAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return careerData.size();
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
