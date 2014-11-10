package com.android.moment.moment.network;

import android.util.Log;

import com.android.moment.moment.net.core.handler.MessageHandler;
import com.android.moment.moment.net.core.message.Message;
import com.android.moment.moment.net.core.message.PushMessage;
import com.android.moment.moment.net.model.component.ResourcePath;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


/**
 * Created by eluleci on 07/11/14.
 */
public class WebSocketClient {

    //List of the message handlers which are waiting for the response to their requests.
    private Map<Integer, MessageHandler> messageQueue = new HashMap<Integer, MessageHandler>();
    private Map<String, MessageHandler> pushMessageSubscribers = new HashMap<String, MessageHandler>();

    //////

    private final String wsuri = "ws://10.0.2.137:8080/ws";

    private static WebSocketClient instance;

    private final String TAG = "WebSocketClient";
    private final WebSocketConnection mConnection = new WebSocketConnection();
    private ConnectionStatusListener connectionStatusListener;

    private Map<String, MessageHandler> referenceMessageHandlers = new HashMap<String, MessageHandler>();
    private Map<Integer, MessageHandler> messageHandlers = new HashMap<Integer, MessageHandler>();

    private SecureRandom random = new SecureRandom();

    public static WebSocketClient getInstance() {
        if (instance == null) instance = new WebSocketClient();
        return instance;
    }

    private WebSocketClient() {
        connect();
    }


    private void connect() {

        Log.d(TAG, "Connecting...");

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    connectionStatusListener.onStatusChanged(true);
                    Log.d(TAG, "Status: Connected to " + wsuri);
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "Got message: " + payload);

                    onTextMessageReceived(payload);

                    /*Message message = new Message();
                    try {
                        JSONObject jsonObject = new JSONObject(payload);
                        if (jsonObject.has("rid"))
                            message.setRid(jsonObject.getInt("rid"));
                        if (jsonObject.has("status"))
                            message.setStatus(jsonObject.getInt("status"));
                        if (jsonObject.has("body"))
                            message.setBody(jsonObject.getJSONObject("body"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Converted to object : " + message);

                    if (message.getRid() == 0) {
                        Log.d(TAG, "this is push message");
                    }
                    if (message.getRid() != 0 && messageHandlers.containsKey(message.getRid())) {
                        Log.d(TAG, "message handler found with rid: " + message.getRid());

                        onMessageReceived(message);
                    }*/
                }

                @Override
                public void onClose(int code, String reason) {
                    connectionStatusListener.onStatusChanged(false);
                    Log.e(TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }
    }

    /**
     * Sends a request message to the server.
     *
     * @param requestMessageHandler is the handler that calls this method.
     */
    public synchronized void sendMessage(MessageHandler requestMessageHandler) {
        if (requestMessageHandler == null) {
            throw new IllegalArgumentException("Parameter requestMessageHandler must not be null.");
        }

        // getting prepared message from handler and generating tx for message
        Message message = requestMessageHandler.prepareMessage();
        if (message == null) {
            Log.e(TAG, "Handlers message is null while sending message. "
                    + requestMessageHandler.getClass().toString());
            return;
        }
//        message.setRid(generateNextTx());
        message.setRid(new BigInteger(130, random).intValue());

        // mapping message handler with tx value and sending message
        messageQueue.put(message.getRid(), requestMessageHandler);
        mConnection.sendTextMessage(message.toString());

        Log.d(TAG, "Sending a request message: " + message.toString());
    }

    public void onTextMessageReceived(final String payload) {

        try {
            final JSONObject message = new JSONObject(payload);
            Runnable runnable;
            if (message.has("rid")) {
                // If a message has field 'tx', it means that this is a respond
                // to a request that is sent by this client. First we find the
                // message handler which owns the message and then we check for
                // error and notify the message handler. Finally we remove the
                // message handler from the message queue.
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            Log.v(TAG, "MESSAGE RECEIEVED: " + message.toString(4));
                            handleResponse(message);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getLocalizedMessage(), e);
                        }
                    }
                };

            } else {
                // If there is no 'cid' or 'tx' in the message, it means that this is a push message
                // So we first check the subscription id of it and handle errors if the
                // subscription id is not valid. Then we find the MessageHandler which has
                // subscribed with this subscription id. If everything goes well, we just simply
                // create the PushMessage object from the values we get and notify the handler.
                runnable = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Log.v(TAG, "PUSH MESSAGE RECEIEVED: " + message.toString(4));
                            handlePushMessage(message);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getLocalizedMessage(), e);
                        }

                    }

                };
            }
