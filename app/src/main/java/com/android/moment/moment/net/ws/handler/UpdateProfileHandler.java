package com.android.moment.moment.net.ws.handler;

import android.util.Log;

import com.android.moment.moment.net.core.handler.MessageHandler;
import com.android.moment.moment.net.core.message.Message;
import com.android.moment.moment.net.core.message.PushMessage;
import com.android.moment.moment.net.model.Profile;
import com.android.moment.moment.net.model.component.ResourcePath;
import com.android.moment.moment.net.ws.manager.ProfileManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eluleci on 10/11/14.
 */
public class UpdateProfileHandler extends MessageHandler {

    private static final String TAG = "UpdateProfileMessageHandler";

    private ProfileManager profileManager;
    private Profile profile;

    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public Message prepareMessage() {

        JSONObject body = new JSONObject();
        try {
            body.put(Profile.ID.toString(), profile.getId());
            body.put(Profile.NAME.toString(), profile.getName());
            body.put(Profile.AVATAR.toString(), profile.getAvatar());
        } catch (JSONException e) {
            Log.e(TAG, "could not add board properties to body JSONObject");
        }
        // Message to create a new profile
        ResourcePath res = new ResourcePath(ResourcePath.Resource.PROFILES);
        return new Message.Builder().cmd(Message.Command.UPDATE).id(profile.getId()).body(body).build();
    }

    @Override
    public Object onReceiveResponse(Message responseMessage) throws JSONException {
        Log.d(TAG, "Profile successfully updated");
        return null;
    }

    @Override
    public Object onReceivePushMessage(PushMessage pushMessage) throws JSONException {
        return null;
    }
}
