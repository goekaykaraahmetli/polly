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
import com.polly.utils.SavingClass;
import com.polly.utils.item.SearchListItem;
import com.polly.utils.listadapter.ListAdapter;
import com.polly.utils.user.UserManager;
import com.polly.utils.wrapper.UsergroupWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PollSearch extends Fragment {
    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UsergroupWrapper> usergroups;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.usergroup_layout, container, false);
        setHasOptionsMenu(true);
        SavingClass saving = new ViewModelProvider(getActivity()).get(SavingClass.class);
        try {
            usergroups = UserManager.getMyUsergroups();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ArrayList<SearchListItem> exampleList = new ArrayList<>();
        if(usergroups != null){
            for(int i = 0; i< usergroups.size() ; i ++){
                exampleList.add(new SearchListItem(R.drawable.ic_usergroup, usergroups.get(0).getName()));
            }
        }

/**        exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 1"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 2"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 3"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 4"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 5"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 6"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 7"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 8"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 9"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 10"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 11"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 12"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 13"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 14"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 15"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 16"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 17"));
 exampleList.add(new SearchListItem(R.drawable.ic_usergroup, "Usergroup 18")); **/


        mRecyclerView = root.findViewById(R.id.usergroupRecyclerView);
        mRecyclerView.setHasFixedSize(true); //Performance
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ListAdapter(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Confirm Usergroup");
                alert.setMessage("Choose " + exampleList.get(position).getmText1() + "?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saving.setUserGroupId(usergroups.get(position).getId());
                        saving.setUsergroupName(exampleList.get(position).getmText1());
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.polloptionFragment);
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.create().show();
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
