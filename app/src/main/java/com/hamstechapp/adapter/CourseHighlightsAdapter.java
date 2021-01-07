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

public class CourseHighlightsAdapter extends RecyclerView.Adapter<CourseHighlightsAdapter.ViewHolder> {

    Context context;
    ArrayList<CourseDataModel> courseHighlights;
    int dataSize;

    public CourseHighlightsAdapter(Context context, ArrayList<CourseDataModel> courseHighlights){
        this.context = context;
        this.courseHighlights = courseHighlights;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.coursehighlights_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.txtHighlight.setText(courseHighlights.get(position).getCourseHighlight());

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return courseHighlights.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtHighlight;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHighlight = itemView.findViewById(R.id.txtHighlight);
        }
    }
}
