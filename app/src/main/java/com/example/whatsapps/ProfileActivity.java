package com.example.whatsapps;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{

    String receiverUserID,senderUserID,currentState;
    @BindView(R.id.circleProfileImage)
    CircleImageView circleProfileImage;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.btn_send_massage)
    Button btnSendMassage;
    @BindView(R.id.btn_cancel_request)
    Button btn_cancel_request;
    DatabaseReference RootRef,RootRefRequestChat,ContactsRef,NotificationRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        receiverUserID = getIntent().getStringExtra("id");
        RootRef= FirebaseDatabase.getInstance().getReference().child("Users");
        RootRefRequestChat=FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        NotificationRef=FirebaseDatabase.getInstance().getReference().child("Notifications");
        ContactsRef=FirebaseDatabase.getInstance().getReference().child("contacts");
        mAuth=FirebaseAuth.getInstance();
        senderUserID=mAuth.getCurrentUser().getUid();
        currentState="new";
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        RootRef.child(receiverUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    String image=null;
                    username.setText(snapshot.child("Name").getValue().toString());
                    status.setText(snapshot.child("Status").getValue().toString());
                    if (snapshot.child("image").exists())
                    {
                         image=snapshot.child("image").getValue().toString();
                    }
                    Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(circleProfileImage);

                    ManageRequestChat();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(ProfileActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ManageRequestChat()
    {
        RootRefRequestChat.child(senderUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    String request_type;
                    if (snapshot.child(receiverUserID).child("request_type").exists())
                    {
                         request_type = snapshot.child(receiverUserID).child("request_type").getValue().toString();
                        if (request_type.equals("sent"))
                        {
                            currentState="request_sent";
                            btnSendMassage.setText(R.string.cancel_request);
                        }
                        if (request_type.equals("received"))
                        {
                            currentState="request_received";
                            btnSendMassage.setText(R.string.accept_request);
                            btn_cancel_request.setVisibility(View.VISIBLE);
                            btn_cancel_request.setEnabled(true);
                            btn_cancel_request.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CancelChatRequest();
                                }
                            });
                        }
                    }
                }
                else
                {
                  ContactsRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                          if (snapshot.hasChild(receiverUserID))
                          {
                              btnSendMassage.setEnabled(true);
                              btnSendMassage.setText(R.string.remove_account);
                              currentState="Friends";
                          }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {
                          Toast.makeText(ProfileActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                  });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(ProfileActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (!senderUserID.equals(receiverUserID))
        {
              btnSendMassage.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      btnSendMassage.setEnabled(false);
                      if (currentState.equals("new"))
                      {
                          SendChatRequest();
                      }
                      if (currentState.equals("request_sent"))
                      {
                          CancelChatRequest();
                      }
                      if (currentState.equals("request_received"))
                      {
                          AcceptChatRequest();
                      }
                      if (currentState.equals("Friends"))
                      {
                          RemoveChatContact();
                      }
                  }
              });
        }
        else
        {
            btnSendMassage.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveChatContact()
    {
        ContactsRef.child(senderUserID).child(receiverUserID).removeValue().
        addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
              if (task.isSuccessful())
              {
                  ContactsRef.child(receiverUserID).child(senderUserID).removeValue().
                  addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task)
                      {
                        if (task.isSuccessful())
                        {
                           btnSendMassage.setText(R.string.send_request);
                           btnSendMassage.setEnabled(true);
                           currentState="new";
                           btn_cancel_request.setEnabled(false);
                           btn_cancel_request.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            Toast.makeText(ProfileActivity.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                      }
                  });
              }
            }
        });
    }

    private void AcceptChatRequest()
    {
      ContactsRef.child(senderUserID).child(receiverUserID).child("Contacts").setValue("saved").
          addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task)
          {
              if (task.isSuccessful())
              {
                 ContactsRef.child(receiverUserID).child(senderUserID).child("Contacts").setValue("saved").
                 addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task)
                     {
                       if (task.isSuccessful())
                       {
                           RootRefRequestChat.child(senderUserID).child(receiverUserID).removeValue().
                           addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task)
                               {
                                 if (task.isSuccessful())
                                 {
                                     RootRefRequestChat.child(receiverUserID).child(senderUserID).removeValue().
                                     addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task)
                                         {
                                          if (task.isSuccessful())
                                          {
                                              btnSendMassage.setEnabled(true);
                                              currentState="Friends";
                                              btnSendMassage.setText(R.string.remove_account);
                                              btn_cancel_request.setEnabled(false);
                                              btn_cancel_request.setVisibility(View.INVISIBLE);
                                          }
                                          else
                                          {
                                              Toast.makeText(ProfileActivity.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                          }
                                         }
                                     });
                                 }
                               }
                           });
                       }
                     }
                 }) ;
              }
          }
      })  ;
    }

    private void CancelChatRequest()
    {
        RootRefRequestChat.child(senderUserID).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    RootRefRequestChat.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                btnSendMassage.setEnabled(true);
                                btnSendMassage.setText("Send Request");
                                currentState="new";
                                btn_cancel_request.setVisibility(View.INVISIBLE);
                                btn_cancel_request.setEnabled(false);
                                Toast.makeText(ProfileActivity.this, R.string.cancel_msg, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private void SendChatRequest()
    {
        RootRefRequestChat.child(senderUserID).child(receiverUserID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    RootRefRequestChat.child(receiverUserID).child(senderUserID).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                           btnSendMassage.setEnabled(true);
                                           btnSendMassage.setText(R.string.cancel_request);
                                           currentState="request_sent";
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}