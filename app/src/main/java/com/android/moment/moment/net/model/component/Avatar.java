package com.android.moment.moment.net.model.component;

import org.json.JSONException;
import org.json.JSONObject;

public class Avatar extends Targets {

    private final String id;
    private String published;

    public Avatar(String id, String published) {
        this.id = id;
        this.published = published;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getId() {
        return id;
    }


    /**
     * parses an Avatar form a JSONObject that represents an avatar
     *
     * @param avatarObject
     * @return
     * @throws org.json.JSONException
     */
    public static Avatar createFromJsonObject(JSONObject avatarObject) throws JSONException {
        String avatarId = avatarObject.getString("id");
        String published = avatarObject.getString("published");
        Avatar avatar = new Avatar(avatarId, published);

        JSONObject targets = avatarObject.getJSONObject("targets");
        avatar.updateImages(Targets.createFromJsonObject(targets));
        return avatar;
    }
}