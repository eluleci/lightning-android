package com.android.moment.moment.net.core.manager;

import android.util.Log;

import com.android.moment.moment.db.manager.PersistenceManager;
import com.android.moment.moment.net.core.handler.ListMessageHandler;
import com.android.moment.moment.net.core.handler.MessageHandler;
import com.android.moment.moment.net.core.handler.ModelMessageHandler;
import com.android.moment.moment.net.model.Model;
import com.android.moment.moment.net.model.ObservableList;
import com.android.moment.moment.net.model.component.ResourcePath;
import com.android.moment.moment.network.WebSocketClient;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manager provides access to Model objects which reflect model on server.
 */
public class ManagerOld {
    private static final String TAG = "Manager";
    private PersistenceManager persistenceManager;
    protected final Map<ResourcePath, ModelMessageHandler> centralHandlerRegistry;
    protected WebSocketClient websocketClient;

    /**
     * sets the resourcePath of the user that cannot be unsubscribed. This user will be excluded
     * from any Unsubscribe-Messages
     *
     * @param activeUserRes
     */
    public void setExcludedUser(ResourcePath activeUserRes) {
        if (activeUserRes == null) {
            throw new IllegalArgumentException("activeUserRes must not be null");
        } else if (!activeUserRes.getResourceType().equals(ResourcePath.Resource.PROFILES)) {
            throw new IllegalArgumentException("Only a Profiles-RessourcePath can be excluded.");
        }
        this.activeUserRes = activeUserRes;
    }

    private ResourcePath activeUserRes;

    private boolean multiThreading = true;

    public ManagerOld(WebSocketClient websocketClient, Map<ResourcePath, ModelMessageHandler> centralHandlerRegistry) {

        if (websocketClient == null) {
            throw new IllegalArgumentException("webSocketClient must not be null.");
        } else if (centralHandlerRegistry == null) {
            throw new IllegalArgumentException("centralHandlerRegistry must not be null");
        }
        this.centralHandlerRegistry = centralHandlerRegistry;
        this.websocketClient = websocketClient;
    }

    /**
     * resubscribes a messageHandler with specified fields
     *
     * @param model  the Model to resubscribe
     * @param fields the fields that will be used for resubscription
     */
    public void resubscribeModel(Model model, Set<String> fields) {
        if (model == null) {
            throw new IllegalArgumentException("Model must not be null");
        }
        ModelMessageHandler existingHandler = centralHandlerRegistry.get(model.getResourcePath());
        if (existingHandler == null) {
            throw new IllegalArgumentException("No handler found for the Model: " + model.getResourcePath());
        }
        updateModelData(existingHandler, fields, true);
    }

