package com.android.moment.moment.lightning.model;

import android.util.Log;

import com.android.moment.moment.lightning.net.LOListHandler;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by eluleci on 12/11/14.
 */
public class LightningObjectList implements List<LightningObject> {

    private static final String TAG = "LightningObjectList";
    private Lightning lightning;

    private List<LightningObject> list;

    protected final String res;
    private JSONArray body;

    private LOListHandler listHandler;
    private List<Observer> observers;

    public Lightning getLightning() {
        return lightning;
    }

    public void setLightning(Lightning lightning) {
        this.lightning = lightning;
    }

    public void addObserver(Observer o) {
        observers.add(o);
        System.out.println("os: " + observers.size());
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
        System.out.println("os: " + observers.size());
    }

    public void notifyObservers() {
        for (Observer o : observers) {
            o.update("size", list.size());
        }
    }

    protected LightningObjectList(String res) {
        this.res = res;
        this.list = new ArrayList<LightningObject>();
        this.observers = new ArrayList<Observer>();
        this.listHandler = new LOListHandler(this);
    }

    public String getRes() {
        return res;
    }

    public JSONArray getBody() {
        return body;
    }

    public void setBody(JSONArray body) {
        this.body = body;
    }

    public void fetch() {
        listHandler.getListData();
    }

    public boolean containsRes(String res) {
        for (LightningObject lo : list) {
            if (lo.getRes().equals(res)) return true;
        }
        return false;
    }

    public void save() {

        for (LightningObject o : list) {
            if (o.getId() == null) {
                // it means that the object is totally new item and it will be created under the
                // domain of this list
                o.setRes(res);
            }
            o.save();
        }
    }

    public LOListHandler getDataHandler() {
        return listHandler;
    }

    @Override
    public void add(int location, LightningObject object) {
        if (!containsRes(object.getRes())) {
            list.add(location, object);
            notifyObservers();
        } else {
            Log.d(TAG, "Object already exists in list. " + object.getRes());
        }
    }

    @Override
    public boolean add(LightningObject object) {
        if (!containsRes(object.getRes())) {

            boolean result = list.add(object);
            if (result) {
                System.out.println("notf s: " + list.size());
                notifyObservers();
            }
            return result;
        }
        return false;
    }

    @Override
    public boolean addAll(int location, Collection<? extends LightningObject> collection) {
        boolean result = list.addAll(location, collection);
        if (result) notifyObservers();
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends LightningObject> collection) {
        boolean result = list.addAll(collection);
        Log.d(TAG, "Adding all to list. " + result);
        if (result) notifyObservers();
        return result;
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        return list.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return list.containsAll(collection);
    }

    @Override
    public boolean equals(Object object) {
        return list.equals(object);
    }

    @Override
    public LightningObject get(int location) {
        return list.get(location);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public int indexOf(Object object) {
        return list.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Iterator<LightningObject> iterator() {
        return list.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return list.lastIndexOf(object);
    }

    @Override
    public ListIterator<LightningObject> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<LightningObject> listIterator(int location) {
        return list.listIterator(location);
    }

    @Override
    public LightningObject remove(int location) {
        LightningObject result = list.remove(location);
        if (result != null) notifyObservers();
        return result;
    }

    @Override
    public boolean remove(Object object) {
        boolean result = list.remove(object);
        if (result) notifyObservers();
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean result = list.removeAll(collection);
        if (result) notifyObservers();
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return list.retainAll(collection);
    }

    @Override
    public LightningObject set(int location, LightningObject object) {
        return list.set(location, object);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public List<LightningObject> subList(int start, int end) {
        return list.subList(start, end);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return list.toArray(array);
    }
}
