package com.hamstechapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hamstechapp.R;
import com.hamstechapp.datamodel.CourseDataModel;

import java.util.ArrayList;

public class CurriculumListAdapter extends RecyclerView.Adapter<CurriculumListAdapter.ViewHolder> {

    Context context;
    ArrayList<CourseDataModel> careerData;

    public CurriculumListAdapter(Context context, ArrayList<CourseDataModel> careerData){
        this.context = context;
        this.careerData = careerData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.careeroption_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.txtTitle.setText(careerData.get(position).getCourseName());
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearParent = itemView.findViewById(R.id.linearParent);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
