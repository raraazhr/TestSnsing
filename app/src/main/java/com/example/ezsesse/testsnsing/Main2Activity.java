package com.example.ezsesse.testsnsing;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.support.annotation.FloatRange;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.sensingkit.sensingkitlib.SKException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {
    TextView mStatus;
    Button mSensingButton;
    Button mPauseButton;
    SensingSession mSensingSession;
    private PowerManager.WakeLock mWakeLock;
    private Fragment fragment =null;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mContext =this;
//        fragment = BlankFragment.newInstance();
//        android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
//        transaction.commit();
//        transaction.replace(R.id.frame_layout_adm,fragment,"");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        mStatus = (TextView)findViewById(R.id.test);

        mSensingButton = (Button)findViewById(R.id.startbutton);
        mSensingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mStatus.setText("Sensing...");
                createSensingSession();
                startSensing();
            }
        });

        mPauseButton = (Button)findViewById(R.id.stopbutton);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStatus.setText("Sensing Stop");
                stopSensing();
            }
        });

    }


    private SensingSession createSensingSession() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.UK);
        String folderName = dateFormat.format(new Date());


        try {
            mSensingSession = new SensingSession(this, folderName);
        }
        catch (SKException ex) {
            Log.e("", ex.getMessage());
            mSensingButton = null;
        }

        return mSensingSession;
    }

    public void startSensing() {

        // Set the status

        if (mSensingSession != null) {
            Log.e("", "Sensing Session is already created!");
        }
        else {
            mSensingSession = createSensingSession();
        }

        try {
            acquireWakeLock();
            mSensingSession.start();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }
    }

    public void stopSensing() {

        // Set the status

        try {

            if (mSensingSession.isSensing()) {
                mSensingSession.stop();
            }

            mSensingSession.close();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }
        // Hide notification
//        hideNotification();
        releaseWakeLock();
        mSensingSession = null;
    }

    private void acquireWakeLock() {
        if ((mWakeLock == null) || (!mWakeLock.isHeld())) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
            mWakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

}
