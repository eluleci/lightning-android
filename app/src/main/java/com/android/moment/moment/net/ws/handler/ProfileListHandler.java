package com.android.moment.moment.net.ws.handler;

import android.util.Log;

import com.android.moment.moment.net.core.handler.ModelMessageHandler;
import com.android.moment.moment.net.core.message.Message;
import com.android.moment.moment.net.core.message.PushMessage;
import com.android.moment.moment.net.model.ObservableList;
import com.android.moment.moment.net.model.ObservableListImpl;
import com.android.moment.moment.net.model.Profile;
import com.android.moment.moment.net.ws.manager.ProfileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eluleci on 10/11/14.
 */
public class ProfileListHandler extends ModelMessageHandler<ObservableList<Profile>> {

    private static final String TAG = "BoardListHandler";

    private ProfileManager profileManager;

    public ProfileListHandler(String id) {
        model = new ObservableListImpl<Profile>(id);
    }

    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public Message prepareMessage() {
        return new Message.Builder().cmd(Message.Command.READ).id(model.getId()).build();
    }

    @Override
    public ObservableList<Profile> onReceiveResponse(Message responseMessage) throws JSONException {

        List<Profile> profiles = new ArrayList<Profile>();

        Log.d(TAG, "Response received");
        Log.d(TAG, responseMessage.toString());
        JSONArray list = responseMessage.getBody().getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {

            JSONObject profileObject = list.getJSONObject(i);
            Profile profile = profileManager.getLocalProfile(profileObject.getString("id"));
            profile.setName(profileObject.getString("name"));
            profile.setAvatar(profileObject.getString("avatar"));
            profiles.add(profile);
        }
        synchronized (model) {
            model.clearSilent();
            model.addAll(profiles);
        }
        return model;
    }

    @Override
    public ObservableList<Profile> onReceivePushMessage(PushMessage pushMessage) throws JSONException {

        Log.d(TAG, "Push message received to handler");

//        if (pushMessage.getMasterCmd().equals(Message.Command.CREATE)) {

        JSONObject body = pushMessage.getBody();

        Profile profile = profileManager.getLocalProfile(body.getString("id"));
        profile.setName(body.getString("name"));
        profile.setAvatar(body.getString("avatar"));

        model.add(profile);
//        }
        return model;
    }
}
