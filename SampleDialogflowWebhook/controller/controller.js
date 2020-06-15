'use strict';

/**
 * Intent to send UI controls to Android
 */
const androidIntent = (agent) => {

    // const params = {"template": "text"};
    // const params = {"template": "button", "buttonItems":[{"uiText":"Yes", "actionText":"Yes", "isPositive": true},{"uiText":"No", "actionText":"No", "isPositive": false}], "align": "h", "size":"l", "eventToCall":"android_event" };
    // const params = {"template": "hyperlink", "linkItems":[{"uiText":"Easy action", "linkType":"internal", "navigateAndroid":"com.tyagiabhinav.dialogflowchat.NavTestActivity", "navigateIOS":"", "isPositive": true},{"uiText":"Next Screen", "linkType":"external", "navigateAndroid":"http://www.google.com", "navigateIOS":"http://www.google.com", "isPositive": false}], "align": "v", "size":"l", "eventToCall":"android_event" };
    const params = {"template": "checkbox", "items":[{"uiText":"item 1<br> this item is best", "id":"1"},{"uiText":"item 2<br> this item is OK", "id":"2"},{"uiText":"item 3", "id":"3"}], "buttonItems":[{"uiText":"Yes", "actionText":"Yahoo", "isPositive": true},{"uiText":"No", "actionText":"Nooo", "isPositive": false}], "align": "h", "size":"l", "eventToCall":"android_event" };
    const param_context = {name: "param_context", lifespan: 10, parameters: params};
    agent.context.set(param_context);
    agent.add('This is a message section for showing text');
};

/**
 * Intent to send UI controls to Android
 */
const androidEvent = (agent) => {
    const params = {"template": "text"};
    const param_context = {name: "param_context", lifespan: 10, parameters: params};
    agent.context.set(param_context);
    agent.add('Event captured successfully!');
};

module.exports = {
    fallback,
    androidIntent,
    androidEvent
};
