package com.tyagiabhinav.dialogflowchatlibrary.templateutil;

import com.google.protobuf.Struct;

public class ReturnMessage {

    private String actionText;
    private String eventName;
    private Struct parameters;

    private ReturnMessage(MessageBuilder msgBuilder) {
        this.actionText = msgBuilder.actionText;
        this.eventName = msgBuilder.eventName;
        this.parameters = msgBuilder.parameters;
    }

    public String getActionText() {
        return actionText;
    }

    public Struct getParam() {
        return parameters;
    }

    public String getEventName() {
        return eventName;
    }

    public static class MessageBuilder {

        private String actionText;
        private String eventName;
        private Struct parameters;

        public MessageBuilder(String actionText) {
            this.actionText = actionText;
        }

        public MessageBuilder(String actionText, Struct parameters) {
            this.actionText = actionText;
            this.parameters = parameters;
        }

        public MessageBuilder(String actionText, String eventName) {
            this.actionText = actionText;
            this.eventName = eventName;
        }

        public MessageBuilder(String actionText, String eventName, Struct parameters) {
            this.actionText = actionText;
            this.eventName = eventName;
            this.parameters = parameters;
        }

        public MessageBuilder setActionText(String actionText) {
            this.actionText = actionText;
            return this;
        }

        public MessageBuilder setParameters(Struct parameters) {
            this.parameters = parameters;
            return this;
        }

        public MessageBuilder setEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public ReturnMessage build() {
            ReturnMessage msg = new ReturnMessage(this);
            return msg;
        }
    }

    public static class Parameters {

        private static Parameters mParams;
        private Struct mParameters;

        private Parameters() {
            //Prevent form the reflection api.
            if (mParams != null) {
                throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
            }
        }


        public static Parameters getInstance() {
            if (mParams == null) { //if there is no instance available... create new one
                synchronized (Parameters.class) {
                    if (mParams == null) mParams = new Parameters();
                }
            }
            return mParams;
        }

        //Make singleton from serialize and deserialize operation.
        protected Parameters readResolve() {
            return getInstance();
        }


        public void setParams(Struct parameters) {
            mParameters = parameters;
        }

        public Struct getParams() {
            if (mParameters == null) {
                mParameters = Struct.newBuilder().build();
            }
            return mParameters;
        }
    }
}
