package com.android.moment.moment.net.core.message;

/**
 * This interface contains all the parameters while sending and receiving
 * messages.
 *
 * @author eluleci
 */
public interface Parameters {

    public static final String CID = "cid";
    public static final String AUTHENTICATED = "authenticated";
    public static final String USER_ID = "userId";
    public static final String ERROR = "error";
    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String RID = "rid";
    public static final String OPTS = "opts";
    public static final String BODY = "body";
    public static final String METADATA = "metadata";
    public static final String CMD = "cmd";
    public static final String RES = "res";
    public static final String _RES = "_res";
    public static final String SUBSCRIPTION = "subscription";
    public static final String SELF_SUBSCRIPTION = "selfSubscription";
    public static final String SUBSCRIPTIONS = "subscriptions";
    public static final String AUTHOR = "author";
    public static final String ITEMS = "items";
    public static final String LIKED_BY_ME = "likedByMe";
    public static final String LIKERS_SIZE = "likers$size";
    public static final String ENTRY = "entry";
    public static final String STATUS = "status";
    public static final String WHEN = "when";
    public static final String OBJECT = "object";
    public static final String CONTENT = "content";

    /**
     * Push message parameters
     */
    public static final String OPS = "ops";
    public static final String FIELD = "field";
    public static final String VALUE = "value";

}
