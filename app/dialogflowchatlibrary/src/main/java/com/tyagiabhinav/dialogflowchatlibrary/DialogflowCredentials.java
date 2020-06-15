package com.tyagiabhinav.dialogflowchatlibrary;

import java.io.InputStream;

public class DialogflowCredentials {

    private static DialogflowCredentials mCredentials;
    private InputStream mInputStream;

    private DialogflowCredentials() {
        //Prevent form the reflection api.
        if (mCredentials != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }


    public static DialogflowCredentials getInstance() {
        if (mCredentials == null) { //if there is no instance available... create new one
            synchronized (DialogflowCredentials.class) {
                if (mCredentials == null) mCredentials = new DialogflowCredentials();
            }
        }
        return mCredentials;
    }

    //Make singleton from serialize and deserialize operation.
    protected DialogflowCredentials readResolve() {
        return getInstance();
    }


    public void setInputStream(InputStream is) {
        mInputStream = is;
    }

    protected InputStream getInputStream() {
        return mInputStream;
    }
}
