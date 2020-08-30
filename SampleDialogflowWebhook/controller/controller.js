'use strict';

/**
 * Intent to send UI controls to Android
 */
const androidIntent = (agent) => {

    const params = {"template": "text"};
    // const params = {"template": "button", "buttonItems":[{"uiText":"Action 1", "actionText":"action 1 selected", "isPositive": true},{"uiText":"Action 2", "actionText":"action 2 selected", "isPositive": false}], "align": "h", "size":"l", "eventToCall":"android_event" };
    // const params = {"template": "checkbox", "checkboxItems":[{"uiText":"item 1<br> this item is best", "id":"1"},{"uiText":"item 2<br> this item is OK", "id":"2"},{"uiText":"item 3", "id":"3"}], "buttonItems":[{"uiText":"Yes", "actionText":"process selected", "isPositive": true},{"uiText":"No", "actionText":"cancel", "isPositive": false}], "align": "h", "size":"l", "eventToCall":"android_event" };
    // const params = {"template": "card", "cardItems":{"imgUrl":"https://picsum.photos/seed/picsum/900/500", "title":"<b>Image Title</b><i>(optional)</i>", "description":"<i>Image description. (optional)</i>"}, "buttonItems":[{"uiText":"Yes", "actionText":"process selected", "isPositive": true},{"uiText":"No", "actionText":"cancel", "isPositive": false}], "align": "h", "size":"l", "eventToCall":"android_event" };
    // const params = {"template": "carousel", "carouselItems":[{"id":"1","imgUrl":"https://loremflickr.com/900/500/dog", "title":"<b>Image Title</b><i>(optional)</i>", "description":"<i>Image description. (optional)</i>","toast":"selected Dog"},{"id":"2","imgUrl":"https://loremflickr.com/900/500/cat", "title":"<b>Image Title</b><i>(optional)</i>","toast":"selected Cat", "description":"<i>Image description. (optional)</i>"},{"id":"3","imgUrl":"https://loremflickr.com/900/500/owl", "title":"<b>Image Title</b><i>(optional)</i>", "description":"<i>Image description. (optional)</i>","toast":"selected Owl"}], "buttonItems":[{"uiText":"Select", "actionText":"process selected", "isPositive": true},{"uiText":"Cancel", "actionText":"cancel", "isPositive": false}], "align": "h", "size":"l", "eventToCall":"android_event" };
    // const params = {"template": "hyperlink", "linkItems":[{"uiText":"Next Activity", "linkType":"internal", "navigateAndroid":"com.tyagiabhinav.dialogflowchat.NavTestActivity", "navigateIOS":"", "isPositive": true},{"uiText":"Google", "linkType":"external", "navigateAndroid":"http://www.google.com", "navigateIOS":"http://www.google.com", "isPositive": false}], "align": "v", "size":"l" };
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
    androidIntent,
    androidEvent
};