    /**
     * activates persistence to store data for offline use.
     *
     * @param persistenceManager the manager to grant access to persistence system
     */
    public final void activatePersistence(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    /**
     * deactivates any persistence system. Any data will be gone as soon as the app is closed.
     */
    public final void deactivatePersistence() {
        persistenceManager = null;
    }

    /**
     * Unsubscribes a model from server-updates.
     * In case of a List, the list-elements will NOT be unsubcribed.
     *
     * @param model a Model object
     * @return true if the model (or an element in case of a list) has been unsubscribed,
     * false otherwise
     */
    public boolean unsubscribeModel(Model model) {
        /*if (model == null) {
            throw new IllegalArgumentException("Model must not be null");
        } else if (model.getResourcePath().equals(activeUserRes)) {
            return false;
        }
        Log.d(TAG, "Unsubscribing from " + model.getResourcePath());
        ModelMessageHandler messageHandler = centralHandlerRegistry.get(model.getResourcePath());
        if (messageHandler != null) {
            messageHandler.setSubscribeOnExecute(false);
            return true;
        } else {
            if (model instanceof Profile) {
                Log.e(TAG, "Tried to unsubscribe a model, but could not find the corresponding " +
                        "ModelMessageHandler for " + ((Profile) model).getDisplayName());
            } else {
                Log.e(TAG, "Tried to unsubscribe a model, but could not find the corresponding " +
                        "ModelMessageHandler for " + model.getResourcePath());
            }

            return false;
        }*/
        return false;
    }

    /**
     * Unsubscribes all element inside the List from server updates.
     * If first element is an Entry, the List will be threated as List<Entry>
     *
     * @param list the list of Model
     * @return true if an element has been unsubscribed
     */
    public boolean unsubscribeListElements(List<? extends Model> list) {
        /*if (list == null) {
            return false;
        }
        List<String> subscriptionIds = new ArrayList<String>();

        if (!list.isEmpty() && list.get(0) instanceof Entry) {
            Log.i(TAG, "List<? extends Model> has been detected to be a List of Entry." +
                    "Try to unsubscribe from Entry-Elements");
            List<Entry> modelList = new ArrayList<Entry>();
            try {
                for (int i = 0; i < list.size(); i++) {
                    modelList.add((Entry) list.get(i));
                }
                subscriptionIds.addAll(unsubscribeEntryListElements(modelList));
            } catch (ClassCastException e) {
                Log.i(TAG, "First element was Entry, but not all elements in List were Entry." +
                        "Trying normal unsubscribeListElements. unsubscribeListElements is not" +
                        "suitable for mixed Lists, containing Entries and normal Models", e);
            }

        }

        for (Model model : list) {
            if (model.getSubscriptionId() != null) {
                subscriptionIds.add(model.getSubscriptionId());
                ModelMessageHandler messageHandler = centralHandlerRegistry.get(model.getResourcePath());
                if (messageHandler != null) {
                    messageHandler.setSubscribeOnExecute(false);
                } else {
                    Log.e(TAG, "Unsubscribe Model without MessageHandler");
                }
            }
        }

        if (!subscriptionIds.isEmpty()) {
            delayedHandlerExecutor.addIdsToUnsubscribe(subscriptionIds);
            return true;
        }*/
        return false;
    }

    /**
     * Updates Model data and guarantees up-to-date data for specified fields.
     *
     * @param existingHandler the handler containing the Model
     * @param subscribe       if handler should be subscribed for push messages
     * @param fields          the fields that should
     */
    public synchronized void updateModelData(final ModelMessageHandler existingHandler,
                                             final Set<String> fields, final boolean subscribe) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                StopWatch stopWatch = new LoggingStopWatch("updateModelData for " + existingHandler.getModel().getResourcePath());
                boolean alreadySubscribed = existingHandler.getSubscribeOnExecute();

                // In case subscribe is true, we try to remove it from delayedUnsubscribe and if it was
                // there (remove() returns true), the model is still subscribed.
                if (subscribe) {
                    existingHandler.getModel().setSubscriptionId(existingHandler.getSubscriptionId());
                    existingHandler.setSubscribeOnExecute(true);
                    alreadySubscribed = true;
                    Log.d(TAG, "removed model from delayed unsubscription" +
                            ", because subscription is desired again");

                }
                boolean fieldsChanged = (fields != null)
                        && !existingHandler.getFields().containsAll(fields);

                if (fieldsChanged) {
                    existingHandler.getFields().addAll(fields);
                }

                if (subscribe && (!alreadySubscribed || fieldsChanged)) {
                    //get from server and subscribe
                    //TODO remove in case of merge subscriptionIds is implemented on backend
//                    if (alreadySubscribed) {
//                        delayedHandlerExecutor.addIdToUnsubscribe(existingHandler.getSubscriptionId());
//                    }

                    existingHandler.setSubscribeOnExecute(true);
                    websocketClient.sendMessage(existingHandler);

                    Log.d(TAG, "Getting data from server and subscribe, for model: "
                            + existingHandler.getModel().getResourcePath()
                            + " fieldsChanged/subscribe/alreadySubscribed: "
                            + fieldsChanged + subscribe + alreadySubscribed);
                    // return ActionDone.GET_AND_SUBSCRIBE;
                } else if (fieldsChanged && !subscribe) {
                    //get from server without subscribe

                    websocketClient.sendMessage(existingHandler);

                    Log.d(TAG, "Getting data from server without subscribe, for model: "
                            + existingHandler.getModel().getResourcePath()
                            + " fieldsChanged/subscribe/alreadySubscribed: "
                            + fieldsChanged + subscribe + alreadySubscribed);

                    // return ActionDone.GET_WITHOUT_SUBSCRIBE;
                } else if (!subscribe && !alreadySubscribed && !fieldsChanged) {
                    //all data is there, but check version, it could be outdated
                    Log.d(TAG, "Using existing data but check version, for model: "
                            + existingHandler.getModel().getResourcePath()
                            + " fieldsChanged/subscribe/alreadySubscribed: "
                            + fieldsChanged + subscribe + alreadySubscribed);

                    // return ActionDone.CHECK_VERSION;
                } else if (alreadySubscribed && !fieldsChanged) {
                    //do nothing model is up to date (already subscribed) and has all data we need
                    Log.d(TAG, "I did nothing, data is already up to date: "
                            + existingHandler.getModel().getResourcePath());

                    // return ActionDone.NONE;
                }
//                stopWatch.stop();
            }
        };
        if (multiThreading) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }

    /**
     * Updates List-Model data to guarantee up-to-date data for all fields that are specified
     * in the handler.
     *
     * @param existingHandler  the handler containing the ListModel
     * @param subscribeOnList  if handler should be subscribed on insertion and deletion
     * @param subscribeOnItems if handler should be subscribed on every item contained
     */
    protected void updateListModelData(ListMessageHandler existingHandler, boolean subscribeOnList, boolean subscribeOnItems) {
        updateListModelData(existingHandler, null, subscribeOnList, subscribeOnItems);
    }


    /**
     * Updates List-Model data to guarantee up-to-date data for specified fields.
     *
     * @param existingHandler  the handler containing the ListModel
     * @param subscribeOnList  if handler should be subscribed on insertion and deletion
     * @param fields           the fields that should
     * @param subscribeOnItems if handler should be subscribed on every item contained
     */
    public void updateListModelData(ListMessageHandler existingHandler, Set<String> fields, boolean subscribeOnList, boolean subscribeOnItems) {
        if (existingHandler == null) {
            throw new IllegalArgumentException("existingHandler must not be null");
        }
//        StopWatch stopWatch = new LoggingStopWatch(TAG + " updateListModelData");
        boolean executeAgain = false;
        if (fields != null) {
            Set<String> existingFields = existingHandler.getFields();
            for (String field : fields) {
                if (!existingFields.contains(field)) {
                    executeAgain = true;
                    break;
                }
            }
            existingHandler.getFields().addAll(fields);
        }

        if (!existingHandler.getSubscribeOnItems()) {
            //means items are not necessarily up-to-date
            existingHandler.setSubscribeOnItems(subscribeOnItems);
            executeAgain = true;
        }
        if (!existingHandler.getSubscribeOnExecute()) {
            //means items are not necessarily up-to-date
            existingHandler.setSubscribeOnExecute(subscribeOnList);
            executeAgain = true;
        }
        if (executeAgain) {
            if (subscribeOnList) {
//                delayedHandlerExecutor.addIdToUnsubscribe(existingHandler.getSubscriptionId());
            }
            if (subscribeOnItems) {
                unsubscribeListElements((ObservableList<Model>) existingHandler.getModel());
            }
            websocketClient.sendMessage(existingHandler);
        }
//        stopWatch.stop();
    }

    public void executeHandler(MessageHandler messageHandler){
        websocketClient.sendMessage(messageHandler);
    }
/*

    public Map<ResourcePath, ModelMessageHandler> getCentralHandlerRegistry() {
        return centralHandlerRegistry;
    }

    public WebSocketClient getWebSocketClient() {
        return websocketClient;
    }

    public void registerHandler(ResourcePath resourcePath, ModelMessageHandler handler) {
        centralHandlerRegistry.put(resourcePath, handler);
    }

    public void unregisterHandler(ResourcePath resourcePath) {
        centralHandlerRegistry.remove(resourcePath);
    }

    public boolean isMultiThreading() {
        return multiThreading;
    }

    public void setMultiThreading(boolean multiThreading) {
        this.multiThreading = multiThreading;
    }
*/

}
