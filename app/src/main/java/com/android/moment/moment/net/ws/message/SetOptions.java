package com.android.moment.moment.net.ws.message;

import com.android.moment.moment.net.core.message.MessageOptions;

import java.util.Set;

public class SetOptions extends MessageOptions {

    private final Set<String> fields;

    public SetOptions(Set<String> fields) {
        this.fields = fields;
    }

}
