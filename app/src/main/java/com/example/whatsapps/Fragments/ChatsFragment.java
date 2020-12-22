package com.example.whatsapps.Fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapps.Activities.ChatActivity;
import com.example.whatsapps.ProfileActivity;
import com.example.whatsapps.ProfileImageShow;
import com.example.whatsapps.R;
import com.example.whatsapps.UserInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    private String CurrentUserID;
    private DatabaseReference ContactRef;
    private FirebaseAuth mAuth;
    RecyclerView RVFriends;
    DatabaseReference FriendsRef;
    String img;
    public ChatsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chats, container, false);
        mAuth=FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();
        ContactRef= FirebaseDatabase.getInstance().getReference().child("contacts");
        RVFriends=view.findViewById(R.id.RVChatFriends);
        RVFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        FriendsRef=FirebaseDatabase.getInstance().getReference().child("Users");
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        RetrieveFriends();
    }

    public void RetrieveFriends()
    {
        FirebaseRecyclerOptions Options=new
                FirebaseRecyclerOptions.Builder<UserInfo>().setQuery(ContactRef.child(CurrentUserID),UserInfo.class).build();
        FirebaseRecyclerAdapter<UserInfo,FriendViewHolder> adapter=new FirebaseRecyclerAdapter<UserInfo, FriendViewHolder>(Options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder holder, int position, @NonNull UserInfo model)
            {
                String Friend=getRef(position).getKey();
                FriendsRef.child(Friend).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            String name=snapshot.child("Name").getValue().toString();
                            String status=snapshot.child("Status").getValue().toString();
                            img=null;
                            if (snapshot.hasChild("image"))
                            {
                                img=snapshot.child("image").getValue().toString();
                            }
                            Picasso.get().load(img).placeholder(R.drawable.ic_profile).into(holder.ProfileImage);
                            holder.Name.setText(name);
                            if (snapshot.child("userState").exists())
                            {
                                String time=snapshot.child("userState").child("Time").getValue().toString();
                                String date=snapshot.child("userState").child("Date").getValue().toString();
                                String state=snapshot.child("userState").child("State").getValue().toString();
                                if (state.equals("online"))
                                {
                                    holder.Status.setText("Online Now");
                                }
                                else
                                {
                                    holder.Status.setText("Last Seen: "+time+" "+date);
                                }
                            }
                            else
                            {
                                holder.Status.setText("Offline");
                            }

                            String finalImg = img;


                            holder.ProfileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                    View view=LayoutInflater.from(getContext()).inflate(R.layout.dialog_view_user,null,false);
                                    TextView username= view.findViewById(R.id.name);
                                    ImageView imageView=view.findViewById(R.id.imageProfile);

                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent=new Intent(getContext(), ProfileImageShow.class);
                                            intent.putExtra("image",finalImg);
                                            intent.putExtra("name",name);
                                            startActivity(intent);
                                        }
                                    });

                                    username.setText(name);
                                    Picasso.get().load(finalImg).placeholder(R.drawable.ic_profile).into(imageView);
                                    builder.setView(view);
                                    final AlertDialog alertDialog= builder.show();
                                    alertDialog.setCanceledOnTouchOutside(false);

                                    view.findViewById(R.id.openChat).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent=new Intent(getContext(), ChatActivity.class);
                                            intent.putExtra("FriendName",name);
                                            intent.putExtra("FriendID",Friend);
                                            intent.putExtra("image", finalImg);
                                            startActivity(intent);
                                            alertDialog.dismiss();
                                        }
                                    });

                                    view.findViewById(R.id.openProfile).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent intent=new Intent(getContext(), ProfileActivity.class);
                                            intent.putExtra("id",Friend);
                                            startActivity(intent);
                                            alertDialog.dismiss();

                                        }
                                    });
                                }
                            });


                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent=new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("FriendName",name);
                                    intent.putExtra("FriendID",Friend);
                                    intent.putExtra("image", finalImg);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                        Toast.makeText(getActivity(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View  view=LayoutInflater.from(getContext()).inflate(R.layout.users_display_layout,null,false);
                FriendViewHolder holder=new FriendViewHolder(view);
                return holder;
            }
        };

        RVFriends.setAdapter(adapter);
        adapter.startListening();
    }


    public class FriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView Name, Status;
        CircleImageView ProfileImage;
        Button Accept,Cancel;
        public FriendViewHolder(@NonNull View itemView)
        {
            super(itemView);
            Name = itemView.findViewById(R.id.userName);
            Status = itemView.findViewById(R.id.userStatus);
            ProfileImage = itemView.findViewById(R.id.userProfileImage);
            Accept=itemView.findViewById(R.id.btn_accept);
            Cancel=itemView.findViewById(R.id.btn_cancel);

        }
    }




    public void FirebaseSearch(String Text){

        if (Text.isEmpty())
        {
            RetrieveFriends();
        }
        else {
            String query = Text.toLowerCase();
            Query query1 = FriendsRef.orderByChild("Name").startAt(query).endAt(query + "\uf8ff");
            FirebaseRecyclerOptions Options = new
                    FirebaseRecyclerOptions.Builder<UserInfo>().setQuery(query1, UserInfo.class).build();

            FirebaseRecyclerAdapter<UserInfo, ChatsFragment.FriendViewHolder> adapter = new FirebaseRecyclerAdapter<UserInfo, ChatsFragment.FriendViewHolder>(Options) {
                @Override
                protected void onBindViewHolder(@NonNull ChatsFragment.FriendViewHolder holder, int position, @NonNull UserInfo model) {
                    String Friend = getRef(position).getKey();
                    FriendsRef.child(Friend).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                String name = snapshot.child("Name").getValue().toString();
                                String status = snapshot.child("Status").getValue().toString();
                                img = null;

                                if (snapshot.child("userState").exists())
                                {
                                    String time=snapshot.child("userState").child("Time").getValue().toString();
                                    String date=snapshot.child("userState").child("Date").getValue().toString();
                                    String state=snapshot.child("userState").child("State").getValue().toString();
                                    if (state.equals("online"))
                                    {
                                        holder.Status.setText("Online Now");
                                    }
                                    else
                                    {
                                        holder.Status.setText("Last Seen: "+time+" "+date);
                                    }
                                }
                                else
                                {
                                    holder.Status.setText("Offline");
                                }

                                if (snapshot.hasChild("image")) {
                                    img = snapshot.child("image").getValue().toString();
                                }

                                Picasso.get().load(img).placeholder(R.drawable.ic_profile).into(holder.ProfileImage);
                                holder.Name.setText(name);
                                String finalImg = img;
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(), ChatActivity.class);
                                        intent.putExtra("FriendName", name);
                                        intent.putExtra("FriendID", Friend);
                                        intent.putExtra("image", finalImg);
                                        startActivity(intent);
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @NonNull
                @Override
                public ChatsFragment.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.users_display_layout, null, false);
                    ChatsFragment.FriendViewHolder holder = new ChatsFragment.FriendViewHolder(view);
                    return holder;
                }
            };
            RVFriends.setAdapter(adapter);
            adapter.startListening();
        }
    }
}