package com.polly.utils.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.polly.R;
import com.polly.utils.Organizer;
import com.polly.utils.command.poll.FindPollCommand;
import com.polly.utils.communication.DataStreamManager;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.item.PollItem;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.PollOptionListWrapper;
import com.polly.utils.wrapper.PollOptionsWrapper;
import com.polly.visuals.MainActivity;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListAdapterPoll extends RecyclerView.Adapter<ListAdapterPoll.ListViewHolder>{
    private List<PollItem> mExampleList;
    private List<PollItem> exampleListFull;
    private OnItemClickListener mListener;
    private static ResponseCommunicator communicator = initialiseCommunicator();

    private static ResponseCommunicator initialiseCommunicator(){
        return new ResponseCommunicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("PollSearchFragment received message from " + message.getSender() + " with responseId " + message.getResponseId());
                System.out.println("from type: " + message.getDataType().getName());

            }
        };
    }
        public List<PollItem> performFiltering(CharSequence charSequence, boolean isActive, boolean isExpired) {
            List<PollItem> filteredList = new ArrayList<>();
            List<PollOptionsWrapper> pollItems = null;

            if(charSequence == null){
                filteredList.addAll(exampleListFull);
            }else{
                String filterPattern = charSequence.toString();

                try {
                    Message message = communicator.sendWithResponse(DataStreamManager.PARTNERS_DEFAULT_COMMUNICATION_ID, new FindPollCommand(filterPattern, "", isActive, isExpired));
                    if(message.getDataType().equals(PollOptionListWrapper.class)){
                        pollItems = ((PollOptionListWrapper) message.getData()).getList();
                        for(PollOptionsWrapper pollOption: pollItems){
                            PollItem pollItem = new PollItem(pollOption.getBasicPollInformation().getId(), pollOption.getBasicPollInformation().getName(), pollOption.getBasicPollInformation().getCreator());
                            filteredList.add(pollItem);
                        }
                    }else if(message.getDataType().equals(ErrorWrapper.class)){
                        Toast.makeText(Organizer.getMainActivity(), ((ErrorWrapper) message.getData()).getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Organizer.getMainActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mExampleList.clear();
            mExampleList.addAll((List) filteredList);
            notifyDataSetChanged();

            return mExampleList;
        }


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
            mCreator = itemView.findViewById(R.id.creatorName);

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
