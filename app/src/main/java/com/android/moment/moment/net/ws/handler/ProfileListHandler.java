package com.android.moment.moment.net.ws.handler;

import android.util.Log;

import com.android.moment.moment.net.core.handler.ModelMessageHandler;
import com.android.moment.moment.net.core.message.Message;
import com.android.moment.moment.net.core.message.PushMessage;
import com.android.moment.moment.net.model.ObservableList;
import com.android.moment.moment.net.model.ObservableListImpl;
import com.android.moment.moment.net.model.Profile;
import com.android.moment.moment.net.model.component.ResourcePath;
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

    public ProfileListHandler(ResourcePath userRes) {
        ResourcePath boardRes = new ResourcePath(ResourcePath.Resource.BOARDS, userRes);
        model = new ObservableListImpl<Profile>(boardRes);
    }

    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public Message prepareMessage() {
        ResourcePath res = model.getResourcePath();
        return new Message.Builder().cmd(Message.Command.READ).res(res).build();
    }

    @Override
    public ObservableList<Profile> onReceiveResponse(Message responseMessage) throws JSONException {

        List<Profile> profiles = new ArrayList<Profile>();

        Log.d(TAG, "Response received");
        Log.d(TAG, responseMessage.toString());
        JSONArray list = responseMessage.getBody().getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {

            JSONObject profileObject = list.getJSONObject(i);
            ResourcePath res = new ResourcePath(profileObject.getString("name"), ResourcePath.Resource.PROFILES, null);
            Profile profile = profileManager.getLocalProfile(res);
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

            Profile profile = profileManager.getLocalProfile(new ResourcePath(body.getString("id"),
                    ResourcePath.Resource.PROFILES));
            profile.setName(body.getString("name"));
            profile.setAvatar(body.getString("avatar"));

            model.add(profile);
//        }
        return model;
    }
}
