package com.example.ezsesse.testsnsing;

import android.Manifest;
import android.annotation.SuppressLint;
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
import org.sensingkit.sensingkitlib.SKExceptionErrorCode;
import org.sensingkit.sensingkitlib.SKSensorDataListener;
import org.sensingkit.sensingkitlib.SKSensorModuleType;
import org.sensingkit.sensingkitlib.SensingKitLib;
import org.sensingkit.sensingkitlib.SensingKitLibInterface;
import org.sensingkit.sensingkitlib.data.SKAccelerometerData;
import org.sensingkit.sensingkitlib.data.SKGyroscopeData;
import org.sensingkit.sensingkitlib.data.SKSensorData;
import org.sensingkit.sensingkitlib.modules.SKActivityRecognitionIntentService;
import org.sensingkit.sensingkitlib.modules.SKGyroscope;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity implements SKSensorDataListener{
    TextView mStatus;
    Button mSensingButton;
    Button mPauseButton;
    boolean isRegistered = false;
    TextView xAcc;
    TextView yAcc;
    TextView zAcc;
    TextView xGyr;
    TextView yGyr;
    TextView zGyr;

    /*
    * Baru
    * */
    SensingKitLibInterface mSensingKitLib;

    SensingSession mSensingSession;
    private PowerManager.WakeLock mWakeLock;
    private Fragment fragment =null;
    private Context mContext;

    public Main2Activity() {
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xAcc = (TextView)findViewById(R.id.xPos);
        yAcc = (TextView)findViewById(R.id.yPos);
        zAcc = (TextView)findViewById(R.id.zPos);
        xGyr = (TextView)findViewById(R.id.xGyro);
        yGyr = (TextView)findViewById(R.id.yGyro);
        zGyr = (TextView)findViewById(R.id.zGyro);

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
                if (isRegistered){
                    try {
                        startSensing();
                    } catch (SKException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        createSensingSession();
                        startSensing();
                    } catch (SKException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        mPauseButton = (Button)findViewById(R.id.stopbutton);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStatus.setText("Sensing Stop");
                try {
                    stopSensing();
                } catch (SKException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void createSensingSession() throws SKException {
        isRegistered=true;
        mSensingKitLib = SensingKitLib.getSensingKitLib(this);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.GYROSCOPE);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.ACCELEROMETER, this);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.GYROSCOPE, this);
        if (!mSensingKitLib.isSensorModuleSensing(SKSensorModuleType.GYROSCOPE)){
            xGyr.setText("no sensor found");
            yGyr.setText("no sensor found");
            zGyr.setText("no sensor found");
        }
        if (!mSensingKitLib.isSensorModuleSensing(SKSensorModuleType.ACCELEROMETER)){
            xAcc.setText("no sensor found");
            yAcc.setText("no sensor found");
            zAcc.setText("no sensor found");
        }
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.UK);
//        String folderName = dateFormat.format(new Date());
//
//
//        try {
//            mSensingSession = new SensingSession(this, folderName);
//        }
//        catch (SKException ex) {
//            Log.e("", ex.getMessage());
//            mSensingButton = null;
//        }
//
//        return mSensingSession;
    }

    public void startSensing() throws SKException {
        //acquireWakeLock();
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.GYROSCOPE);
//        if (mSensingSession != null) {
//            Log.e("", "Sensing Session is already created!");
//        }
//        else {
//            mSensingSession = createSensingSession();
//        }
//
//        try {
//            acquireWakeLock();
//            mSensingSession.start();
//        }
//        catch (SKException ex) {
//            ex.printStackTrace();
//        }
    }

    public void stopSensing() throws SKException {
        //releaseWakeLock();
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.GYROSCOPE);
        // Set the status
//        try {
//
//            if (mSensingSession.isSensing()) {
//                mSensingSession.stop();
//            }
//
//            mSensingSession.close();
//        }
//        catch (SKException ex) {
//            ex.printStackTrace();
//        }
//        // Hide notification
////        hideNotification();
//        releaseWakeLock();
//        mSensingSession = null;
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


    @Override
    public void onDataReceived(SKSensorModuleType skSensorModuleType, SKSensorData skSensorData) {
        if (skSensorModuleType.equals(SKSensorModuleType.ACCELEROMETER)){
            SKAccelerometerData skAccelerometerData = (SKAccelerometerData)skSensorData;
            xAcc.setText(String.valueOf(skAccelerometerData.getX()));
            yAcc.setText(String.valueOf(skAccelerometerData.getY()));
            zAcc.setText(String.valueOf(skAccelerometerData.getZ()));
        }

        else if (skSensorModuleType.equals(SKSensorModuleType.GYROSCOPE)){
            SKGyroscopeData skGyroscopeData = (SKGyroscopeData)skSensorData;
            xGyr.setText(String.valueOf(skGyroscopeData.getX()));
            yGyr.setText(String.valueOf(skGyroscopeData.getY()));
            zGyr.setText(String.valueOf(skGyroscopeData.getZ()));
        }
    }
}
