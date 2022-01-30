package com.polly.visuals;


import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polly.R;
import com.polly.utils.Organizer;
import com.polly.utils.item.SearchListItemUser;
import com.polly.utils.listadapter.ListAdapterUser;
import com.polly.utils.wrapper.UserWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ManageGroup extends Fragment {
    public static String userGroupName;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private RecyclerView mRecyclerView;
    private ListAdapterUser mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean pressedSelected = false;
    //private ArrayList<SearchListItemUser> exampleList;
    List<UserWrapper> list;
    public static String username;
    public static String currentRoom;

    public static String myUsername;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.manage_group_layout, container, false);
        setHasOptionsMenu(true);
        ArrayList<SearchListItemUser> exampleList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(currentRoom).child("Users");
        AddNewUserChooser.userGroupName =currentRoom;
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!(((String) dataSnapshot.getKey()).equals(myUsername)))
                    exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, (String) dataSnapshot.getKey(), false));

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
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, (String) dataSnapshot.getValue(), false));
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

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        System.out.println(exampleList.size());
        if(list != null) {
            for(int i = 0; i< list.size(); i++) {
                if(!list.get(i).getName().equals(username))
                    exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, list.get(i).getName(), false));
            }

        }



        root.findViewById(R.id.add_new_member_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.addNewUserChooser);
            }
        });
        root.findViewById(R.id.delete_user_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> canVoteList = new HashMap<>();
                if (exampleList != null){
                    for(int i = 0; i < exampleList.size(); i++){
                        if(exampleList.get(i).isCheckbox()){
                            FirebaseDatabase.getInstance().getReference(currentRoom).child("Users").child(exampleList.get(i).getmText1()).removeValue();
                        }
                    }

                }

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.chat_Room);
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
