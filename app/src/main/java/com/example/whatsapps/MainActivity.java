package com.example.whatsapps;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.whatsapps.Activities.ChatActivity;
import com.example.whatsapps.Activities.FindFriendsActivity;
import com.example.whatsapps.Activities.GroupChatActivity;
import com.example.whatsapps.Activities.LogInActivity;
import com.example.whatsapps.Activities.SettingActivity;
import com.example.whatsapps.Fragments.ChatsFragment;
import com.example.whatsapps.Fragments.onItemSelectedListenerListView;
import com.example.whatsapps.Fragments.searchListener;
import com.example.whatsapps.TabsAndPager.TabsAccesorsAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements onItemSelectedListenerListView

{

    @BindView(R.id.viewMainPager)
    ViewPager viewMainPager;
    TabsAccesorsAdapter tabsAccesorsAdapter;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference RootRef;
    String CurrentUserID;
    @BindView(R.id.mainActivityToolBar)
    Toolbar mainToolBar;

    private String CurrentUserId;
    Boolean CheckFriend=false;
    NotificationManagerCompat notificationManager;
    private static final String CHANNEL_ID="88";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        createNotificationChannel();
        initialViewsMethod();
    }

     private void initialViewsMethod()
     {

        tabsAccesorsAdapter = new TabsAccesorsAdapter(getSupportFragmentManager());
        viewMainPager.setAdapter(tabsAccesorsAdapter);
        tabLayout.setupWithViewPager(viewMainPager);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        RootRef = firebaseDatabase.getReference();
        setSupportActionBar(mainToolBar);
        getSupportActionBar().setTitle(R.string.whats_name);

      }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            sendUserToLogInActivity();
        }
        else
        {

          //  showNotification();
            UpdateUserStatus("online");
            VerifyUserExistence();
            checkFriendsOnline();
        }
    }
    private void checkFriendsOnline()
    {
        CurrentUserID = auth.getCurrentUser().getUid();
        RootRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        if (dataSnapshot.child("userState").child("State").equals("online")&&!dataSnapshot.getKey().equals(CurrentUserID))
                        {
                            CheckFriend=true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (CheckFriend)
        {
            showNotification();
        }
    }

    private void showNotification()
    {

        NotificationCompat.Builder  builder=new NotificationCompat.Builder(this,"my_noti");
        builder.setContentTitle(getString(R.string.noti_title));
        builder.setContentText(getString(R.string.noti_body));
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_active_24);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        notificationManager=NotificationManagerCompat.from(this);
        notificationManager.notify(3,builder.build());

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my_noti","my_noti",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            UpdateUserStatus("offline");
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            UpdateUserStatus("offline");
        }
    }

    private void UpdateUserStatus(String State)
    {
        CurrentUserId=auth.getCurrentUser().getUid();
        Calendar calender=Calendar.getInstance();
        String SaveCurrentTime,SaveCurrentDate;

        SimpleDateFormat date=new SimpleDateFormat("MMM dd,yyyy");
        SaveCurrentDate=date.format(calender.getTime());

        SimpleDateFormat time=new SimpleDateFormat("hh:mm a");
        SaveCurrentTime=time.format(calender.getTime());

        HashMap<String,Object> StateMap=new HashMap<>();
        StateMap.put("Time",SaveCurrentTime);
        StateMap.put("Date",SaveCurrentDate);
        StateMap.put("State",State);

        RootRef.child("Users").child(CurrentUserId).child("userState").updateChildren(StateMap).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                 if(!task.isSuccessful())
                 {
                     Toast.makeText(MainActivity.this, "Error Online State : "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                 }
            }
        });
    }


    private void VerifyUserExistence()
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            CurrentUserID = auth.getCurrentUser().getUid();
            RootRef.child("Users").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if (!snapshot.child("Name").exists())
                    {
                        sendUserToSettingActivity();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
    private void sendUserToLogInActivity()
    {
        Intent LogInIntent = new Intent(this, LogInActivity.class);
        startActivity(LogInIntent);
        finish();
    }

    private void sendUserToSettingActivity()
    {
        Intent SettingIntent = new Intent(this, SettingActivity.class);
        startActivity(SettingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item=menu.findItem(R.id.main_search_option);
        SearchView searchView= (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
             //   TabsAccesorsAdapter.chatsFragment.FirebaseSearch(query);
                TabsAccesorsAdapter.groupFragment.ListViewAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
               // TabsAccesorsAdapter.chatsFragment.FirebaseSearch(newText);
                TabsAccesorsAdapter.groupFragment.ListViewAdapter.getFilter().filter(newText);

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
            case R.id.main_find_friends_option:
                SendUserToFindFriendsActivity();
                break;
            case R.id.main_settings_option:
                sendUserToSettingActivity();
                break;
            case R.id.main_logout_option:
                UpdateUserStatus("offline");
                auth.signOut();
                sendUserToLogInActivity();
                break;
            case R.id.main_create_group_option:
                RequestNewGroup();
                break;
            case R.id.main_language_option:
                ChangeLanguageApp();
                break;
            default:
                break;
        }
        return true;
    }

    private void ChangeLanguageApp()
    {

        CharSequence sequence[]=new CharSequence[]
        {
          "Arabic",
          "English"
        };

        AlertDialog.Builder build=new AlertDialog.Builder(this);
        build.setTitle(R.string.change_language);
        build.setItems(sequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which==0)
                {
                    setLocals("ar");
                    Toast.makeText(MainActivity.this, R.string.arabic, Toast.LENGTH_LONG).show();
                }
                if (which==1)
                {
                    setLocals("en");
                    Toast.makeText(MainActivity.this, R.string.english, Toast.LENGTH_LONG).show();
                }
            }
        });
        build.show();
    }

    //change Language
    private void setLocals(String locals)
    {
        Locale locale=new Locale(locals);
        Resources resource =getResources();
        DisplayMetrics displayMetrics=new DisplayMetrics();
        Configuration configuration=resource.getConfiguration();
        configuration.locale=locale;
        resource.updateConfiguration(configuration,displayMetrics);
    }

    private void SendUserToFindFriendsActivity()
    {
           Intent intent=new Intent(this, FindFriendsActivity.class);
           startActivity(intent);
    }

    private void RequestNewGroup()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialogTheme);
        builder.setTitle(R.string.create_group);
        final EditText GroupName = new EditText(this);
        GroupName.setHint(R.string.enter_group_name);
        GroupName.setAllCaps(false);
        builder.setView(GroupName);

        builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String groupName = GroupName.getText().toString();
                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, R.string.please, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void CreateNewGroup(String GroupName)
    {
        RootRef.child("Groups").child(GroupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, GroupName + getString(R.string.group_msg_success), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Error Group Create : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void ItemSelected(String GroupName)
    {
        Intent GroupChatIntent = new Intent(this, GroupChatActivity.class);
        GroupChatIntent.putExtra("GroupName", GroupName);
        startActivity(GroupChatIntent);
    }
}
