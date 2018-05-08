package com.etrausta.driverguide;

/**
 * Created by jonth on 14.2.2018.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Intent;
import android.widget.Toast;

import java.util.Locale;

public class MainChat extends Fragment implements AIListener, OnInitListener {
    RelativeLayout fab;
    ListView listOfMsg;
    EditText inputEditText;
    private AIService aiService;
    DatabaseReference ref;
    Boolean flagForImage = true;
    private FirebaseListAdapter<ChatMessage> adapter;
    private TextToSpeech tts;
    private int DATA_CHECK_CODE = 0;

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
        //Búa til api og dialogflow kóðinn er í "clientaccesstoken"
        final ai.api.android.AIConfiguration config = new ai.api.android.AIConfiguration("a9be4307cca64af69a4630bc6a6943af",
                ai.api.android.AIConfiguration.SupportedLanguages.English,
                ai.api.android.AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(getActivity(), config);
        aiService.setListener(this);

        int permission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }

        final AIDataService aiDataService = new AIDataService(config);
        final AIRequest aiRequest = new AIRequest();
        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);

        inputEditText = (EditText) getActivity().findViewById(R.id.inputEditText);
        fab = (RelativeLayout) view.findViewById(R.id.fab);

        //Athugar hvort það sé búið að skrifa eða ekki.
        //Ef það er búið að skrifa eitthvað þá tekur hann skilaboðin og sendir þau á chatbot, inn á firebase og sýnir það á skjánum og fær svar frá chatbot, setur það inn á firebase og sýnir það.
        //Ef ekki er búið að skrifa neitt þá kallar hann á innbyggða fallið startlistening sem byrjar að hlusta eftir tali. OnResult fallið sér svo um að sýna þegar talað er við bottan.
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
            final String message = inputEditText.getText().toString().trim();
            if (!message.equals("")) {
                ChatMessage chatMessage = new ChatMessage(message, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                ref.child("chat").push().setValue(chatMessage);
                aiRequest.setQuery(message);
                new AsyncTask<AIRequest, Void, AIResponse>() {

                    @Override
                    protected AIResponse doInBackground(AIRequest... aiRequests) {
                        final AIRequest request = aiRequests[0];
                        try {
                            final AIResponse response = aiDataService.request(aiRequest);
                            return response;
                        } catch (AIServiceException e) {

                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(AIResponse response) {
                        if (response != null) {
                            Result result = response.getResult();
                            String reply = result.getFulfillment().getSpeech();
                            ChatMessage chatMessage = new ChatMessage(reply, "Caren");
                            ref.child("chat").push().setValue(chatMessage);

                        }
                    }

                }.execute(aiRequest);
            }
            else {
                aiService.startListening();
            }
            inputEditText.setText("");

            Intent checkTTSIntent = new Intent();
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkTTSIntent, DATA_CHECK_CODE);
            //Tekur út lyklaborð þegar búið er að senda
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            }
        });

        //Sér um að skipta um mynd þegar byrjað er að skrifa
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ImageView fab_img = (ImageView) getActivity().findViewById(R.id.fab_img);
                Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_send_white_24dp);
                Bitmap img2 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic_white_24dp);

                if (charSequence.toString().trim().length() != 0 && flagForImage) {
                    ImageViewAnimatedChange(getActivity(), fab_img, img);
                    flagForImage = false;
                }
                else if (charSequence.toString().trim().length() == 0) {
                    ImageViewAnimatedChange(getActivity(), fab_img, img2);
                    flagForImage = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        displayChatMessage(view);
        adapter.notifyDataSetChanged();
    }

    private void displayChatMessage(View view) {
        listOfMsg = (ListView) view.findViewById(R.id.listOfMsg);
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(),
                ChatMessage.class,
                R.layout.list_item,
                ref.child("chat")) {

            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //sér um að kalla á id inn í list_item.xml
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

    public void ImageViewAnimatedChange(Context context, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(context, R.anim.zoom_in);

        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();

        String message = result.getResolvedQuery();
        ChatMessage chatMessage0 = new ChatMessage(message, FirebaseAuth.getInstance().getCurrentUser().getEmail());
        ref.child("chat").push().setValue(chatMessage0);

        String reply = result.getFulfillment().getSpeech();
        ChatMessage chatMessage = new ChatMessage(reply, "Caren");
        ref.child("chat").push().setValue(chatMessage);

        speakUp(reply);
    }
    private void speakUp(String speech) {
        tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.RECORD_AUDIO},
                101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                }
                else {

                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //Notandi hefur þau gögn sem þarf fyrir text to speech
                tts = new TextToSpeech(getActivity(), this);
            }
            else {
                //engin gögn - settu inn núna
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        Log.i("info", "Botti byrjar að hlusta");
    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    //Setur tungumálið hjá chatbot og athugar hvort virki.
    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            if(tts.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Log.i("info", "Villa, text to speech virkar ekki");
        }
    }


}