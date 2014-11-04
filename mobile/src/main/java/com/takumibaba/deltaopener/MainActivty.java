package com.takumibaba.deltaopener;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.ArrayList;
import java.util.List;


public class MainActivty extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationClient.OnAddGeofencesResultListener {

    private LocationClient lc;
    private List<Geofence> geos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activty);

        startService(new Intent(MainActivty.this, WearListenerService.class));

        Geofence geoEnter = new Geofence.Builder().setRequestId("DELTAOPENER-ENTER").setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setCircularRegion(35.38926863, 139.4258058, 60.0f).setExpirationDuration(Geofence.NEVER_EXPIRE).build();
        Geofence geoExit = new Geofence.Builder().setRequestId("DELTAOPENER-EXIT").setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(35.38926863, 139.4258058, 60.0f).setExpirationDuration(Geofence.NEVER_EXPIRE).build();
        geos = new ArrayList<Geofence>();
        geos.add(geoEnter);
        geos.add(geoExit);

        lc = new LocationClient(this, this, this);
        lc.connect();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent i = new Intent(this, GeofenceService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        lc.addGeofences(geos, pi, this);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onAddGeofencesResult(int i, String[] strings) {

    }
}
