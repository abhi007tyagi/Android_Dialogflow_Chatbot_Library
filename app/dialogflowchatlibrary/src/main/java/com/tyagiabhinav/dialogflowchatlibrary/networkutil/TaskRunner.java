package com.tyagiabhinav.dialogflowchatlibrary.networkutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;

import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.grpc.util.GracefulSwitchLoadBalancer;

public class TaskRunner {

    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    private ChatbotCallback chatbotCallback;
    private SessionName session;
    private SessionsClient sessionsClient;
    private QueryInput queryInput;
    private Handler handler = new Handler(Looper.getMainLooper());

    public TaskRunner(ChatbotCallback callback, SessionName session, SessionsClient sessionsClient, QueryInput queryInput) {
        this.chatbotCallback = callback;
        this.session = session;
        this.sessionsClient = sessionsClient;
        this.queryInput = queryInput;
    }

    public TaskRunner() {
    }

    public void executeChat() {
        Log.i("Task Runner", "execute");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DetectIntentResponse response = null;
                try {
                    DetectIntentRequest detectIntentRequest =
                            DetectIntentRequest.newBuilder()
                                    .setSession(session.toString())
                                    .setQueryInput(queryInput)
                                    .build();
                    response = sessionsClient.detectIntent(detectIntentRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final DetectIntentResponse finalResponse = response;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        chatbotCallback.OnChatbotResponse(finalResponse);
                    }
                });
            }
        });
    }

    public void executeTask(Runnable task) {
        Log.d("Task Runner", "task");
        executor.execute(task);
    }
}
