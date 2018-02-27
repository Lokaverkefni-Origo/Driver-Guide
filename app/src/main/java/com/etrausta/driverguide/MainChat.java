package com.etrausta.driverguide;

/**
 * Created by jonth on 14.2.2018.
 */

import android.content.Context;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.main_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputEditText = (EditText) getActivity().findViewById(R.id.inputEditText);
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(inputEditText.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                inputEditText.setText("");
                //FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage("þetta er dummy",
                        //"Api Jónsson"));

                //remove keyboard after clicking send
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        displayChatMessage(view);
        adapter.notifyDataSetChanged();
    }

    private void displayChatMessage(View view) {
        ListView listOfMsg = (ListView) view.findViewById(R.id.listOfMsg);
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(),
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
    }
}