package com.android.moment.moment.sample.model;

import com.android.moment.moment.lightning.model.Observable;
import com.android.moment.moment.lightning.model.Observer;
import com.android.moment.moment.sample.net.ProfileMessageHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eluleci on 02/12/14.
 */
public class Profile implements Observable {

    public final String NAME = "name";
    public final String AVATAR = "avatar";
    public final String STATUS = "status";

    private ProfileMessageHandler profileMessageHandler;
    private Map<String, List<Observer>> observers = new HashMap<String, List<Observer>>();

    private String res;
    private String name;
    private String avatar;
    private String status;

    public Profile() {
        profileMessageHandler = new ProfileMessageHandler(this);
    }

    public Profile(String res) {
        this.res = res;
    }

    public ProfileMessageHandler getProfileMessageHandler() {
        return profileMessageHandler;
    }

    public void setName(String name) {
        this.name = name;
        notifyObservers(NAME, name);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        notifyObservers(AVATAR, avatar);
    }

    public void setStatus(String status) {
        this.status = status;
        notifyObservers(STATUS, status);
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void addObserver(String key, Observer o) {

        // getting observer list for given key. (create new list if doesn't exist)
        List<Observer> obs = observers.get(key);
        if (obs == null) {
            obs = new ArrayList<Observer>();
            observers.put(key, obs);
        }
        obs.add(o);

        // notifying new observers with the current data
        if (key.equals(NAME) && name != null) {
            notifyObservers(key, getName());
        } else if (key.equals(AVATAR) && avatar != null) {
            notifyObservers(key, getAvatar());
        } else if (key.equals(AVATAR) && status != null) {
            notifyObservers(key, getStatus());
        }
    }

    @Override
    public void removeObserver(String key, Observer o) {
        List<Observer> obs = observers.get(key);
        if (obs == null) return;
        obs.remove(o);
    }

    @Override
    public void notifyObservers(String key, Object value) {

        // notifying the observers of the specified key
        List<Observer> obs = observers.get(key);
        if (obs == null) return;
        for (Observer o : obs) {
            o.update(key, value);
        }
    }
}
