package com.android.moment.moment.lightning;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class stores the data for both sent and receives messages. Uses builder pattern for creation
 * because that there a lot of optional parameters while creating a message.
 *
 * @author eluleci
 */
public class Message {

    private int rid = 0;
    private final String res;
    private final int status;
    private final Command cmd;
    private final JSONObject body;

    public Message(Builder builder) {
        this.cmd = builder.cmd;
        this.res = builder.res;
        this.status = builder.status;
        this.body = builder.body;
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

    public int getStatus() {
        return status;
    }

    public Command getCmd() {
        return cmd;
    }

    public String getRes() {
        return res;
    }

    public JSONObject getBody() {
        return body;
    }

    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        try {
            if (rid != 0) message.put("rid", rid);
            if (status != 0) message.put("status", status);
            if (cmd != null) message.put("cmd", cmd.toString());
            if (res != null) message.put("res", "/" + res);
            if (body != null) message.put("body", body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message.toString();
    }

    public static class Builder {

        private Command cmd;
        private String res;
        private int status;
        private JSONObject body;

        public Builder cmd(Command cmd) {
            this.cmd = cmd;
            return this;
        }

        public Builder res(String res) {
            this.res = res;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder body(JSONObject body) {
            this.body = body;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }

    public enum Command {
        GET("get"), POST("post"), DISCONNECT("disconnect");

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
