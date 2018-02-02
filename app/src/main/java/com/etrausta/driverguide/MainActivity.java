package com.etrausta.driverguide;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activityMain;
    FloatingActionButton fab;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuSignOut) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activityMain, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode ==RESULT_OK) {
                Snackbar.make(activityMain, "Successfully signed in. Welcome!", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            }
            else {
                Snackbar.make(activityMain, "Sign in failed, try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityMain = (RelativeLayout) findViewById(R.id.activityMain);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputEditText = (EditText) findViewById(R.id.inputEditText);
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(inputEditText.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                inputEditText.setText("");
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage("þetta er dummy",
                        "Api Jónsson"));
            }
        });

        //check if not sign in then navigate signing page
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
        }
        else {
            Snackbar.make(activityMain,"Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            //load content
            displayChatMessage();
            adapter.notifyDataSetChanged();
        }

    }

    private void displayChatMessage() {
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
    }
}
