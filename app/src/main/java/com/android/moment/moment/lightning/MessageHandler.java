package com.android.moment.moment.lightning;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;

/**
 * This class is the abstract class for handling all kind of messages. Instances of this classes
 * must override methods prepareMessage and onReceiveResponse. MessageActionListener must be set for
 * listening the result of the message.
 *
 * @author eluleci
 */
public abstract class MessageHandler<E> {
    private static Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    private String subscriptionId = null;
    protected MessageActionListener<? super E> messageActionListener;
    protected boolean subscribeOnExecute = false;

    /**
     * sets the subscriptionId and registers in WebSocketClient as "handler to be called"
     *
     * @param id the res to be set and registered
     */
    public void setSubscriptionId(String id) {
        this.subscriptionId = id;
    }

    /**
     * Null if not subscribed
     *
     * @return subscriptionId of MessageHandler
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * Generates the message object.
     *
     * @return
     */
    public abstract Message prepareMessage();

    /**
     * Processes a response message
     * Called when response data is successfully received.
     *
     * @param responseMessage the response as Message-object
     * @throws org.json.JSONException in case the parsing of the message's body fails
     */
    public abstract E onReceiveResponse(Message responseMessage) throws JSONException;

    /**
     * processes a response-message and notifies the MessageActionListener
     *
     * @param responseMessage the response as Message-object
     * @throws org.json.JSONException in case the parsing of the message's body fails
     */
    public void applyResponse(Message responseMessage) throws JSONException {
        final E model = this.onReceiveResponse(responseMessage);
        if (messageActionListener != null) {
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    messageActionListener.onSuccess(MessageHandler.this, model);
                }
            });
        }
    }

    /**
     * processes a pushMessage.
     * Called when new push message is received
     *
     * @param pushMessage the push message as Message-object
     */
    public abstract E onReceivePushMessage(PushMessage pushMessage) throws JSONException;

    /**
     * processes a push-message and notifies the MessageActionListener
     *
     * @param pushMessage the push message as Message-object
     * @throws org.json.JSONException in case the parsing of the message's body fails
     */
    public void applyPushMessage(final PushMessage pushMessage) throws JSONException {
        final E model = this.onReceivePushMessage(pushMessage);
        if (messageActionListener != null) {
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    messageActionListener.onPushMessage(pushMessage);
                }
            });
        }
    }

    /**
     * Called when response contains error.
     *
     * @param messageHandler is the owner of the message.
     * @param error          is the received error object.
     */
    public void onError(final MessageHandler messageHandler, final MessageError error) {
        if (messageActionListener != null) uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                messageActionListener.onError(messageHandler, error);
            }
        });
    }

    /**
     * Setting request result listener.
     *
     * @param requestResultListener the MessageActionListener to be informed about request results
     */
    public final void setMessageActionListener(MessageActionListener<? super E> requestResultListener) {
        this.messageActionListener = requestResultListener;
    }

    /**
     * @param subscribe if the handler should subscribe for push messages when executed
     */
    public void setSubscribeOnExecute(boolean subscribe) {
        this.subscribeOnExecute = subscribe;
    }

    /**
     * @return true if handler subscribes for push messages
     */
    public boolean getSubscribeOnExecute() {
        return subscribeOnExecute;
    }

    private String previousSubscriptionId;

    /**
     * @return request result listener.
     */
    public MessageActionListener getListener() {
        return messageActionListener;
    }

    public void execute() {
        WebSocketClient.getInstance().sendMessage(this);
    }
}