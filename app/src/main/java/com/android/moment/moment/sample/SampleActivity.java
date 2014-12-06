package com.android.moment.moment.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.android.moment.moment.lightning.message.MessageActionListener;
import com.android.moment.moment.lightning.message.MessageError;
import com.android.moment.moment.lightning.message.PushMessage;
import com.android.moment.moment.lightning.model.Binder;
import com.android.moment.moment.lightning.model.Observer;
import com.android.moment.moment.lightning.net.MessageHandler;
import com.android.moment.moment.sample.model.Profile;
import com.android.moment.moment.sample.model.ProfileManager;
import com.android.moment.moment.views.ObserverTextView;
import com.android.moment.moment.views.UrlImageView;

/**
 * Created by eluleci on 02/12/14.
 */
public class SampleActivity extends Activity {

    private ProfileManager profileManager = new ProfileManager();
    private Binder binder = new Binder();
    private Profile profile = new Profile();

    private ObserverTextView textView;
    private UrlImageView imageView;
    private UrlImageView statusImageView;

    /**
     * This activity has no functionality. It is used only for code snippets
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /**
         * Getting same object reference from manager in different activities and fragments
         */
        profile = profileManager.getProfile("/Profile/123");


        /**
         * 1.
         *
         * Listening data receive process and watching for changes. The most basic implementation.
         */
        profile.getProfileMessageHandler().setMessageActionListener(new MessageActionListener<Profile>() {
            @Override
            public void onSuccess(MessageHandler messageHandler, Profile profile) {

                // onSuccess() is called when a successful response to a request is received

                if (profile.getName() != null) {
                    textView.setText(profile.getName());
                }

                if (profile.getAvatar() != null) {

                    // load the avatar bitmap from URL and apply it to imageView
                    Bitmap bitmap = Bitmap.createBitmap(null);
                    imageView.setImageBitmap(bitmap);
                }

            }

            @Override
            public void onError(MessageHandler messageHandler, MessageError error) {

                // onError() is called when the request fails somehow

                // TODO Handle error
            }

            @Override
            public void onPushMessage(PushMessage pushMessage) {

                // onPushMessage() is called when there is a change on an object that this
                // connection is subscribed to

                if (profile.getName() != null) {
                    textView.setText(profile.getName());
                }

                if (profile.getAvatar() != null) {

                    // load the avatar bitmap and apply it to imageView
                    Bitmap bitmap = Bitmap.createBitmap(null);
                    imageView.setImageBitmap(bitmap);
                }
            }
        });


        /**
         * 2.
         *
         * Adding observers to specific fields of the model
         */
        profile.addObserver("name", new Observer<String>() {
            @Override
            public void update(String key, String value) {
                // name is retrieved for the first time or updated with a push message
                textView.setText(value);
            }
        });


        /**
         * 3.
         *
         * Creating custom UI views and using them as observer
         */
        profile.addObserver("name", textView);
        profile.addObserver("avatar", imageView);
        profile.addObserver("status", statusImageView);

        /**
         * Removing all observers from the observer one by one
         */
        profile.removeObserver("name", textView);
        profile.removeObserver("avatar", imageView);
        profile.removeObserver("status", statusImageView);


        /**
         * 4.
         *
         * Using Binder objects for binding observers to observables
         */
        binder.bind(profile, "name", textView);
        binder.bind(profile, "avatar", imageView);
        binder.bind(profile, "status", statusImageView);

        /**
         * Removing all observers from the observer via binder
         */
        binder.unbindAll();
    }
}
