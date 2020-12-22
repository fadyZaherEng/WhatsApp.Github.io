package com.example.whatsapps.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapps.R;
import com.example.whatsapps.UserInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment
{
    private RecyclerView RequestsList;
    private String CurrentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RequestRef;
    private DatabaseReference ContactRef;
    private DatabaseReference UserRef;

    public RequestsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_requests, container, false);
        mAuth=FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();
        RequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        RequestsList=view.findViewById(R.id.RVRequests);
        ContactRef=FirebaseDatabase.getInstance().getReference().child("contacts");
        RequestsList.setLayoutManager(new LinearLayoutManager(getContext()));
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions Options=new
                FirebaseRecyclerOptions.Builder<UserInfo>().setQuery(RequestRef.child(CurrentUserID),UserInfo.class).build();
        FirebaseRecyclerAdapter<UserInfo, RequestViewHolder> adapter=new
                FirebaseRecyclerAdapter<UserInfo, RequestViewHolder>(Options)
                {
                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view=LayoutInflater.from(getContext()).inflate(R.layout.users_display_layout,parent,false);
                        RequestViewHolder holder=new RequestViewHolder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull UserInfo model)
                    {
                        String RequestUserId=getRef(position).getKey();
                        DatabaseReference RequestType=getRef(position).child("request_type").getRef();
                        RequestType.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String requestType=snapshot.getValue().toString();
                                    if (requestType.equals("received")) {
                                        UserRef.child(RequestUserId).addValueEventListener(new ValueEventListener()
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
                                                    Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(holder.ProfileImage);
                                                    holder.Acceept.setVisibility(View.VISIBLE);
                                                    holder.Cancel.setVisibility(View.VISIBLE);
                                                    holder.Acceept.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v)
                                                        {
                                                            AcceptedRequested(RequestUserId);
                                                        }
                                                    });
                                                    holder.Cancel.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v)
                                                        {
                                                            CanceledRequest(RequestUserId);
                                                        }
                                                    });
                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v)
                                                        {
                                                            CharSequence sequence[]=new CharSequence[]
                                                            {
                                                                    "Accept",
                                                                    "Cancel"
                                                            };
                                                            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                            builder.setTitle(name+"  Chat Request");
                                                            builder.setItems(sequence, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which)
                                                                {
                                                                   if (which==0)
                                                                   {
                                                                      AcceptedRequested(RequestUserId);
                                                                   }
                                                                   if (which==1)
                                                                   {
                                                                       CanceledRequest(RequestUserId);
                                                                   }
                                                                }
                                                            });
                                                            builder.show();
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
                                    else
                                    {
                                        UserRef.child(RequestUserId).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                if (snapshot.exists()) {
                                                    String name = snapshot.child("Name").getValue().toString();
                                                    String status = snapshot.child("Status").getValue().toString();
                                                    String image = null;
                                                    if (snapshot.hasChild("image")) {
                                                        image = snapshot.child("image").getValue().toString();
                                                    }
                                                    holder.Name.setText(name);
                                                    holder.Status.setText(status);
                                                    Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(holder.ProfileImage);
                                                    holder.Acceept.setVisibility(View.GONE);
                                                    holder.Cancel.setVisibility(View.GONE);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getActivity(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                };
        RequestsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void CanceledRequest(String RequestUserId)
    {
        RequestRef.child(CurrentUserID).child(RequestUserId).removeValue().
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            RequestRef.child(RequestUserId).child(CurrentUserID).removeValue().
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(getContext(), R.string.contact_delete, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }) ;
                        }
                    }
                }) ;
    }

    private void AcceptedRequested(String RequestUserId)
    {
        ContactRef.child(CurrentUserID).child(RequestUserId).child("Contacts").setValue("saved").
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactRef.child(RequestUserId).child(CurrentUserID).child("Contacts").setValue("saved").
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                RequestRef.child(CurrentUserID).child(RequestUserId).removeValue().
                                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    RequestRef.child(RequestUserId).child(CurrentUserID).removeValue().
                                                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        Toast.makeText(getContext(), R.string.contact_success, Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            }) ;
                                                                }
                                                            }
                                                        }) ;
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder
    {
        TextView Name, Status;
        CircleImageView ProfileImage;
        Button Acceept,Cancel;
        public RequestViewHolder(@NonNull View itemView)
        {
            super(itemView);
            Name = itemView.findViewById(R.id.userName);
            Status = itemView.findViewById(R.id.userStatus);
            ProfileImage = itemView.findViewById(R.id.userProfileImage);
            Acceept=itemView.findViewById(R.id.btn_accept);
            Cancel=itemView.findViewById(R.id.btn_cancel);
        }
    }
}