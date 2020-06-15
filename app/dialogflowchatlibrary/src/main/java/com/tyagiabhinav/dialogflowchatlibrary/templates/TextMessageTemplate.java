package com.tyagiabhinav.dialogflowchatlibrary.templates;

import android.content.Context;
import android.widget.FrameLayout;

import com.tyagiabhinav.dialogflowchatlibrary.templateutil.OnClickCallback;

public class TextMessageTemplate extends MessageLayoutTemplate {

    public TextMessageTemplate(Context context, OnClickCallback callback, int type) {
        super(context, callback, type);
    }

    @Override
    FrameLayout populateRichMessageContainer() {
        return null;
    }

}
