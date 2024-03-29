package com.nts.dispatch_cc.internal;

import android.content.Context;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;

public class ClientFactory {
    private static volatile AWSAppSyncClient client;

    public synchronized static AWSAppSyncClient getInstance(Context context) {
        if (client == null) {
            AWSConfiguration awsConfig = new AWSConfiguration(context);

            client = AWSAppSyncClient.builder()
                    .context(context)
                    .awsConfiguration(awsConfig)
                    .useClientDatabasePrefix(true)
                    .build();
        }
        return client;
    }
}
