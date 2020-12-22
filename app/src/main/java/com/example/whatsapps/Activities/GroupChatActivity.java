package com.example.whatsapps.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsapps.MassageInfo;
import com.example.whatsapps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity
{

    @BindView(R.id.toolBarGroupChat)
    Toolbar toolBarGroupChat;
    @BindView(R.id.groupChatTextDisplay)
    TextView groupChatTextDisplay;
    @BindView(R.id.scrollGroupChat)
    ScrollView scrollGroupChat;
    @BindView(R.id.input_group_massage)
    EditText inputGroupMassage;


    String GroupName, CurrentUserID, CurrentUserName;
    DatabaseReference RootRefUser;
    DatabaseReference RootRefGroup;
    FirebaseAuth mAuth;
    @BindView(R.id.sendMassageButton)
    ImageView sendMassageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        GroupName = getIntent().getStringExtra("GroupName");

        initializeFields();
        sendMassageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SaveMassageDataToDatabase();
                inputGroupMassage.setText("");
                scrollGroupChat.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void initializeFields()
    {
        ButterKnife.bind(this);
        setSupportActionBar(toolBarGroupChat);
        getSupportActionBar().setTitle(GroupName);
        RootRefUser = FirebaseDatabase.getInstance().getReference().child("Users");
        RootRefGroup = FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupName);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        GetUerName();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        ///work .
//        RootRefGroup.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
//                    String msg=dataSnapshot.child("inputMassage").getValue().toString();
//                    groupChatTextDisplay.append(msg+"\n\n");
//                    scrollGroupChat.fullScroll(ScrollView.FOCUS_DOWN);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        RootRefGroup.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                DisplayMassage(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    private void DisplayMassage(DataSnapshot snapshot)
    {
        Iterator iterator=snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String date=(String)((DataSnapshot)iterator.next()).getValue();
            String time=(String)((DataSnapshot)iterator.next()).getValue();
            String msg=(String)((DataSnapshot)iterator.next()).getValue();
            String name=(String)((DataSnapshot)iterator.next()).getValue();
            groupChatTextDisplay.append(name + "\n" +msg+ "         " +time+ " " +"\n\n");
            scrollGroupChat.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void SaveMassageDataToDatabase()
    {

        String MassageKey = RootRefGroup.push().getKey();
        String massage = inputGroupMassage.getText().toString();
        if (TextUtils.isEmpty(massage))
        {
            Toast.makeText(this, R.string.Empty, Toast.LENGTH_SHORT).show();
        } else
            {
            Calendar CurrentDate = Calendar.getInstance();
            SimpleDateFormat CurrentDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            String currentDate = CurrentDateFormat.format(CurrentDate.getTime());

            Calendar CurrentTime = Calendar.getInstance();
            SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
            String currentTime = CurrentTimeFormat.format(CurrentTime.getTime());

            MassageInfo massageInfo = new MassageInfo(CurrentUserName, currentDate, currentTime, massage);
            RootRefGroup.child(MassageKey).setValue(massageInfo);
        }
    }

    private void GetUerName()
    {
        RootRefUser.child(CurrentUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    CurrentUserName = snapshot.child("Name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(GroupChatActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}