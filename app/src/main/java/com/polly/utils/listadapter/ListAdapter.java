package com.polly.utils.listadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.polly.R;
import com.polly.utils.item.SearchListItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> implements Filterable {
    private List<SearchListItem> mExampleList;
    private List<SearchListItem> exampleListFull;
    private OnItemClickListener mListener;
    private Context context;
    private int selectedPos = RecyclerView.NO_POSITION;

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<SearchListItem> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(exampleListFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(SearchListItem item : exampleListFull){
                    if(item.getmText1().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
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
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.usergroup_item, parent, false);
        ListViewHolder evh = new ListViewHolder(v, mListener);
        return evh;
    }

    public ListAdapter(ArrayList<SearchListItem> exampleList, Context context){
        this.mExampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        SearchListItem currentItem = mExampleList.get(position);

        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.mTextView1.setText(currentItem.getmText1());

        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextView1;
        public MaterialCardView mcardView;

        public ListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.usergroupInstance);
            mcardView = itemView.findViewById(R.id.listViewCard);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(getAdapterPosition());
                        }
                    }
                    notifyItemChanged(selectedPos);
                    selectedPos = getLayoutPosition();
                    notifyItemChanged(selectedPos);
                }
            });

        }
    }

}
