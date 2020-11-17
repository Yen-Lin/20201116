package com.koddev.authenticatorapp.fragment.Users;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.ValueEventListener;
import com.koddev.authenticatorapp.AgentActivity;
import com.koddev.authenticatorapp.Login.DashboardActivity;
import com.koddev.authenticatorapp.Login.MainActivity;
import com.koddev.authenticatorapp.users.AdapterUsers;
import com.koddev.authenticatorapp.users.ModelUser;
import com.koddev.authenticatorapp.R;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;


    FirebaseAuth firebaseAuth;

    public UsersFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_users, container, false);


        firebaseAuth=FirebaseAuth.getInstance();


        recyclerView = view.findViewById(R.id.users_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userList = new ArrayList<>();
        getAllUsers();

        return view;
    }

    private void getAllUsers() {
         final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot  ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!modelUser.getUid().equals(fUser.getUid())){
                        userList.add(modelUser);
                    }
                    adapterUsers = new AdapterUsers(getActivity(),userList);
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUsers(final String query) {

        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot  ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!modelUser.getUid().equals(fUser.getUid())){
//這
                        if(modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                               modelUser.getEmail().toLowerCase().contains(query.toLowerCase())||
                                modelUser.getAge().toLowerCase().contains(query.toLowerCase())||
                                modelUser.getGender().toLowerCase().contains(query.toLowerCase())||
                                modelUser.getBirthday().toLowerCase().contains(query.toLowerCase())){

                            userList.add(modelUser);
                        }



                    }


                    adapterUsers = new AdapterUsers(getActivity(),userList);
                    
                    adapterUsers.notifyDataSetChanged();

                    recyclerView.setAdapter(adapterUsers);
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

        menu.findItem(R.id.action_add_post).setVisible(false);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                if(!TextUtils.isEmpty(s.trim())) {

                    searchUsers(s);
                }
                else {
                    getAllUsers();

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if(!TextUtils.isEmpty(s.trim())) {

                    searchUsers(s);
                }
                else {
                    getAllUsers();

                }
                return false;
            }
        });

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