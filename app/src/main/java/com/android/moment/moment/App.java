package com.android.moment.moment;

import android.app.Application;
import android.content.res.Configuration;

import com.parse.Parse;

/**
 * Created by eluleci on 10/11/14.
 */
public class App extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "oxjtnRnmGUKyM9SFd1szSKzO9wKHGlgC6WgyRpq8", "kVXJaN62AwG9DZO9QchyC7LIAC11bq2WziIzarxE");
    }


}
