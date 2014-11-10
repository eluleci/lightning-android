package com.android.moment.moment.net.ws.manager;

import com.android.moment.moment.net.core.handler.MessageActionListener;
import com.android.moment.moment.net.core.handler.MessageHandler;
import com.android.moment.moment.net.model.ObservableList;
import com.android.moment.moment.net.model.Profile;
import com.android.moment.moment.net.ws.handler.ProfileCreateHandler;
import com.android.moment.moment.net.ws.handler.ProfileHandler;
import com.android.moment.moment.net.ws.handler.ProfileListHandler;
import com.android.moment.moment.net.ws.handler.UpdateProfileHandler;
import com.android.moment.moment.network.WebSocketClient;

import java.util.HashMap;

/**
 * BoardManager provides access to Board objects which reflect model on server.
 * Not thread safe.
 */
public final class ProfileManager {
    private static final String TAG = "ProfileManager";

    private final HashMap<String, ProfileHandler> itemHandlers;
    private final HashMap<String, ProfileListHandler> listHandlers;

    public ProfileManager() {
        listHandlers = new HashMap<String, ProfileListHandler>();
        itemHandlers = new HashMap<String, ProfileHandler>();
    }

    public void executeHandler(MessageHandler messageHandler) {
        WebSocketClient.getInstance().sendMessage(messageHandler);
    }

    /**
     * Returns an ObservableList with Board-objects from a person
     *
     * @param id the persons resourcePath
     * @return the Observable list with boards
     */
    public synchronized ObservableList<Profile> getList(String id) {

        ProfileListHandler profileListHandler;
        if (listHandlers.containsKey(id)) {
            profileListHandler = listHandlers.get(id);
        } else {
            profileListHandler = new ProfileListHandler(id);
            profileListHandler.setProfileManager(this);
            listHandlers.put(id, profileListHandler);
        }
        executeHandler(profileListHandler);
        return profileListHandler.getModel();
    }

    public synchronized void createProfile(Profile profile, MessageActionListener<Profile> listener) {

        ProfileCreateHandler profileCreateHandler = new ProfileCreateHandler();
        profileCreateHandler.setProfile(profile);
        profileCreateHandler.setMessageActionListener(listener);
        profileCreateHandler.setProfileManager(this);
        executeHandler(profileCreateHandler);
    }

    public synchronized void updateProfile(Profile profile, MessageActionListener<Profile> listener) {

        UpdateProfileHandler profileCreateHandler = new UpdateProfileHandler();
        profileCreateHandler.setProfile(profile);
        profileCreateHandler.setMessageActionListener(listener);
        profileCreateHandler.setProfileManager(this);
        executeHandler(profileCreateHandler);
    }

    /**
     * Returns the board with specified id, without fetching any data from server.
     * The board can contain data, if it was requested with fields and subscribe-parameter before.
     *
     * @param id
     * @return
     */
    public synchronized Profile getLocalProfile(String id) {

        if (itemHandlers.containsKey(id)) {
            return itemHandlers.get(id).getModel();
        } else {
            ProfileHandler handler = new ProfileHandler(id);
            this.itemHandlers.put(id, handler);
            WebSocketClient.getInstance().bindSubscription(id, handler);
            return handler.getModel();
        }
    }

    public void reset() {
        itemHandlers.clear();
        listHandlers.clear();
    }
}
