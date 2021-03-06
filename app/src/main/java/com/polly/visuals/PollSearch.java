package com.polly.visuals;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.polly.R;
import com.polly.utils.SavingClass;
import com.polly.utils.ShowPollPage;
import com.polly.utils.command.poll.FindPollCommand;
import com.polly.utils.communication.DataStreamManager;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.item.PollItem;
import com.polly.utils.listadapter.ListAdapterPoll;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.PollOptionListWrapper;
import com.polly.utils.wrapper.PollOptionsWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PollSearch extends Fragment {
    private RecyclerView mRecyclerView;
    private ListAdapterPoll mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PollOptionsWrapper> pollItems;
    private static ResponseCommunicator communicator = initialiseCommunicator();
    boolean isActive;
    boolean isExpired;
    ArrayList<PollItem> exampleList = new ArrayList<>();

    private static ResponseCommunicator initialiseCommunicator(){
        return new ResponseCommunicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("PollSearchFragment received message from " + message.getSender() + " with responseId " + message.getResponseId());
                System.out.println("from type: " + message.getDataType().getName());

            }
        };
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.poll_layout, container, false);
        setHasOptionsMenu(true);
        SwitchCompat isActiveSwitch = (SwitchCompat) root.findViewById(R.id.isActivePoll);
        SwitchCompat isExpiredSwitch = (SwitchCompat) root.findViewById(R.id.isExpiredPoll);
        isActive = isActiveSwitch.isChecked();
        isExpired = isExpiredSwitch.isChecked();
        try {
            Message message = communicator.sendWithResponse(DataStreamManager.PARTNERS_DEFAULT_COMMUNICATION_ID, new FindPollCommand("", "", isActive, isExpired));
            if(message.getDataType().equals(PollOptionListWrapper.class)){
                pollItems = ((PollOptionListWrapper) message.getData()).getList();
            }else if(message.getDataType().equals(ErrorWrapper.class)){
                Toast.makeText(getActivity(), ((ErrorWrapper) message.getData()).getMessage(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(pollItems != null){
            for(int i = 0; i< pollItems.size() ; i ++){
                exampleList.add(new PollItem(pollItems.get(i).getBasicPollInformation().getId(), pollItems.get(i).getBasicPollInformation().getName(), pollItems.get(i).getBasicPollInformation().getCreator()));
            }
        }


        mRecyclerView = root.findViewById(R.id.PollRecyclerView);
        mRecyclerView.setHasFixedSize(true); //Performance
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ListAdapterPoll(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new ListAdapterPoll.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShowPollPage.enterPoll(getContext(), exampleList.get(position).getId());
            }
        });

        ((SwitchCompat) root.findViewById(R.id.isActivePoll)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    try {
                        isActive = true;
                        Message message = communicator.sendWithResponse(DataStreamManager.PARTNERS_DEFAULT_COMMUNICATION_ID, new FindPollCommand("", "", isActive, isExpired));
                        if(message.getDataType().equals(PollOptionListWrapper.class)){
                            pollItems = ((PollOptionListWrapper) message.getData()).getList();
                            exampleList.clear();
                            for(int i = 0; i < pollItems.size(); i++) {
                                exampleList.add(new PollItem(pollItems.get(i).getBasicPollInformation().getId(), pollItems.get(i).getBasicPollInformation().getName(), pollItems.get(i).getBasicPollInformation().getCreator()));
                            }
                            mAdapter.notifyDataSetChanged();
                        }else if(message.getDataType().equals(ErrorWrapper.class)){
                            Toast.makeText(getActivity(), ((ErrorWrapper) message.getData()).getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        isActive = false;
                        Message message = communicator.sendWithResponse(DataStreamManager.PARTNERS_DEFAULT_COMMUNICATION_ID, new FindPollCommand("", "", false, isExpired));
                        if(message.getDataType().equals(PollOptionListWrapper.class)){
                            pollItems = ((PollOptionListWrapper) message.getData()).getList();
                            exampleList.clear();
                            for(int i = 0; i < pollItems.size(); i++) {
                                exampleList.add(new PollItem(pollItems.get(i).getBasicPollInformation().getId(), pollItems.get(i).getBasicPollInformation().getName(), pollItems.get(i).getBasicPollInformation().getCreator()));
                            }
                            mAdapter.notifyDataSetChanged();
                        }else if(message.getDataType().equals(ErrorWrapper.class)){
                            Toast.makeText(getActivity(), ((ErrorWrapper) message.getData()).getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ((SwitchCompat) root.findViewById(R.id.isExpiredPoll)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    try {
                        isExpired = true;
                        Message message = communicator.sendWithResponse(DataStreamManager.PARTNERS_DEFAULT_COMMUNICATION_ID, new FindPollCommand("", "", isActive, isExpired));
                        if(message.getDataType().equals(PollOptionListWrapper.class)){
                            pollItems = ((PollOptionListWrapper) message.getData()).getList();
                            exampleList.clear();
                            for(int i = 0; i < pollItems.size(); i++) {
                                exampleList.add(new PollItem(pollItems.get(i).getBasicPollInformation().getId(), pollItems.get(i).getBasicPollInformation().getName(), pollItems.get(i).getBasicPollInformation().getCreator()));
                            }
                            mAdapter.notifyDataSetChanged();
                        }else if(message.getDataType().equals(ErrorWrapper.class)){
                            Toast.makeText(getActivity(), ((ErrorWrapper) message.getData()).getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        isExpired = false;
                        Message message = communicator.sendWithResponse(DataStreamManager.PARTNERS_DEFAULT_COMMUNICATION_ID, new FindPollCommand("", "", isActive, isExpired));
                        if(message.getDataType().equals(PollOptionListWrapper.class)){
                            pollItems = ((PollOptionListWrapper) message.getData()).getList();
                            exampleList.clear();
                            for(int i = 0; i < pollItems.size(); i++) {
                                exampleList.add(new PollItem(pollItems.get(i).getBasicPollInformation().getId(), pollItems.get(i).getBasicPollInformation().getName(), pollItems.get(i).getBasicPollInformation().getCreator()));
                            }
                            mAdapter.notifyDataSetChanged();
                        }else if(message.getDataType().equals(ErrorWrapper.class)){
                            Toast.makeText(getActivity(), ((ErrorWrapper) message.getData()).getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return root;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.testmenu, menu);

        MenuItem menuItem = menu.findItem(R.id.searchMenu);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mAdapter == null)return false;
                System.out.println("isActive is: " + isActive);
                System.out.println("isExpired is: " + isExpired);
                mAdapter.performFiltering(newText, isActive, isExpired);
                return false;
            }
        });
    }
}
