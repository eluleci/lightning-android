package com.android.moment.moment.net.model;

import android.os.Handler;
import android.os.Looper;

import com.android.moment.moment.net.model.component.ResourcePath;
import com.android.moment.moment.net.model.observer.AbstractFieldsObservable;
import com.android.moment.moment.net.model.observer.Field;
import com.android.moment.moment.net.model.observer.FieldObserver;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ObservableListImpl<E> extends AbstractFieldsObservable implements ObservableList<E> {

    private boolean isReceived;
    private Handler uiHandler;
    private final List<E> listComponent;
    private final ResourcePath resourcePath;
    private String subscriptionId;
    private boolean ascending;
    private JSONObject metadata;

    /**
     * Constructor uses ArrayList by default.
     */
    public ObservableListImpl(ResourcePath resourcePath) {
        listComponent = new ArrayList<E>();
        this.resourcePath = resourcePath;
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
        notifyFieldObservers(METADATA, metadata);
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public ResourcePath getResourcePath() {
        return resourcePath;
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
    public void add(int location, E object) {
        listComponent.add(location, object);
        notifyFieldObservers(LIST, ObservableListImpl.this);
    }

    @Override
    public boolean add(final E object) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean b = listComponent.add(object);
                System.out.println("1");
                notifyFieldObservers(LIST, ObservableListImpl.this);
            }
        });
        return true;
    }

    @Override
    public boolean addAll(final int location, final Collection<? extends E> collection) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean b = listComponent.addAll(location, collection);
                notifyFieldObservers(LIST, ObservableListImpl.this);
            }
        });
        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                listComponent.addAll(collection);
                notifyFieldObservers(LIST, ObservableListImpl.this);
            }
        });
        return true;
    }


    @Override
    public void clear() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                listComponent.clear();
                notifyFieldObservers(LIST, ObservableListImpl.this);
            }
        });
    }

    /**
     * Clears the list without notifying FieldObservers.
     */
    public void clearSilent() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                listComponent.clear();
            }
        });
    }

    @Override
    public boolean contains(Object object) {
        return listComponent.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return listComponent.containsAll(collection);
    }

    @Override
    public E get(int location) {
        return listComponent.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return listComponent.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return listComponent.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return listComponent.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return listComponent.lastIndexOf(object);
    }

    @Override
    public ListIterator<E> listIterator() {
        return listComponent.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int location) {
        return listComponent.listIterator(location);
    }

    @Override
    public E remove(int location) {
        E element = listComponent.remove(location);
        notifyFieldObservers(LIST, ObservableListImpl.this);
        return element;
    }

    @Override
    public boolean remove(Object object) {
        boolean b = listComponent.remove(object);
        notifyFieldObservers(LIST, ObservableListImpl.this);
        return b;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean b = listComponent.removeAll(collection);
        notifyFieldObservers(LIST, ObservableListImpl.this);
        return b;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean b = listComponent.retainAll(collection);
        notifyFieldObservers(LIST, ObservableListImpl.this);
        return b;
    }

    @Override
    public E set(int location, E object) {
        E element = listComponent.set(location, object);
        notifyFieldObservers(LIST, ObservableListImpl.this);
        return element;
    }

    @Override
    public int size() {
        return listComponent.size();
    }

    @Override
    public List<E> subList(int start, int end) {
        return listComponent.subList(start, end);
    }

    @Override
    public Object[] toArray() {
        return listComponent.toArray();
    }

    @Override
    public <E> E[] toArray(E[] array) {
        return listComponent.toArray(array);
    }

    @Override
    public void onAddFieldObserver(Field field, FieldObserver fieldObserver) {
        if (field == LIST && isReceived())
            notifyFieldObservers(LIST, this);
    }

    @Override
    public void onRemoveFieldObserver(Field field, FieldObserver fieldObserver) {

    }
}
