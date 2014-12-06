package com.android.moment.moment.sample.net;

import com.android.moment.moment.lightning.message.Message;
import com.android.moment.moment.lightning.message.PushMessage;
import com.android.moment.moment.lightning.net.MessageHandler;
import com.android.moment.moment.sample.model.Profile;

import org.json.JSONException;

/**
 * Created by eluleci on 02/12/14.
 */
public class ProfileMessageHandler extends MessageHandler<Profile> {

    private Profile model;

    public ProfileMessageHandler(Profile model) {
        this.model = model;
    }

    @Override
    public Message prepareMessage() {

        // constructing message
        return new Message.Builder().cmd(Message.Command.GET).res("/Profile/123").build();

        // The built message will be like:
        //
        //  {
        //      cmd: 'GET',
        //      res: '/Profile/123'
        //  }
    }

    @Override
    public Profile onReceiveResponse(Message responseMessage) throws JSONException {

        // parsing response
        String name = responseMessage.getBody().getString("name");
        String avatar = responseMessage.getBody().getString("avatar");

        model.setName(name);
        model.setAvatar(avatar);

        return model;

        // The parsed response message body will be like:
        //
        //  {
        //      res: '/Profile/123'
        //      id: '123',
        //      name: 'Some Profile Name',
        //      avatar: 'some.profile.avatar/url',
        //      status: 'online',
        //  }
    }

    @Override
    public Profile onReceivePushMessage(PushMessage pushMessage) throws JSONException {

        // parsing push message
        if (pushMessage.getBody().has("name")) {

            String name = pushMessage.getBody().getString("name");
            model.setName(name);

        } else if (pushMessage.getBody().has("avatar")) {

            String avatar = pushMessage.getBody().getString("avatar");
            model.setAvatar(avatar);

        } else if (pushMessage.getBody().has("status")) {

            String status = pushMessage.getBody().getString("status");
            model.setStatus(status);
        }
        return model;

        // The parsed push message will be like:
        //
        //  {
        //      cmd: 'post',
        //      body: {
        //          status: 'inactive'
        //      }
        //  }
    }
}