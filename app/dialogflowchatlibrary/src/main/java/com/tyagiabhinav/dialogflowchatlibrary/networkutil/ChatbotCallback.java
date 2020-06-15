package com.tyagiabhinav.dialogflowchatlibrary.networkutil;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;

public interface ChatbotCallback {
    void OnChatbotResponse(DetectIntentResponse response);
}
