package com.polly.visuals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.polly.R;
import com.polly.utils.user.UserManager;
import com.polly.utils.wrapper.UserWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VotingCandidates extends Fragment {
    private RecyclerView mRecyclerView;
    private ListAdapterUser mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean pressedSelected = false;
    private ArrayList<SearchListItemUser> exampleList;
    private List<UserWrapper> list;
    public static String username;


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

        try {
            username = UserManager.getMyUsername();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            list = UserManager.findUsers("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(saving.getUserArrayVoting() == null && list != null) {
            for(int i = 0; i< list.size(); i++) {
                if(!list.get(i).getName().equals(username))
                    exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, list.get(i).getName(), false));
            }
        }else{
            exampleList = saving.getUserArrayVoting();
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
                    exampleList.get(position).setCheckbox(!exampleList.get(position).isCheckbox());
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

        root.findViewById(R.id.add_new_member_btn).setOnClickListener(new View.OnClickListener() {
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
                    Button showSelected = (Button) root.findViewById(R.id.add_new_member_btn);
                    showSelected.setText("show all");
                    pressedSelected = true;
                }else{
                    Button showSelected = (Button) root.findViewById(R.id.add_new_member_btn);
                    showSelected.setText("show selected");
                    mRecyclerView.setAdapter(mAdapter);
                    pressedSelected = false;
                }

            }
        });
        root.findViewById(R.id.delete_user_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> canVoteList = new ArrayList<>();
                if (exampleList != null){
                    for(int i = 0; i < exampleList.size(); i++){
                        if(exampleList.get(i).isCheckbox()){
                            canVoteList.add(exampleList.get(i).getmText1());
                        }
                    }
                    saving.setCanVoteList(canVoteList);
                    saving.setUserArrayVoting(exampleList);
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
