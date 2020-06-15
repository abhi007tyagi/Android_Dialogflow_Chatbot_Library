'use strict';


const express = require('express');
const bodyParser = require('body-parser');
const morganBody = require('morgan-body');
const morgan = require('morgan');

const handler = require('./handler/handler');

const app = express();
app.use(morgan(':method :url :status :res[content-length] --- :response-time ms'));

app.use(bodyParser.json());
morganBody(app, {maxBodyLength:100000});
app.use(bodyParser.urlencoded({ extended: true }));

/*
* Webhook to be exposed for fulfillment in dialogflow
*/

app.post('/webhook', (req, res) => {
    console.info(`\n\n>>>>>>> S E R V E R   H I T <<<<<<<`);
    handler.WebhookProcessor(req, res);
});

// GET
app.get('/', (req, res) => {
    console.info(`\n\n>>>>>>> S E R V E R   H I T   G E T <<<<<<<`);
    res.send('Server running OK');
});

app.listen(8080, () => {
    console.info(`Assistant webhook listening on port 8080!`);
});

module.exports.app = app;
