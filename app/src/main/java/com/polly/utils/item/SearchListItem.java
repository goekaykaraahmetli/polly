package com.polly.utils.item;

public class SearchListItem {
    private int mImageResource;
    private String mText1;

    public SearchListItem(int image, String text){
        this.mImageResource = image;
        this.mText1 = text;
    }
    public int getmImageResource() {
        return mImageResource;
    }

    public String getmText1() {
        return mText1;
    }

}
