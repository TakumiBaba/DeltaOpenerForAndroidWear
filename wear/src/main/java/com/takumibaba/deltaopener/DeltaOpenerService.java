package com.takumibaba.deltaopener;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by takumi on 2014/07/08.
 */
public class DeltaOpenerService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private GoogleApiClient client;
    private Random random;

    public DeltaOpenerService(){
        super(DeltaOpenerService.class.getSimpleName());
    }


    @Override
    public void onCreate(){
        super.onCreate();
        random = new Random(1L);
        client = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("wear", "onconnected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("wear", "onConnectionSuspended");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("wear", "onHandleIntent");
        client.blockingConnect(100, TimeUnit.MILLISECONDS);
        if(!client.isConnected()) return;

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/door");
        putDataMapRequest.getDataMap().putString("status", "open");
        Wearable.DataApi.putDataItem(client, putDataMapRequest.asPutDataRequest()).await();
        client.disconnect();
        Log.d("door", "open");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("wear", "onConnectionFailed");
    }

    private String getId(){
        return String.valueOf(random.nextDouble());
    }
}
