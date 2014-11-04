package com.takumibaba.deltaopener;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WareActivity extends Activity{

    private TextView mTextView;
    private static Notification.Builder notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        Intent openerOperation = new Intent(this, DeltaOpenerService.class);
        PendingIntent i = PendingIntent.getService(this, 0, openerOperation, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action action = new Notification.Action(R.drawable.ic_launcher, "", i);

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


        finish();
    }

}
