package com.android.moment.moment.net.core.handler;

import android.util.Log;

import com.android.moment.moment.net.model.Model;
import com.android.moment.moment.net.core.message.Message;
import com.android.moment.moment.net.core.message.MessageError;
import com.android.moment.moment.net.core.message.PushMessage;

import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

/**
 * A ModelMessageHandler gets data of a model from server and handles its push messages.
 *
 * @param <E>
 */
public abstract class ModelMessageHandler<E extends Model> extends MessageHandler<E> {
    private static final String TAG = "ModelMessageHandler";
    protected E model;
    protected Set<String> fields = new HashSet<String>();


    /**
     * @return the model object that is managed by this handler
     */
    public E getModel() {
        return model;
    }

    public final void setModel(E model) {
        this.model = model;
    }

    public Set<String> getFields() {
        return fields;
    }

    @Override
    public void setSubscriptionId(String id) {
        super.setSubscriptionId(id);
        model.setSubscriptionId(id);
    }

    @Override
    public void onError(MessageHandler messageHandler, MessageError error) {
        this.fields.clear();
        super.onError(messageHandler, error);
    }

    @Override
    public void applyPushMessage(PushMessage pushMessage) throws JSONException {

        // applying push message in DELETE command without checking revision
        if (pushMessage.getMasterCmd().equals(Message.Command.DELETE)) {
            super.applyPushMessage(pushMessage);
            return;
        }

//        if (!model.getResourcePath().setVersion(pushMessage.getBodyRes().getVersion())) {
//            Log.d(TAG, "Push message has been ignored, because version number was <= current version nr");
//            return;
//        } else {
            super.applyPushMessage(pushMessage);
//        }
    }
}
