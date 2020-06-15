package com.tyagiabhinav.dialogflowchatlibrary;


import android.graphics.drawable.Drawable;

import com.tyagiabhinav.dialogflowchatlibrary.components.AppToolbar;

import androidx.appcompat.widget.Toolbar;

public class ChatbotSettings {
    private static final ChatbotSettings ourInstance = new ChatbotSettings();


    private static ChatbotSettings mSettings;
    private Chatbot mChatbot;

    private ChatbotSettings() {
        //Prevent form the reflection api.
        if (mSettings != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static ChatbotSettings getInstance() {
        if (mSettings == null) { //if there is no instance available... create new one
            synchronized (DialogflowCredentials.class) {
                if (mSettings == null) mSettings = new ChatbotSettings();
            }
        }
        return mSettings;
    }

    //Make singleton from serialize and deserialize operation.
    protected ChatbotSettings readResolve() {
        return getInstance();
    }

    public Chatbot getChatbot() {
        return mChatbot;
    }

    public void setChatbot(Chatbot mChatbot) {
        this.mChatbot = mChatbot;
    }

    public void setAppToolbar(Toolbar toolbar) {
        this.mChatbot.setAppToolbar(toolbar);
    }

    public Toolbar getAppToolbar() {
        return this.mChatbot.getAppToolbar();
    }

    public void setChatBotAvatar(Drawable chatBotAvatar) {
        this.mChatbot.setChatBotAvatar(chatBotAvatar);
    }

    public Drawable getChatBotAvatar() {
        return this.mChatbot.getChatBotAvatar();
    }

    public void setChatUserAvatar(Drawable chatUserAvatar) {
        this.mChatbot.setChatUserAvatar(chatUserAvatar);
    }

    public Drawable getChatUserAvatar() {
        return this.mChatbot.getChatUserAvatar();
    }

    public boolean isMicAvailable() {
        return this.mChatbot.isMicAvailable();
    }

    public void setMicAvailability(boolean showMic) {
        this.mChatbot.setMicAvailable(showMic);
    }

    public boolean isAutoWelcome() {
        return this.mChatbot.doAutoWelcome();
    }

    public void setAutoWelcome(boolean showWelcomeMessage) {
        this.mChatbot.setAutoWelcome(showWelcomeMessage);
    }
}
