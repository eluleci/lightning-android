package com.android.moment.moment.net.model;

import com.android.moment.moment.net.model.observer.AbstractFieldsObservable;
import com.android.moment.moment.net.model.observer.Field;
import com.android.moment.moment.net.model.observer.FieldObserver;

import java.io.Serializable;

public class Profile extends AbstractFieldsObservable implements Model, Serializable {

    public static final Field<String> ID = new Field<String>("id");
    public static final Field<String> NAME = new Field<String>("name");
    public static final Field<String> AVATAR = new Field<String>("avatar");

    private String id;
    private String name;
    private String avatar;
    private String subscriptionId;

    @Override
    public void onAddFieldObserver(Field field, FieldObserver fieldObserver) {
        if (field == NAME && name != null) {
            fieldObserver.updateData(field, name);
        } else if (field == AVATAR && avatar != null) {
            fieldObserver.updateData(field, avatar);
        }
    }

    @Override
    public void onRemoveFieldObserver(Field field, FieldObserver fieldObserver) {

    }

    //********************* SETTERS *********************


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
        this.notifyFieldObservers(NAME, name);
    }

    public String getName() {
        return name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        this.notifyFieldObservers(AVATAR, avatar);
    }

    public String getAvatar() {
        return avatar;
    }

    //********************* GETTERS *********************

    @Override
    public void setSubscriptionId(String id) {
        this.subscriptionId = id;
    }

    @Override
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @Override
    public String toString() {
        return name;
    }
}
