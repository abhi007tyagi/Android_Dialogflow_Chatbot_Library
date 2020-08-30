package com.tyagiabhinav.dialogflowchatlibrary.templates;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.OnClickCallback;
import com.google.protobuf.Value;

import java.util.List;
import java.util.Map;

public class HyperLinkTemplate extends MessageLayoutTemplate {

    private static final String TAG = HyperLinkTemplate.class.getSimpleName();

    public HyperLinkTemplate(Context context, OnClickCallback callback, int type) {
        super(context, callback, type);
        setOnlyTextResponse(false);
    }

    @Override
    FrameLayout populateRichMessageContainer() {
        FrameLayout richMessageContainer = getRichMessageContainer();
        DetectIntentResponse response = getResponse();
        List<com.google.cloud.dialogflow.v2.Context> contextList = response.getQueryResult().getOutputContextsList();

        LinearLayout btnLayout = getVerticalContainer();
//        btnLayout.setGravity(Gravity.CENTER);
        btnLayout.setFocusableInTouchMode(true);
        for (com.google.cloud.dialogflow.v2.Context context : contextList) {
            Map<String, Value> contextParam = context.getParameters().getFieldsMap();
            List<Value> hyperLinkList = (contextParam.get("linkItems") != null) ? contextParam.get("linkItems").getListValue().getValuesList() : null;
            String align = context.getParameters().getFieldsMap().get("align").getStringValue();
            String sizeValue = context.getParameters().getFieldsMap().get("size").getStringValue();
//            String eventName = context.getParameters().getFieldsMap().get("eventToCall").getStringValue();

            if (align.equalsIgnoreCase("horizontal") || align.equalsIgnoreCase("h")) {
                btnLayout = getHorizontalContainer();
            }

            if (hyperLinkList != null) {
                for (Value item : hyperLinkList) {
                    btnLayout.addView(getBtn("hyperlink", item.getStructValue().getFieldsMap(), sizeValue, null));
                }
            }
        }
        Log.d(TAG, "populateRichMessageContainer: btn layout count: " + btnLayout.getChildCount());
        richMessageContainer.addView(btnLayout);

        return richMessageContainer;
    }


}
