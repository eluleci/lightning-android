package com.android.moment.moment.net.model.observer;

public interface FieldsObservable {

    /**
     * adds a FieldObserver to this, must call onAddFieldObserver.
     *
     * @param field         the field that wants to be observed, must be part Field of this
     * @param fieldObserver the observer that observes the Field
     * @param <E>           the type of the Field and FieldObserver to guarantee compatibility
     * @return LifeTimeBinder for binding the fieldObserver's lifetime to another object (e.g. for anonymous classes)
     */
    public <E> void addFieldObserver(Field<? extends E> field, FieldObserver<? super E> fieldObserver);

    /**
     * removes a FieldObserver from this if exists, must call onRemoveFieldObserver.
     *
     * @param field         the Field that was observed
     * @param fieldObserver the FieldObserver to be removed
     * @param <E>           the type of the observer and field
     */
    public <E> void removeFieldObserver(Field<? extends E> field, FieldObserver<? super E> fieldObserver);

    /**
     * is called after the FieldObserver is added. This method must check if data already exists and notify the FieldObserver if it is the case.
     *
     * @param field         the field that is observed now
     * @param fieldObserver the observer to be notified in case data exists
     * @param <E>           the type of Field and Observer
     */
    public <E> void onAddFieldObserver(Field<? extends E> field, FieldObserver<? super E> fieldObserver);

    /**
     * is called after a FieldObserver is removed.
     *
     * @param field         the Field that was observed by the FieldObserver
     * @param fieldObserver the FieldObserver that observed the Field
     * @param <E>           the type of FieldObserver and Field
     */
    public <E> void onRemoveFieldObserver(Field<? extends E> field, FieldObserver<? super E> fieldObserver);

    /**
     * notifies the FieldObservers with new data of the field
     *
     * @param field the Field whose/which data has changed
     * @param data  the most up-to-date data of the Field
     * @param <E>   the type of the field and data
     */
    public <E> void notifyFieldObservers(final Field<E> field, final E data);
}