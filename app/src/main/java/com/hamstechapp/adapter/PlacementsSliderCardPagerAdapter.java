package com.hamstechapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.hamstechapp.R;
import com.hamstechapp.datamodel.HomePageDatamodel;

import java.util.ArrayList;

public class PlacementsSliderCardPagerAdapter extends PagerAdapter {

    ArrayList<HomePageDatamodel> placementsData;
    Context context;

    public PlacementsSliderCardPagerAdapter(Context context, ArrayList<HomePageDatamodel> placementsData) {
        this.context = context;
        this.placementsData = placementsData;
    }

    @Override
    public int getCount() {
        return placementsData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.banner_image_adapter, container, false);
        container.addView(view);
        bind(view,position);
        CardView cardView = view.findViewById(R.id.cardView);

        cardView.setMaxCardElevation(0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void bind(View view,int position) {
        ImageView image = view.findViewById(R.id.imageview);

        Glide.with(context)
                .load(placementsData.get(position).getPlacementImage())
                .into(image);

    }

}
