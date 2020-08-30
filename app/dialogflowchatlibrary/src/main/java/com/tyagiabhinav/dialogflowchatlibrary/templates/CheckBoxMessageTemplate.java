package com.tyagiabhinav.dialogflowchatlibrary.templates;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.tyagiabhinav.dialogflowchatlibrary.R;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.OnClickCallback;
import com.tyagiabhinav.dialogflowchatlibrary.templateutil.ReturnMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckBoxMessageTemplate extends MessageLayoutTemplate {

    private static final String TAG = ButtonMessageTemplate.class.getSimpleName();

    private boolean isViewCollapsed;
    private ImageView collapseBtn;

    public CheckBoxMessageTemplate(Context context, OnClickCallback callback, int type) {
        super(context, callback, type);
        setOnlyTextResponse(false);
    }

    @Override
    FrameLayout populateRichMessageContainer() {
        FrameLayout richMessageContainer = getRichMessageContainer();
        DetectIntentResponse response = getResponse();
        List<com.google.cloud.dialogflow.v2.Context> contextList = response.getQueryResult().getOutputContextsList();

        LinearLayout checkboxContainer = getVerticalContainer();
        LinearLayout checkboxLayout = getCheckBoxContainer();
        final LinearLayout checkboxItemLayout = checkboxLayout.findViewById(R.id.checkboxItems);
        collapseBtn = checkboxLayout.findViewById(R.id.collapseBtn);
        collapseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isViewCollapsed) {
                    checkboxItemLayout.setVisibility(View.VISIBLE);
                    collapseBtn.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down, theme));
                    isViewCollapsed = false;
                } else {
                    checkboxItemLayout.setVisibility(View.GONE);
                    collapseBtn.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up, theme));
                    isViewCollapsed = true;
                }
            }
        });

        LinearLayout btnLayout = getVerticalContainer();
//        btnLayout.setGravity(Gravity.CENTER);
        btnLayout.setFocusableInTouchMode(true);

        final ReturnMessage.Parameters parameters = ReturnMessage.Parameters.getInstance();
        Struct params = parameters.getParams();
        final Map<String, Value> selectedItems = new HashMap<>();
        params = params.toBuilder().putFields("template", Value.newBuilder().setStringValue("checkbox").build()).build();
        parameters.setParams(params);

        for (com.google.cloud.dialogflow.v2.Context context : contextList) {
            Map<String, Value> contextParam = context.getParameters().getFieldsMap();
            List<Value> list = (contextParam.get("checkboxItems") != null) ? contextParam.get("checkboxItems").getListValue().getValuesList() : null;
            List<Value> buttonList = (contextParam.get("buttonItems") != null) ? contextParam.get("buttonItems").getListValue().getValuesList() : null;
            String align = contextParam.get("align").getStringValue();
            String sizeValue = contextParam.get("size").getStringValue();
            String eventName = contextParam.get("eventToCall").getStringValue();

            if (align.equalsIgnoreCase("horizontal") || align.equalsIgnoreCase("h")) {
                btnLayout = getHorizontalContainer();
            }

            if (list != null) {
                if (list.size() > 1) {
                    checkboxItemLayout.setVisibility(View.VISIBLE);
                    collapseBtn.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down, theme));
                    isViewCollapsed = false;
                } else {
                    checkboxItemLayout.setVisibility(View.GONE);
                    collapseBtn.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up, theme));
                    isViewCollapsed = true;
                }
                if (list.size() > 0) {
                    for (final Value chkItem : list) {
                        final Map<String, Value> itemInfo = chkItem.getStructValue().getFieldsMap();
                        CheckBox checkBox = getCheckBox(itemInfo.get("uiText").getStringValue());
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                // TODO add items
                                Struct tempParam;
                                if (isChecked) {
                                    // checked
                                    selectedItems.put(itemInfo.get("id").getStringValue(), chkItem);
                                } else {
                                    // unchecked
                                    selectedItems.remove(itemInfo.get("id").getStringValue());
                                }
                                List<Value> allItems = new ArrayList<>(selectedItems.values());
                                tempParam = parameters.getParams().toBuilder().putFields("selectedItems", Value.newBuilder().setListValue(ListValue.newBuilder().addAllValues(allItems).build()).build()).build();
                                parameters.setParams(tempParam);
                            }
                        });
                        checkboxItemLayout.addView(checkBox);
                    }
                }
            }

            if (buttonList != null && !buttonList.isEmpty()) {
                for (Value btnItem : buttonList) {
                    btnLayout.addView(getBtn("button", btnItem.getStructValue().getFieldsMap(), sizeValue, eventName));
                }
            }
        }
        Log.d(TAG, "populateRichMessageContainer: btn layout count: " + btnLayout.getChildCount());

        checkboxContainer.addView(checkboxLayout);
        checkboxContainer.addView(btnLayout);
        richMessageContainer.addView(checkboxContainer);

        return richMessageContainer;
    }
}
