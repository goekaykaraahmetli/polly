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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.encoders.ObjectEncoder;
import com.polly.R;
import com.polly.config.Config;
import com.polly.utils.command.user.GetUsernameCommand;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.user.UserManager;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.UserWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserGroupSelect extends Fragment {
    public static String userGroupName;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private RecyclerView mRecyclerView;
    private ListAdapterUser mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean pressedSelected = false;
    ArrayList<SearchListItemUser> exampleList;
    List<UserWrapper> list;
    String username;

    private static ResponseCommunicator communicator = initialiseCommunicator();
    private static ResponseCommunicator initialiseCommunicator(){
        return new ResponseCommunicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("PolloptionFragment received message from " + message.getSender() + " with responseId " + message.getResponseId());
                System.out.println("from type: " + message.getDataType().getName());

                for(Long l : communicator.responseIds){
                    System.out.println(l);
                }

                // no default input handling
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.user_layout, container, false);
        setHasOptionsMenu(true);
        SavingClass saving = new ViewModelProvider(getActivity()).get(SavingClass.class);
        exampleList = new ArrayList<>();

        Message usernameMessage = null;
        try {
            usernameMessage = communicator.sendWithResponse(Config.serverCommunicationId, new GetUsernameCommand());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(usernameMessage.getDataType().equals(String.class))
            username = (String) usernameMessage.getData();
        else if(usernameMessage.getDataType().equals(ErrorWrapper.class)){
            Toast.makeText(getActivity(), "Server communication failed", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        try {
            list = UserManager.findUsers("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 1", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 2", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 3", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 4", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 5", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 6", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 7", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 8", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 9", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 10", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 11", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 12", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 13", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 14", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 15", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 16", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 17", false));
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "Moritz Willimowski", false));

        if(list != null) {
            for(int i = 0; i< list.size(); i++) {
                if(!list.get(i).getName().equals(username))
                    exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, list.get(i).getName(), false));
            }

        }

        mRecyclerView = root.findViewById(R.id.userRecyclerView);
        mRecyclerView.setHasFixedSize(true); //Performance
        mLayoutManager = new LinearLayoutManager(getContext());
        if(exampleList != null){
            mAdapter = new ListAdapterUser(exampleList);

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);


            mAdapter.setOnItemClickListener(new ListAdapterUser.OnItemClickListener() {

                @Override
                public void onItemClick(int position) {
                    if(exampleList.get(position).isCheckbox()){
                        exampleList.get(position).setCheckbox(false);
                    }else {
                        exampleList.get(position).setCheckbox(true);
                    }
                    mAdapter.notifyItemChanged(position);
                }

                @Override
                public void onChecked(int position) {
                    if(exampleList.get(position).isCheckbox()){
                        exampleList.get(position).setCheckbox(false);
                    }else {
                        exampleList.get(position).setCheckbox(true);
                    }
                    mAdapter.notifyItemChanged(position);
                }
            });
        }

        root.findViewById(R.id.showSelected).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exampleList == null) return;
                if(!pressedSelected){
                    ArrayList<SearchListItemUser> selected = new ArrayList<>();
                    for(int i = 0; i<exampleList.size();i++){
                        if(exampleList.get(i).isCheckbox()){
                            selected.add(exampleList.get(i));
                        }
                    }
                    ListAdapterUser selectedList = new ListAdapterUser(selected);
                    mRecyclerView.setAdapter(selectedList);
                    selectedList.setOnItemClickListener(new ListAdapterUser.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            if(selected.get(position).isCheckbox()){
                                exampleList.get(position).setCheckbox(false);
                                selected.get(position).setCheckbox(false);
                            }else {
                                exampleList.get(position).setCheckbox(true);
                                selected.get(position).setCheckbox(true);
                            }
                            mAdapter.notifyItemChanged(position);
                            selectedList.notifyItemChanged(position);
                        }

                        @Override
                        public void onChecked(int position) {
                            if(selected.get(position).isCheckbox()){
                                exampleList.get(position).setCheckbox(false);
                                selected.get(position).setCheckbox(false);
                            }else {
                                exampleList.get(position).setCheckbox(true);
                                selected.get(position).setCheckbox(true);
                            }
                            mAdapter.notifyItemChanged(position);
                            selectedList.notifyItemChanged(position);
                        }
                    });
                    Button showSelected = (Button) root.findViewById(R.id.showSelected);
                    showSelected.setText("show all");
                    pressedSelected = true;
                }else{
                    Button showSelected = (Button) root.findViewById(R.id.showSelected);
                    showSelected.setText("show selected");
                    mRecyclerView.setAdapter(mAdapter);
                    pressedSelected = false;
                }

            }
        });
        root.findViewById(R.id.saveAndBackVoting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> canVoteList = new HashMap<>();
                if (exampleList != null){
                    for(int i = 0; i < exampleList.size(); i++){
                        if(exampleList.get(i).isCheckbox()){
                            canVoteList.put(exampleList.get(i).getmText1(), "");
                        }
                    }
                    canVoteList.put(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), "");
                    FirebaseDatabase.getInstance().getReference(userGroupName).child("Users").updateChildren(canVoteList);
                }

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.polloptionFragment);
            }
        });
        return root;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.testmenu, menu);

        MenuItem searchItem = menu.findItem(R.id.searchMenu);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mAdapter == null)return false;
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
