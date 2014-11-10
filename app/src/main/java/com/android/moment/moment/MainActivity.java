package com.android.moment.moment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.moment.moment.net.core.handler.MessageActionListener;
import com.android.moment.moment.net.core.handler.MessageHandler;
import com.android.moment.moment.net.core.message.MessageError;
import com.android.moment.moment.net.core.message.PushMessage;
import com.android.moment.moment.net.model.ObservableList;
import com.android.moment.moment.net.model.Profile;
import com.android.moment.moment.net.model.component.ResourcePath;
import com.android.moment.moment.net.model.observer.Binder;
import com.android.moment.moment.net.model.observer.Field;
import com.android.moment.moment.net.model.observer.FieldObserver;
import com.android.moment.moment.net.ws.manager.ProfileManager;
import com.android.moment.moment.network.WebSocketClient;
import com.squareup.picasso.Picasso;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private ProfileManager profileManager = new ProfileManager();

    private ObservableList<Profile> profileList;
    private Binder binder = new Binder();

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
                    ResourcePath resourcePath = ResourcePath.generateResourcePath("profiles");
                    profileList = profileManager.getList(resourcePath);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            String url = "https://graph.facebook.com/" + "eluleci" + "/picture?type=large";
            ResourcePath resourcePath = ResourcePath.generateResourcePath("profiles.123");
            Profile profile = new Profile(resourcePath);
            profile.setName("Emrullah Lüleci");
            profile.setAvatar(url);
            profileManager.createProfile(profile, new MessageActionListener<Profile>() {
                @Override
                public void onSuccess(MessageHandler messageHandler, Profile data) {
                    Log.d(TAG, "Profile created");
                }

                @Override
                public void onError(MessageHandler messageHandler, MessageError error) {

                }

                @Override
                public void onPushMessage(PushMessage pushMessage) {

                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        WebSocketClient.getInstance().disconnect();
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

            Profile profile = profileList.get(position);

            convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.profile_grid, parent, false);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.avatar);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(Boolean.TRUE);

            binder.bind(profile, Profile.NAME, new FieldObserver<String>() {
                @Override
                public void updateData(Field<String> field, String data) {
                    System.out.println("name " + data);
                }
            });

            if (profile != null) {
                ((TextView) convertView.findViewById(R.id.name)).setText(profile.getName());
                Picasso.with(getApplicationContext()).load(profile.getAvatar()).into(imageView);
            }
            return convertView;
        }
    }
}
