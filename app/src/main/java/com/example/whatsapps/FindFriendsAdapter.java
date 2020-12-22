package com.example.whatsapps;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.ViewHolder> implements Filterable {
   private ArrayList<UserInfo> BackUpList;
   private Context context;
   public  ArrayList<UserInfo> Users;

    public FindFriendsAdapter(Context context, ArrayList<UserInfo> users)
    {
        this.context = context;
        Users = users;
        BackUpList=new ArrayList<>(Users);
    }
    public void addItem(UserInfo userInfo)
    {
        Users.add(userInfo);
        notifyDataSetChanged();
    }
    public UserInfo getItem(int pos)
    {
       return Users.get(pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
       View view= LayoutInflater.from(context).inflate(R.layout.users_display_layout,null,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.Name.setText(Users.get(position).UserName);
        holder.Status.setText(Users.get(position).UserStatus);
        Picasso.get().load(Users.get(position).ProfileImage).placeholder(R.drawable.ic_profile).into(holder.ProfileImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String id=Users.get(position).id;
                Intent profileActivity =new Intent(context,ProfileActivity.class);
                profileActivity.putExtra("id",id);
                context.startActivity(profileActivity);
            }
        });
    }


    @Override
    public int getItemCount() {
        return Users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView Name,Status;
        CircleImageView ProfileImage;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            Name=itemView.findViewById(R.id.userName);
            Status=itemView.findViewById(R.id.userStatus);
            ProfileImage=itemView.findViewById(R.id.userProfileImage);
        }
    }




    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence Keybword) {
           ArrayList<UserInfo> FilterList=new ArrayList<>();
            if (Keybword.toString().isEmpty())
            {
                FilterList.addAll(BackUpList);
            }
            else
            {
                for (UserInfo userInfo:BackUpList)
                {
                    if (userInfo.getUserName().toLowerCase().contains(Keybword.toString().toLowerCase()))
                    {
                        FilterList.add(userInfo);
                    }
                }
            }
            FilterResults filterResults=new FilterResults();
            filterResults.values=FilterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
                Users.clear();
                Users.addAll((ArrayList<UserInfo>)results.values);
                notifyDataSetChanged();
        }
    };

}