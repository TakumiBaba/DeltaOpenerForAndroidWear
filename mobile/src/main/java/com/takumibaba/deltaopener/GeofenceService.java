package com.takumibaba.deltaopener;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

/**
 * Created by takumi on 2014/07/09.
 */
public class GeofenceService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mClient;

    public GeofenceService() {
        super(GeofenceService.class.getSimpleName());
    }
    @Override
    public void onCreate(){
        super.onCreate();
        mClient = new GoogleApiClient.Builder(this).addApi(Wearable.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = LocationClient.getGeofenceTransition(intent);
        mClient.blockingConnect(100, TimeUnit.MILLISECONDS);
        String status = "";
        if(Geofence.GEOFENCE_TRANSITION_ENTER == type){
            Log.d("geofence", "ENTER!!");
            status = "ENTER";
        }else if (Geofence.GEOFENCE_TRANSITION_EXIT == type){
            Log.d("geofence", "EXIT!!");
            status = "EXIT";
        }
        if(!mClient.isConnected()) return;
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/geofence");
        putDataMapRequest.getDataMap().putString("status", status);
        Wearable.DataApi.putDataItem(mClient, putDataMapRequest.asPutDataRequest()).await();
        mClient.disconnect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
