package com.takumibaba.deltaopener;

import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by takumi on 2014/07/08.
 */
public class WearListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient client;
    @Override
    public void onCreate(){
        super.onCreate();
        client = new GoogleApiClient.Builder(this).addApi(Wearable.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent){
        Log.d("receive!!", messageEvent.getPath());
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("data change", dataEvents.getStatus().toString());
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for(DataEvent event: events){
            Log.d("location listener", event.getDataItem().getUri().getPath());
            Log.d("location listener", DataMap.fromByteArray(event.getDataItem().getData()).getString("status"));
            String path = event.getDataItem().getUri().getPath();
            if(!path.equals("/door")) return;
            String status = DataMap.fromByteArray(event.getDataItem().getData()).getString("status");
            if(!status.equals("open")) return;
            JSONObject object = new JSONObject();
            try {
                object.put("type", "region");
                object.put("action", "enter");
                object.put("where", "delta");
                object.put("who", "takumibaba");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AndroidHttpClient c = AndroidHttpClient.newInstance("DeltaDoorOpenerWear");
            HttpPost post = new HttpPost("http://node-linda-base.herokuapp.com/masuilab");
            BasicNameValuePair pair = new BasicNameValuePair("tuple", object.toString());
            List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
            list.add(pair);
            try {
                post.setEntity(new UrlEncodedFormEntity(list));
                c.execute(post);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            client.blockingConnect(100, TimeUnit.MILLISECONDS);
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/door");
            putDataMapRequest.getDataMap().putString("status", "close");
            Wearable.DataApi.putDataItem(client, putDataMapRequest.asPutDataRequest()).await();
            client.disconnect();
//            ここでPOST投げる？
        }
    }

    @Override
    public void onPeerConnected(com.google.android.gms.wearable.Node peer){
        Log.d("receive", "connected!!");
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
