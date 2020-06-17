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
Note: Do not put parent activity as it may interfere with overridden back click alert dialog


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

-keep public class com.google.a** {
  public protected *;
}

```

#### 7. Interacting with webhook to show simple text, text message with buttons, checkboxes or navigating to other activities

## a. Sample Dialogflow Webhook NodeJS code 
Get the sample NodeJS webhook code from the SampleDialogflowWebhook

## b. Dialogflow Intent to capture events from Android app
- Create a common intent and assign an event name like "android_event"

![event setup in Dialogflow Intent](https://github.com/abhi007tyagi/Android_Dialogflow_Chatbot_Library/blob/master/SampleDialogflowWebhook/images_for_understanding/android_event.JPG)


- Enable webhook fulfillment for the intent

![webhook enable for Intent](https://github.com/abhi007tyagi/Android_Dialogflow_Chatbot_Library/blob/master/SampleDialogflowWebhook/images_for_understanding/Capture_Android_Event.JPG)


## c. Intents to send UI interaction with Android
Enable webhook fulfillment for all these intents

![webhook enable for Intent](https://github.com/abhi007tyagi/Android_Dialogflow_Chatbot_Library/blob/master/SampleDialogflowWebhook/images_for_understanding/Test%20Intent.JPG)

Code to send from webhook
```
    const params = <Depending on Interaction Type Change params here>;
    const param_context = {name: "param_context", lifespan: 10, parameters: params};
    agent.context.set(param_context);
    agent.add('This is a message section for showing text'); // Text message to be shown to the user

```
- Simple Text
 ```
    const params = {"template": "text"};
 ```
![ANdroid UI for Simple Text](https://github.com/abhi007tyagi/Android_Dialogflow_Chatbot_Library/blob/master/SampleDialogflowWebhook/images_for_understanding/text_message.JPG)

- Text with Buttons
 ```
    const params = {"template": "button", "buttonItems":[{"uiText":"Action 1", "actionText":"action 1 selected", "isPositive": true},{"uiText":"Action 2", "actionText":"action 2 selected", "isPositive": false}], "align": "h", "size":"l", "eventToCall":"android_event" };
 ```
![Android UI for messages with Buttons](https://github.com/abhi007tyagi/Android_Dialogflow_Chatbot_Library/blob/master/SampleDialogflowWebhook/images_for_understanding/button_message.JPG)

  captured response in the event capture intent
```
    {
    				"name": "projects/<project name>/agent/sessions/959b7153-f21a-4bfa-b154-a3f97a6a79d8/contexts/android_event",
    				"parameters": {
    					"selectedButton": {
    						"isPositive": true,
    						"actionText": "action 1 selected",
    						"uiText": "Action 1"
    					}
    				}
    			}
```    

- Text with Checkboxes
 ```
    const params = {"template": "checkbox", "items":[{"uiText":"item 1<br> this item is best", "id":"1"},{"uiText":"item 2<br> this item is OK", "id":"2"},{"uiText":"item 3", "id":"3"}], "buttonItems":[{"uiText":"Yes", "actionText":"process selected", "isPositive": true},{"uiText":"No", "actionText":"cancel", "isPositive": false}], "align": "h", "size":"l", "eventToCall":"android_event" };
 ```  
 ![Android UI for messages with Checkbox](https://github.com/abhi007tyagi/Android_Dialogflow_Chatbot_Library/blob/master/SampleDialogflowWebhook/images_for_understanding/checkbox_message.JPG)
 
  captured response in the event capture intent
```
    {
    				"name": "projects/<project name>/agent/sessions/959b7153-f21a-4bfa-b154-a3f97a6a79d8/contexts/android_event",
    				"parameters": {
    					"selectedButton": {
    						"uiText": "Yes",
    						"isPositive": true,
    						"actionText": "process selected"
    					},
    					"template": "checkbox",
    					"selectedItems": [
    						{
    							"id": "1",
    							"uiText": "item 1<br> this item is best"
    						}
    					]
    				}
    			}
```     
  
- Text with Buttons to Navigate to other activities or URL
```
    const params = {"template": "hyperlink", "linkItems":[{"uiText":"Next Activity", "linkType":"internal", "navigateAndroid":"com.tyagiabhinav.dialogflowchat.NavTestActivity", "navigateIOS":"", "isPositive": true},{"uiText":"Google", "linkType":"external", "navigateAndroid":"http://www.google.com", "navigateIOS":"http://www.google.com", "isPositive": false}], "align": "v", "size":"l", "eventToCall":"android_event" };
```  
![Android UI for messages with Navigation Buttons](https://github.com/abhi007tyagi/Android_Dialogflow_Chatbot_Library/blob/master/SampleDialogflowWebhook/images_for_understanding/navigate_message.JPG)
      
- params attribute information

  - template - to show what UI you want on Android chat
  - *items - list of interactable UI components  
  - uiText - text shown for the item on Android like Button text, Text for checkbox. Simple HTML formatting will work for Checkboxes
  - actionText - text to be shown when a button is clicked as input from user in the chat
  - isPositive - for colour scheme
  - align - for horzontal or vertical layout of buttons. takes values h/v
  - size - text size for button. takes values s,m,l (s=10, m=14, l=18)  
  - eventToCall - event name provided in the Dialogflow Intent to capture user interaction result from Android like "android_event"
  - id - unique number for identification
  - linkType - within app or outside app navigation. takes value internal/external
  - navigateAndroid - complete class name or URL to navigate
  
    

