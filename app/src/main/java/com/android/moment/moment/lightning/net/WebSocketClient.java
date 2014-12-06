package com.android.moment.moment.lightning.net;

import android.util.Log;

import com.android.moment.moment.lightning.message.Message;
import com.android.moment.moment.lightning.message.MessageError;
import com.android.moment.moment.lightning.message.PushMessage;

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

    // List of the message handlers which are waiting for the response to their requests.
    private Map<Integer, MessageHandler> responseWaitingQueue = new HashMap<Integer, MessageHandler>();
    private Map<String, MessageHandler> pushMessageSubscribers = new HashMap<String, MessageHandler>();

    // private final String wsuri = "ws://10.0.2.137:8080/ws";
    private final String wsuri = "ws://uekk53169e3d.eluleci.koding.io:8080/ws";

    private final String TAG = "WebSocketClient";
    private final WebSocketConnection mConnection = new WebSocketConnection();
    private ConnectionStatusListener connectionStatusListener;

    private SecureRandom random = new SecureRandom();

    public void connect() {

        if (mConnection.isConnected()) {
            Log.d(TAG, "Connection is already open.");
            return;
        }

        try {
            Log.d(TAG, "Connecting to " + wsuri);
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    connectionStatusListener.onConnectionStatusChange(true);
                    Log.d(TAG, "Connected established to " + wsuri);
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "Message received: " + payload);
                    onTextMessageReceived(payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    connectionStatusListener.onConnectionStatusChange(false);
                    Log.e(TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
        }
    }

    public boolean isConnected() {
        return mConnection.isConnected();
    }

    public void disconnect() {
        if (mConnection.isConnected()) {
            // sending disconnect message
            Log.d(TAG, "Disconnecting.");
            Message message = new Message.Builder().cmd(Message.Command.DISCONNECT).build();
            mConnection.sendTextMessage(message.toString());
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

        // getting prepared message from handler
        Message message = requestMessageHandler.prepareMessage();
        if (message == null) {
            Log.e(TAG, "Handlers message is null while sending message. "
                    + requestMessageHandler.getClass().toString());
            return;
        }

        // generating rid
        int rid = new BigInteger(130, random).intValue();

        // setting rid of message
        message.setRid(rid);

        // mapping message handler with rid value
        responseWaitingQueue.put(message.getRid(), requestMessageHandler);

        // sending message
        mConnection.sendTextMessage(message.toString());

        Log.d(TAG, "Sending a request message: " + message.toString());
    }

    public void onTextMessageReceived(final String payload) {

        try {
            final JSONObject message = new JSONObject(payload);
            Runnable runnable;
            if (message.has("rid")) {
                // If a message has field 'rid', it means that this is a respond
                // to a request that is sent by this client. First we find the
                // message handler which owns the message and then we check for
                // error and notify the message handler. Finally we remove the
                // message handler from the message queue.
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.v(TAG, "RESPONSE MESSAGE RECEIEVED: " + message.toString(4));
                            handleResponse(message);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getLocalizedMessage(), e);
                        }
                    }
                };

            } else {
                // If there is no 'rid' in the message, it means that this is a push message
                // So we first check the subscription res of it and handle errors if the
                // subscription res is not valid. Then we find the MessageHandler which has
                // subscribed with this subscription res. If everything goes well, we just simply
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
//            new Thread(runnable).start();
            runnable.run();

        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    /**
     * handles the response of a request
     *
     * @param message the message from server
     * @throws org.json.JSONException
     */
    private void handleResponse(JSONObject message) throws JSONException, NullPointerException {
        int rid = message.getInt("rid");
        MessageHandler messageHandler = responseWaitingQueue.get(rid);

        if (messageHandler == null) {
            Log.e(TAG, "MessageHandler with rid " + rid + " couldn't be found.");
            return;
        }

        if (message.has("error")) { // handling error

            JSONObject errorObject = message.getJSONObject("error");
            int code = errorObject.getInt("code");
            String errorMessage = errorObject.getString("message");

            MessageError error = new MessageError(code, errorMessage);
            Log.e(TAG, "Error returned. Message was " + messageHandler.prepareMessage() + "   Error:" + error.toString());

            messageHandler.onError(messageHandler, error);

        } else { // getting message

            Message.Builder messageBuilder = new Message.Builder();

            // if message has res , add it to subscribers list
            if (message.has("res")) {
                String res = message.getString("res");

                // sometimes res comes with the string value "null", or it is simply the answer
                if (res != null && !res.equals("null")) {
                    messageBuilder.res(res);
                    messageHandler.setSubscriptionId(res);
                    bindSubscription(res, messageHandler);
                }
            }

            if (message.has("status")) {
                messageBuilder.status(message.getInt("status"));
            }

            if (message.has("body")) {
                messageBuilder.body(message.getJSONObject("body"));
            }

            if (message.has("cmd")) {
                Message.Command masterCmd = Message.Command.parseCommand(message.getString("cmd"));
                messageBuilder.cmd(masterCmd);
            }

            Message responseMessage = messageBuilder.build();
            messageHandler.applyResponse(responseMessage);

            responseWaitingQueue.remove(rid);
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

        if (message.has("res")) {
            String res = message.getString("res");

            if (res == null) {
                // Every push message must have a valid subscription res.
                Log.e(TAG, "Subscription res of push message is null");
                return;
            }

            if (!pushMessageSubscribers.containsKey(res)) {
                // It means we have subscription which has no handler.
                Log.e(TAG, "Push message has no handler for res " + res + "\n"
                        + message.toString());
                return;
            }

            MessageHandler messageHandler = pushMessageSubscribers.get(res);

            PushMessage.Builder pushMessageBuilder = new PushMessage.Builder();
            pushMessageBuilder.res(res);

            // add self subscription if have one (used for new creation of read request)
//                if (opts.has(SELF_SUBSCRIPTION) && !opts.getString(SELF_SUBSCRIPTION).equals("null")) {
//                    pushMessageBuilder.selfSubscription(opts.getString(SELF_SUBSCRIPTION));
//                }

            Message.Command masterCmd = null;
            JSONObject body = null;

            if (message.has("cmd")) {
                masterCmd = Message.Command.parseCommand(message.getString("cmd"));
                pushMessageBuilder.cmd(masterCmd);
            } else {
                Log.e(TAG, "There is no command tag inside push message.");
                return;
            }

            // delete message doesn't have 'res'
//            if (masterCmd != Message.Command.DELETE && message.has("res")) {
//                masterRes = ResourcePath.generateResourcePath(message.getString("res"));
//                pushMessageBuilder.res(masterRes);
//            }

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
     * Sets a message handler to handle push messages on a certain res
     *
     * @param res
     * @param messageHandler
     */
    public void bindSubscription(String res, MessageHandler messageHandler) {
        if (messageHandler == null) {
            throw new IllegalArgumentException("MessageHandler must not be null");
        }
        if (res == null || res.equals("null")) {
            throw new IllegalArgumentException("Res must not be null or \"null\"");
        }
        if (!pushMessageSubscribers.containsKey(res)) {
            pushMessageSubscribers.put(res, messageHandler);
            messageHandler.setSubscriptionId(res);
            Log.d(TAG, "Handler is bound with the subscription res " + res);
        }
    }

    public void setConnectionStatusListener(ConnectionStatusListener connectionStatusListener) {
        this.connectionStatusListener = connectionStatusListener;
    }

    public interface ConnectionStatusListener {

        public boolean onConnectionStatusChange(boolean connected);
    }

}
