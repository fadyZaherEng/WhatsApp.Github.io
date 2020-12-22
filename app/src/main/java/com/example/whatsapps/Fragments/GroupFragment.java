package com.example.whatsapps.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Toast;

import com.example.whatsapps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GroupFragment extends Fragment implements Filterable
{
     ArrayList<String> GroupsName;
     ArrayList<String> GroupsNameAll;
     public static ArrayAdapter<String> ListViewAdapter;
     ListView ListViewGroup;
     DatabaseReference RootRef;
     onItemSelectedListenerListView itemSelectedListenerListView;
    public GroupFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        if (context instanceof onItemSelectedListenerListView)
        {
            itemSelectedListenerListView= (onItemSelectedListenerListView) context;
        }
        else
        {
            throw new  RuntimeException(getString(R.string.listener));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false);

    }

    private void ListViewSelectedItemMethod(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String GroupName=parent.getItemAtPosition(position).toString();
              itemSelectedListenerListView.ItemSelected(GroupName);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GroupsName = new ArrayList<>();
        GroupsNameAll = new ArrayList<>();
        ListViewAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, GroupsName);
        ListViewGroup = view.findViewById(R.id.list_view_group);
        ListViewSelectedItemMethod(ListViewGroup);
        ListViewGroup.setAdapter(ListViewAdapter);

        RootRef = FirebaseDatabase.getInstance().getReference().child("Groups");

            RootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        GroupsName.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                GroupsName.add(dataSnapshot.getKey());
                                GroupsNameAll.add(dataSnapshot.getKey());
                            }
                        }
                        ListViewAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
             //       Toast.makeText(getActivity(), "Error Group Fragment : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    @Override
    public void onDetach() {
        super.onDetach();
        itemSelectedListenerListView=null;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<String>FilterList=new ArrayList<>();
            if (constraint.toString().isEmpty())
            {
                FilterList.addAll(GroupsNameAll);
            }
            else
            {
                for (String name:GroupsNameAll)
                {
                    if (name.toLowerCase().contains(constraint.toString().toLowerCase()))
                    {
                        FilterList.add(name);
                    }
                }
            }
            FilterResults filterResults=new FilterResults() ;
            filterResults.values=FilterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
          GroupsName.clear();
          GroupsName.addAll((Collection<? extends String>) results);
          ListViewAdapter.notifyDataSetChanged();
        }
    };
}
