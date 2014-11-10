package com.android.moment.moment.net.ws.handler;

import com.android.moment.moment.net.core.handler.MessageHandler;
import com.android.moment.moment.net.core.handler.ModelMessageHandler;
import com.android.moment.moment.net.core.message.Message;
import com.android.moment.moment.net.core.message.MessageError;
import com.android.moment.moment.net.core.message.MessageOptions;
import com.android.moment.moment.net.core.message.PushMessage;
import com.android.moment.moment.net.model.Profile;
import com.android.moment.moment.net.model.component.ResourcePath;
import com.android.moment.moment.net.ws.message.GetOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileHandler extends ModelMessageHandler<Profile> {

    private static final String TAG = "ProfileHandler";

    public ProfileHandler(ResourcePath res) {
        model = new Profile(res);
    }

    @Override
    public Message prepareMessage() {

        // creating resource path
        ResourcePath res = model.getResourcePath();
        // creating opts
        MessageOptions opts = new GetOptions(subscribeOnExecute, fields);
        // creating message for receiving user board list
        return new Message.Builder().cmd(Message.Command.GET).res(res).opts(opts).build();
    }

    @Override
    public Profile onReceiveResponse(Message responseMessage) throws JSONException {

        /*ResourcePath res = ResourcePath.generateResourcePath(responseMessage.getBody().getString(_RES));
        model.getResourcePath().setVersion(res.getVersion());

        // getting data from body
        JSONObject boardBody = responseMessage.getBody();
        updateBoard(model, boardBody);

        //TODO: check if other fields are available*/
        return model;
    }

    @Override
    public void onError(MessageHandler messageHandler, MessageError error) {
        super.onError(messageHandler, error);
        /*if (error.getCode() == 404) {
            SalamWorld.getBoardManager().removeBoard(getModel().getResourcePath());
            model.notifyFieldObservers(Model.DELETED, model);
        }*/
    }

    @Override
    public Profile onReceivePushMessage(PushMessage pushMessage) throws JSONException {

        /*model.getResourcePath().setVersion(pushMessage.getBodyRes().getVersion());
        JSONObject newData = new JSONObject();

        if (pushMessage.getMasterCmd() == Message.Command.DELETE) {
            ResourcePath res = pushMessage.getBodyRes();
            SalamWorld.getBoardManager().removeBoard(res);
            model.notifyFieldObservers(Model.DELETED, model);
            return model;
        }

        if (pushMessage.getOperations() != null) {
            for (PushOperation operation : pushMessage.getOperations()) {
                try {
                    newData.put(operation.getField().toString(), operation.getValue());
                } catch (JSONException e) {
                    Log.e(TAG, "Push message could not be handled: " + e.getLocalizedMessage());
                }
            }
        }
        updateBoard(model, newData);
        // TODO handle pull, inc, addToSet*/
        return model;
    }

    /**
     * updates a Board model with the data in the the JSONObject data
     *
     * @param profile the model to be updated
     * @param data    the body with the new data
     * @throws org.json.JSONException can only happen when data is changed asynchronously
     */
    protected static void updateBoard(Profile profile, JSONObject data) throws JSONException {
        if (data.has(Profile.NAME.toString())) {
            String name = data.getString(Profile.NAME.toString());
            profile.setName(name);
        }
        if (data.has(Profile.AVATAR.toString())) {
            String avatar = data.getString(Profile.AVATAR.toString());
            profile.setName(avatar);
        }
    }

}
