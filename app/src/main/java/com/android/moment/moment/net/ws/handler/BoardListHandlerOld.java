package com.android.moment.moment.net.ws.handler;


import com.android.moment.moment.net.core.handler.ListMessageHandler;
import com.android.moment.moment.net.core.handler.MessageActionListener;
import com.android.moment.moment.net.core.message.Message;
import com.android.moment.moment.net.core.message.PushMessage;
import com.android.moment.moment.net.model.Profile;
import com.android.moment.moment.net.model.ObservableList;
import com.android.moment.moment.net.model.ObservableListImpl;
import com.android.moment.moment.net.model.component.Avatar;
import com.android.moment.moment.net.model.component.ResourcePath;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO
 */
public class BoardListHandlerOld extends ListMessageHandler<Profile> {

    private static final String TAG = "BoardListHandler";

    /**
     * TODO
     *
     * @param userRes
     */
    public BoardListHandlerOld(ResourcePath userRes) {
        ResourcePath boardRes = new ResourcePath(ResourcePath.Resource.BOARDS, userRes);
        model = new ObservableListImpl<Profile>(boardRes);
    }

    @Override
    public Message prepareMessage() {

        // creating resource path
        ResourcePath res = model.getResourcePath();

        // creating opts
//        ReadOptions opts = new ReadOptions(fields);
//        opts.setAscending(ascending);
//        opts.setSubscribeOnInsertion(this.getSubscribeOnExecute());
//        opts.setSubscribeOnItems(this.getSubscribeOnItems());

        // creating message for receiving user board list
//        return new Message.Builder().cmd(Message.Command.READ).res(res).opts(opts).build();
        return new Message.Builder().cmd(Message.Command.READ).res(res).build();
    }

    @Override
    public ObservableList<Profile> onReceiveResponse(Message responseMessage)
            throws JSONException {

        /*JSONObject body = responseMessage.getBody();
        JSONArray items = body.getJSONArray(ITEMS);
        List<Board> newBoards = new ArrayList<Board>();
        for (int i = 0; i < items.length(); i++) {
            // getting data from message
            JSONObject boardBody = items.getJSONObject(i).getJSONObject(BODY);
            ResourcePath boardRes = ResourcePath.generateResourcePath(boardBody.getString(_RES));
            Board board = SalamWorld.getBoardManager().getLocalBoard(boardRes);

            updateBoard(board, boardBody);
            if (items.getJSONObject(i).has(SUBSCRIPTION)) {
                String subscriptionId = items.getJSONObject(i).getString(SUBSCRIPTION);
                if (subscriptionId != null && !subscriptionId.equals("null")) {
                    //if we subscribed on items, we will associate the local boards with this subscriptionID
                    boolean b = SalamWorld.getBoardManager().bindSubscription(board, subscriptionId);
                    Log.d(TAG, "board is bound to subscriptionId: " + b + " for board: " + board.getName());
                }
            } else {
                Log.d(TAG, "board has no subscription Id" + board.getName());
            }
            // getting local board objects from manager and updating it
            newBoards.add(board);
        }
        synchronized (model) {
            model.clearSilent();
            model.addAll(newBoards);
        }*/
        return model;
    }

    protected final void updateBoard(Profile profile, JSONObject jsonObject) throws JSONException {

        if (jsonObject.has(Profile.NAME.toString())) {
            String name = jsonObject.getString(Profile.NAME.toString());
            profile.setName(name);
        }
        if (jsonObject.has(Profile.AVATAR.toString())) {
            JSONObject avatarData = jsonObject.getJSONObject(Profile.AVATAR.toString());
//            profile.setAvatar(Avatar.createFromJsonObject(avatarData));
        }
    }

    @Override
    public ObservableList<Profile> onReceivePushMessage(PushMessage pushMessage) throws JSONException {

        /*if (pushMessage.getMasterCmd() == Message.Command.DELETE) {
            ResourcePath res = pushMessage.getBodyRes();
            // Finds the board with this resource path and deletes it.
            for (int i = 0; i < model.size(); i++) {
                if (model.get(i).getResourcePath().equals(res)) {
                    model.remove(i);
                    break;
                }
            }
        }*/

        /*if (pushMessage.getMasterCmd() == Message.Command.CREATE) {
            JSONObject item_body = (JSONObject) pushMessage.getOperations().get(0).getValue();
            String res = item_body.getString(_RES);
            Board board = SalamWorld.getBoardManager().getLocalBoard(ResourcePath.generateResourcePath(res));
            if (pushMessage.getSelfSubscription() != null) {
                SalamWorld.getBoardManager().bindSubscription(board, pushMessage.getSelfSubscription());
            }
            updateBoard(board, item_body);
            model.add(0, board);
        }*/
        return model;
    }

    public interface BoardsActionListener extends MessageActionListener<ObservableList<Profile>> {
    }

}
