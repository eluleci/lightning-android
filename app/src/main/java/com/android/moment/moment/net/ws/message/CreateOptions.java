package com.android.moment.moment.net.ws.message;

import com.android.moment.moment.net.core.message.MessageOptions;

import java.util.Set;

public class CreateOptions extends MessageOptions {

    private final boolean subscribe;
    private final Set<String> fields;

    public CreateOptions(boolean subscribe, Set<String> fields) {
        this.subscribe = subscribe;
        this.fields = fields;
    }


}
