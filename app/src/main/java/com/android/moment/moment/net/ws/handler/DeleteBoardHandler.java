package com.android.moment.moment.net.ws.handler;

import com.android.moment.moment.net.core.handler.DeleteHandler;
import com.android.moment.moment.net.model.component.ResourcePath;
import com.android.moment.moment.net.core.message.Message;

import org.json.JSONException;


public class DeleteBoardHandler extends DeleteHandler {

    public DeleteBoardHandler(ResourcePath resourcePath) {
        super(resourcePath);
    }

    @Override
    public ResourcePath onReceiveResponse(Message responseMessage) throws JSONException {

        ResourcePath res = null;
        /*JSONObject body = responseMessage.getBody();
        if (body.has(Parameters._RES)) {
            res = ResourcePath.generateResourcePath(body.getString(Parameters._RES));
            SalamWorld.getBoardManager().removeBoard(res);
        }*/
        return res;
    }
}
