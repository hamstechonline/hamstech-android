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

public class CareerOptionsListAdapter extends RecyclerView.Adapter<CareerOptionsListAdapter.ViewHolder> {

    Context context;
    ArrayList<CourseDataModel> careerData;
    int dataSize;

    public CareerOptionsListAdapter(Context context,ArrayList<CourseDataModel> careerData, int dataSize){
        this.context = context;
        this.careerData = careerData;
        this.dataSize = dataSize;
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
            holder.txtTitle.setText(careerData.get(position).getCareerOption());
            /*holder.linearParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentCourseDetails = new Intent(context, CourseDetailsActivity.class);
                    context.startActivity(intentCourseDetails);
                }
            });*/
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int itemCount;
        /*if (careerData.size() >= 4){
            itemCount = 4;
        } else {
            itemCount = careerData.size();
        }*/
        return dataSize;
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
