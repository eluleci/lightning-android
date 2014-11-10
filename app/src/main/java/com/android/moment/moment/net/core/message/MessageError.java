package com.android.moment.moment.net.core.message;

import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * This class stores the data for message errors. Types of the errors are
 * separated as subclasses.
 *
 */
public class MessageError {

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public JSONObject getErrors() {
        return errors;
    }

    private final int code;
	private final String message;
	private JSONObject errors;

	public MessageError(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public class General {

		public static final int INVALID_TX_FIELD = 1;
		public static final int LOGIN_REQUIRED = 403;
		public static final int BAD_REQUEST = 400;
		public static final int NOT_FOUND = 404;
		public static final int ROUTE_NOT_FOUND = 405;
		public static final int INTERNAL_SERVER_ERROR = 500;
		public static final int SERVICE_UNAVAILABLE = 1337;

	}

    public static final int NOT_MODIFIED = 711;

	public class Authentication {

		public static final int INVALID_LOGIN_MESSAGE = 13;
		public static final int INVALID_ACCESS_TOKEN = 14;
		public static final int AUTHERIZATION_SERVER_PROBLEM = 15;
		public static final int INVALID_CREDITENTIALS = 16;
		public static final int ACCOUNT_EXPIRED = 17;
		public static final int EMAIL_ALREADY_EXISTS = 18;
		public static final int INVALID_CONFIRMATION_TOKEN = 19;

	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
