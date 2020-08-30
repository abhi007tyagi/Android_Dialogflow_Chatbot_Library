package com.tyagiabhinav.dialogflowchatlibrary.templates;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.protobuf.Value;
import com.tyagiabhinav.dialogflowchatlibrary.R;
import com.tyagiabhinav.dialogflowchatlibrary.networkutil.TaskRunner;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.OnClickCallback;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class CardMessageTemplate extends MessageLayoutTemplate {

    private static final String TAG = CardMessageTemplate.class.getSimpleName();

    public CardMessageTemplate(Context context, OnClickCallback callback, int type) {
        super(context, callback, type);
        setOnlyTextResponse(false);
    }

    @Override
    FrameLayout populateRichMessageContainer() {
        FrameLayout richMessageContainer = getRichMessageContainer();
        DetectIntentResponse response = getResponse();
        List<com.google.cloud.dialogflow.v2.Context> contextList = response.getQueryResult().getOutputContextsList();
        LinearLayout cardContainer = getVerticalContainer();

        final LinearLayout cardLayout = getCardLayout(null);
        LinearLayout btnLayout = getVerticalContainer();
        btnLayout.setFocusableInTouchMode(true);
        for (com.google.cloud.dialogflow.v2.Context context : contextList) {
            Map<String, Value> contextParam = context.getParameters().getFieldsMap();
            Map<String, Value> cardItems = (contextParam.get("cardItems") != null) ? contextParam.get("cardItems").getStructValue().getFieldsMap() : null;
            List<Value> buttonList = (contextParam.get("buttonItems") != null) ? contextParam.get("buttonItems").getListValue().getValuesList() : null;
            String align = context.getParameters().getFieldsMap().get("align").getStringValue();
            String sizeValue = context.getParameters().getFieldsMap().get("size").getStringValue();
            String eventName = context.getParameters().getFieldsMap().get("eventToCall").getStringValue();

            if (cardItems != null) {
                final String imgUrl = (cardItems.get("imgUrl") != null) ? cardItems.get("imgUrl").getStringValue() : null;
                String title = (cardItems.get("title") != null) ? cardItems.get("title").getStringValue() : null;
                String description = (cardItems.get("description") != null) ? cardItems.get("description").getStringValue() : null;
                TextView titleView = cardLayout.findViewById(R.id.title);
                TextView descriptionView = cardLayout.findViewById(R.id.description);
                titleView.setMovementMethod(LinkMovementMethod.getInstance());
                if (title != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        titleView.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        titleView.setText(Html.fromHtml(title));
                    }
                } else {
                    titleView.setVisibility(GONE);
                }

                if (description != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        descriptionView.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        descriptionView.setText(Html.fromHtml(description));
                    }
                } else {
                    descriptionView.setVisibility(GONE);
                }

                Runnable downloadImage = new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bmp = null;
                        try {
                            InputStream in = new java.net.URL(imgUrl).openStream();
                            bmp = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("Image download error", e.getMessage());
                            e.printStackTrace();
                        }
                        final Bitmap finalBmp = bmp;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (finalBmp != null) {
                                    ((ImageView) cardLayout.findViewById(R.id.img)).setImageBitmap(finalBmp);
                                } else {
                                    ((ImageView) cardLayout.findViewById(R.id.img)).setImageResource(R.drawable.error_image);
                                }
                            }
                        });
                    }
                };
                new TaskRunner().executeTask(downloadImage);
            }

            if (align.equalsIgnoreCase("horizontal") || align.equalsIgnoreCase("h")) {
                btnLayout = getHorizontalContainer();
            }

            if (buttonList != null) {
                for (Value item : buttonList) {
                    btnLayout.addView(getBtn("button", item.getStructValue().getFieldsMap(), sizeValue, eventName));
                }
            }
        }
        Log.d(TAG, "populateRichMessageContainer: btn layout count: " + btnLayout.getChildCount());

        cardContainer.addView(cardLayout);
        cardContainer.addView(btnLayout);
        richMessageContainer.addView(cardContainer);

        return richMessageContainer;
    }


}
