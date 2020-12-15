package com.hamstechapp.datamodel;


public class CardItem {

    private int mTextResource;
    private int mTitleResource;

    public CardItem(int image) {
        mTitleResource = image;

    }

    public int getTitle() {
        return mTitleResource;
    }
}
