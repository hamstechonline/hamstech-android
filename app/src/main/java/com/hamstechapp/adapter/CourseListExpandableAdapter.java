package com.hamstechapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hamstechapp.R;
import com.hamstechapp.datamodel.HomePageDatamodel;

import java.util.ArrayList;

public class CourseListExpandableAdapter extends RecyclerView.Adapter<CourseListExpandableAdapter.ViewHolder> {

    Context context;
    ArrayList<HomePageDatamodel> courseData;
    ArrayList<HomePageDatamodel> categoryData;
    ArrayList<HomePageDatamodel> childListData;
    ChildListAdapter childListAdapter;

    public CourseListExpandableAdapter(Context context,
                                       ArrayList<HomePageDatamodel> categoryData,
                                       ArrayList<HomePageDatamodel> courseData) {
        this.context = context;
        this.categoryData = categoryData;
        this.courseData = courseData;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.rigistercourse_groupadapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.txtGroupName.setText(categoryData.get(position).getCatName());
            holder.groupArrow.setButtonDrawable(R.drawable.spinner_checkbox);
            childListData = new ArrayList<>();
            holder.txtGroupName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.groupArrow.isChecked()){
                        holder.groupArrow.setChecked(false);
                        childListData.clear();
                        for (int cat = 0; cat < courseData.size(); cat ++) {
                            if (categoryData.get(position).getCatId().equals(courseData.get(cat).getCatId())) {
                                childListData.add(courseData.get(cat));
                            }
                        }
                        childListAdapter = new ChildListAdapter(context,childListData);
                        holder.childItemList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                        holder.childItemList.setAdapter(childListAdapter);
                    } else {
                        holder.groupArrow.setChecked(true);
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return categoryData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtGroupName;
        CheckBox groupArrow;
        RecyclerView childItemList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGroupName = itemView.findViewById(R.id.txtGroupName);
            groupArrow = itemView.findViewById(R.id.groupArrow);
            childItemList = itemView.findViewById(R.id.childItemList);
        }
    }

    public class ChildListAdapter extends RecyclerView.Adapter<ChildListAdapter.ViewHolder> {

        Context context;
        ArrayList<HomePageDatamodel> childListData;

        public ChildListAdapter(Context context,
                                           ArrayList<HomePageDatamodel> childListData) {
            this.context = context;
            this.childListData = childListData;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v = inflater.inflate(R.layout.rigistercourse_childadapter, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            try {
                holder.txtChildName.setText(categoryData.get(position).getCatName());
                holder.txtChildName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, ""+childListData.get(position).getCourseName(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return childListData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView txtChildName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtChildName = itemView.findViewById(R.id.txtChildName);
            }
        }
    }
}

