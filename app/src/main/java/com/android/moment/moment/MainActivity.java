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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.moment.moment.net.core.handler.MessageActionListener;
import com.android.moment.moment.net.core.handler.MessageHandler;
import com.android.moment.moment.net.core.message.MessageError;
import com.android.moment.moment.net.core.message.PushMessage;
import com.android.moment.moment.net.model.ObservableList;
import com.android.moment.moment.net.model.Profile;
import com.android.moment.moment.net.model.observer.Binder;
import com.android.moment.moment.net.model.observer.Field;
import com.android.moment.moment.net.model.observer.FieldObserver;
import com.android.moment.moment.net.ws.manager.ProfileManager;
import com.android.moment.moment.network.WebSocketClient;
import com.android.moment.moment.views.CustomImageView;
import com.android.moment.moment.views.CustomTextView;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final int RESULT_LOAD_IMAGE = 23;
    private ProfileManager profileManager = new ProfileManager();

    private ObservableList<Profile> profileList;
    private Binder binder = new Binder();

    private Profile selectedProfile;

    /**
     * @param savedInstanceState
     */
    private GridView gridview;
    private ImageAdapter imageAdapter = new ImageAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WebSocketClient.getInstance().setConnectionStatusListener(new WebSocketClient.ConnectionStatusListener() {
            @Override
            public void onStatusChanged(boolean connected) {
                if (connected) {
                    Log.d(TAG, "Connection established. Getting Profile list.");
                    profileList = profileManager.getList("MAIN_PROFILE_LIST");

                    binder.bind(profileList, ObservableList.LIST, new FieldObserver<ObservableList<?>>() {
                        @Override
                        public void updateData(Field<ObservableList<?>> field, ObservableList<?> data) {
                            imageAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        /**
         * Setting UI
         */
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                Toast.makeText(HelloGridView.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
/*
        profileManager.setListDataChangeListener(new ListDataChangeListener<Profile>() {
            @Override
            public void onListDataChange(List<Profile> list) {
                Log.d(TAG, "List data changed");
                profileList = list;
                imageAdapter.notifyDataSetChanged();
            }
        });*/
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
            String url = "https://graph.facebook.com/" + "eluleci" + "/picture?type=large";
            Profile profile = new Profile();
            profile.setName("Emrullah Lüleci");
            profile.setAvatar(url);
            profileManager.createProfile(profile, new MessageActionListener<Profile>() {
                @Override
                public void onSuccess(MessageHandler messageHandler, Profile data) {
                    Log.d(TAG, "Profile created " + data.getId());

                    SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                    editor.putString("currentUserId", data.getId());
                    editor.apply();
                    invalidateOptionsMenu();
                }

                @Override
                public void onError(MessageHandler messageHandler, MessageError error) {

                }

                @Override
                public void onPushMessage(PushMessage pushMessage) {

                }
            });
            return true;
        } else if (id == R.id.action_pick) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            /*String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
*/
//            System.out.println(picturePath);

            // String picturePath contains the path of selected Image
            Bitmap bitmap = null;//; = BitmapFactory.decodeFile(picturePath);
//            Uri imageUri = Uri.parse(picturePath);
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
                    Log.d(TAG, file.getUrl());
                    selectedProfile.setAvatar(file.getUrl());
                    profileManager.updateProfile(selectedProfile, null);
                }
            });

//            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            System.out.println(encoded);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        WebSocketClient.getInstance().disconnect();
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
            return profileList != null ? profileList.size() : 0;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            final Profile profile = profileList.get(position);

            convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.profile_grid, parent, false);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.avatar);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(Boolean.TRUE);

            if (profile != null) {
                binder.bind(profile, Profile.NAME, ((CustomTextView) convertView.findViewById(R.id.name)));
                binder.bind(profile, Profile.AVATAR, ((CustomImageView) convertView.findViewById(R.id.avatar)));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedProfile = profile;
                    openSelectImageIntent();
                }
            });

            return convertView;
        }
    }
}
