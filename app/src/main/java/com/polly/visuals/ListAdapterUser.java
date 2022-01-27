package com.polly.visuals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.polly.R;
import com.polly.utils.user.UserManager;
import com.polly.utils.wrapper.UserWrapper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListAdapterUser extends RecyclerView.Adapter<ListAdapterUser.ListViewHolder> implements Filterable {
    private List<SearchListItemUser> mExampleList;
    private List<SearchListItemUser> exampleListFull;
    private OnItemClickListener mListener;

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<SearchListItemUser> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(exampleListFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                try {
                    for(UserWrapper item : UserManager.findUsers(filterPattern)){
                        SearchListItemUser userItem = new SearchListItemUser(R.drawable.ic_usergroup, item.getName(), false);
                            filteredList.add(userItem);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mExampleList.clear();
            mExampleList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        ListViewHolder evh = new ListViewHolder(v, mListener);
        return evh;
    }

    public ListAdapterUser(ArrayList<SearchListItemUser> exampleList){
        this.mExampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        SearchListItemUser currentItem = mExampleList.get(position);

        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.mTextView1.setText(currentItem.getmText1());
        holder.mCheckBox.setChecked(currentItem.isCheckbox());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextView1;
        public CheckBox mCheckBox;
        public ListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.userImage);
            mTextView1 = itemView.findViewById(R.id.userInstance);
            mCheckBox = itemView.findViewById(R.id.userCheckbox);

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
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onChecked(getAdapterPosition());
                        }
                    }
                }
            });
        }
    }

}
