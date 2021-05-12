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
import java.util.Arrays;
import java.util.List;

public class BranchListAdapter extends RecyclerView.Adapter<BranchListAdapter.ViewHolder> {

    Context context;
    //ArrayList<Double> branchLats;
    double branchLats[];
    List<String> branchNames;

    public BranchListAdapter(Context context, double branchLats[]){
        this.context = context;
        this.branchLats = branchLats;
        branchNames = Arrays.asList(context.getResources().getStringArray(R.array.branchNames));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.branch_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.txtNearestBranch.setText(branchNames.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return branchLats.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtNearestBranch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNearestBranch = itemView.findViewById(R.id.txtNearestBranch);
        }
    }
}
