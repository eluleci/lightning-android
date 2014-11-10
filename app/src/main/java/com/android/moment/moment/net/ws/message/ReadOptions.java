package com.android.moment.moment.net.ws.message;

import com.android.moment.moment.net.core.message.MessageOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ReadOptions extends MessageOptions {

    private Set<String> fields;
    private ArrayList<String> values;
    private String query;
    private boolean ascending = true;
    private int limit = 55;
    private boolean totalCounter = true;
    private boolean subscribeOnInsertion = false;
    private boolean subscribeOnItems = false;

    public ReadOptions(Set<String> fields) {
        this.fields = fields;
    }

    public ReadOptions() {

    }

    /**
     * ************ getter and setter *******************
     */
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isSubscribeOnItems() {
        return subscribeOnItems;
    }

    public void setSubscribeOnItems(boolean subscribeOnItems) {
        this.subscribeOnItems = subscribeOnItems;
    }

    public Set<String> getFields() {
        return fields;
    }

    public void setFields(Set<String> fields) {
        if (fields == null) {
            fields = new HashSet<String>();
        }
        this.fields = fields;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isTotalCounter() {
        return totalCounter;
    }

    public void setTotalCounter(boolean totalCounter) {
        this.totalCounter = totalCounter;
    }

    public boolean isSubscribeOnInsertion() {
        return subscribeOnInsertion;
    }

    public void setSubscribeOnInsertion(boolean subscribeOnInsertion) {
        this.subscribeOnInsertion = subscribeOnInsertion;
    }

    /**
     * Builder for Message options
     */
    public static class Builder {
        private final ReadOptions opts = new ReadOptions();

        public Builder setFields(Set<String> fields) {
            opts.fields = fields;
            return this;
        }

        public Builder setAscending(boolean ascending) {
            opts.ascending = ascending;
            return this;
        }

        public Builder setValues(ArrayList<String> values) {
            opts.values = values;
            return this;
        }

        public Builder setLimit(int limit) {
            opts.limit = limit;
            return this;
        }

        public Builder setTotalCounter(boolean getTotalCounter) {
            opts.totalCounter = getTotalCounter;
            return this;
        }

        public Builder setSubscribeOnInsertion(boolean subscribeOnInsertion) {
            opts.subscribeOnInsertion = subscribeOnInsertion;
            return this;
        }

        public Builder setSubscribeOnItems(boolean subscribeOnItems) {
            opts.subscribeOnItems = subscribeOnItems;
            return this;
        }

        public ReadOptions build() {
            return opts;
        }

        public Builder setQuery(String query) {
            opts.query = query;
            return this;
        }
    }

}
