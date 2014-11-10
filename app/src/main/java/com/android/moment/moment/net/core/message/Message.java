package com.android.moment.moment.net.core.message;


import com.android.moment.moment.net.model.component.ResourcePath;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class stores the data for both sent and receives messages. Uses builder pattern for creation
 * because that there a lot of optional parameters while creating a message.
 *
 * @author eluleci
 */
public class Message implements Parameters {

    private int rid = 0;
    private final Command cmd;
    private final MessageOptions opts;
    private final String id;
    private final JSONObject body;
    private final JSONObject metadata;

    public Message(Builder builder) {
        this.cmd = builder.cmd;
        this.opts = builder.opts;
        this.id = builder.id;
        this.body = builder.body;
        this.metadata = builder.metadata;
    }

    /**
     * Checks the command of the message. If command is not delete then sets the rid.
     *
     * @param rid
     * @return
     */
    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getRid() {
        return this.rid;
    }

    public Command getCmd() {
        return cmd;
    }

    public String getId() {
        return id;
    }

    public JSONObject getBody() {
        return body;
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public MessageOptions getOpts() {
        return this.opts;
    }

    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        try {
            if (rid != 0) message.put(RID, rid);
            if (cmd != null) message.put(CMD, cmd.toString());
            if (id != null) message.put(RES, id);
            if (opts != null) message.put(OPTS, opts.js());
            if (body != null) message.put(BODY, body);
            if (metadata != null) message.put(METADATA, metadata);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonToStringModified(message);
    }

    private static final String BACKSLASH_SLASH = "\\\\/";

    /**
     * returns a String from a JSON-object without replacing "/" with "\/"
     *
     * @param jsonObject
     * @return
     */
    public String jsonToStringModified(JSONObject jsonObject) {
        return jsonObject.toString();//.replaceAll(BACKSLASH_SLASH, "/");
    }

    public static class Builder {

        private Command cmd;
        private MessageOptions opts;
        private String id;
        private JSONObject body;
        private JSONObject metadata;

        public Builder cmd(Command cmd) {
            this.cmd = cmd;
            return this;
        }

        public Builder opts(MessageOptions opts) {
            this.opts = opts;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder body(JSONObject body) {
            this.body = body;
            return this;
        }

        public Builder metadata(JSONObject metadata) {
            this.metadata = metadata;
            return this;
        }

        public Message build() {
            return new Message(this);
        }

    }

    public enum Command {
        FETCH("fetch"), CREATE_FETCHER("create-fetcher"), PING("ping"), CREATE("create"),
        READ("read"), UPDATE("update"), DELETE("delete"), UNSUBSCRIBE("unsubscribe"),
        SUBSCRIBE("subscribe"), X_LIKE("x-like"), X_UNLIKE("x-unlike"), ADD_TO_SET("addToSet"),
        PULL("pull"), INC("inc"), SEARCH("search"), SET("set"), GET("get"),
        MARK_ALL_READ("mark-all-read"), MARK_ALL_SEEN("mark-all-seen"), MARK_SEEN("mark-seen"),
        MARK_READ("mark-read"), CHANGE_PASSWORD("change-password"), EXPLORE("explore");

        /**
         * @param name
         */
        private Command(final String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Command parseCommand(String commandString) {
            for (Command c : values())
                if (c.name.equals(commandString)) {
                    return c;
                }
            return null;
        }
    }

}
