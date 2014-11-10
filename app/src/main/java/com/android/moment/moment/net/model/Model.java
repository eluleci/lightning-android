package com.android.moment.moment.net.model;

import com.android.moment.moment.net.model.component.ResourcePath;
import com.android.moment.moment.net.model.observer.Field;
import com.android.moment.moment.net.model.observer.FieldsObservable;

/**
 * Interface for model classes
 */
public interface Model extends FieldsObservable {
    /**
     *
     * @return the unique resourcePath of this model object, corresponding to the object on server.
     */
    public String getId();
    public static final Field<Model> DELETED = new Field<Model>("deleted");

    public void setSubscriptionId(String id);
    public String getSubscriptionId();
}
