package com.tyagiabhinav.dialogflowchatlibrary.templates;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.tyagiabhinav.dialogflowchatlibrary.ChatbotSettings;
import com.tyagiabhinav.dialogflowchatlibrary.R;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.Constants;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.OnClickCallback;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.ReturnMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MessageLayoutTemplate extends FrameLayout {

    private static final String TAG = MessageLayoutTemplate.class.getSimpleName();

    public Context context;
    public Theme theme;
    private RelativeLayout msgLayout;
    private LinearLayout templateLinearLayout;
    private LinearLayout layout;

    private TextView tv;
    private FrameLayout richMessageContainer;

    private DetectIntentResponse response;
    private OnClickCallback callback;

    private boolean onlyTextResponse = true;
    private List<View> viewsToDisable = new ArrayList<>();

    abstract FrameLayout populateRichMessageContainer();
//    abstract void setOnUserActionClick(OnClickCallback callback);


    public MessageLayoutTemplate(Context context, OnClickCallback callback, int type) {
        super(context);

        this.callback = callback;

        LayoutInflater inflater = LayoutInflater.from(context);
        theme = context.getTheme();
        switch (type) {
            case Constants.BOT:
                msgLayout = (RelativeLayout) inflater.inflate(R.layout.bot_msg, null);
                Drawable chatBotAvatar = ChatbotSettings.getInstance().getChatBotAvatar();
                if(chatBotAvatar != null){
                    ((ImageView) msgLayout.findViewById(R.id.botIcon)).setImageDrawable(chatBotAvatar);
                }
                break;
            case Constants.USER:
                msgLayout = (RelativeLayout) inflater.inflate(R.layout.user_msg, null);
                Drawable chatUserAvatar = ChatbotSettings.getInstance().getChatBotAvatar();
                if(chatUserAvatar != null){
                    ((ImageView) msgLayout.findViewById(R.id.userIcon)).setImageDrawable(chatUserAvatar);
                }
                break;
        }

        templateLinearLayout = msgLayout.findViewById(R.id.msgLinearLayout);

        layout = (LinearLayout) inflater.inflate(R.layout.msg_template_layout, null);
        layout.setFocusableInTouchMode(true);
        tv = layout.findViewById(R.id.chatMsg);
        richMessageContainer = layout.findViewById(R.id.richMessageContainer);

        this.context = context;
    }

    public final RelativeLayout showMessage(String msg) {

        templateLinearLayout.addView(populateTextView(msg));
        return msgLayout;
    }


    public final RelativeLayout showMessage(DetectIntentResponse response) {

        this.response = response;

        String botReply = "There was some communication issue. Please Try again!";
        if (response != null) {
            // process aiResponse here
            botReply = response.getQueryResult().getFulfillmentText();
        }
        Log.d(TAG, "V2 Bot Reply: " + botReply);

        templateLinearLayout.addView(populateTextView(botReply));
        return msgLayout;
    }

    private LinearLayout populateTextView(String message) {

        layout.setFocusableInTouchMode(true);
        tv = layout.findViewById(R.id.chatMsg);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tv.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tv.setText(Html.fromHtml(message));
        }

        if (!isOnlyTextResponse()) {
            richMessageContainer.setVisibility(View.VISIBLE);
            populateRichMessageContainer();
        } else {
            richMessageContainer.setVisibility(View.GONE);
        }

        layout.requestLayout();
        layout.requestFocus();
        return layout;
    }

    boolean isOnlyTextResponse() {
        return onlyTextResponse;
    }

    void setOnlyTextResponse(boolean onlyTextResponse) {
        this.onlyTextResponse = onlyTextResponse;
    }

    FrameLayout getRichMessageContainer() {
        return richMessageContainer;
    }

    DetectIntentResponse getResponse() {
        return response;
    }

    OnClickCallback getCallback() {
        return callback;
    }

    LinearLayout getVerticalContainer() {
        LayoutInflater inflater = LayoutInflater.from(context);
        return (LinearLayout) inflater.inflate(R.layout.vertical_container, null);
    }

    LinearLayout getHorizontalContainer() {
        LayoutInflater inflater = LayoutInflater.from(context);
        return (LinearLayout) inflater.inflate(R.layout.horizontal_container, null);
    }

    LinearLayout getCheckBoxContainer() {
        LayoutInflater inflater = LayoutInflater.from(context);
        return (LinearLayout) inflater.inflate(R.layout.checkbox_container, null);
    }

    LinearLayout getCardLayout() {
        LayoutInflater inflater = LayoutInflater.from(context);
        return (LinearLayout) inflater.inflate(R.layout.card_layout, null);
    }

    void setViewsToDisable(View view) {
        viewsToDisable.add(view);
    }

    void disableAllViews() {
        if (viewsToDisable.size() > 0) {
            for (View view : viewsToDisable) {
                view.setEnabled(false);
                view.setClickable(false);
            }
            viewsToDisable.clear();
        }
    }


    Button getBtn(final String buttonType, final Map<String, Value> btnMap, String sizeValue, final String eventName) {

        final String text = btnMap.get("uiText").getStringValue();
        boolean isPositive = btnMap.get("isPositive").getBoolValue();
//        final String actionText = btnMap.get("actionText").getStringValue();

        float textSize = 12.0f;
        switch (sizeValue) {
            case "s":
            case "S":
            case "small":
                textSize = 10.0f;
                break;
            case "m":
            case "M":
            case "medium":
                textSize = 14.0f;
                break;
            case "l":
            case "L":
            case "large":
                textSize = 18.0f;
                break;
        }

        LinearLayout.LayoutParams btnParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        btnParam.setMargins(16, 12, 16, 12);
        final Button btn = new Button(getContext());
        btn.setPadding(36, 16, 36, 16);
        btn.setText(text);
        btn.setMaxLines(2);
        btn.setTextSize(textSize);
//        btn.setTextSize(getResources().getDimension(R.dimen.text_size_button)); // Makes large text
        btn.setLayoutParams(btnParam);
        TypedValue typedValue = new TypedValue();
        if (isPositive) {
            btn.setTextColor(getResources().getColor(R.color.chatBGnText, theme));//getColorStateList(R.color.chat_btn_pos_text_color));
            btn.setBackground(getResources().getDrawable(R.drawable.bot_pos_btn, theme));
        } else {
            btn.setTextColor(getResources().getColor(R.color.chatPrimary, theme));//getColorStateList(R.color.chat_btn_neg_text_color));
            btn.setBackground(getResources().getDrawable(R.drawable.bot_neg_btn, theme));
        }
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (buttonType) {
                    case "button":
                        Log.d(TAG, "onClick button: " + text);
                        OnClickCallback callback = getCallback();
                        if (callback != null) {
                            ReturnMessage.MessageBuilder msgBuilder = new ReturnMessage.MessageBuilder(btnMap.get("actionText").getStringValue());
//                    if(!actionText.trim().isEmpty()){
//                        msgBuilder.setActionText(actionText);
//                    }
                            if (!eventName.trim().isEmpty()) {
                                msgBuilder.setEventName(eventName);
                            }
                            if (btnMap != null && !btnMap.isEmpty()) {
                                Struct param = ReturnMessage.Parameters.getInstance().getParams();//putFields("template", Value.newBuilder().setStringValue("button").build())
                                param = param.toBuilder().putFields("selectedButton", Value.newBuilder().setStructValue(Struct.newBuilder().putAllFields(btnMap).build()).build()).build();

                                msgBuilder.setParameters(param);
                                ReturnMessage.Parameters.getInstance().setParams(null);

                            }
                            ReturnMessage msg = msgBuilder.build();
                            callback.OnUserClickAction(msg);
                        }
                        break;
                    case "hyperlink":
                        Log.d(TAG, "onClick: hyperlink" + text);
                        final String linkType = btnMap.get("linkType").getStringValue();
                        final String navigateAndroid = btnMap.get("navigateAndroid").getStringValue();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage("Navigating to next screen will loose this chat session. Do you want to proceed?");

                        alertDialogBuilder
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Log.d(TAG, "clicked: YES");
                                                dialog.cancel();
                                                switch (linkType) {
                                                    case "internal":
                                                        try {
                                                            if (navigateAndroid != null && !navigateAndroid.trim().isEmpty()) {
                                                                Intent intent = new Intent(context, Class.forName(navigateAndroid));
                                                                context.startActivity(intent);
                                                            } else {
                                                                Toast.makeText(context, "Unable to navigate. Please try again later.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } catch (ClassNotFoundException e) {
                                                            e.printStackTrace();
                                                            Toast.makeText(context, "Unable to navigate. Please try again later.", Toast.LENGTH_SHORT).show();
                                                        }
                                                        break;
                                                    case "external":
                                                        if (navigateAndroid != null && !navigateAndroid.trim().isEmpty()) {
                                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigateAndroid));
                                                            context.startActivity(browserIntent);
                                                        } else {
                                                            Toast.makeText(context, "Unable to navigate. Please try again later.", Toast.LENGTH_SHORT).show();
                                                        }
                                                        break;
                                                }
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
                        break;
                }
                disableAllViews();
            }
        });
        setViewsToDisable(btn);
        return btn;
    }

    CheckBox getCheckBox(String text) {
        LinearLayout.LayoutParams checkBoxParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        CheckBox checkBox = new CheckBox(getContext());
        checkBox.setLayoutParams(checkBoxParam);
        checkBox.setButtonDrawable(getResources().getDrawable(R.drawable.chat_box_selector, theme)); //using as per existing app's ui style scheme. new one was pixelated on large screen sizes.
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            checkBox.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            checkBox.setText(Html.fromHtml(text));
        }
        checkBox.setTextSize(21.0f);
        checkBox.setPadding(8, 18, 8, 8);

        viewsToDisable.add(checkBox);
        return checkBox;
    }

}
