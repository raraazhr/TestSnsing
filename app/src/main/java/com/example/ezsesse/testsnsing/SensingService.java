package com.example.ezsesse.testsnsing;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import org.sensingkit.sensingkitlib.SKException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ezsesse on 1/6/2019.
 */

public class SensingService extends Service {

    @SuppressWarnings("unused")
    protected static final String TAG = "SensingService";

    public enum SensingServiceStatus {
        Stopped,
        Sensing,
        Paused
    }

    private final IBinder mBinder = new LocalBinder();

    private PowerManager.WakeLock mWakeLock;

    // Sensing Session
    private SensingSession mSensingSession;

    private SensingServiceStatus mStatus = SensingServiceStatus.Stopped;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onDestroy() {

        try {
            mSensingSession.stop();
            mSensingSession.close();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        SensingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SensingService.this;
        }
    }

    private SensingSession createSensingSession() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.UK);
        String folderName = dateFormat.format(new Date());

        SensingSession session;

        try {
            session = new SensingSession(this, folderName);
        }
        catch (SKException ex) {
            Log.e(TAG, ex.getMessage());
            session = null;
        }

        return session;
    }


    // --- Wake Lock

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

    // --- Public API

    public void startSensing() {

        // Set the status
        mStatus = SensingServiceStatus.Sensing;

        if (mSensingSession != null) {
            Log.e(TAG, "Sensing Session is already created!");
        }

        mSensingSession = createSensingSession();

        try {
            acquireWakeLock();
            mSensingSession.start();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }
    }

    public void pauseSensing() {

        // Set the status
        mStatus = SensingServiceStatus.Paused;

        try {
            releaseWakeLock();
            mSensingSession.stop();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

    }

    public void continueSensing() {

        // Set the status
        mStatus = SensingServiceStatus.Sensing;

        acquireWakeLock();

        try {
            mSensingSession.start();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

    }

    public void stopSensing() {

        // Set the status
        mStatus = SensingServiceStatus.Stopped;

        try {

            if (mSensingSession.isSensing()) {
                mSensingSession.stop();
            }

            mSensingSession.close();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

        releaseWakeLock();

        // Hide notification
//        hideNotification();

        mSensingSession = null;
    }

    public SensingServiceStatus getSensingStatus() {
        return mStatus;
    }

}