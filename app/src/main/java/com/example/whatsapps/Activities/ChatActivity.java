package com.example.whatsapps.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapps.MessageAdapter;
import com.example.whatsapps.MessageDetails;
import com.example.whatsapps.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    String FriendName, MessageRecieverID, FriendImage, MessageSenderID;
    FirebaseAuth mAuth;
    DatabaseReference RootRef;
    TextView Name, LastSeen;
    CircleImageView FriendImg;
    MessageAdapter adapter;
    ArrayList<MessageDetails> messages;
    RecyclerView.LayoutManager layoutManager;
    String SaveCurrentTime, SaveCurrentDate;
    Calendar calender;
    String Checker = "", myUri = "";
    Uri FileUri;
    private StorageTask UploadTask;
    private static int GalleryPick = 432;
    ProgressDialog progressDialog;


    @BindView(R.id.ChatActivityToolBar)
    Toolbar ChatActivityToolBar;
    @BindView(R.id.RVChatActivity)
    RecyclerView RVChatActivity;
    @BindView(R.id.inputMassageChatActivity)
    EditText inputMassageChatActivity;
    @BindView(R.id.imgSendMassageChatActivity)
    ImageButton imgSendMassageChatActivity;
    @BindView(R.id.imgSendFileChatActivity)
    ImageButton imgSendFileChatActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);


        progressDialog = new ProgressDialog(this);
        initializeControllers();

        imgSendMassageChatActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });


        imgSendFileChatActivity.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "Images",
                        "PDF Files",
                        "Ms Word Files"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (which == 0)
                        {
                            Checker = "image";
                            Intent intentGallery = new Intent();
                            intentGallery.setAction(Intent.ACTION_GET_CONTENT);
                            intentGallery.setType("image/*");
                            startActivityForResult(Intent.createChooser(intentGallery,"Select Image"),2);
                        }
                        if (which == 1)
                        {
                            Checker = "pdf";
                            Intent intentPDF = new Intent();
                            intentPDF.setAction(Intent.ACTION_GET_CONTENT);
                            intentPDF.setType("application/pdf");
                            startActivityForResult(intentPDF, 3);
                        }
                        if (which == 2)
                        {
                               Checker = "docx";
                            Intent intentDoc = new Intent();
                            intentDoc.setAction(Intent.ACTION_GET_CONTENT);
                            intentDoc.setType("application/msword");
                            startActivityForResult(intentDoc, 4);
                        }
                    }
                });
                builder.show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK )
        {


            //loading dialog
            progressDialog.setTitle(getString(R.string.sendFile));
            progressDialog.setMessage(getString(R.string.pleaseSendfile));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            FileUri=data.getData();

             if (Checker.equals("image"))
            {

                //path
                String MessageSenderRef = "Messages/" + MessageSenderID + "/" + MessageRecieverID;
                String MessageReceiverRef = "Messages/" + MessageRecieverID + "/" + MessageSenderID;
                String pushID = RootRef.child("Messages").child(MessageSenderID).child(MessageRecieverID).push().getKey();

             //   StorageReference FilePath = reference.child(pushID +"."+"jpg");


                StorageReference reference = FirebaseStorage.getInstance().getReference().child("Image Files").child(pushID);

                reference.putFile(FileUri).addOnCompleteListener(new OnCompleteListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.storage.UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful()) {

                         //   StorageReference Path = reference.child(pushID + "." + "jpg");

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                  String  myUri = uri.toString();

                                    //message
                                    Map messageTextBody = new HashMap();

                                    messageTextBody.put("message", myUri);
                                    messageTextBody.put("type", Checker);
                                    messageTextBody.put("from", MessageSenderID);
                                    messageTextBody.put("to", MessageRecieverID);
                                    messageTextBody.put("time", SaveCurrentTime);
                                    messageTextBody.put("date", SaveCurrentDate);
                                    messageTextBody.put("messageID", pushID);

                                    //set message in path
                                    Map messageBodyDetails = new HashMap();

                                    messageBodyDetails.put(MessageSenderRef + "/" + pushID, messageTextBody);
                                    messageBodyDetails.put(MessageReceiverRef + "/" + pushID, messageTextBody);

                                    //implement using update children
                                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if (!task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(ChatActivity.this, R.string.msg_fail, Toast.LENGTH_SHORT).show();
                                            }
                                            if (task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                inputMassageChatActivity.setText("");
                                            }
                                        }
                                    });

                                }
                            });
                        }
                    }
                });
            }
            else
            {
                progressDialog.dismiss();
                Toast.makeText(this, "Nothing Selected,Error", Toast.LENGTH_SHORT).show();
            }
        }




        if (requestCode == 3 && resultCode == RESULT_OK ) {


            //loading dialog
            progressDialog.setTitle(getString(R.string.sendFile));
            progressDialog.setMessage(getString(R.string.pleaseSendfile));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            FileUri=data.getData();

            if (Checker.equals("pdf"))
            {

                //path
                String MessageSenderRef = "Messages/" + MessageSenderID + "/" + MessageRecieverID;
                String MessageReceiverRef = "Messages/" + MessageRecieverID + "/" + MessageSenderID;
                String pushID = RootRef.child("Messages").child(MessageSenderID).child(MessageRecieverID).push().getKey();


                StorageReference reference = FirebaseStorage.getInstance().getReference().child("PDF Files").child(pushID);

                reference.putFile(FileUri).addOnCompleteListener(new OnCompleteListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.storage.UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful()) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    String  myUri = uri.toString();

                                    //message
                                    Map messageTextBody = new HashMap();

                                    messageTextBody.put("message", myUri);
                                    messageTextBody.put("type", Checker);
                                    messageTextBody.put("from", MessageSenderID);
                                    messageTextBody.put("to", MessageRecieverID);
                                    messageTextBody.put("time", SaveCurrentTime);
                                    messageTextBody.put("date", SaveCurrentDate);
                                    messageTextBody.put("messageID", pushID);

                                    //set message in path
                                    Map messageBodyDetails = new HashMap();

                                    messageBodyDetails.put(MessageSenderRef + "/" + pushID, messageTextBody);
                                    messageBodyDetails.put(MessageReceiverRef + "/" + pushID, messageTextBody);

                                    //implement using update children
                                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if (!task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(ChatActivity.this, R.string.msg_fail, Toast.LENGTH_SHORT).show();
                                            }
                                            if (task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                inputMassageChatActivity.setText("");
                                            }
                                        }
                                    });

                                }
                            });
                        }
                    }
                });
            }
            else
            {
                progressDialog.dismiss();
                Toast.makeText(this, "Nothing Selected,Error", Toast.LENGTH_SHORT).show();
            }
        }





        if (requestCode == 4 && resultCode == RESULT_OK ) {


            //loading dialog
            progressDialog.setTitle(getString(R.string.sendFile));
            progressDialog.setMessage(getString(R.string.pleaseSendfile));
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            FileUri=data.getData();

            if (Checker.equals("docx"))
            {

                //path
                String MessageSenderRef = "Messages/" + MessageSenderID + "/" + MessageRecieverID;
                String MessageReceiverRef = "Messages/" + MessageRecieverID + "/" + MessageSenderID;
                String pushID = RootRef.child("Messages").child(MessageSenderID).child(MessageRecieverID).push().getKey();


                StorageReference reference = FirebaseStorage.getInstance().getReference().child("Docx Files").child(pushID);

                reference.putFile(FileUri).addOnCompleteListener(new OnCompleteListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.storage.UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful()) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    String  myUri = uri.toString();

                                    //message
                                    Map messageTextBody = new HashMap();

                                    messageTextBody.put("message", myUri);
                                    messageTextBody.put("type", Checker);
                                    messageTextBody.put("from", MessageSenderID);
                                    messageTextBody.put("to", MessageRecieverID);
                                    messageTextBody.put("time", SaveCurrentTime);
                                    messageTextBody.put("date", SaveCurrentDate);
                                    messageTextBody.put("messageID", pushID);

                                    //set message in path
                                    Map messageBodyDetails = new HashMap();

                                    messageBodyDetails.put(MessageSenderRef + "/" + pushID, messageTextBody);
                                    messageBodyDetails.put(MessageReceiverRef + "/" + pushID, messageTextBody);

                                    //implement using update children
                                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if (!task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(ChatActivity.this, R.string.msg_fail, Toast.LENGTH_SHORT).show();
                                            }
                                            if (task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                inputMassageChatActivity.setText("");
                                            }
                                        }
                                    });

                                }
                            });
                        }
                    }
                });
            }
            else
            {
                progressDialog.dismiss();
                Toast.makeText(this, "Nothing Selected,Error", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initializeControllers()
    {

        mAuth = FirebaseAuth.getInstance();
        MessageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        FriendName = getIntent().getStringExtra("FriendName");
        MessageRecieverID = getIntent().getStringExtra("FriendID");
        FriendImage = getIntent().getStringExtra("image");


        setSupportActionBar(ChatActivityToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_bar_layout, null, false);
        actionBar.setCustomView(view);


        Name = view.findViewById(R.id.UserNameProfileChat);
        LastSeen = view.findViewById(R.id.UserLastSeenProfileChat);
        FriendImg = view.findViewById(R.id.imageProfileChat);
        Name.setText(FriendName);
        Picasso.get().load(FriendImage).placeholder(R.drawable.ic_profile).into(FriendImg);

        messages = new ArrayList<>();

        calender = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MMM dd,yyyy");
        SaveCurrentDate = date.format(calender.getTime());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime = time.format(calender.getTime());
    }

    private void DisplayLastSeen()
    {

        RootRef.child("Users").child(MessageRecieverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if (snapshot.child("userState").exists())
                {
                    String time = snapshot.child("userState").child("Time").getValue().toString();
                    String date = snapshot.child("userState").child("Date").getValue().toString();
                    String state = snapshot.child("userState").child("State").getValue().toString();
                    if (state.equals("online"))
                    {
                        LastSeen.setText("Online Now");
                    }
                    else
                    {
                        LastSeen.setText("Last Seen: " + date + " " + time);
                    }
                }
                else
                {
                    LastSeen.setText("Offline");
                }
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

    @Override
    protected void onStart()
    {
        super.onStart();

        DisplayLastSeen();

        RootRef.child("Messages").child(MessageSenderID).child(MessageRecieverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    messages.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        String from="",to="",type="",message="",messgageID="",time="",date="";
                        if (dataSnapshot.hasChild("from"))
                        {
                            from=dataSnapshot.child("from").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("to"))
                        {
                            to=dataSnapshot.child("to").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("time"))
                        {
                            time=dataSnapshot.child("time").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("type"))
                        {
                            type=dataSnapshot.child("type").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("date"))
                        {
                            date=dataSnapshot.child("date").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("message"))
                        {
                            message=dataSnapshot.child("message").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("messageID"))
                        {
                            messgageID=dataSnapshot.child("messageID").getValue().toString();
                        }

                        messages.add(new MessageDetails(from,type,message, to,messgageID,time, date));


                    }
                    CreateAdapter();

                }
                else
                {
                    CreateAdapter();
                    adapter.clearList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(ChatActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void CreateAdapter()
    {
        adapter = new MessageAdapter(messages);
        layoutManager = new LinearLayoutManager(getBaseContext());
        RVChatActivity.setLayoutManager(layoutManager);
        RVChatActivity.setHasFixedSize(true);
        RVChatActivity.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        RVChatActivity.smoothScrollToPosition(adapter.getItemCount());
    }

    private void SendMessage()
    {

        String Message = inputMassageChatActivity.getText().toString();
        if (Message.isEmpty())
        {
            Toast.makeText(this, R.string.enter_msg, Toast.LENGTH_SHORT).show();
        }
        else
         {
            //path
            String MessageSenderRef = "Messages/" + MessageSenderID + "/" + MessageRecieverID;
            String MessageReceiverRef = "Messages/" + MessageRecieverID + "/" + MessageSenderID;
            String pushID = RootRef.child("Messages").child(MessageSenderID).child(MessageRecieverID).push().getKey();

            //message
            Map messageTextBody = new HashMap();

            messageTextBody.put("message", Message);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", MessageSenderID);
            messageTextBody.put("to", MessageRecieverID);
            messageTextBody.put("time", SaveCurrentTime);
            messageTextBody.put("date", SaveCurrentDate);
            messageTextBody.put("messageID", pushID);

            //set message in path
            Map messageBodyDetails = new HashMap();

            messageBodyDetails.put(MessageSenderRef + "/" + pushID, messageTextBody);
            messageBodyDetails.put(MessageReceiverRef + "/" + pushID, messageTextBody);

            //implement using update children
            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, R.string.msg_fail, Toast.LENGTH_SHORT).show();
                    }
                    if (task.isSuccessful())
                    {
                        inputMassageChatActivity.setText("");
                    }
                }
            });
        }
    }
}