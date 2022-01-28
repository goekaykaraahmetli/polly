package com.polly.utils.item;

public class SearchListItemUser {
    private int mImageResource;
    private String mText1;
    private boolean checkbox;

    public SearchListItemUser(int image, String text, boolean checked){
        this.mImageResource = image;
        this.mText1 = text;
        this.checkbox = checked;
    }
    public void changeText1(String text){
        mText1 = text;
    }
    public int getmImageResource() {
        return mImageResource;
    }

    public void setCheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }

    public boolean isCheckbox() {
        return checkbox;
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
