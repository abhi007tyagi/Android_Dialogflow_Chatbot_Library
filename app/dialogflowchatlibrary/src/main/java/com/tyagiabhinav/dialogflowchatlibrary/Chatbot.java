package com.tyagiabhinav.dialogflowchatlibrary;

import android.graphics.drawable.Drawable;

import com.tyagiabhinav.dialogflowchatlibrary.components.AppToolbar;

import androidx.appcompat.widget.Toolbar;

public class Chatbot {

    private AppToolbar mAppToolbar;
    private Drawable chatBotAvatar;
    private Drawable chatUserAvatar;
    private boolean showMic;
    private boolean doAutoWelcome;
    private boolean isDefaultColour;

    private Chatbot(ChatbotBuilder chatbotBuilder) {
        this.mAppToolbar = chatbotBuilder.mAppToolbar;
        this.chatBotAvatar = chatbotBuilder.chatBotAvatar;
        this.chatUserAvatar = chatbotBuilder.chatUserAvatar;
        this.showMic = chatbotBuilder.showMic;
        this.doAutoWelcome = chatbotBuilder.doAutoWelcome;
        this.isDefaultColour = false;
    }

    protected void setAppToolbar(Toolbar toolbar) {
        this.mAppToolbar = new AppToolbar();
        this.mAppToolbar.setToolbar(toolbar);
    }

    protected Toolbar getAppToolbar() {
        if (this.mAppToolbar != null) {
            return this.mAppToolbar.getToolbar();
        }
        return null;
    }

    public Drawable getChatBotAvatar() {
        return chatBotAvatar;
    }

    public void setChatBotAvatar(Drawable chatBotAvatar) {
        this.chatBotAvatar = chatBotAvatar;
    }

    public Drawable getChatUserAvatar() {
        return chatUserAvatar;
    }

    public void setChatUserAvatar(Drawable chatUserAvatar) {
        this.chatUserAvatar = chatUserAvatar;
    }

    protected void setMicAvailable(boolean showMic) {
        this.showMic = showMic;
    }

    protected boolean isMicAvailable() {
        return this.showMic;
    }

    protected void setAutoWelcome(boolean doAutoWelcome) {
        this.doAutoWelcome = doAutoWelcome;
    }

    protected boolean doAutoWelcome() {
        return this.doAutoWelcome;
    }

    public boolean isDefaultColour() {
        return this.isDefaultColour;
    }

    public static class ChatbotBuilder {
        private AppToolbar mAppToolbar;
        private Drawable chatBotAvatar;
        private Drawable chatUserAvatar;
        private boolean showMic;
        private boolean doAutoWelcome;

        public ChatbotBuilder() {
            this.mAppToolbar = null;
            this.chatBotAvatar = null;
            this.chatUserAvatar = null;
            this.showMic = false;
            this.doAutoWelcome = true;
        }

        public Chatbot build() {
            return new Chatbot(this);
        }

        public ChatbotBuilder setChatBotAvatar(Drawable chatBotAvatar) {
            this.chatBotAvatar = chatBotAvatar;
            return this;
        }

        public ChatbotBuilder setChatUserAvatar(Drawable chatUserAvatar) {
            this.chatUserAvatar = chatUserAvatar;
            return this;
        }

        public ChatbotBuilder setShowMic(boolean showMic) {
            this.showMic = showMic;
            return this;
        }

        public ChatbotBuilder setDoAutoWelcome(boolean doAutoWelcome) {
            this.doAutoWelcome = doAutoWelcome;
            return this;
        }
    }

}
