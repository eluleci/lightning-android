package com.android.moment.moment.net.core.message;

import com.android.moment.moment.net.model.observer.Field;
import com.android.moment.moment.net.core.message.Message;

public class PushOperation {
    private Message.Command cmd;
    private Field field;
    private Object value;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Message.Command getCmd() {
        return cmd;
    }

    public void setCmd(Message.Command cmd) {
        this.cmd = cmd;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}