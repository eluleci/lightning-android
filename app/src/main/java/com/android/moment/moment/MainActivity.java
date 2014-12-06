package com.android.moment.moment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.moment.moment.lightning.message.MessageActionListener;
import com.android.moment.moment.lightning.message.MessageError;
import com.android.moment.moment.lightning.message.PushMessage;
import com.android.moment.moment.lightning.model.Binder;
import com.android.moment.moment.lightning.model.Lightning;
import com.android.moment.moment.lightning.model.LightningObject;
import com.android.moment.moment.lightning.model.LightningObjectList;
import com.android.moment.moment.lightning.model.Observer;
import com.android.moment.moment.lightning.net.MessageHandler;
import com.android.moment.moment.lightning.net.WebSocketClient;
import com.android.moment.moment.views.ObserverTextView;
import com.android.moment.moment.views.StatusIconView;
import com.android.moment.moment.views.UrlImageView;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity
        implements WebSocketClient.ConnectionStatusListener, JoinDialogFragment.JoinStatusListener {

    private static final String TAG = "MainActivity";
    private static final int RESULT_LOAD_IMAGE = 23;
    static boolean active;

    private Lightning lightning = new Lightning();
    private LightningObjectList profileList;
    private Binder binder;

    private String currentProfileRes;
    private Observer listObserver = new Observer() {
        @Override
        public void update(String key, Object value) {
            getActionBar().setTitle(profileList.size() + " people");
            imageAdapter.notifyDataSetChanged();
            gridview.invalidateViews();

            if (currentProfileRes != null) {
                checkUserInList();
            }
        }
    };

    private GridView gridview;
    private ImageAdapter imageAdapter = new ImageAdapter();

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getting current user's res from preferences
        currentProfileRes = getPreferences(Context.MODE_PRIVATE).getString("currentUserId", null);

        // listening connection status changes of web socket connection
        lightning.setConnectionStatusListener(this);
        lightning.connect();
        binder = new Binder();

        // adding observer for list size. notifies list adapter when size changes
        profileList = lightning.getList("Profile");
        profileList.addObserver(listObserver);
        profileList.getDataHandler().setMessageActionListener(new MessageActionListener<List<LightningObject>>() {
            @Override
            public void onSuccess(MessageHandler messageHandler, List<LightningObject> data) {

            }

            @Override
            public void onError(MessageHandler messageHandler, MessageError error) {
                if (error.getCode() == MessageError.General.NOT_FOUND) {
                    if (currentProfileRes != null) {
                        resetUserData();
                    }
                }
            }

            @Override
            public void onPushMessage(PushMessage pushMessage) {

            }
        });

        /**
         * Initialising UI
         */
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);
    }

    @Override
    public boolean onConnectionStatusChange(boolean connected) {

        Log.d(TAG, "Connection status changed to " + connected);
        if (connected) {
            Log.d(TAG, "Connection established. Getting Profiles.");
            profileList.clear();
            profileList.fetch();
            getActionBar().setTitle("Getting list");
            return true;

        } else {
            // if app is active and connection is lost, re-connect again
            if (active) {
                Log.d(TAG, "Connection lost. Connecting...");
                getActionBar().setTitle("Connection lost. Connecting...");
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        changeUserStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        changeUserStatus("inactive");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        active = false;

        if (currentProfileRes == null) return;

        changeUserStatus("offline");

        // wait for 1 second and close the connection
        new Handler().postDelayed(new Runnable() {
            public void run() {
                lightning.disconnect();
            }
        }, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (getPreferences(Context.MODE_PRIVATE).getString("currentUserId", null) == null)
            getMenuInflater().inflate(R.menu.menu_main, menu);
//        else
//            getMenuInflater().inflate(R.menu.menu_joined, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_join) {
            DialogFragment newFragment = new JoinDialogFragment();
            newFragment.show(getFragmentManager(), "joinFragment");
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when user selects an image to upload. If image is valid, it is saved
     * in Parse files and the generated url is saved as avatar field of the existing profile.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap == null) {
                System.out.println("bitmap is null");
                Toast.makeText(getApplicationContext(), "Error while getting image!", Toast.LENGTH_LONG).show();
                return;
            }

            // resizing image
            int w = 240, h = 240;
            if (bitmap.getWidth() > bitmap.getHeight()) {
                w = (int) (bitmap.getWidth() / (float) (bitmap.getHeight() / h));
            } else if (bitmap.getHeight() > bitmap.getWidth()) {
                h = (int) (bitmap.getHeight() / (float) (bitmap.getWidth() / w));
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] bitmapData = byteArrayOutputStream.toByteArray();

            final ParseFile file = new ParseFile("moment.jpg", bitmapData);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    Log.d(TAG, "Image is uploaded. URL: " + file.getUrl());
                    LightningObject profileToUpdate = lightning.getObject(currentProfileRes);
                    profileToUpdate.set("avatar", file.getUrl());
                    profileToUpdate.save();
                }
            });
        }
    }

    private void openSelectImageIntent() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onUserJoined(String username) {

        Log.d(TAG, "New user: " + username);

        if (username == null || username.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please provide a valid name!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!lightning.isConnected()) {
            Toast.makeText(getApplicationContext(), "Connection is not open! Try later.", Toast.LENGTH_LONG).show();
            return;
        }

        LightningObject profile = lightning.createObject("Profile");
        profile.set("name", username);
        profile.set("status", "online");
        profile.addObserver("res", new Observer() {
            @Override
            public void update(String key, Object value) {

                // saving current user res
                Log.d(TAG, "Object created with res " + value);
                currentProfileRes = (String) value;

                // saving current user into shared preferences
                SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                editor.putString("currentUserId", value.toString());
                editor.apply();

                // invalidation menu, removing join button
                invalidateOptionsMenu();

                // invalidating views to add edit button on current users cell
                gridview.invalidateViews();
            }
        });
        profileList.add(profile);
        profileList.save();
    }

    private void changeUserStatus(String status) {
        if (currentProfileRes != null && lightning.isConnected()) {
            LightningObject currentUser = lightning.getObject(currentProfileRes);
            currentUser.set("status", status);
            currentUser.save();
        }
    }

    private void checkUserInList() {
        LightningObject currentUser = null;
        for (LightningObject profile : profileList) {
            if (profile.getRes().equals(currentProfileRes)) {
                currentUser = profile;
                break;
            }
        }
        if (currentUser != null) {
            Log.e(TAG, "Local user exists in server. Setting status to online.");
            currentUser.set("status", "online");
            currentUser.save();
        } else {
            Log.e(TAG, "Local user doesn't exist in server. Removing its data");

            currentProfileRes = null;

            // removing current user from shared preferences
            resetUserData();

            // invalidation menu, removing join button
            invalidateOptionsMenu();
        }
    }

    private void resetUserData() {
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.remove("currentUserId");
        editor.apply();
    }

    public class ImageAdapter extends BaseAdapter {

        public ImageAdapter() {
        }

        public int getCount() {
//            System.out.println("Getting list size in adapter " + profileList.size());
            return profileList != null ? profileList.size() : 0;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final LightningObject profile = profileList.get(position);

            convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.profile_grid, parent, false);

            // finding views
            View editBtn = convertView.findViewById(R.id.edit_button);
            ObserverTextView vName = (ObserverTextView) convertView.findViewById(R.id.name);
            UrlImageView vAvatar = (UrlImageView) convertView.findViewById(R.id.avatar);
            vAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            vAvatar.setAdjustViewBounds(Boolean.TRUE);
            StatusIconView vStatusIconView = (StatusIconView) convertView.findViewById(R.id.status_dot);

            // binding views to fields
            binder.bind(profile, "name", vName);
            binder.bind(profile, "avatar", vAvatar);
            binder.bind(profile, "status", vStatusIconView);

            if (profile.getRes() != null && currentProfileRes != null && profile.getRes().equals(currentProfileRes)) {
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentProfileRes = profile.getRes();
                        openSelectImageIntent();
                    }
                });

                editBtn.setVisibility(View.VISIBLE);
                vStatusIconView.setVisibility(View.GONE);
            } else {
                editBtn.setVisibility(View.GONE);
                vStatusIconView.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }
}
