package com.android.moment.moment.net.ws.message;

import com.android.moment.moment.net.core.message.MessageOptions;

public class ResponseOptions extends MessageOptions {

    private String subscription;

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

}
