package com.android.moment.moment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import com.android.moment.moment.lightning.model.Binder;
import com.android.moment.moment.lightning.model.Lightning;
import com.android.moment.moment.lightning.model.LightningObject;
import com.android.moment.moment.lightning.model.LightningObjectList;
import com.android.moment.moment.lightning.model.Observer;
import com.android.moment.moment.lightning.net.WebSocketClient;
import com.android.moment.moment.views.CustomImageView;
import com.android.moment.moment.views.CustomTextView;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//import com.android.moment.moment.net.model.observer.Binder;


public class MainActivity extends Activity implements WebSocketClient.ConnectionStatusListener {

    private static final String TAG = "MainActivity";
    private static final int RESULT_LOAD_IMAGE = 23;
    static boolean active;

    private Lightning lightning = new Lightning();
    private LightningObjectList profileList;
    private Binder binder;

    private String selectedProfileRes;
    private Observer listObserver;

    private GridView gridview;
    private ImageAdapter imageAdapter = new ImageAdapter();

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listObserver = new Observer() {
            @Override
            public void update(String key, Object value) {
                if (profileList.size() == 0)
                    getActionBar().setTitle("List is empty");
                else if (profileList.size() == 1)
                    getActionBar().setTitle(profileList.size() + " person");
                else
                    getActionBar().setTitle(profileList.size() + " people");
                Log.d(TAG, gridview.toString());
                imageAdapter.notifyDataSetChanged();
                gridview.invalidateViews();
            }
        };

        // listening connection status changes of web socket connection
        lightning.setConnectionStatusListener(this);
        lightning.connect();
        binder = new Binder();

        // adding observer for list size. notifies list adapter when size changes
        profileList = lightning.getList("Profile");
        profileList.addObserver(listObserver);

        /**
         * Initialising UI
         */
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);
    }

    @Override
    public void onStatusChanged(boolean connected) {

        Log.d(TAG, "Connection status changed to " + connected);
        if (connected) {
            Log.d(TAG, "Connection established. Getting Profiles.");
            profileList.clear();
            profileList.fetch();
            getActionBar().setTitle("Getting list");

        } else {
            Log.d(TAG, "Connection lost. Connecting...");
            getActionBar().setTitle("Connection lost. Connecting...");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        active = false;
        lightning.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        if (getPreferences(Context.MODE_PRIVATE).getString("currentUserId", null) == null)
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        else
//            getMenuInflater().inflate(R.menu.menu_joined, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_join) {

            LightningObject profile = lightning.createObject("Profile");
            profile.set("name", "Ahmet İsmail Yalçınkaya");
            profile.set("avatar", "https://graph.facebook.com/eluleci/picture?type=large");
            profile.addObserver("res", new Observer() {
                @Override
                public void update(String key, Object value) {
                    Log.d(TAG, "Object created with res " + value);
                    SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                    editor.putString("currentUserId", value.toString());
                    editor.apply();
                    invalidateOptionsMenu();
                }
            });
            profileList.add(profile);
            profileList.save();

        } else if (id == R.id.action_pick) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
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
                    LightningObject selectedProfile = lightning.getObject(selectedProfileRes);
                    selectedProfile.set("avatar", file.getUrl());
                    selectedProfile.save();
                }
            });
        }
    }

    private void openSelectImageIntent() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
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
            CustomTextView vName = (CustomTextView) convertView.findViewById(R.id.name);
            CustomImageView vAvatar = (CustomImageView) convertView.findViewById(R.id.avatar);
            vAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            vAvatar.setAdjustViewBounds(Boolean.TRUE);

            // binding views to fields
            binder.bind(profile, "name", vName);
            binder.bind(profile, "avatar", vAvatar);

            // opening image select intent when item is clicked
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedProfileRes = profile.getRes();
                    openSelectImageIntent();
                }
            });

            return convertView;
        }
    }
}
