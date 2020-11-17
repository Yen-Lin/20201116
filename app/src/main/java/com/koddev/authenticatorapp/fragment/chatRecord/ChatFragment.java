package com.koddev.authenticatorapp.fragment.chatRecord;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.koddev.authenticatorapp.AgentActivity;
import com.koddev.authenticatorapp.Login.MainActivity;
import com.koddev.authenticatorapp.R;
import com.koddev.authenticatorapp.Chat.util.Define;

import com.koddev.authenticatorapp.Chat.AdapterChat;
import com.koddev.authenticatorapp.users.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterChat adapterChat;
    List<ModelUser> chatList;


    FirebaseAuth firebaseAuth;

    public ChatFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_chat, container, false);


        firebaseAuth=FirebaseAuth.getInstance();


        recyclerView = view.findViewById(R.id.chat_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        chatList = new ArrayList<>();
        getAllChat();

        return view;
    }

    private void getAllChat() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.child(Define.ChatContent).exists()) {
                        Log.i("ChatFragment", "name : " + ds.child("name").getValue());
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        chatList.add(modelUser);
                    }
                    adapterChat = new AdapterChat(getActivity(),chatList);
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void  checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

        } else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }



    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }




        if (item.getItemId()==R.id.action_Service)
        {
            startActivity(new Intent(getActivity(), AgentActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }
}