package com.android.moment.moment.lightning.message;

import org.json.JSONObject;

public class PushMessage {

    private final Message.Command masterCmd;
    private final String res;
    private final String selfSubscription;
    private final JSONObject body;

    private PushMessage(Builder builder) {
        this.masterCmd = builder.cmd;
        this.res = builder.res;
        this.body = builder.body;
        this.selfSubscription = builder.selfSubscription;
    }

    public Message.Command getMasterCmd() {
        return masterCmd;
    }

    public String getRes() {
        return res;
    }

    public String getSelfSubscription() {
        return selfSubscription;
    }

    public JSONObject getBody() {
        return body;
    }

    public static class Builder {

        private Message.Command cmd;
        private String res;
        private String selfSubscription;
        private JSONObject body;

        public Builder cmd(Message.Command cmd) {
            this.cmd = cmd;
            return this;
        }

        public Builder res(String res) {
            this.res = res;
            return this;
        }

        public Builder body(JSONObject body) {
            this.body = body;
            return this;
        }

        public PushMessage build() {
            return new PushMessage(this);
        }

    }

}
