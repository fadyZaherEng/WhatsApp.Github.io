package com.example.whatsapps.TabsAndPager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.whatsapps.Fragments.CallsFragment;
import com.example.whatsapps.Fragments.ChatsFragment;
import com.example.whatsapps.Fragments.GroupFragment;
import com.example.whatsapps.Fragments.RequestsFragment;


public class TabsAccesorsAdapter extends FragmentStatePagerAdapter {
public static ChatsFragment chatsFragment;
public static GroupFragment groupFragment;
public static CallsFragment callsFragment;
public static RequestsFragment requestsFragment;
    public TabsAccesorsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                  chatsFragment=new ChatsFragment();
                return chatsFragment;
            case 1:
                 groupFragment=new GroupFragment();
                return groupFragment;
            case 2:
                 callsFragment=new CallsFragment();
                return callsFragment;
            case 3:
                 requestsFragment=new RequestsFragment();
                return requestsFragment;
            default:
                break;
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "CHATS";
            case 1:
                return "GROUP";
            case 2:
                return "ContactS";
            case 3:
                return "Requests";
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
