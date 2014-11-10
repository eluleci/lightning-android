package com.android.moment.moment.net.core.handler;

import com.android.moment.moment.net.model.Model;
import com.android.moment.moment.net.model.ObservableList;
import com.android.moment.moment.net.core.message.Message;

import org.json.JSONException;


public abstract class ListMessageHandler<E extends Model> extends ModelMessageHandler<ObservableList<E>> {

    private boolean subscribeOnItems;

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        model.setAscending(ascending);
        this.ascending = ascending;
    }

    protected boolean ascending;

    public void setSubscribeOnItems(boolean subscribeOnItems) {
        this.subscribeOnItems = subscribeOnItems;
    }

    public boolean getSubscribeOnItems() {
        return subscribeOnItems;
    }

    @Override
    public void applyResponse(Message responseMessage) throws JSONException {
        super.applyResponse(responseMessage);
        model.setReceived(true);
    }
}
