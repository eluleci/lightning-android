package com.android.moment.moment.net.ws;

public class WebSocketClient {}/* implements WebSocketConnectionObserver, Parameters {
    private static final String TAG = "WebSocketClient";
    private static final String TAG2 = "WebSocketClient connect";
    private static final long INTERVALL_FOR_LOGGING = 10000;
    private final String wss = "SERVER_ADDRESS";//Constants.WS_SERVER;
    private final int WAITING_QUEUE_MAX_SIZE = 15;
    private long waitBeforeDisconnect = 3000;
    private boolean authenticated = false;
    private final WebSocketConnection mConnection;
    private String accessToken = null;
    private boolean connecting = false;
    private final Handler uiThreadHandler;
    private final AtomicInteger clientCounter;
    private boolean disconnecting = false;
    private boolean connected = false;
    private boolean multithreaded = true;
    private boolean logSubscriptions = false;

    *//**
     * activates/deactivates multithreading of WebSocketClient.
     * Default is true;
     *
     * @param multithreaded true for activation, false for deactivation
     *//*
    public void setMultithreaded(boolean multithreaded) {
        this.multithreaded = multithreaded;
    }


    // List of the connection status listeners which binds themselves as listener for connection status.
    private final List<ConnectionStatusListener> connectionStatusListeners;

    //waiting for execution, e.g. when there is no connection
    private final Queue<MessageHandler> waitingForExecution;

    //List of the message handlers which are waiting for the response to their requests.
    private final Map<Integer, MessageHandler> messageQueue;

    //List of the subscribers.

    private final Map<String, MessageHandler> pushMessageSubscribers;
    private String cid = "";
    private final AtomicInteger txGenerator;

    *//**
     * Constructs a new WebSocketClient with a new default WebSocketConnection
     *//*
    public WebSocketClient() {
        this(new WebSocketConnection());
    }

    *//**
     * Constructs a new WebSocketClient with specified WebSocketConnection
     *
     * @param mConnection the WebSocketConnection to be used by this WebSocketClient
     *//*
    public WebSocketClient(WebSocketConnection mConnection) {
        this.mConnection = mConnection;
        connectionStatusListeners =
                Collections.synchronizedList(new ArrayList<ConnectionStatusListener>());
        waitingForExecution = new LinkedList<MessageHandler>();
        messageQueue = Collections.synchronizedSortedMap(new TreeMap<Integer, MessageHandler>());
        pushMessageSubscribers = Collections.synchronizedMap(new HashMap<String, MessageHandler>() {
        });
        txGenerator = new AtomicInteger(Integer.MIN_VALUE);

        clientCounter = new AtomicInteger(0);
        uiThreadHandler = new Handler(Looper.getMainLooper());
    }

    public WebSocketClient(WebSocketConnection mConnection, List<ConnectionStatusListener> connectionStatusListeners,
                           Queue<MessageHandler> waitingForExecution,
                           Map<Integer, MessageHandler> messageQueue,
                           Map<String, MessageHandler> pushMessageSubscribers) {
        this.mConnection = mConnection;
        this.connectionStatusListeners = Collections.synchronizedList(connectionStatusListeners);
        this.waitingForExecution = waitingForExecution;
        this.messageQueue = Collections.synchronizedMap(messageQueue);
        this.pushMessageSubscribers = Collections.synchronizedMap(pushMessageSubscribers);
        txGenerator = new AtomicInteger(Integer.MIN_VALUE);
        clientCounter = new AtomicInteger(0);
        uiThreadHandler = new Handler(Looper.getMainLooper());
    }


    *//**
     * @return the client counter, which represents number of active clients of this
     *//*
    public int getClientCounter() {
        return clientCounter.get();
    }

    *//**
     * increments the counter of clients. As long as the counter is >0, websocketClient
     * doesn't disconnect
     *//*

    public void incrementClientCounter() {
        clientCounter.getAndIncrement();
    }

    *//**
     * increments the counter of clients. As long as the counter is >0, websocketClient
     * doesn't disconnect. If counter is 0, delayed disconnect will be started.
     *//*
    public void decrementClientCounter() {
        if (clientCounter.decrementAndGet() == 0) {
            this.disconnect();
        }
    }

    *//**
     * if turned on, WebSocketClient will write the current subscribed Models to Log
     *
     * @param logSubscriptions true, if feature should be turned on
     *//*
    public void setLogSubscriptions(boolean logSubscriptions) {
        this.logSubscriptions = logSubscriptions;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    *//**
     * @return true if WebSocketClient is connected, can be unauthenticated
     *//*
    public boolean isConnected() {
        return connected;
    }


    *//**
     * @return true if WebSocketClient is authenticated
     *//*
    public boolean isAuthenticated() {
        return authenticated;
    }

    *//**
     * Connects to web socket server.
     *//*
    public synchronized void connect(String accessToken) {
        this.setAccessToken(accessToken);
        Log.d(TAG2, "WebSocketClient connect. Already connecting =  " + connecting + "  connected = " + connected);

        if (connecting) return;


        if (connected && !authenticated) {
            // In case of using new AccessToken (from RefreshToken) we are already connected but
            // not authenticated. We send a simple login-message.
            login(accessToken);
            return;
        }

        connecting = true;
//        StopWatch stopWatch = new LoggingStopWatch("Websocketclient");

        try {
            String uri = wss;
            if (cid.length() > 0) uri += "?cid=" + cid;
            Log.d(TAG, "Connecting to address " + uri);
            URI mServerURI = new URI(uri);
//            StopWatch stopWatch1 = new LoggingStopWatch(TAG + " connect");
            mConnection.connect(mServerURI, this);
//            stopWatch1.stop();
        } catch (URISyntaxException e) {
            Log.e(TAG, "URI Syntax Exception");
            e.printStackTrace();
        } catch (WebSocketException e) {
            Log.e(TAG, "Websocket Except");
            e.printStackTrace();
        }
//        stopWatch.stop();
    }

    *//**
     * Disconnects from web socket server.
     *//*
    public synchronized void disconnect() {
        Log.d(TAG2, "WebSocketClient delayed disconnect started");
        if (disconnecting) {
            Log.d(TAG2, "WebSocketClient delayed disconnect already running");
            return;
        }
        disconnecting = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                disconnecting = false;
                try {
                    Thread.sleep(waitBeforeDisconnect);
                } catch (InterruptedException e) {
                    Log.e(TAG, "sleep has been interrupted, disconnect cancelled");
                }
                if (clientCounter.get() == 0 && WebSocketClient.this.isAuthenticated()) {
                    Log.d(TAG2, "WebSocketClient disconnecting");

                    mConnection.disconnect();
                    accessToken = null;

                    synchronized (pushMessageSubscribers) {
                        for (MessageHandler current : pushMessageSubscribers.values()) {
                            current.setSubscribeOnExecute(false);
                            current.setSubscriptionId(null);
                        }
                        pushMessageSubscribers.clear();
                    }
                    notifyConnectionListeners(Status.CLOSED);
                } else {
                    Log.d(TAG2, "WebSocketClient disconnecting has been canceled");
                }
            }
        };
        if (multithreaded) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }

    *//**
     * Authenticates the connection with the user access token.
     *//*
    public void authenticate(String accessToken) {
        Log.d(TAG, "AUTHENTICATING with access token: " + accessToken);
        if (accessToken != null) {
            try {
                JSONObject authMessage = new JSONObject();
                authMessage.put("initialize", true);
                authMessage.put("clientVersion", Constants.WS_CLIENT_VERSION);
                authMessage.put("accessToken", accessToken);
                Log.d(TAG, authMessage.toString());
                mConnection.sendTextMessage(authMessage.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    *//**
     * sends login-message with accessToken
     *
     * @param accessToken the accessToken used to login
     *//*
    public void login(String accessToken) {
        Log.d(TAG, "LOGIN with access token: " + accessToken);
        if (accessToken != null) {
            try {
                JSONObject authMessage = new JSONObject();
                authMessage.put("login", true);
                authMessage.put("accessToken", accessToken);
                Log.d(TAG, authMessage.toString());
                mConnection.sendTextMessage(authMessage.toString());
            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
        }
    }

    *//**
     * Sends a request message to the server.
     *
     * @param requestMessageHandler is the handler that calls this method.
     *//*
    public synchronized void sendMessage(MessageHandler requestMessageHandler) {
        if (requestMessageHandler == null) {
            throw new IllegalArgumentException("Parameter requestMessageHandler must not be null.");
        }
        if (!authenticated) {
            Log.i(TAG, "Not authenticated, add MessageHandler to waitingList");
            waitingForExecution.add(requestMessageHandler);
            if (waitingForExecution.size() > WAITING_QUEUE_MAX_SIZE) {
                waitingForExecution.poll();
            }
            return;
        }

        // getting prepared message from handler and generating tx for message
        Message message = requestMessageHandler.prepareMessage();
        if (message == null) {
            Log.e(TAG, "Handlers message is null while sending message. "
                    + requestMessageHandler.getClass().toString());
            return;
        }
        message.setRid(generateNextTx());

        // mapping message handler with tx value and sending message
        messageQueue.put(message.getRid(), requestMessageHandler);
        mConnection.sendTextMessage(message.toString());

        Log.d(TAG, "Sending a request message: " + message.toString());
    }

    *//**
     * This method is called when web socket connection is opened. Authentication process starts after
     * opening a connection.
     *//*
    @Override
    public void onOpen() {
        Log.d(TAG, "CONNECTION OPENED");
        connected = true;
        authenticate(accessToken);
    }

    *//**
     * This method is called when web socket connection is closed. All of the connection status
     * listeners are notified.
     *//*
    @Override
    public void onClose(WebSocketCloseNotification code, String reason) {
        Log.d(TAG, "CONNECTION CLOSED. " + reason);
        connecting = false;
        connected = false;
        authenticated = false;
        notifyConnectionListeners(Status.CLOSED);
    }

    *//**
     * This method is called when a message is received. It checks the errors and notifies the message
     * handler if the message has 'tx'.
     *//*
    @Override
    public void onTextMessage(final String payload) {

        try {
            final JSONObject message = new JSONObject(payload);
            Runnable runnable;
            if (message.has(CID)) {

                // If message has field 'cid', it means that this is a response
                // to a login or logout request. So we check for error and then
                // notify message action listeners if there is an error.
                runnable = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Log.v(TAG, "MESSAGE RECEIEVED: " + message.toString(4));
                            handleLoginLogout(message);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getLocalizedMessage(), e);
                        }
                    }

                };
            } else if (message.has(RID)) {
                // If a message has field 'tx', it means that this is a respond
                // to a request that is sent by this client. First we find the
                // message handler which owns the message and then we check for
                // error and notify the message handler. Finally we remove the
                // message handler from the message queue.
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.v(TAG, "MESSAGE RECEIEVED: " + message.toString(4));
                            handleResponse(message);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getLocalizedMessage(), e);
                        }
                    }
                };

            } else {
                // If there is no 'cid' or 'tx' in the message, it means that this is a push message
                // So we first check the subscription id of it and handle errors if the
                // subscription id is not valid. Then we find the MessageHandler which has
                // subscribed with this subscription id. If everything goes well, we just simply
                // create the PushMessage object from the values we get and notify the handler.
                runnable = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Log.v(TAG, "PUSH MESSAGE RECEIEVED: " + message.toString(4));
                            handlePushMessage(message);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getLocalizedMessage(), e);
                        }

                    }

                };
            }
            if (multithreaded) {
                new Thread(runnable).start();
            } else {
                runnable.run();
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    *//**
     * handles Login/Logout message, particularly when it has CID
     *
     * @param message the message from server
     * @throws org.json.JSONException
     *//*
    private void handleLoginLogout(JSONObject message) throws JSONException {

        connecting = false;

        Log.d(TAG, "Authentication response is received.");

        cid = message.getString(CID);
        boolean authenticated = message.getBoolean(AUTHENTICATED);

        if (message.has(ERROR)) { // handling error
            JSONObject errorObject = message.getJSONObject(ERROR);
            Log.e(TAG, "Authentication has error. \n" + errorObject.toString());
            notifyConnectionListeners(Status.AUTHENTICATION_FAILED);
        } else {
            this.authenticated = authenticated;
            String userPath = message.getString(USER_ID);
            notifyConnectionListeners(Status.AUTHENTICATED);
            Log.e(TAG, authenticated + " " + logSubscriptions);
            if (logSubscriptions) {
                new Logger().start();
            }
        }
    }

    *//**
     * handles a message as response of a request, particularly it has field RID
     *
     * @param message the message from server
     * @throws org.json.JSONException
     *//*
    private void handleResponse(JSONObject message) throws JSONException, NullPointerException {
        int tx = message.getInt(RID);
        MessageHandler messageHandler = messageQueue.get(tx);

        if (messageHandler == null) {
            Log.e(TAG, "MessageHandler with tx " + tx + " couldn't be found.");
            return;
        }

        if (message.has(ERROR)) { // handling error

            JSONObject errorObject = message.getJSONObject(ERROR);
            int code = errorObject.getInt(CODE);
            String errorMessage = errorObject.getString(MESSAGE);

            MessageError error = new MessageError(code, errorMessage);
            Log.e(TAG, "Error returned. Message was " + messageHandler.prepareMessage() + "   Error:" + error.toString());

            messageHandler.onError(messageHandler, error);

        } else { // getting message

            Message.Builder messageBuilder = new Message.Builder();

            // if there is opts, add to message builder
            if (message.has(OPTS)) {
                JSONObject opts = message.getJSONObject(OPTS);
                ResponseOptions messageOptions = new ResponseOptions();

                // if message has subscription , add it to subscribers list
                if (opts.has(SUBSCRIPTION)) {
                    String subscription = opts.getString(SUBSCRIPTION);

                    // sometimes subscription tag comes with the string value "null", or it is simply the answer
                    if (!subscription.equals("null") && subscription != null && !(opts.has(CMD) &&
                            Message.Command.valueOf(opts.getString(CMD)) != Message.Command.UNSUBSCRIBE)) {
                        bindSubscription(subscription, messageHandler);
                        messageOptions.setSubscription(subscription);

                        Log.d(TAG, "Handler is subscribed with id " + subscription);
                    }
                }

                messageBuilder.opts(messageOptions);
            }

            // if there is body, add to message builder
            if (message.has(BODY)) {
                JSONObject body = message.getJSONObject(BODY);
                messageBuilder.body(body);
            }

            // if there is metadata, add to message builder
            if (message.has(METADATA)) {
                JSONObject metadata = message.getJSONObject(METADATA);
                messageBuilder.metadata(metadata);
            }

            if (message.has(CMD)) {
                Message.Command masterCmd = Message.Command.parseCommand(message.getString(CMD));
                messageBuilder.cmd(masterCmd);
            }

            Message responseMessage = messageBuilder.build();
            messageHandler.applyResponse(responseMessage);

            messageQueue.remove(tx);
        }
    }

    *//**
     * handles a push message
     *
     * @param message the message from server
     * @throws org.json.JSONException
     *//*
    private void handlePushMessage(JSONObject message) throws JSONException {
        if (message.has(OPTS)) {
            JSONObject opts = message.getJSONObject(OPTS);

            if (opts.has(SUBSCRIPTION)) {
                String subscriptionId = opts.getString(SUBSCRIPTION);

                if (subscriptionId == null) {
                    // Every push message must have a valid subscription id.
                    Log.e(TAG, "Subscription id of push message is null");
                    return;
                }

                if (!pushMessageSubscribers.containsKey(subscriptionId)) {
                    // It means we have subscription which has no handler.
                    Log.e(TAG, "Push message has no handler for id " + subscriptionId + "\n"
                            + message.toString());
                    return;
                }

                MessageHandler messageHandler = pushMessageSubscribers.get(subscriptionId);

                PushMessage.Builder pushMessageBuilder = new PushMessage.Builder();
                pushMessageBuilder.subscription(subscriptionId);

                // add self subscription if have one (used for new creation of read request)
                if (opts.has(SELF_SUBSCRIPTION) && !opts.getString(SELF_SUBSCRIPTION).equals("null")) {
                    pushMessageBuilder.selfSubscription(opts.getString(SELF_SUBSCRIPTION));
                }

                Message.Command masterCmd = null;
                ResourcePath masterRes = null;
                JSONObject body = null;

                if (message.has(CMD)) {
                    masterCmd = Message.Command.parseCommand(message.getString(CMD));
                    pushMessageBuilder.cmd(masterCmd);
                } else {
                    Log.e(TAG, "There is no command tag inside push message.");
                    return;
                }

                // delete message doesn't have 'res'
                if (masterCmd != Message.Command.DELETE && message.has(RES)) {
                    masterRes = ResourcePath.generateResourcePath(message.getString(RES));
                    pushMessageBuilder.res(masterRes);
                }

                if (message.has(BODY)) {
                    body = message.getJSONObject(BODY);

                    // setting body
                    pushMessageBuilder.body(body);

                    // setting 'res' of body
                    if (body.has(_RES)) {
                        ResourcePath res = ResourcePath.generateResourcePath(body.getString(_RES));
                        pushMessageBuilder.bodyRes(res);
                        //we also set the messages res to body's res for intuitive use
                        pushMessageBuilder.res(res);
                    }
                    if (body.has(OPS)) {
                        JSONArray operationsArrayObject = body.getJSONArray(OPS);

                        ArrayList<PushOperation> ops = new ArrayList<PushOperation>();
                        for (int i = 0; i < operationsArrayObject.length(); i++) {

                            JSONObject op = operationsArrayObject.getJSONObject(i);
                            PushOperation o = new PushOperation();

                            if (op.has(CMD))
                                o.setCmd(Message.Command.parseCommand(op.getString(CMD)));

                            o.setField(Field.parseField(op.getString(FIELD)));
                            o.setValue(op.get(VALUE));
                            ops.add(o);
                        }
                        pushMessageBuilder.ops(ops);
                    } else if (masterCmd.equals(Message.Command.CREATE)) {
                        List<PushOperation> ops = new ArrayList<PushOperation>();
                        PushOperation operation = new PushOperation();
                        operation.setCmd(Message.Command.CREATE);
                        operation.setField(Field.NONE);
                        operation.setValue(body);
                        ops.add(operation);
                        pushMessageBuilder.ops(ops);
                    }
                }
                PushMessage pushMessage = pushMessageBuilder.build();
                messageHandler.applyPushMessage(pushMessage);
            } else {
                Log.e(TAG, "no subscription found");
            }
        } else {
            Log.e(TAG, "No opts found in push message.");
        }
    }


    @Override
    *//**
     * This method is not provided by WebSocketClient
     *//*
    public void onRawTextMessage(byte[] payload) {
        throw new UnsupportedOperationException("This method is not supported by WebSocketclient");
    }

    @Override
    *//**
     * This method is not provided by WebSocketClient
     *//*
    public void onBinaryMessage(byte[] payload) {
        throw new UnsupportedOperationException("This method is not supported by WebSocketclient");
    }

    *//**
     * Sets a message handler to handle a messages on a certain subscription id
     *
     * @param subscriptionId
     * @param messageHandler
     *//*
    public void bindSubscription(String subscriptionId, MessageHandler messageHandler) {
        if (messageHandler == null) {
            throw new IllegalArgumentException("messageHandler must not be null");
        }
        if (subscriptionId == null || subscriptionId.equals("null")) {
            throw new IllegalArgumentException("subscriptionId must not be null or \"null\"");
        }
        pushMessageSubscribers.put(subscriptionId, messageHandler);
        messageHandler.setSubscriptionId(subscriptionId);
    }


    *//**
     * sets the accessToken which will be used for establishing connection
     *
     * @param accessToken
     *//*

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setDisconnectOffset(long offset) {
        this.waitBeforeDisconnect = offset;
    }

    public Queue<MessageHandler> getWaitingForExecution() {
        return waitingForExecution;
    }

    public Map<Integer, MessageHandler> getMessageQueue() {
        return messageQueue;
    }

    public List<ConnectionStatusListener> getConnectionStatusListeners() {
        return connectionStatusListeners;
    }

    *//**
     * Inserts the listener to connection status listener list.
     *
     * @param connectionStatusListener
     *//*
    public void addConnectionStatusChangeListener(ConnectionStatusListener connectionStatusListener) {
        if (!connectionStatusListeners.contains(connectionStatusListener)) {
            connectionStatusListeners.add(connectionStatusListener);
        }
    }


    *//**
     * Removes the listener from connection status listener list.
     *
     * @param connectionStatusListener
     *//*
    public void removeConnectionStatusChangeListener(ConnectionStatusListener connectionStatusListener) {
        connectionStatusListeners.remove(connectionStatusListener);
    }

    *//**
     * Notifies all of the connection status change listeners in the list.
     *//*
    private void notifyConnectionListeners(final Status status) {
        Log.d(TAG, "Connection status changed. Status code: " + status);
        if (status == Status.AUTHENTICATED) {
            executeWaitingHandlers();
        }
        if (multithreaded) {
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (connectionStatusListeners) {
                        for (ConnectionStatusListener csl : connectionStatusListeners) {
                            csl.onStatusChanged(status);
                        }
                    }
                }
            });
        } else {
            synchronized (connectionStatusListeners) {
                for (ConnectionStatusListener csl : connectionStatusListeners) {
                    csl.onStatusChanged(status);
                }
            }
        }
    }

    *//**
     * executes waiting messageHandlers
     *//*
    private void executeWaitingHandlers() {
        Log.d(TAG, "Start sending " + waitingForExecution.size() + " waiting messages");
        while (!waitingForExecution.isEmpty()) {
            this.sendMessage(waitingForExecution.poll());
        }
    }

    *//**
     * generates a unique tx value for request messages
     *
     * @return generated value
     *//*
    private int generateNextTx() {
        // please note that this will automatically restart at Integer.MIN_VALUE after reaching INTEGER.MAX_VALUE
        return txGenerator.getAndIncrement();
    }

    *//**
     * removes subscriptionIds from internal subscription-registry.
     * This method does not unsubscribe, unsubscribe from server must be done before.
     *
     * @param data the list of subscriptionIds to remove
     *//*
    public void removeSubscriptionIds(List<String> data) {
        for (String subscriptionId : data) {
            MessageHandler removedHandler = pushMessageSubscribers.remove(subscriptionId);
            ModelMessageHandler handler = (ModelMessageHandler) removedHandler;
            if (removedHandler != null && handler.getSubscriptionId().equals(subscriptionId)) {
                removedHandler.setSubscriptionId(null);
            }
        }
    }

    *//**
     * This is the interface for listening connection status of the web socket client.
     *//*
    public interface ConnectionStatusListener {
        public void onStatusChanged(Status status);
    }

    *//**
     * Presence represents the connection status of the websocket client
     *//*
    public enum Status {
        CONNECTED, AUTHENTICATED, AUTHENTICATION_FAILED, CLOSED
    }

    *//**
     * This class is for debugging and writes out the currently subscribed elements to LogCat.
     * TODO remove for production
     *//*
    private class Logger extends Thread {
        public void run() {
            while (authenticated && logSubscriptions) {
                try {
                    Thread.sleep(INTERVALL_FOR_LOGGING);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Log-thread's sleep has been interrupted");
                }
                StringBuilder builder = new StringBuilder();
                synchronized (pushMessageSubscribers) {
                    List<ModelMessageHandler> collectionsList = new ArrayList<ModelMessageHandler>();
                    List<ModelMessageHandler> modelList = new ArrayList<ModelMessageHandler>();
                    for (String key : pushMessageSubscribers.keySet()) {
                        ModelMessageHandler current = (ModelMessageHandler) pushMessageSubscribers.get(key);
                        if (current.getModel() instanceof ObservableList) {
                            collectionsList.add(current);
                        } else {
                            modelList.add(current);
                        }
                    }
                    builder.append("\n" +
                            "\nCollections that are subscribed (except Notifications): \n \n");
                    for (ModelMessageHandler current : collectionsList) {
                        builder.append("\n").append(current.getSubscriptionId())
                                .append("   ").append(current.getModel().getResourcePath());
                    }
                    builder.append("\n" +
                            "\n Models that are subscribed: \n \n");
                    for (ModelMessageHandler current : modelList) {
                        if (current instanceof ProfileGetHandler) {
                            builder.append("\n").append(current.getSubscriptionId()).append("   ")
                                    .append(current.getModel().getResourcePath()).append("   ")
                                    .append(((ProfileGetHandler) current).getModel().getDisplayName());
                        } else if (current instanceof BoardHandler) {
                            builder.append("\n").append(current.getSubscriptionId()).append("   ")
                                    .append(current.getModel().getResourcePath()).append("   ")
                                    .append(((BoardHandler) current).getModel().getName());
                        } else if (current instanceof ConversationHandler) {
                            builder.append("\n").append(current.getSubscriptionId()).append("   ")
                                    .append(current.getModel().getResourcePath()).append("   ")
                                    .append(((ConversationHandler) current).getModel().getTitle());
                        } else {
                            builder.append("   ").append(current.getSubscriptionId()).append("   ")
                                    .append("\n").append(current.getModel().getResourcePath());
                        }

                    }
                }
                String log = builder.toString();
                Log.d(TAG + " subscriptions", log);
            }
        }
    }
}*/