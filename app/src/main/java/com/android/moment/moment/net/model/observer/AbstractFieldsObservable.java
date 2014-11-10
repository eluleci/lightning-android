package com.android.moment.moment.net.model.observer;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * Abstract implementation of FieldsObservable just for the sake of code re-use.
 */
public abstract class AbstractFieldsObservable implements FieldsObservable {

    private static final String TAG = "AbstractFieldsObservable";
    private final Map<Field, ArrayList<WeakObserverReference>> fieldObserverMap =
            Collections.synchronizedMap(new HashMap<Field, ArrayList<WeakObserverReference>>());
    private final static Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    private Queue<WeakReference> toRemove = new LinkedList<WeakReference>();


    /**
     * adds a FieldObserver to this, must call onAddFieldObserver.
     *
     * @param field         the field that wants to be observed, must be part Field of this
     * @param fieldObserver the observer that observes the Field
     * @param <E>           the type of the Field and FieldObserver to guarantee compatibility
     * @return LifeTimeBinder for binding the fieldObserver's lifetime to another object (e.g. for anonymous classes)
     */
    public synchronized final <E> void addFieldObserver(Field<? extends E> field, FieldObserver<? super E> fieldObserver) {
        // if there is already a list with that tag, insert into that list
        // else if there is no list associated with that tag, create it
        ArrayList<WeakObserverReference> existingList = fieldObserverMap.get(field);
        if (existingList == null) {
            existingList = new ArrayList<WeakObserverReference>();
            fieldObserverMap.put(field, existingList);
        }
        existingList.add(new WeakObserverReference(fieldObserver));
        onAddFieldObserver(field, fieldObserver);
    }

    /**
     * removes a FieldObserver from this if exists, must call onRemoveFieldObserver.
     *
     * @param field         the Field that was observed
     * @param fieldObserver the FieldObserver to be removed
     * @param <E>           the type of the observer and field
     */
    public synchronized final <E> void removeFieldObserver(Field<? extends E> field, FieldObserver<? super E> fieldObserver) {
        // fieldObserverMap contains ArrayList<WeakObserverReference>,
        // therefore just calling remove(fieldObserver) on them will not remove the fieldObserver,
        // because       aFieldObserver != (WeakObserverReference to aFieldObserver)
        if (fieldObserverMap.containsKey(field)) {
            fieldObserverMap.get(field).remove(fieldObserver);
            ArrayList<WeakObserverReference> observerList = fieldObserverMap.get(field);
            for (WeakObserverReference weakReference : observerList) {
                if (weakReference.get() == fieldObserver) {
                    observerList.remove(weakReference);
                    break;
                }
            }
            onRemoveFieldObserver(field, fieldObserver);
        }
    }

    @Override
    public abstract <E> void onAddFieldObserver(Field<? extends E> field, FieldObserver<? super E> fieldObserver);

    @Override
    public abstract <E> void onRemoveFieldObserver(Field<? extends E> field, FieldObserver<? super E> fieldObserver);

    @Override
    public synchronized final <E> void notifyFieldObservers(final Field<E> field, final E data) {

        List<WeakObserverReference> fieldsList = fieldObserverMap.get(field);
        //if there are Observers for this field, we notify them
        if (fieldsList != null) {

            for (final WeakObserverReference weakReference : fieldsList) {
                final FieldObserver temp = weakReference.get();

                if (temp != null) {
                    uiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            temp.updateData(field, data);
                        }
                    });
                } else {
                    toRemove.add(weakReference);
                }
            }

            while (!toRemove.isEmpty()) {
                fieldsList.remove(toRemove.poll());
            }
        }

        notifyObjectObserver(field, data);
    }
    /**
     * This method is called while notifying subscribers and used for notifying observers which
     * are waiting any change on any field.
     *
     * @param field
     * @param data
     */
    private synchronized <E> void notifyObjectObserver(final Field<E> field, final E data) {
        //if there are Observers for the field ALL_FIELDS, we notify them.
        List<WeakObserverReference> fieldsList = fieldObserverMap.get(Field.ALL_FIELDS);
        //if there are Observers for this field, we notify them
        if (fieldsList != null) {

            for (final WeakObserverReference weakReference : fieldsList) {
                final FieldObserver temp = weakReference.get();
                if (temp != null) {
                    uiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            temp.updateData(field, data);
                        }
                    });
                } else {
                    toRemove.add(weakReference);
                }
            }
            while (!toRemove.isEmpty()) {
                fieldsList.remove(toRemove.poll());
            }
        }
    }

    private class WeakObserverReference extends WeakReference<FieldObserver> {

        public WeakObserverReference(FieldObserver r) {
            super(r);
        }

        private String tag = "";

        public String getTag() {
            return tag;
        }
    }
}
