package com.hamstechapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hamstechapp.R;
import com.hamstechapp.datamodel.OnBoardingDataModel;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public  class CustomPageAdapter extends PagerAdapter {
   Context mContext;
   LayoutInflater mLayoutInflater;
   Integer[] res;
    ArrayList<OnBoardingDataModel> boardingList;

   public CustomPageAdapter(Context context , ArrayList<OnBoardingDataModel> boardingList) {
       mContext = context;
       mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       this.boardingList = boardingList;
       this.res = res;
   }

   @Override
   public int getCount() {
       return boardingList.size();
   }

   @Override
   public boolean isViewFromObject(View view, Object object) {
       return view == ((LinearLayout) object);
   }

   @Override
   public Object instantiateItem(ViewGroup container, int position) {
       View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

       GifImageView imageView = itemView.findViewById(R.id.imageView);
       TextView txtTitle = itemView.findViewById(R.id.txtTitle);
       TextView txtDescription = itemView.findViewById(R.id.txtDescription);

       Glide.with(mContext)
               .load(boardingList.get(position).getCat_image_url())
               .diskCacheStrategy(DiskCacheStrategy.NONE)
               .into(imageView);
       txtTitle.setVisibility(View.GONE);
       txtDescription.setVisibility(View.GONE);

       container.addView(itemView);

       return itemView;
   }

   @Override
   public void destroyItem(ViewGroup container, int position, Object object) {
       container.removeView((LinearLayout) object);
   }
}
