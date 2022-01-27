package com.polly.visuals;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polly.R;
import com.polly.config.Config;
import com.polly.utils.command.user.GetUsernameCommand;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class MainActivityChat extends Fragment {

    public static final int RC_SIGN_IN=1;
    private static final String ANONYMOUS = "";
    private Button  add_room;
    private EditText room_name;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private int c=0;
    private String name;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    private static ResponseCommunicator communicator = initialiseCommunicator();
    private static ResponseCommunicator initialiseCommunicator(){
        return new ResponseCommunicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("AccountFragment received message from " + message.getSender() + " with responseId " + message.getResponseId());
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
        View view = inflater.inflate(R.layout.activity_main23, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        room_name = (EditText) view.findViewById(R.id.room_name_edittext);
        room_name.setText("");
        room_name.setVisibility(View.GONE);
        listView = (ListView) view.findViewById(R.id.listView);
        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);

        try {
            Message usernameMessage = communicator.sendWithResponse(Config.serverCommunicationId, new GetUsernameCommand());
            if(usernameMessage.getDataType().equals(String.class))
                name = ((String)usernameMessage.getData());
            else if(usernameMessage.getDataType().equals(ErrorWrapper.class)){
                Toast.makeText(getActivity(), "Server communication failed", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }


        }
        catch (IOException e){
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }




        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (c==0) {
                    room_name.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(),"Enter a room name", Toast.LENGTH_SHORT).show();
                }
                else if (!(room_name.getText().toString().equals(""))&&c==1) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(room_name.getText().toString(), "");
                    UserGroupSelect.userGroupName = room_name.getText().toString();
                    root.updateChildren(map);
                    room_name.setText("");
                    room_name.setVisibility(View.GONE);
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.userGroupSelect);
                }
                else{
                    Toast.makeText(getActivity(),"Enter a valid room name", Toast.LENGTH_SHORT).show();
                    room_name.setVisibility(View.GONE);
                }
                c=(c+1)%2;
            }
        });


        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_rooms);

        listView.setAdapter(arrayAdapter);


        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    DataSnapshot next = (DataSnapshot) i.next();
                    if(next.child("Users").hasChild(name))
                        set.add(next.getKey());
                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(),Chat_Room.class);
                intent.putExtra("room_name",((TextView)view).getText().toString() );
                intent.putExtra("user_name",name);
                startActivity(intent);
            }
        });



    return view;
    }

    /*private void request_user_name() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name:");

        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                  name = input_field.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show();
    }*/



}
