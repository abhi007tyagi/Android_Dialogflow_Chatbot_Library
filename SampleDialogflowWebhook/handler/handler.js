'use strict';

const {WebhookClient} = require('dialogflow-fulfillment');
const controller = require('../controller/controller');

/**
 * Function to initialize WebhookClient to handle the communication with Dialogflow's webhook
 * and to map the incoming intent to specific fulfillmentController
 * @param {Object} req Express HTTP request object
 * @param {Object} res Express HTTP response object
 */

const WebhookProcessor = (req, res) => {

    /**
     * Constructor for WebhookClient object
     * @param {Object} options JSON configuration
     * @param {Object} options.request Express HTTP request object
     * @param {Object} options.response Express HTTP response object
     */
    const agent = new WebhookClient({request: req, response: res});
    const intentMap = new Map();

    // Run the proper function handler based on the matched Dialogflow intent name
    intentMap.set('Android Intent', controller.androidIntent);
    intentMap.set('Capture Android Event', controller.androidEvent);

    agent.handleRequest(intentMap);
};

module.exports = {
    WebhookProcessor
};
