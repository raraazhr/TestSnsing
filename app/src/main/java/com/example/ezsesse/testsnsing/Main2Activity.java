package com.example.ezsesse.testsnsing;

import android.content.Context;
import android.os.PowerManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatus = (TextView)findViewById(R.id.test);

        mSensingButton = (Button)findViewById(R.id.startbutton);
        mSensingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createSensingSession();
                startSensing();

            }
        });

        mPauseButton = (Button)findViewById(R.id.stopbutton);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSensing();
            }
        });

    }


    private SensingSession createSensingSession() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.UK);
        String folderName = dateFormat.format(new Date());

        SensingSession session;

        try {
            session = new SensingSession(this, folderName);
        }
        catch (SKException ex) {
            Log.e("", ex.getMessage());
            session = null;
        }

        return session;
    }

    public void startSensing() {

        // Set the status

        if (mSensingSession != null) {
            Log.e("", "Sensing Session is already created!");
        }

        mSensingSession = createSensingSession();

        try {
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

        mSensingSession = null;
    }

}
