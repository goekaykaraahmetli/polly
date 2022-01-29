package com.polly.utils.item;

public class PollResultItem {
    private int mImageResource;
    private String mText1;
    private int progress;
    private String progressText;

    public PollResultItem(int image, String text, int progress, String progressText){
        this.mImageResource = image;
        this.mText1 = text;
        this.progress = progress;
        this.progressText = progressText;

    }
    public void changeText1(String text){
        mText1 = text;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }

    public String getProgressText() {
        return progressText;
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
