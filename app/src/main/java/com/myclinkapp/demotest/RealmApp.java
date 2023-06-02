package com.myclinkapp.demotest;

import android.app.Activity;
import android.util.Log;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.DiscardUnsyncedChangesStrategy;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class RealmApp {
    private static final String TAG = "RealmApp";
    private Realm globalRealm;
    private Activity activity;
    private App app;
    public static final String AppId = "application-0-lwvlp";
    public App getApp(Activity activity) {
        this.activity = activity;
        Realm.init(activity);
        // replace this with your App ID
        app = new App(new AppConfiguration.Builder(AppId)
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

        return app;
    }

    private void handleManualReset(App app, SyncSession session, ClientResetRequiredError error) {
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
        SyncConfiguration syncConfig = new SyncConfiguration.Builder(app.currentUser(), AppId)
                .build();
        Realm newRealm = Realm.getInstance(syncConfig);

        // Perform any additional recovery logic
        // ...

        // Close the new realm
        newRealm.close();
    }

}
