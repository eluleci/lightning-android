package com.android.moment.moment.net.ws.manager;

import com.android.moment.moment.net.core.handler.MessageActionListener;
import com.android.moment.moment.net.core.handler.MessageHandler;
import com.android.moment.moment.net.model.ObservableList;
import com.android.moment.moment.net.model.Profile;
import com.android.moment.moment.net.model.component.ResourcePath;
import com.android.moment.moment.net.ws.handler.ProfileCreateHandler;
import com.android.moment.moment.net.ws.handler.ProfileHandler;
import com.android.moment.moment.net.ws.handler.ProfileListHandler;
import com.android.moment.moment.network.WebSocketClient;

import java.util.HashMap;
import java.util.Set;

/**
 * BoardManager provides access to Board objects which reflect model on server.
 * Not thread safe.
 */
public final class ProfileManager {
    private static final String TAG = "ProfileManager";

    private final HashMap<ResourcePath, ProfileHandler> itemHandlers;
    private final HashMap<ResourcePath, ProfileListHandler> listHandlers;

    public ProfileManager() {
        listHandlers = new HashMap<ResourcePath, ProfileListHandler>();
        itemHandlers = new HashMap<ResourcePath, ProfileHandler>();
    }

    public void executeHandler(MessageHandler messageHandler) {
        WebSocketClient.getInstance().sendMessage(messageHandler);
    }

    /**
     * Returns an ObservableList with Board-objects from a person
     *
     * @param resourcePath the persons resourcePath
     * @return the Observable list with boards
     */
    public synchronized ObservableList<Profile> getList(ResourcePath resourcePath) {

        ProfileListHandler profileListHandler;
        if (listHandlers.containsKey(resourcePath)) {
            profileListHandler = listHandlers.get(resourcePath);
        } else {
            profileListHandler = new ProfileListHandler(resourcePath);
            profileListHandler.setProfileManager(this);
            listHandlers.put(resourcePath, profileListHandler);
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
    /*
    public synchronized ObservableList<Board> getList(ResourcePath resourcePath, Set<String> fields,
                                                      boolean subscribeOnList, boolean subscribeOnItems) {
        BoardListHandlerOld boardListHandlerOld;
        if (listHandlers.containsKey(resourcePath)) {
            boardListHandlerOld = listHandlers.get(resourcePath);
        } else {
            boardListHandlerOld = new BoardListHandlerOld(resourcePath);
            listHandlers.put(resourcePath, boardListHandlerOld);
            manager.registerHandler(resourcePath, boardListHandlerOld);
        }
        manager.updateListModelData(boardListHandlerOld, fields, subscribeOnList, subscribeOnItems);
        return boardListHandlerOld.getModel();
    }*/


    /**
     * returns the board-object with specified resourcePath, gets the fields' value from server and subscribes if specified
     *
     * @param resourcePath the resourcePath of the board
     * @param fields       the fields that are needed to be filled from server
     * @param subscribe    true if board should be subscribed
     * @return
     */
    public synchronized Profile getBoard(ResourcePath resourcePath, Set<String> fields, boolean subscribe) {
        /*BoardHandler boardHandler;
        if (itemHandlers.containsKey(resourcePath)) {
            boardHandler = itemHandlers.get(resourcePath);
        } else {
            boardHandler = new BoardHandler(resourcePath);
            itemHandlers.put(resourcePath, boardHandler);
//            manager.registerHandler(resourcePath, boardHandler);
        }
        manager.updateModelData(boardHandler, fields, subscribe);
        return boardHandler.getModel();*/
        return null;
    }

    /**
     * Returns the board with specified resourcePath, without fetching any data from server.
     * The board can contain data, if it was requested with fields and subscribe-parameter before.
     *
     * @param resourcePath
     * @return
     */
    public synchronized Profile getLocalProfile(ResourcePath resourcePath) {

        if (itemHandlers.containsKey(resourcePath)) {
            return itemHandlers.get(resourcePath).getModel();
        } else {
            ProfileHandler handler = new ProfileHandler(resourcePath);
            this.itemHandlers.put(resourcePath, handler);
//            WebSocketClient.getInstance().bindSubscription(resourcePath.getId(), handler);
            return handler.getModel();
        }
    }

    /**
     * Finds the board in local boardList and removes it.
     *
     * @param resourcePath the resourcePath of the board
     */
    public synchronized void removeBoard(ResourcePath resourcePath) {
        /*// Step 1 remove the board from Profile's board-list
        ResourcePath ownerProfileRes = resourcePath.getParent();
        ListMessageHandler<Board> messageHandler = listHandlers.get(ownerProfileRes);
        if (messageHandler != null) {
            ObservableList<Board> boardList = messageHandler.getModel();
            for (int i = 0; i < boardList.size(); i++) {
                if (boardList.get(i).getResourcePath().equals(resourcePath)) {
                    boardList.remove(i);
                    break;
                }
            }
        }

        //Step 2 delete the board
        BoardHandler boardHandler = itemHandlers.get(resourcePath);
        if (boardHandler != null) {
            boardHandler.getModel().notifyFieldObservers(Model.DELETED, boardHandler.getModel());
            itemHandlers.remove(boardHandler);
        }*/
    }

    /**
     * Binds a subscriptionId to the board, for example when it has been received with a BoardList.
     * Any previously binded handler will be removed.
     *
     * @param profile        the board to be associated with the subscriptionId
     * @param subscriptionId the subscriptionId to be binded to the board
     * @return true if subscription-id could be successfully set
     */
    public boolean bindSubscription(Profile profile, String subscriptionId) {
        if (subscriptionId == null || subscriptionId.equals("null")) {
            throw new IllegalArgumentException("null not allowed");

        } else if (profile == null) {
            throw new IllegalArgumentException("board must not be null");
        }
        ProfileHandler handler = itemHandlers.get(profile.getResourcePath());
        if (handler != null && handler.getSubscriptionId() == null) {
//            SalamWorld.getWebSocketClient().bindSubscription(subscriptionId, handler);
            return true;
        }
        return false;
    }

/*
    public void createBoard(Profile profile, String name, Category category, MessageActionListener mal) throws JSONException {
        this.createBoard(profile, name, category, null, mal);
    }*/


    /*public void createBoard(Profile profile, String name, Category category, String defaultImage, MessageActionListener mal) {
        BoardCreateHandler createHandler = new BoardCreateHandler(profile, name, category);
        createHandler.setCoverCode(defaultImage);
        createHandler.setSubscribeOnExecute(true);
        createHandler.setMessageActionListener(mal);
        manager.getWebSocketClient().sendMessage(createHandler);
    }*/

    public void reset() {
        itemHandlers.clear();
        listHandlers.clear();
    }
}
