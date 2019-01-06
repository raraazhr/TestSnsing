package com.example.ezsesse.testsnsing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ezsesse on 1/5/2019.
 */

public class BlankAdapter extends BaseAdapter {
    private Context mContext;
    private String[] SensorName = {
            "ACCELEROMETER",
            "GYROSCOPE"
    };

    private  ArrayList<accelerometer> data=new ArrayList<>();
    public BlankAdapter (Context c, ArrayList<accelerometer> data) {
        mContext = c;
        this.data = data;
    }
    @Override
    public int getCount() {
        return SensorName.length;
    }

    @Override
    public Object getItem(int position) {
        return SensorName[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class HasilSensor
    {
        TextView Sensorn;
        TextView grid_x;
        TextView grid_y;
        TextView grid_z;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    HasilSensor hasilSensor = new HasilSensor();
    View sensorView;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            sensorView = inflater.inflate(R.layout.valueprint, null);
        } else {
            sensorView = convertView;
        }
        hasilSensor.Sensorn = sensorView.findViewById(R.id.sensor);
        hasilSensor.grid_x = sensorView.findViewById(R.id.xPos);
        hasilSensor.grid_y = sensorView.findViewById(R.id.yPos);
        hasilSensor.grid_z = sensorView.findViewById(R.id.zPos);

        hasilSensor.Sensorn.setText(SensorName[position]);
        hasilSensor.grid_x.setText("ini x");
        hasilSensor.grid_y.setText("ini y");
        hasilSensor.grid_z.setText("ini z");
        return sensorView;
    }
}
