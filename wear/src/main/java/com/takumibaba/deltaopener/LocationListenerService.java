package com.takumibaba.deltaopener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by takumi on 2014/07/09.
 */
public class LocationListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient client;
    private static Notification.Builder notification;

    @Override
    public void onCreate(){
        super.onCreate();
        client = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for(DataEvent event: events) {
            Log.d("location listener", event.getDataItem().getUri().getPath());
            Log.d("location listener", DataMap.fromByteArray(event.getDataItem().getData()).getString("status"));
            String path = event.getDataItem().getUri().getPath();
            if (path.equals("/door")){
                client.blockingConnect(100, TimeUnit.MILLISECONDS);
                if(!client.isConnected()) return;

                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/door");
                putDataMapRequest.getDataMap().putString("status", "close");
                Wearable.DataApi.putDataItem(client, putDataMapRequest.asPutDataRequest()).await();
                Log.d("door", "close?");
                client.disconnect();
            }else if(path.equals("/geofence")){
                String status = DataMap.fromByteArray(event.getDataItem().getData()).getString("status");
                if(status.equals("ENTER")){
                    Intent openerOperation = new Intent(this, DeltaOpenerService.class);
                    PendingIntent i = PendingIntent.getService(this, 0, openerOperation, 0);

                    Notification.Action action = new Notification.Action(R.drawable.go_to_phone_animation, "", i);

                    SpannableString title = new SpannableString(getString(R.string.app_name));
                    title.setSpan(new RelativeSizeSpan(0.8f), 0, title.length(), Spannable.SPAN_POINT_MARK);

                    notification = new Notification.Builder(this)
                            .setContentTitle(title)
                            .setContentText("デルタのドアを開ける")
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setVibrate(new long[] {0, 50})  // Vibrate to bring card to top of stream.
                            .extend(new Notification.WearableExtender()
                                    .addAction(action)
                                    .setContentAction(0)
                                    .setHintHideIcon(true))
                            .setLocalOnly(true)
                            .setPriority(Notification.PRIORITY_MAX);
                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                            .notify(3, notification.build());
                }else if(status.equals("EXIT")){
                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(3);
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
