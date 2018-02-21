package com.etrausta.driverguide;

/**
 * Created by jonth on 14.2.2018.
 */

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainChat extends Fragment {
    FloatingActionButton fab;
    private FirebaseListAdapter<ChatMessage> adapter;

    public static MainChat newInstance() {
        MainChat fragment = new MainChat();
        return fragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputEditText = (EditText) getView().findViewById(R.id.inputEditText);
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(inputEditText.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                inputEditText.setText("");
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage("þetta er dummy",
                        "Api Jónsson"));
            }
        });
        //displayChatMessage();
        adapter.notifyDataSetChanged();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_chat, container, false);
    }


    /*private void displayChatMessage() {
        ListView listOfMsg = (ListView) findViewById(R.id.listOfMsg);
        adapter = new FirebaseListAdapter<ChatMessage>(this,
                ChatMessage.class,
                R.layout.list_item,
                FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //get references to the views of list_item.xml
                TextView messageTxt = (TextView)v.findViewById(R.id.msgTxt);
                TextView messageUser = (TextView)v.findViewById(R.id.msgUser);
                TextView messageTime = (TextView)v.findViewById(R.id.msgTime);

                messageTxt.setText(model.getMsgTxt());
                messageUser.setText(model.getMsgUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMsgTime()));
            }
        };
        listOfMsg.setAdapter(adapter);
    }*/
}
