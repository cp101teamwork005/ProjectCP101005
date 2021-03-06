package com.example.yangwensing.myapplication.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangwensing.myapplication.R;
import com.example.yangwensing.myapplication.chat.ChatList;
import com.example.yangwensing.myapplication.classes.ClassCreate;
import com.example.yangwensing.myapplication.classes.ClassManager;
import com.example.yangwensing.myapplication.classes.Classes;
import com.example.yangwensing.myapplication.info.StudentInfoEditFragment;
import com.example.yangwensing.myapplication.login.LoginFragment;
import com.example.yangwensing.myapplication.main.Common;
import com.example.yangwensing.myapplication.main.MyTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.PublicKey;
import java.util.List;

import static com.example.yangwensing.myapplication.info.StudentInfoEditFragment.TAG;

/**
 * Created by nameless on 2018/5/10.
 */

public class ChatList extends Fragment {

    private RecyclerView rvChatlist;
    private MyTask chatlistTask;
    String sender = "";
    int classid = 0;
    String classidString = "";
    private BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatlist, container, false);
        rvChatlist = view.findViewById(R.id.rvchatlist);
        rvChatlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        bottomNavigationView = getActivity().findViewById(R.id.btNavigation_Bar);


        SharedPreferences preferences = getActivity().getSharedPreferences(Common.PREF_FILE, Context.MODE_PRIVATE);
        sender = preferences.getString("account","user");
        classid = preferences.getInt("classId",0);

        classidString = String.valueOf(classid);

        getActivity().setTitle(R.string.title_contactParents);


        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        showAllcanchat();
        //隱藏底部導覽列
//        bottomNavigationView.setVisibility(View.GONE);
    }



    private class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<ChatLists> chatLists;


        ChatListRecyclerViewAdapter(Context context, List<ChatLists> chatLists) {
            layoutInflater = LayoutInflater.from(context);
            this.chatLists = chatLists;
        }


        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.chat, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final ChatLists cl = chatLists.get(position);
            String url = Common.URL + "/LoginHelp";
            holder.tvreceiver.setText(cl.getReceiver());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment chatFragment = new ChatFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("receiver", cl.getReceiver());
                    bundle.putString("sender",sender);
                    chatFragment.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.replace(R.id.main_content,chatFragment,"FromChatList");
                    fragmentTransaction.commit();
                }
            });

        }


        @Override
        public int getItemCount() {
            return chatLists.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvreceiver;

            MyViewHolder(View itemView) {
                super(itemView);
                tvreceiver = itemView.findViewById(R.id.tvreceiver);


            }
        }
    }
    private void showAllcanchat() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/LoginHelp";

            List<ChatLists> chatLists = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getchatlist");
            jsonObject.addProperty("senderte",sender);
            jsonObject.addProperty("id", classid);
            String jsonOut = jsonObject.toString();
            chatlistTask = new MyTask(url, jsonOut);
            try {
                String jsonIn = chatlistTask.execute().get();
                Log.d(TAG, jsonIn);
                Type listType = new TypeToken<List<ChatLists>>() {
                }.getType();
                chatLists = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (chatLists == null || chatLists.isEmpty()) {
                Toast.makeText(getActivity(), "你可能不是班導", Toast.LENGTH_SHORT).show();

            } else {
                rvChatlist.setAdapter(new ChatList.ChatListRecyclerViewAdapter(getActivity(), chatLists));
            }
        } else {
            Toast.makeText(getActivity(), "No Net", Toast.LENGTH_SHORT).show();
        }
    }
    public void onStop() {
        super.onStop();
        if (chatlistTask != null) {
            chatlistTask.cancel(true);
        }
        //重新顯示底部導覽列
//        bottomNavigationView.setVisibility(View.VISIBLE);
    }
}
