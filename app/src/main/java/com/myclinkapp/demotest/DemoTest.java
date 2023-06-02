package com.myclinkapp.demotest;

import android.app.Application;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.DiscardUnsyncedChangesStrategy;
import io.realm.mongodb.sync.MutableSubscriptionSet;
import io.realm.mongodb.sync.Subscription;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class DemoTest extends Application {
    private final String appID="application-0-lwvlp";
    private Realm globalRealm;
    private static final String TAG = "DemoTest";
    private App app;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        Realm.init(this);

        AtomicReference<App> globalApp = new AtomicReference<>(app);

// accessing the app from within the lambda below requires an effectively final object


        app = new App(new AppConfiguration.Builder(appID)
                .defaultSyncClientResetStrategy(new DiscardUnsyncedChangesStrategy() {
                    @Override
                    public void onBeforeReset(Realm realm) {
                        Log.w("EXAMPLE", "Beginning client reset for " + realm.getPath());

                        realm.close();
                     }
                    @Override
                    public void onAfterReset(Realm before, Realm after) {
                        Log.w("EXAMPLE", "Finished client reset for " + before.getPath());
                    }
                    @Override
                    public void onError(SyncSession session, ClientResetRequiredError error) {
                        Log.e("EXAMPLE", "Couldn't handle the client reset automatically." +
                                " Falling back to manual client reset execution: "
                                + error.getErrorMessage());
                        // close all instances of your realm -- this application only uses one
                        globalRealm.close();
                        try {
                            Log.w("EXAMPLE", "About to execute the client reset.");
                            // execute the client reset, moving the current realm to a backup file
                            error.executeClientReset();
                            Log.w("EXAMPLE", "Executed the client reset.");
                        } catch (IllegalStateException e) {
                            Log.e("EXAMPLE", "Failed to execute the client reset: " + e.getMessage());
                            // The client reset can only proceed if there are no open realms.
                            // if execution failed, ask the user to restart the app, and we'll client reset
                            // when we first open the app connection.
                            AlertDialog restartDialog = new AlertDialog.Builder(getApplicationContext())
                                    .setMessage("Sync error. Restart the application to resume sync.")
                                    .setTitle("Restart to Continue")
                                    .create();
                            restartDialog.show();
                        }
                        // open a new instance of the realm. This initializes a new file for the new realm
                        // and downloads the backend state. Do this in a background thread so we can wait
                        // for server changes to fully download.
                        SyncConfiguration globalConfig = new SyncConfiguration.Builder(app.currentUser())
                                //.allowWritesOnUiThread(true)
                                //.allowQueriesOnUiThread(true)
                                .initialSubscriptions(new SyncConfiguration.InitialFlexibleSyncSubscriptions() {
                                    @Override
                                    public void configure(Realm realm, MutableSubscriptionSet subscriptions) {
                                        // add a subscription with a name

                                    }
                                })
                                .build();

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            Realm newRealm = Realm.getInstance(globalConfig);
                            // ensure that the backend state is fully downloaded before proceeding
                            try {
                                globalApp.get().getSync().getSession(globalConfig).downloadAllServerChanges(10000,
                                        TimeUnit.MILLISECONDS);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.w("EXAMPLE",
                                    "Downloaded server changes for a fresh instance of the realm.");
                            newRealm.close();
                        });
                        // execute the recovery logic on a background thread
                        try {
                            executor.awaitTermination(20000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .build());
        globalApp.set(app);
    }
}
