package com.android.moment.moment.net.model.observer;


import com.android.moment.moment.net.model.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Binder is a helper class to collect bindings between FieldObservers
 * and a Observable's fields methods to manage them.
 * g * Binder is thread-safe. All methods of Binder are synchronized.
 * Binder stores a reference to the FieldObserver and the Model internally.
 */
public class Binder {

    private Set<Model> models = new HashSet<Model>();
    private List<Binding> bindings = new ArrayList<Binding>();

    private List<Binder> rowBinders = new ArrayList<Binder>();

    public Binder() {
        models = new HashSet<Model>();
        bindings = new ArrayList<Binding>();
        rowBinders = new ArrayList<Binder>();
        rowBinders.add(this);
    }

    /**
     * Binds an Observer to a Observable's Field
     *
     * @param observable the Observable to bind to
     * @param field      the Field of the Observable to bind to
     * @param observer   the FieldObserver to be updated by Observable
     * @param <E>        the Type of the Field
     */
    public synchronized <E> void bind(FieldsObservable observable, Field<E> field, FieldObserver<? super E> observer) {
        observable.addFieldObserver(field, observer);
        bindings.add(new Binding(observable, field, observer));
        if (observable instanceof Model) {
            models.add((Model) observable);
        }
    }

    /**
     * removes all Observers that was added with this Binder.
     */
    public synchronized void unbindAll() {
        for (Binding current : bindings) {
            current.observable.removeFieldObserver(current.field, current.fieldObserver);
        }
        bindings.clear();
    }

    /**
     * Returns a Set of all Model objects that this Binder has bound to a FieldObserver.
     *
     * @return Set of models that were bound by this
     */
    public synchronized Set<Model> getBoundedModels() {
        return models;
    }

    /**
     * removes all FieldObservers from an Observable object, IF it has been bound with this Binder
     *
     * @param observable the Observable to unbind from
     */
    public synchronized void unbindAllFromObservable(FieldsObservable observable) {
        for (int i = 0, limit = bindings.size(); i < limit; i++) {
            if (bindings.get(i).observable == observable) {
                bindings.get(i).observable.removeFieldObserver(bindings.get(i).field, bindings.get(i).fieldObserver);
                bindings.remove(bindings.get(i));
            }
        }
    }

    /**
     * removes a FieldObserver from its Observable Field, IF it has been bound with this Binder.
     *
     * @param observer the FieldObserver that should be unbind
     */
    public synchronized boolean unbind(FieldObserver observer) {
        for (int i = 0, limit = bindings.size(); i < limit; i++) {
            if (bindings.get(i).fieldObserver == observer) {
                bindings.get(i).observable.removeFieldObserver(bindings.get(i).field, observer);
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
        Binding(FieldsObservable observable, Field field, FieldObserver observer) {
            this.field = field;
            this.observable = observable;
            this.fieldObserver = observer;
        }

        private FieldsObservable observable;
        private Field field;
        private FieldObserver fieldObserver;
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
