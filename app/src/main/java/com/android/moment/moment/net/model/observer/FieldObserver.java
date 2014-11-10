package com.android.moment.moment.net.model.observer;

/**
 * FieldObserver can observe a
 * Created by eluleci on 03/01/14.
 */
public interface FieldObserver<E> {

    /**
     * is called when data has changed.
     * @param field the Field-tag of the data
     * @param data the new data
     */
    public void updateData(Field<E> field, E data);

}
