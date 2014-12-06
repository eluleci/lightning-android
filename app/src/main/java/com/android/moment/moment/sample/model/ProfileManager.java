package com.android.moment.moment.sample.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eluleci on 02/12/14.
 */
public class ProfileManager {

    private Map<String, Profile> existingProfiles = new HashMap<String, Profile>();

    /**
     * Returns a profile with the given resource id. It returns the existing profile if the profile
     * already exists in the list.
     *
     * @param res id of the requested object
     * @return
     */
    public Profile getProfile(String res) {

        if (existingProfiles.containsKey(res)) {
            // returning existing profile
            return existingProfiles.get(res);

        } else {
            // creating a new instance
            Profile profile = new Profile(res);

            // adding new object to reference list
            existingProfiles.put(res, profile);

            // sending message for getting its' data
            profile.getProfileMessageHandler().execute();

            // returning the instance
            return profile;
        }
    }
}
