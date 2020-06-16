# Android Dialogflow ChatbotLibrary
Android Library to easily integrate Dialogflow based chatbots into an existing application with Chat screen.

## Steps
#### 1. Add dependencies and other entries to your project's gradle
dependencies
```

// Chatbot - Java V2
    implementation 'com.tyagiabhinav:androiddialogflowchatbot:0.1.0'
    implementation 'com.google.cloud:google-cloud-dialogflow:2.0.0'
    implementation 'io.grpc:grpc-okhttp:1.29.0'

```

packaging options under android tag
```

// Chatbot - Java V2
    packagingOptions {
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/DEPENDENCIES'
        exclude 'META-INF/INDEX.LIST'
    }

```


#### 2. Add ChatbotActivity to your projects Manifest file
```

<activity
            android:name="com.tyagiabhinav.dialogflowchatlibrary.ChatbotActivity"
            android:launchMode="singleTask"
            android:theme="@style/Theme.AppCompat.Light.NoActionBar" />

```


#### 3. Add Google Credential JSON file for Dialogflow Project
Generate a Google Credential JSON file for your Dialogflow Agent and 
save it under res->raw-><credential_file.json>



#### 4. Call ChatbotActivity
```

public void openChatbot(View view) {
        // provide your Dialogflow's Google Credential JSON saved under RAW folder in resources
        DialogflowCredentials.getInstance().setInputStream(getResources().openRawResource(R.raw.test_agent_credentials));

        ChatbotSettings.getInstance().setChatbot( new Chatbot.ChatbotBuilder()
//                .setDoAutoWelcome(false) // True by Default, False if you do not want the Bot to greet the user Automatically. Dialogflow agent must have a welcome intent to handle this
//                .setChatBotAvatar(getDrawable(R.drawable.avatarBot)) // provide avatar for your bot if default is not required
//                .setChatUserAvatar(getDrawable(R.drawable.avatarUser)) // provide avatar for your the user if default is not required
                .setShowMic(true) // False by Default, True if you want to use Voice input from the user to chat
                .build());
        Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
        Bundle bundle = new Bundle();

        // provide a UUID for your session with the Dialogflow agent
        bundle.putString(ChatbotActivity.SESSION_ID, UUID.randomUUID().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtras(bundle);
        startActivity(intent);
    }

```


#### 5. Replace default colours
In your application, add following colours and replace the default values
```

<color name="userAvatarBG">#666666</color>
    <color name="chatPrimary">#CC0000</color>
    <color name="chatPrimaryFocus">#DD0000</color>
    <color name="chatPrimaryPressed">#A60000</color>
    <color name="chatPrimaryDisabled">#66cc0000</color>
    <color name="chatBGnText">#FFFFFF</color>
    <color name="chatSecondary">#FFFFFF</color>
    <color name="chatSecondaryFocus">#F1F1F1</color>
    <color name="chatSecondaryPressed">#E9E9E9</color>
    <color name="chatSecondaryDisabled">#66FFFFFF</color>
    <color name="botBGBubbleStroke">#FFCCCC</color>
    <color name="botBGBubble">#FFCCCC</color>
    <color name="userBGBubbleStroke">#CAE4F5</color>
    <color name="userBGBubble">#CAE4F5</color>
    <color name="checkbox">#161616</color>
    <color name="statusBarColor">@color/chatPrimaryPressed</color>

```


#### 6. Add proguard rules to your project (if you don't get response from Chatbot)
```     
-keep public class com.google.api.gax.core.** {
  public protected *;
}
-keep public class com.google.auth.oauth2.** {
  public protected *;
}

```

