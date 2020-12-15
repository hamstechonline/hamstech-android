package com.hamstechapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hamstechapp.R;
import com.hamstechapp.activities.CourseDetailsActivity;
import com.hamstechapp.datamodel.HomePageDatamodel;
import com.hamstechapp.utils.UserDataConstants;

import java.util.ArrayList;

public class CustomAutoCompleteAdapter extends BaseAdapter implements Filterable {

    Context context;

    private final Object mLock = new Object();
    private ArrayList<String> fullList;
    private ArrayList<String> mOriginalValues;
    private ArrayFilter mFilter;
    ImageView imgSearch;
    TextView txtSearch;
    LinearLayout mainLayout;
    ArrayList<HomePageDatamodel> dataModel;

    public CustomAutoCompleteAdapter(Context context, ArrayList<HomePageDatamodel> dataModel, ArrayList<String> objects) {
        this.dataModel = dataModel;
        this.fullList = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public String getItem(int position) {
        return fullList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View rowView =  inflater.inflate(R.layout.search_adapter_layout, parent, false);

        txtSearch = rowView.findViewById(R.id.txtSearch);
        imgSearch = rowView.findViewById(R.id.imgSearch);
        mainLayout = rowView.findViewById(R.id.mainLayout);

        //txtSearch.setText(dataModel.get(position).getCategory_Title());
        //txtSearch.setText(getItem(position).split("\\$")[0]);
        txtSearch.setText(getItem(position));

        Glide.with(context)
                .load(dataModel.get(position).getCourseImage())
                //.placeholder(R.drawable.duser1)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.mipmap.ic_launcher)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .into(imgSearch);

        //clear button click listener
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCourseId(getItem(position).split("-")[1]);
            }
        });

        return rowView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {

            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<String>(fullList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                final ArrayList<String> list;
                synchronized (mLock) {
                    list = new ArrayList<String>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                final ArrayList<String> values;

                synchronized (mLock) {
                    values = new ArrayList<String>(mOriginalValues);
                }
                results.values = values;
                results.count = values.size();

                final int count = values.size();

                final ArrayList<String> newValues = new ArrayList<String>();

                for (int i = 0; i < count; i++) {
                    String item = values.get(i);
                    if (item.toLowerCase().contains(prefixString)) {
                        newValues.add(item);
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            fullList = (ArrayList<String>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    }

    public void getCourseId(String courseName){
        for (int i = 0; i < dataModel.size(); i++) {
            if (dataModel.get(i).getCourseName().equals(courseName)) {
                UserDataConstants.categoryId = dataModel.get(i).getCatId();
                UserDataConstants.courseId = dataModel.get(i).getCourseId();
                UserDataConstants.courseName = courseName;
                Intent intent = new Intent(context, CourseDetailsActivity.class);
                context.startActivity(intent);
            }
        }
    }

}
