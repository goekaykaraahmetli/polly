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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.polly.R;

import java.util.ArrayList;

public class VotingCandidates extends Fragment {
    private RecyclerView mRecyclerView;
    private ListAdapterUser mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.user_layout, container, false);
        setHasOptionsMenu(true);
        SavingClass saving = new ViewModelProvider(getActivity()).get(SavingClass.class);
        ArrayList<SearchListItemUser> exampleList = new ArrayList<>();
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
        exampleList.add(new SearchListItemUser(R.drawable.ic_usergroup, "User 18", false));


        mRecyclerView = root.findViewById(R.id.userRecyclerView);
        mRecyclerView.setHasFixedSize(true); //Performance
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ListAdapterUser(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new ListAdapterUser.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

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
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
