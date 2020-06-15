package com.tyagiabhinav.dialogflowchatlibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.Context;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.EventInput;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.protobuf.Struct;
import com.tyagiabhinav.dialogflowchatlibrary.networkutil.ChatbotCallback;
import com.tyagiabhinav.dialogflowchatlibrary.networkutil.TaskRunner;
import com.tyagiabhinav.dialogflowchatlibrary.templates.ButtonMessageTemplate;
import com.tyagiabhinav.dialogflowchatlibrary.templates.CheckBoxMessageTemplate;
import com.tyagiabhinav.dialogflowchatlibrary.templates.HyperLinkTemplate;
import com.tyagiabhinav.dialogflowchatlibrary.templates.TextMessageTemplate;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.Constants;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.OnClickCallback;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.ReturnMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class ChatbotActivity extends AppCompatActivity implements ChatbotCallback, OnClickCallback {

    private static final String TAG = ChatbotActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;
    private static final int SPEECH_INPUT = 10070;

    public static final String SESSION_ID = "sessionID";


    //UI
    private LinearLayout chatLayout;
    private EditText queryEditText;
    private ImageView chatMic;
    private ImageView sendBtn;

    //Variables
    private SessionsClient sessionsClient;
    private SessionName session;
    private TaskRunner dialogflowTaskRunner;
    private boolean isProgressRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatbot);
        Log.d(TAG, "onCreate: ");

        ChatbotSettings chatSettings = ChatbotSettings.getInstance();

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusBarColor));

        Toolbar toolbar = ChatbotSettings.getInstance().getAppToolbar();
        if (toolbar == null) {
            toolbar = findViewById(R.id.toolbar);
            ChatbotSettings.getInstance().setAppToolbar(toolbar);
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        chatLayout = findViewById(R.id.chatLayout);

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Send click");
                sendMessage(view);
            }
        });

        chatMic = findViewById(R.id.chatMic);
        if (chatSettings.isMicAvailable()) {
            // show mic
            chatMic.setVisibility(View.VISIBLE);
            chatMic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promptSpeech();
                }
            });
        }

        queryEditText = findViewById(R.id.queryEditText);
        queryEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            sendMessage(sendBtn);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        Bundle bundle = getIntent().getExtras();
        String sessionID = null;
        if (bundle != null) {
            sessionID = bundle.getString(SESSION_ID);
            if (sessionID == null || sessionID.trim().isEmpty()) {
                sessionID = UUID.randomUUID().toString();
            }
        }

        try {
            init(sessionID);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ChatbotActivity.this, "Error creating a session!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        closeDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                Log.d(TAG, "onActivityResult: " + result);
                queryEditText.setText(result);
                send(result);
            }
        }
    }

    @Override
    public void OnChatbotResponse(DetectIntentResponse response) {
        removeProcessWaitBubble();
        processResponse(response);
    }

    @Override
    public void OnUserClickAction(ReturnMessage msg) {
        String eventName = msg.getEventName();
        Struct param = msg.getParam();
        if (eventName != null && !eventName.trim().isEmpty()) {
            if (param != null && param.getFieldsCount() > 0) {
                EventInput eventInput = EventInput.newBuilder().setName(eventName).setLanguageCode("en-US").setParameters(param).build();
                send(eventInput, msg.getActionText());
            } else {
                EventInput eventInput = EventInput.newBuilder().setName(eventName).setLanguageCode("en-US").build();
                send(eventInput, msg.getActionText());
            }
        } else {
            send(msg.getActionText());
        }
    }

    private void init(String UUID) throws IOException {
        InputStream credentialStream = DialogflowCredentials.getInstance().getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialStream);
        String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

        SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
        SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
        sessionsClient = SessionsClient.create(sessionsSettings);
        session = SessionName.of(projectId, UUID);

        if (ChatbotSettings.getInstance().isAutoWelcome()) {
            send("hi");
        }
    }

    private void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            send(msg);
        }
    }

    private void send(String message) {
        Log.d(TAG, "send: 1");
        TextMessageTemplate tmt = new TextMessageTemplate(getApplicationContext(), ChatbotActivity.this, Constants.USER);
        if (!ChatbotSettings.getInstance().isAutoWelcome()) {
            chatLayout.addView(tmt.showMessage(message));
            queryEditText.setText("");
            showProcessWaitBubble();
        } else {
            ChatbotSettings.getInstance().setAutoWelcome(false);
        }
        QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
        dialogflowTaskRunner = new TaskRunner(this, session, sessionsClient, queryInput);
        dialogflowTaskRunner.execute();
    }

    private void send(EventInput event, String message) {
        Log.d(TAG, "send: 2");
        TextMessageTemplate tmt = new TextMessageTemplate(getApplicationContext(), ChatbotActivity.this, Constants.USER);
        if (!ChatbotSettings.getInstance().isAutoWelcome()) {
            chatLayout.addView(tmt.showMessage(message));
            queryEditText.setText("");
            showProcessWaitBubble();
        } else {
            ChatbotSettings.getInstance().setAutoWelcome(false);
        }

        QueryInput queryInput = QueryInput.newBuilder().setEvent(event).build();
        dialogflowTaskRunner = new TaskRunner(this, session, sessionsClient, queryInput);
        dialogflowTaskRunner.execute();
    }

    private void showProcessWaitBubble() {
        TextMessageTemplate tmt = new TextMessageTemplate(getApplicationContext(), ChatbotActivity.this, Constants.BOT);
        chatLayout.addView(tmt.showMessage("..."));
        isProgressRunning = true;
        enableDissableChatUI(false);

    }

    private void removeProcessWaitBubble() {
        enableDissableChatUI(true);
        if (isProgressRunning && chatLayout != null && chatLayout.getChildCount() > 0) {
            chatLayout.removeViewAt(chatLayout.getChildCount() - 1);
            isProgressRunning = false;
        }
    }

    private void processResponse(DetectIntentResponse response) {
        Log.d(TAG, "processResponse");
        if (response != null) {
            List<Context> contextList = response.getQueryResult().getOutputContextsList();
            int layoutCount = chatLayout.getChildCount();
            if (contextList.size() > 0) {
                for (Context context : contextList) {
                    if (context.getName().contains("param_context")) {
                        Map paramMap = context.getParameters().getFieldsMap();
                        if (paramMap.containsKey("template")) {
                            String template = context.getParameters().getFieldsMap().get("template").getStringValue();
                            switch (template) {
                                case "text":
                                    Log.d(TAG, "processResponse: Text Template");
                                    TextMessageTemplate tmt = new TextMessageTemplate(ChatbotActivity.this, ChatbotActivity.this, Constants.BOT);
                                    chatLayout.addView(tmt.showMessage(response)); // move focus to text view to automatically make it scroll up if softfocus
                                    queryEditText.requestFocus();
                                    break;
                                case "button":
                                    Log.d(TAG, "processResponse: Button Template");
                                    ButtonMessageTemplate bmt = new ButtonMessageTemplate(ChatbotActivity.this, ChatbotActivity.this, Constants.BOT);
                                    chatLayout.addView(bmt.showMessage(response)); // move focus to text view to automatically make it scroll up if softfocus
//                            queryEditText.requestFocus();
                                    break;
                                case "hyperlink":
                                    Log.d(TAG, "processResponse: Hyperlink Template");
                                    HyperLinkTemplate blt = new HyperLinkTemplate(ChatbotActivity.this, ChatbotActivity.this, Constants.BOT);
                                    chatLayout.addView(blt.showMessage(response)); // move focus to text view to automatically make it scroll up if softfocus
//                            queryEditText.requestFocus();
                                    break;
                                case "checkbox":
                                    Log.d(TAG, "processResponse: CheckBox Template");
                                    CheckBoxMessageTemplate cbmt = new CheckBoxMessageTemplate(ChatbotActivity.this, ChatbotActivity.this, Constants.BOT);
                                    chatLayout.addView(cbmt.showMessage(response)); // move focus to text view to automatically make it scroll up if softfocus
//                            queryEditText.requestFocus();
                                    break;
                            }
                        }
                    } else {
                        // when no param context if found... go to default
                        TextMessageTemplate tmt = new TextMessageTemplate(ChatbotActivity.this, ChatbotActivity.this, Constants.BOT);
                        chatLayout.addView(tmt.showMessage(response));
                        queryEditText.requestFocus();
                    }
                    if(chatLayout.getChildCount() > layoutCount){
                        break; //this check is added as multiple layouts were getting added to chatLayout equal to number of loops
                    }
                }
            } else {
                // when no param context if found... go to default
                TextMessageTemplate tmt = new TextMessageTemplate(ChatbotActivity.this, ChatbotActivity.this, Constants.BOT);
                chatLayout.addView(tmt.showMessage(response));
                queryEditText.requestFocus();
            }
        } else {
            Log.e(TAG, "processResponse: Null Response");
        }
    }

    private void closeDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatbotActivity.this);

        alertDialogBuilder.setTitle("Exit Chat?");
        alertDialogBuilder.setMessage("Do you want to exit the chat? You will loose this chat session.");

        alertDialogBuilder
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "clicked: YES");
                                dialog.cancel();
//                                destroyRequestDialogflowTask();
                                ChatbotSettings.getInstance().setAppToolbar(null);
                                ChatbotSettings.getInstance().setAutoWelcome(true);
//                                super.onBackPressed();
                                ChatbotActivity.this.finish();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "clicked: NO");
                                dialog.cancel();
                            }
                        })
                .create()
                .show();

    }

    private void enableDissableChatUI(boolean bool) {
        chatMic.setEnabled(bool);
        chatMic.setClickable(bool);
        queryEditText.setEnabled(bool);
        queryEditText.setClickable(bool);
        sendBtn.setEnabled(bool);
        sendBtn.setClickable(bool);
    }

    private void promptSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something !");
        try {
            startActivityForResult(intent, SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry! Device does not support speech input", Toast.LENGTH_SHORT).show();
        }
    }
}
