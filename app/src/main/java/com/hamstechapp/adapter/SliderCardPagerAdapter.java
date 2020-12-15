package com.hamstechapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.hamstechapp.R;
import com.hamstechapp.datamodel.HomePageDatamodel;

import java.util.ArrayList;

public class SliderCardPagerAdapter extends PagerAdapter {

    private float mBaseElevation;
    ArrayList<HomePageDatamodel> testimonialsData;
    Context context;

    public SliderCardPagerAdapter(Context context,ArrayList<HomePageDatamodel> testimonialsData) {
        this.context = context;
        this.testimonialsData = testimonialsData;
    }

    @Override
    public int getCount() {
        return testimonialsData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter, container, false);
        container.addView(view);
        bind(view,position);
        CardView cardView = view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        cardView.setMaxCardElevation(10);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void bind(View view,int position) {
        ImageView image = view.findViewById(R.id.imageview);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        TextView txtName = view.findViewById(R.id.txtName);
        TextView txtStudentComment = view.findViewById(R.id.txtStudentComment);

        Glide.with(context)
                .asBitmap()
                .load(testimonialsData.get(position).getTestimonialImage())
                //.placeholder(R.drawable.duser1)
                .into(image);
        txtDescription.setText(testimonialsData.get(position).getTestimonialDescription());
        txtName.setText(testimonialsData.get(position).getTestimonialTitle());
        txtStudentComment.setText(testimonialsData.get(position).getTestimonialPosition());
    }

}
