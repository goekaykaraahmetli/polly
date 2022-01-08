package com.polly.visuals;

public class SearchListItem {
    private int mImageResource;
    private String mText1;

    public SearchListItem(int image, String text){
        this.mImageResource = image;
        this.mText1 = text;
    }
    public void changeText1(String text){
        mText1 = text;
    }
    public int getmImageResource() {
        return mImageResource;
    }

    public String getmText1() {
        return mText1;
    }

    public void setmImageResource(int mImageResource) {
        this.mImageResource = mImageResource;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }
}
