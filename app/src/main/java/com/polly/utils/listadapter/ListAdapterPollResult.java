package com.polly.utils.listadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.polly.R;
import com.polly.utils.item.PollResultItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterPollResult extends RecyclerView.Adapter<ListAdapterPollResult.ListViewHolder> {
    private List<PollResultItem> mExampleList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener{
        void onItemClick(int position);
        void onChecked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pollresult_item, parent, false);
        ListViewHolder evh = new ListViewHolder(v, mListener);
        return evh;
    }

    public ListAdapterPollResult(ArrayList<PollResultItem> exampleList){
        this.mExampleList = exampleList;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        PollResultItem currentItem = mExampleList.get(position);

        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.mTextView1.setText(currentItem.getmText1());
        holder.mProgressBar.setProgress(currentItem.getProgress());
        holder.mProgressText.setText(currentItem.getProgressText());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextView1;
        public ProgressBar mProgressBar;
        public TextView mProgressText;
        public ListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.userImage);
            mTextView1 = itemView.findViewById(R.id.userInstance);
            mProgressBar = itemView.findViewById(R.id.progress_bar);
            mProgressText = itemView.findViewById(R.id.progress_bar_text);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(getAdapterPosition());
                        }
                    }
                }
            });
        }
    }

}
