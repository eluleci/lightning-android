package com.android.moment.moment.net.model.observer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Field<E> {
    private static final List<Field> allFields = new ArrayList<Field>();
    public static final Field<Object> ALL_FIELDS = new Field<Object>("all");
    public static final Field<Object> NONE = new Field<Object>("none");
    /**
     * Constructs a new Field-Object with a specific name
     *
     * @param name the name of the Field
     */
    public Field(String name) {
        this.name = name;
        allFields.add(this);
    }

    /**
     * @return the name of this field
     */
    public String getName() {
        return name;
    }

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    @Override
    /**
     * compares to fields by comparing their names.
     */
    public boolean equals(Object o) {
        if (!(o instanceof Field)) return false;

        Field o1 = (Field) o;
        return o1.name.equals(this.name);
    }


    /**
     * Parses a Field and returns a Field-object with specified name.
     *
     * @param fieldString the name of the Field-object
     * @return the specific Field if found, Field.NONE otherwise
     */
    public static Field parseField(String fieldString) {
        for (Field f : allFields) {
            if (f.name.equals(fieldString)) {
                return f;
            }
        }
        return NONE;
    }

    /**
     * Constructs a new FieldsBuilder
     *
     * @return the constructed FieldsBuilder
     */
    public static FieldsBuilder getFieldsBuilder() {
        return new FieldsBuilder();
    }

    /**
     * FieldsBuilder is a helper class to create a Set of Fields represented by their name-Strings.
     * FieldsBuilder is not Threadsafe.
     */
    public static class FieldsBuilder {

        /**
         * Constructs a new FieldsBuilder
         */
        public FieldsBuilder() {
        }

        private final HashSet<String> fieldsSet = new HashSet<String>();

        /**
         * Adds a Field's name to the Set of Fields
         *
         * @param field the Field to add
         * @return this
         */
        public FieldsBuilder addField(Field field) {
            fieldsSet.add(field.toString());
            return this;
        }

        /**
         * Adds a String to the Set of Fields
         *
         * @param field the String to add
         * @return this
         */
        public FieldsBuilder addField(String field) {
            fieldsSet.add(field);
            return this;
        }

        /**
         * Adds all elements of a Collection of Strings to the Set
         *
         * @param fields the List of Strings to add
         * @return this
         */
        public FieldsBuilder addFields(Collection<String> fields) {
            fieldsSet.addAll(fields);
            return this;
        }

        /**
         * Builds the Set of Strings and returns it.
         *
         * @return the created Set of Strings
         */
        public Set<String> build() {
            Set<String> fields = new HashSet<String>();
            fields.addAll(fieldsSet);
            return fields;
        }
    }
}