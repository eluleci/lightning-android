package com.android.moment.moment.net.model;

import com.android.moment.moment.net.model.component.Avatar;
import com.android.moment.moment.net.model.component.ResourcePath;
import com.android.moment.moment.net.model.observer.AbstractFieldsObservable;
import com.android.moment.moment.net.model.observer.Field;
import com.android.moment.moment.net.model.observer.FieldObserver;

import java.io.Serializable;

public class Profile extends AbstractFieldsObservable implements Model, Serializable {

    public static final Field<String> NAME = new Field<String>("name");
    public static final Field<String> AVATAR = new Field<String>("avatar");

    private final ResourcePath res;

    private String name;
    private String avatar;
    private String subscriptionId;

    public Profile(ResourcePath res) {
        this.res = res;
    }

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
    public void setName(String name) {
        this.name = name;
        this.notifyFieldObservers(NAME, name);
    }

    public String getName() {
        return name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    //********************* GETTERS *********************
    @Override
    public ResourcePath getResourcePath() {
        return res;
    }

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
