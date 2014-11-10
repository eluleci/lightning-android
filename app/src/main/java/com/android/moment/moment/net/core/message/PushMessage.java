package com.android.moment.moment.net.core.message;

import com.android.moment.moment.net.model.component.ResourcePath;

import org.json.JSONObject;

import java.util.List;

public class PushMessage {

    private final Message.Command masterCmd;
    private final String subscription;
    private final String selfSubscription;
    private final ResourcePath masterRes;
    private final ResourcePath bodyRes;
    private final List<PushOperation> operations;
    private final JSONObject body;

    private PushMessage(Builder builder) {
        this.masterCmd = builder.cmd;
        this.subscription = builder.subscription;
        this.masterRes = builder.res;
        this.bodyRes = builder.bodyRes;
        this.body = builder.body;
        this.operations = builder.operations;
        this.selfSubscription = builder.selfSubscription;
    }

    public Message.Command getMasterCmd() {
        return masterCmd;
    }

    public String getSubscription() {
        return subscription;
    }

    public String getSelfSubscription() {
        return selfSubscription;
    }

    public ResourcePath getMasterRes() {
        return masterRes;
    }

    public ResourcePath getBodyRes() {
        return bodyRes;
    }

    public JSONObject getBody() {
        return body;
    }

    public List<PushOperation> getOperations() {
        return operations;
    }

    public static class Builder {

        private Message.Command cmd;
        private String subscription;
        private String selfSubscription;
        private ResourcePath res;
        private ResourcePath bodyRes;
        private List<PushOperation> operations;
        private JSONObject body;

        public Builder cmd(Message.Command cmd) {
            this.cmd = cmd;
            return this;
        }

        public Builder subscription(String subscription) {
            this.subscription = subscription;
            return this;
        }

        public Builder selfSubscription(String selfSubscription) {
            this.selfSubscription = selfSubscription;
            return this;
        }

        public Builder res(ResourcePath res) {
            this.res = res;
            return this;
        }

        public Builder bodyRes(ResourcePath bodyRes) {
            this.bodyRes = bodyRes;
            return this;
        }

        public Builder ops(List<PushOperation> operations) {
            this.operations = operations;
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
