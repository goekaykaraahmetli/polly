package com.polly.visuals;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polly.R;
import com.polly.utils.Organizer;
import com.polly.utils.command.poll.GetPollOptionsCommand;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.item.PollItem;
import com.polly.utils.listadapter.ListAdapterPoll;
import com.polly.utils.poll.PollManager;
import com.polly.utils.wrapper.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Chat_Room extends Fragment {

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
    private Button btn_send_msg;
    private EditText input_msg;
    private LinearLayout linearLayout;

    public static String user_name,room_name;
    private DatabaseReference root ;
    private String temp_key;

    private String chat_msg,chat_user_name;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle("Polly");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.chat_room, container, false);

        btn_send_msg = (Button) view.findViewById(R.id.btn_send);
        input_msg = (EditText) view.findViewById(R.id.msg_input);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutChat);



        ManageGroup.currentRoom = room_name;
        ManageGroup.myUsername = user_name;
        CreatePollUsergroup.userGroupName = room_name;
        CreatePollFragment.currRoomName = room_name;
        getActivity().setTitle(" Room: "+room_name);

        Button manageButton = view.findViewById(R.id.btn_manage);
        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child(room_name).child("Users").child(user_name).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue().equals("Admin"))
                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.manageGroup);
                        else
                            Toast.makeText(getActivity(), "You are not a group admin", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        view.findViewById(R.id.add_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.createPollUsergroup);
            }
        });

        root = FirebaseDatabase.getInstance().getReference().child(room_name).child("Messages");


        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",user_name);
                map2.put("msg",input_msg.getText().toString());
                if(input_msg.getText().toString().equals(""))
                    Toast.makeText(Organizer.getMainActivity(), "Please enter something", Toast.LENGTH_SHORT).show();
                else{
                    message_root.updateChildren(map2);
                    input_msg.setText("");}
                ScrollView scrollView = view.findViewById(R.id.scrollViewChatRoom);
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 100);
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){

                    System.out.println((String) ((DataSnapshot)i.next()).getKey());
/*
                    if(((String) ((DataSnapshot)i.next()).getValue()).equals("")) {
                        long id = Long.parseLong(((DataSnapshot)i.next()).getKey());
                        TextView textView = new TextView(getContext());
                        textView.setText("Someone created a poll with ID: " + id);

                    }



                    chat_msg = (String) ((DataSnapshot)i.next()).getValue();
                    chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
                    TextView msg = new TextView(Organizer.getMainActivity());
                    msg.setText(chat_user_name +" : "+chat_msg);
                    if(chat_user_name.equals(user_name)) {
                        msg.setBackground(Organizer.getMainActivity().getDrawable(R.drawable.send_green));
                    }
                    else
                        msg.setBackground(Organizer.getMainActivity().getDrawable(R.drawable.receive));
                    linearLayout.addView(msg);*/
                }
                ScrollView scrollView = view.findViewById(R.id.scrollViewChatRoom);
                scrollView.fullScroll(View.FOCUS_DOWN);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    chat_msg = (String) ((DataSnapshot)i.next()).getValue();
                    chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
                    TextView msg = new TextView(getContext());
                    msg.setText(chat_user_name +" : "+chat_msg);
                    if(chat_user_name.equals(user_name)) {
                        msg.setBackground(getActivity().getDrawable(R.drawable.send_green));
                    }
                    else
                        msg.setBackground(getActivity().getDrawable(R.drawable.receive));
                    linearLayout.addView(msg);
                }
                ScrollView scrollView = view.findViewById(R.id.scrollViewChatRoom);
                scrollView.fullScroll(View.FOCUS_DOWN);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ScrollView scrollView = view.findViewById(R.id.scrollViewChatRoom);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);

        return view;
    }




    private void append_chat_conversation(DataSnapshot dataSnapshot, View view) {

        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            TextView msg = new TextView(view.getContext());
            msg.setText(chat_user_name +" : "+chat_msg);
            if(chat_user_name.equals(user_name)) {
                msg.setBackground(getActivity().getDrawable(R.drawable.send_green));
            }
            else
                msg.setBackground(getActivity().getDrawable(R.drawable.receive));
            linearLayout.addView(msg);
        }
        ScrollView scrollView = view.findViewById(R.id.scrollViewChatRoom);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
