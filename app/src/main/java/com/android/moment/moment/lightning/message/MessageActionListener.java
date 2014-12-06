package com.android.moment.moment.lightning.message;

import com.android.moment.moment.lightning.net.MessageHandler;

/**
 * This is interface for listening the result of the request messages.
 *
 * @param <O> type of the object that is returned to the listener.
 * @author eluleci
 */
public interface MessageActionListener<O> {

    /**
     * Called when the response is successful.
     *
     * @param messageHandler owner of the message.
     * @param data           received data.
     */
    public void onSuccess(MessageHandler messageHandler, O data);

    /**
     * Called when response contains error.
     *
     * @param messageHandler owner of the message.
     * @param error          received error object.
     */
    public void onError(MessageHandler messageHandler, MessageError error);

    /**
     * Called when handler receives push message.
     *
     * @param pushMessage received message
     */
    public void onPushMessage(PushMessage pushMessage);
}
