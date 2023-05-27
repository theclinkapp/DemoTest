package com.myclinkapp.demotest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.myclinkapp.demotest.realm.colleges;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.DiscardUnsyncedChangesStrategy;
import io.realm.mongodb.sync.MutableSubscriptionSet;
import io.realm.mongodb.sync.Subscription;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private App app;
    public static final String appID = "application-0-lwvlp";

    private TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.textView);

        app = new App(new AppConfiguration.Builder(appID)
                .defaultSyncClientResetStrategy(new DiscardUnsyncedChangesStrategy() {
                    @Override
                    public void onBeforeReset(Realm realm) {
                        System.out.println("Beginning client reset for " + realm.getPath());
                    }

                    @Override
                    public void onAfterReset(Realm before, Realm after) {
                        System.out.println("Finished client reset for " + before.getPath());
                    }

                    @Override
                    public void onError(SyncSession session, ClientResetRequiredError error) {
                        System.err.println("Couldn't handle the client reset automatically. " +
                                "Falling back to manual recovery: " + error.getErrorMessage());
                        handleManualReset(app, session, error);
                    }
                })
                .build());


        readWrite();


    }

    private static void handleManualReset(App app, SyncSession session, ClientResetRequiredError error) {
        // Close all open realms
        Realm.getDefaultInstance().close();

        try {
            System.out.println("About to execute the client reset.");
            // Execute the client reset, moving the current realm to a backup file
            error.executeClientReset();
            System.out.println("Executed the client reset.");
        } catch (IllegalStateException e) {
            System.err.println("Failed to execute the client reset: " + e.getMessage());
            // Show a dialog to the user, asking them to restart the app to continue syncing
            Log.e(TAG, "handleManualReset: Restart");
        }

        // Open a new instance of the realm
        SyncConfiguration syncConfig = new SyncConfiguration.Builder(app.currentUser(), appID)
                .build();
        Realm newRealm = Realm.getInstance(syncConfig);

        newRealm.close();
    }

    private void readWrite() {

        Credentials credentials = Credentials.anonymous();

        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully authenticated anonymously.");
                User user = app.currentUser();

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser())
                       .allowWritesOnUiThread(true)
                        .allowQueriesOnUiThread(true)
                        .initialSubscriptions(new SyncConfiguration.InitialFlexibleSyncSubscriptions() {
                            @Override
                            public void configure(Realm realm, MutableSubscriptionSet subscriptions) {
                                // add a subscription with a name
                                subscriptions.addOrUpdate(Subscription.create("universitySubs",
                                        realm.where(universities.class)));
                                subscriptions.addOrUpdate(Subscription.create("collegeSubs",
                                        realm.where(colleges.class)));

                            }
                        })
                        .build();
                Realm.setDefaultConfiguration(config);

                Realm realm=Realm.getDefaultInstance();
                RealmResults<universities> uData = realm.where(universities.class).findAll();

                String universityNames="";
                for (universities data : uData) {
                    Log.e(TAG, "execute: " + data.getFullName());
                    universityNames+="\n"+data.getFullName();

                }
                textView.setText(universityNames);

            } else {
                Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
            }

        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        Realm.getDefaultInstance().close();
    }
}