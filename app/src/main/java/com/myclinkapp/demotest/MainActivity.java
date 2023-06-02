package com.myclinkapp.demotest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.MutableSubscriptionSet;
import io.realm.mongodb.sync.Subscription;
import io.realm.mongodb.sync.SyncConfiguration;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private App app;
    public static final String appID = "application-0-lwvlp";

    private TextView textView;
    private Realm realm;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        app = new RealmApp().getApp(MainActivity.this);


        loginNgetData();


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                realm.close(); // Close the Realm instance
                Realm.deleteRealm(configuration); // Delete the Realm file
                Realm.getDefaultInstance().close();
                // Optionally, delete the Realm file from the file system
                File realmFile = new File(configuration.getPath());
                realmFile.delete();
                logout();


                /*SyncConfiguration config = configuration;
                String realmFilePath = config.getPath();
                Realm realm=Realm.getInstance(config);
                realm.close(); // Close the Realm instance
                assert Realm.getDefaultConfiguration() != null;
                Realm.deleteRealm(Realm.getDefaultConfiguration());
                Realm.deleteRealm(config); // Delete the Realm file
                // Optionally, delete the Realm file from the file system
                File realmFile = new File(realmFilePath);
                realmFile.delete();
                loadInitialRealm(false);*/

            }
        });


    }

    private void logout() {
        user.logOutAsync(new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                Log.v(TAG, "onResult: logout " + result);
                if (result.isSuccess()) {
                    loginNgetData();

                } else {
                    Log.e(TAG, "onResult: ", result.getError());
                }

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
            Log.d(TAG, "onDestroy: Closing Realm Forum");
        }
    }


    private void loginNgetData() {

        user = app.currentUser();
        Credentials credentials = null;
        if (user == null) {
            credentials = Credentials.anonymous();
            app.loginAsync(credentials, result -> {
                if (result.isSuccess()) {
                    Log.v("QUICKSTART", "Successfully authenticated anonymously.");

                    loadInitialRealm(true);
                } else {
                    Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
                }
            });
        } else {
            //newLoginUser Tag false for existing user
            loadInitialRealm(false);

        }
    }

    String universityNames = "";
    public static SyncConfiguration configuration;

    private void loadInitialRealm(Boolean newLoginUser) {

        SyncConfiguration.Builder builder = new SyncConfiguration.Builder(app.currentUser());
        if (newLoginUser) {
            builder.initialSubscriptions(new SyncConfiguration.InitialFlexibleSyncSubscriptions() {
                @Override
                public void configure(Realm realm, MutableSubscriptionSet subscriptions) {
                    // add a subscription with a name
                    subscriptions.addOrUpdate(Subscription.create("universitySubs",
                            realm.where(universities.class)));
                }
            });
        }

        SyncConfiguration config = builder.allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
        configuration = config;
        //Realm.setDefaultConfiguration(config);
        // Realm realm=Realm.getDefaultInstance();
        Log.w(TAG, "loadInitialRealm: ");

        realm = Realm.getInstance(configuration);

        RealmResults<universities> uData = realm.where(universities.class).findAll();

        for (universities data : uData) {
            Log.e(TAG, "execute: " + data.getFullName());
            universityNames += "\n" + data.getFullName();

        }
        textView.setText(universityNames);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (realm != null && !realm.isClosed()) {
            realm.close();
            Log.d(TAG, "onStop: Closing Realm Forum");
        }
    }
}



