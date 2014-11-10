package com.android.moment.moment.net.ws.handler;


public class BoardCreateHandlerOld {}/* extends MessageHandler<Board> {

    private static final String TAG = "BoardCreateHandler";
    private final Profile profile;
    private final String name;
    private String coverCode;

    public BoardCreateHandler(Profile owner, String name, Category category) {
        this.name = name;
        this.profile = owner;
        this.category = category;
    }


    @Override
    public Message prepareMessage() {
        // Create options
        Set<String> fields = new Field.FieldsBuilder().addField(Board.COVER).build();
        MessageOptions opts = new CreateOptions(subscribeOnExecute, fields);
        JSONObject body = new JSONObject();
        try {
            body.put(Board.NAME.toString(), name);
            body.put(Board.CATEGORY.toString(), category.getRes());
            if (coverCode != null) {
                body.put(Board.COVER.toString(), coverCode);
            }
        } catch (JSONException e) {
            Log.e(TAG, "could not add board properties to body JSONObject");
        }
        // Message to create a new board
        ResourcePath res = new ResourcePath(ResourcePath.Resource.BOARDS, profile.getResourcePath());
        return new Message.Builder().cmd(Message.Command.CREATE).res(res).body(body).opts(opts).build();

    }

    @Override
    public Board onReceiveResponse(Message responseMessage) throws JSONException {
        // getting data from body
        JSONObject body = responseMessage.getBody();
        ResourcePath res = ResourcePath.generateResourcePath(body.getString(_RES));
        Board board = SalamWorld.getBoardManager().getLocalBoard(res);
        String categoryString = body.getString(Board.CATEGORY.toString());
        String publishedAt = body.getString(Board.PUBLISHED_AT.toString());
        board.setPublishedAt(TimeParser.parseISOString(publishedAt));
        board.setCategory(Category.valueOfRes(categoryString));
        board.setName(body.getString(Board.NAME.toString()));
        Log.d("CreateBoard", "successfully created: " + name + " " + Category.valueOfRes(categoryString));
        return board;
    }

    @Override
    public Board onReceivePushMessage(PushMessage pushMessage) throws JSONException {
        if (pushMessage.getMasterCmd() == Message.Command.DELETE) {
            ResourcePath res = pushMessage.getBodyRes();
            Board board = SalamWorld.getBoardManager().getLocalBoard(res);
            board.notifyFieldObservers(Model.DELETED, board);
            SalamWorld.getBoardManager().removeBoard(res);

        }
        return null;
    }

    public String getCoverCode() {
        return coverCode;
    }

    public void setCoverCode(String coverCode) {
        this.coverCode = coverCode;
    }

    public interface CreateBoardActionListener extends MessageActionListener<ObservableList<Board>> {
    }
}
*/