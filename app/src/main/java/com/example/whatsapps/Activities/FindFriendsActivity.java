package com.example.whatsapps.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapps.FindFriendsAdapter;
import com.example.whatsapps.R;
import com.example.whatsapps.TabsAndPager.TabsAccesorsAdapter;
import com.example.whatsapps.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindFriendsActivity extends AppCompatActivity
{

    @BindView(R.id.findFriendsToolBar)
    Toolbar findFriendsToolBar;
    @BindView(R.id.RVFindFriend)
    RecyclerView RVFindFriend;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference RootRef;
    ArrayList<UserInfo> Users;
    FindFriendsAdapter findFriendsAdapter;
    UserInfo userInfo;
    String userProfileImage;
    String id,name,status;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        initializeFields();
    }

    private void initializeFields()
    {
        ButterKnife.bind(this);
        setSupportActionBar(findFriendsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setTitle(R.string.findFriends);
        firebaseDatabase=FirebaseDatabase.getInstance();
        Users=new ArrayList<>();
        RootRef=firebaseDatabase.getReference().child("Users");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        RootRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Users.clear();
              if (snapshot.exists())
              {
                  for (DataSnapshot dataSnapshot:snapshot.getChildren())
                  {
                      userProfileImage=null;
                      if (dataSnapshot.hasChild("uID"))
                          id=dataSnapshot.child("uID").getValue().toString();
                      if (dataSnapshot.hasChild("Name"))
                          name=dataSnapshot.child("Name").getValue().toString();
                      if (dataSnapshot.hasChild("Status"))
                          status=dataSnapshot.child("Status").getValue().toString();
                      if (dataSnapshot.hasChild("image"))
                          userProfileImage = dataSnapshot.child("image").getValue().toString();

                      userInfo=new UserInfo(name,status,userProfileImage,id);
                      Users.add(0,userInfo);
                  }
                  findFriendsAdapter =new FindFriendsAdapter(FindFriendsActivity.this,Users);
                  RecyclerView.LayoutManager manager=new LinearLayoutManager(FindFriendsActivity.this);
                  RVFindFriend.setLayoutManager(manager);
                  RVFindFriend.setAdapter(findFriendsAdapter);
                  findFriendsAdapter.notifyDataSetChanged();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(FindFriendsActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.find_friend_option_menu, menu);
        MenuItem item=menu.findItem(R.id.SearchFriend);
        SearchView searchView= (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
              //  findFriendsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                findFriendsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }


}