//            if (multithreaded) {
//                new Thread(runnable).start();
//            } else {
            runnable.run();
//            }

        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }


    /**
     * handles a message as response of a request, particularly it has field RID
     *
     * @param message the message from server
     * @throws org.json.JSONException
     */
    private void handleResponse(JSONObject message) throws JSONException, NullPointerException {
        int rid = message.getInt("rid");
        MessageHandler messageHandler = messageQueue.get(rid);

        if (messageHandler == null) {
            Log.e(TAG, "MessageHandler with tx " + rid + " couldn't be found.");
            return;
        }

        if (message.has("error")) { // handling error

//            JSONObject errorObject = message.getJSONObject(ERROR);
//            int code = errorObject.getInt(CODE);
//            String errorMessage = errorObject.getString(MESSAGE);

//            MessageError error = new MessageError(code, errorMessage);
//            Log.e(TAG, "Error returned. Message was " + messageHandler.prepareMessage() + "   Error:" + error.toString());
            Log.e(TAG, "Error returned. Message was " + messageHandler.prepareMessage() + "   Error:" + message.toString());

//            messageHandler.onError(messageHandler, error);
            messageHandler.onError(messageHandler, null);

        } else { // getting message

            Message.Builder messageBuilder = new Message.Builder();

            // if there is opts, add to message builder
            /*if (message.has(OPTS)) {
                JSONObject opts = message.getJSONObject(OPTS);
                ResponseOptions messageOptions = new ResponseOptions();

                // if message has subscription , add it to subscribers list
                if (opts.has(SUBSCRIPTION)) {
                    String subscription = opts.getString(SUBSCRIPTION);

                    // sometimes subscription tag comes with the string value "null", or it is simply the answer
                    if (!subscription.equals("null") && subscription != null && !(opts.has(CMD) &&
                            Message.Command.valueOf(opts.getString(CMD)) != Message.Command.UNSUBSCRIBE)) {
                        bindSubscription(subscription, messageHandler);
                        messageOptions.setSubscription(subscription);

                        Log.d(TAG, "Handler is subscribed with id " + subscription);
                    }
                }

                messageBuilder.opts(messageOptions);
            }*/

            // if message has subscription , add it to subscribers list
            if (message.has("subscription")) {
                String subscription = message.getString("subscription");

                // sometimes subscription tag comes with the string value "null", or it is simply the answer
                if (!subscription.equals("null") && subscription != null) {
                    messageHandler.setSubscriptionId(subscription);
                    bindSubscription(subscription, messageHandler);
                }
            }

            // if there is body, add to message builder
            if (message.has("body")) {
                JSONObject body = message.getJSONObject("body");
                messageBuilder.body(body);
            }

            // if there is metadata, add to message builder
            /*if (message.has(METADATA)) {
                JSONObject metadata = message.getJSONObject(METADATA);
                messageBuilder.metadata(metadata);
            }*/

            if (message.has("cmd")) {
                Message.Command masterCmd = Message.Command.parseCommand(message.getString("cmd"));
                messageBuilder.cmd(masterCmd);
            }

            Message responseMessage = messageBuilder.build();
            messageHandler.applyResponse(responseMessage);

            messageQueue.remove(rid);
        }
    }

    /**
     * handles a push message
     *
     * @param message the message from server
     * @throws org.json.JSONException
     */
    private void handlePushMessage(JSONObject message) throws JSONException {
//        if (message.has(OPTS)) {
//            JSONObject opts = message.getJSONObject(OPTS);
        JSONObject opts = message;

        if (opts.has("subscription")) {
            String subscriptionId = opts.getString("subscription");

            if (subscriptionId == null) {
                // Every push message must have a valid subscription id.
                Log.e(TAG, "Subscription id of push message is null");
                return;
            }

            if (!pushMessageSubscribers.containsKey(subscriptionId)) {
                // It means we have subscription which has no handler.
                Log.e(TAG, "Push message has no handler for id " + subscriptionId + "\n"
                        + message.toString());
                return;
            }

            MessageHandler messageHandler = pushMessageSubscribers.get(subscriptionId);

            PushMessage.Builder pushMessageBuilder = new PushMessage.Builder();
            pushMessageBuilder.subscription(subscriptionId);

            // add self subscription if have one (used for new creation of read request)
//                if (opts.has(SELF_SUBSCRIPTION) && !opts.getString(SELF_SUBSCRIPTION).equals("null")) {
//                    pushMessageBuilder.selfSubscription(opts.getString(SELF_SUBSCRIPTION));
//                }

            Message.Command masterCmd = null;
            ResourcePath masterRes = null;
            JSONObject body = null;

            if (message.has("cmd")) {
                masterCmd = Message.Command.parseCommand(message.getString("cmd"));
                pushMessageBuilder.cmd(masterCmd);
            } else {
                Log.e(TAG, "There is no command tag inside push message.");
                return;
            }

            // delete message doesn't have 'res'
            if (masterCmd != Message.Command.DELETE && message.has("res")) {
                masterRes = ResourcePath.generateResourcePath(message.getString("res"));
                pushMessageBuilder.res(masterRes);
            }

            if (message.has("body")) {
                body = message.getJSONObject("body");

                // setting body
                pushMessageBuilder.body(body);

                // setting 'res' of body
                    /*if (body.has(_RES)) {
                        ResourcePath res = ResourcePath.generateResourcePath(body.getString(_RES));
                        pushMessageBuilder.bodyRes(res);
                        //we also set the messages res to body's res for intuitive use
                        pushMessageBuilder.res(res);
                    }*/
                    /*if (body.has(OPS)) {
                        JSONArray operationsArrayObject = body.getJSONArray(OPS);

                        ArrayList<PushOperation> ops = new ArrayList<PushOperation>();
                        for (int i = 0; i < operationsArrayObject.length(); i++) {

                            JSONObject op = operationsArrayObject.getJSONObject(i);
                            PushOperation o = new PushOperation();

                            if (op.has(CMD))
                                o.setCmd(Message.Command.parseCommand(op.getString(CMD)));

                            o.setField(Field.parseField(op.getString(FIELD)));
                            o.setValue(op.get(VALUE));
                            ops.add(o);
                        }
                        pushMessageBuilder.ops(ops);
                    } else if (masterCmd.equals(Message.Command.CREATE)) {
                        List<PushOperation> ops = new ArrayList<PushOperation>();
                        PushOperation operation = new PushOperation();
                        operation.setCmd(Message.Command.CREATE);
                        operation.setField(Field.NONE);
                        operation.setValue(body);
                        ops.add(operation);
                        pushMessageBuilder.ops(ops);
                    }*/

            }
            PushMessage pushMessage = pushMessageBuilder.build();
            messageHandler.applyPushMessage(pushMessage);
        } else {
            Log.e(TAG, "no subscription found");
        }
//        } else {
//            Log.e(TAG, "No opts found in push message.");
//        }
    }

    /**
     * Sets a message handler to handle a messages on a certain subscription id
     *
     * @param subscriptionId
     * @param messageHandler
     */
    public void bindSubscription(String subscriptionId, MessageHandler messageHandler) {
        if (messageHandler == null) {
            throw new IllegalArgumentException("messageHandler must not be null");
        }
        if (subscriptionId == null || subscriptionId.equals("null")) {
            throw new IllegalArgumentException("subscriptionId must not be null or \"null\"");
        }
        Log.d(TAG, "Handler is bound with the subscription id " + subscriptionId);
        pushMessageSubscribers.put(subscriptionId, messageHandler);
        messageHandler.setSubscriptionId(subscriptionId);
    }


    public void disconnect() {
        if (mConnection.isConnected()) mConnection.disconnect();
        Log.d(TAG, "Disconnecting...");
    }

    public void setConnectionStatusListener(ConnectionStatusListener connectionStatusListener) {
        this.connectionStatusListener = connectionStatusListener;
    }

    public interface ConnectionStatusListener {

        public void onStatusChanged(boolean connected);
    }

}
