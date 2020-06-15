# Android Dialogflow ChatbotLibrary
Android Library to easily integrate Dialogflow based chatbots into an existing application with Chat screen.

## Steps
#### 1. Add module to your project's gradle


#### 2. Add ChatbotActivity to your projects Manifest file


#### 3. Add Google Credential JSON file for Dialogflow Project


#### 4. Call ChatbotActivity


#### 5. Replace default colours


#### 6. Add proguard rules to your project (if you don't get response from Chatbot)
```     
-keep public class com.google.api.gax.core.** {
  public protected *;
}
-keep public class com.google.auth.oauth2.** {
  public protected *;
}
```

