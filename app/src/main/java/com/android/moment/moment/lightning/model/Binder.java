package com.android.moment.moment.lightning.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Binder is a helper class to collect bindings between FieldObservers
 * and a Observable's fields methods to manage them.
 * g * Binder is thread-safe. All methods of Binder are synchronized.
 * Binder stores a reference to the FieldObserver and the Model internally.
 */
public class Binder {

    private List<Binding> bindings = new ArrayList<Binding>();

    private List<Binder> rowBinders = new ArrayList<Binder>();

    public Binder() {
        bindings = new ArrayList<Binding>();
        rowBinders = new ArrayList<Binder>();
        rowBinders.add(this);
    }

    public synchronized <E> void bind(Observable observable, String key, Observer observer) {
        observable.addObserver(key, observer);
        bindings.add(new Binding(observable, key, observer));
    }

    public synchronized void unbindAll() {
        for (Binding current : bindings) {
            current.observable.removeObserver(current.key, current.observer);
        }
        bindings.clear();
    }

    /**
     * Returns a Set of all Model objects that this Binder has bound to a FieldObserver.
     *
     * @return Set of models that were bound by this
     */
    /*public synchronized Set<Model> getBoundedModels() {
        return models;
    }*/

    /**
     * removes all FieldObservers from an Observable object, IF it has been bound with this Binder
     *
     * @param observable the Observable to unbind from
     */
    public synchronized void unbindAllFromObservable(Observable observable) {
        for (int i = 0, limit = bindings.size(); i < limit; i++) {
            if (bindings.get(i).observable == observable) {
                bindings.get(i).observable.removeObserver(bindings.get(i).key, bindings.get(i).observer);
                bindings.remove(bindings.get(i));
            }
        }
    }

    public synchronized boolean unbind(Observer observer) {
        for (int i = 0, limit = bindings.size(); i < limit; i++) {
            if (bindings.get(i).observer == observer) {
                bindings.get(i).observable.removeObserver(bindings.get(i).key, observer);
                bindings.remove(bindings.get(i));
                return true;
            }
        }
        return false;
    }

    /**
     * Class for storing bindings.
     */
    private static class Binding {

        Binding(Observable observable, String key, Observer observer) {
            this.observable = observable;
            this.key = key;
            this.observer = observer;
        }

        private Observable observable;
        private String key;
        private Observer observer;
    }

    /**
     * Creates a new Binder that is attached to this.
     *
     * @return the newly created Binder
     */
    public Binder createSubBinder() {
        Binder rowBinder = new Binder();
        rowBinders.add(rowBinder);
        return rowBinder;
    }

    /**
     * returns all Binders created by this.
     *
     * @return List of Binders
     */
    public List<Binder> getRowBinders() {
        return rowBinders;
    }
}
