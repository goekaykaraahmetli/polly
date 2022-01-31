package com.polly.utils.listadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.polly.R;
import com.polly.utils.item.PollItem;
import com.polly.utils.item.SearchListItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterPoll extends RecyclerView.Adapter<ListAdapterPoll.ListViewHolder> implements Filterable {
    private List<PollItem> mExampleList;
    private List<PollItem> exampleListFull;
    private OnItemClickListener mListener;

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<PollItem> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(exampleListFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(PollItem item : exampleListFull){
                    if(item.getPollname().toLowerCase().contains(filterPattern)){
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_item, parent, false);
        ListViewHolder evh = new ListViewHolder(v, mListener);
        return evh;
    }

    public ListAdapterPoll(ArrayList<PollItem> exampleList){
        this.mExampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        PollItem currentItem = mExampleList.get(position);

        holder.mid.setText("POLL ID: " + currentItem.getId());
        holder.mPollName.setText(currentItem.getPollname());
        holder.mCreator.setText("Creator: " + currentItem.getCreator());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder{

        public TextView mid;
        public TextView mPollName;
        public TextView mCreator;
        public ListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mid = itemView.findViewById(R.id.PollID);
            mPollName = itemView.findViewById(R.id.PollInstance);
            mPollName = itemView.findViewById(R.id.creatorName);

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
