package com.example.whatsapps.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapps.R;
import com.example.whatsapps.UserInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallsFragment extends Fragment
{
  FirebaseAuth mAuth;
  String CurrentUserId;
  DatabaseReference ContactRef;
  RecyclerView RVContacts;
  DatabaseReference UserRef;

    public CallsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions Options=new
                FirebaseRecyclerOptions.Builder<UserInfo>().setQuery(ContactRef,UserInfo.class).build();
        FirebaseRecyclerAdapter<UserInfo,ContactViewHolder> adapter=new
                FirebaseRecyclerAdapter<UserInfo,ContactViewHolder>(Options)
        {
            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view=LayoutInflater.from(getContext()).inflate(R.layout.users_display_layout,parent,false);
                ContactViewHolder holder=new ContactViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull ContactViewHolder holder, int position, @NonNull UserInfo model)
            {
                String contactID=getRef(position).getKey();
                UserRef.child(contactID).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            String name=snapshot.child("Name").getValue().toString();
                            String status=snapshot.child("Status").getValue().toString();
                            String image = null;
                            if (snapshot.hasChild("image"))
                            {
                                image=snapshot.child("image").getValue().toString();
                            }

                            holder.Name.setText(name);
                            holder.Status.setText(status);

                            if (snapshot.child("userState").exists())
                            {
                                String time=snapshot.child("userState").child("Time").getValue().toString();
                                String date=snapshot.child("userState").child("Date").getValue().toString();
                                String state=snapshot.child("userState").child("State").getValue().toString();
                                if (state.equals("online"))
                                {
                                    holder.onlineIcon.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                                }
                            }
                            else
                            {
                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                            }

                            Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(holder.ProfileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                        Toast.makeText(getActivity(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        RVContacts.setAdapter(adapter);
        adapter.startListening();
    }

  public class ContactViewHolder extends RecyclerView.ViewHolder
  {
        TextView Name, Status;
        CircleImageView ProfileImage;
        ImageView onlineIcon;

        public ContactViewHolder(@NonNull View itemView)
        {
            super(itemView);
            Name = itemView.findViewById(R.id.userName);
            Status = itemView.findViewById(R.id.userStatus);
            ProfileImage = itemView.findViewById(R.id.userProfileImage);
            onlineIcon=itemView.findViewById(R.id.userOnlineStatus);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_calls, container, false);
        mAuth=FirebaseAuth.getInstance();
        CurrentUserId=mAuth.getCurrentUser().getUid();
        ContactRef= FirebaseDatabase.getInstance().getReference().child("contacts").child(CurrentUserId);
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        RVContacts=view.findViewById(R.id.RVContact);
        RVContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

}