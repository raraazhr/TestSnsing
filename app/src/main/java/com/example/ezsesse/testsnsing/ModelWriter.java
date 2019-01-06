package com.example.ezsesse.testsnsing;

import android.util.Log;

import org.sensingkit.sensingkitlib.SKException;
import org.sensingkit.sensingkitlib.SKExceptionErrorCode;
import org.sensingkit.sensingkitlib.SKSensorDataListener;
import org.sensingkit.sensingkitlib.SKSensorModuleType;
import org.sensingkit.sensingkitlib.data.SKAccelerometerData;
import org.sensingkit.sensingkitlib.data.SKSensorData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ezsesse on 1/6/2019.
 */

public class ModelWriter implements SKSensorDataListener {

    @SuppressWarnings("unused")
    private static final String TAG = "ModelWriter";

    private final SKSensorModuleType moduleType;

    private File mFile;
    private BufferedOutputStream mFileBuffer;
    String dataLine;

    public ModelWriter(SKSensorModuleType moduleType, File sessionFolder, String filename) throws SKException {

        this.moduleType = moduleType;
        this.mFile = createFile(sessionFolder, filename);

        try {
            this.mFileBuffer = new BufferedOutputStream(new FileOutputStream(mFile));
        }
        catch (FileNotFoundException ex) {
            throw new SKException(TAG, "File could not be found.", SKExceptionErrorCode.UNKNOWN_ERROR);
        }

    }

    public void flush() throws SKException {

        try {
            mFileBuffer.flush();
        }
        catch (IOException ex) {
            throw new SKException(TAG, ex.getMessage(), SKExceptionErrorCode.UNKNOWN_ERROR);
        }
    }

    public void close() throws SKException {

        try {
            mFileBuffer.close();
        }
        catch (IOException ex) {
            throw new SKException(TAG, ex.getMessage(), SKExceptionErrorCode.UNKNOWN_ERROR);
        }
    }

    private File createFile(File sessionFolder, String filename) throws SKException {

        File file = new File(sessionFolder, filename + ".csv");

        try {
            if (!file.createNewFile()) {
                throw new SKException(TAG, "File could not be created.", SKExceptionErrorCode.UNKNOWN_ERROR);
            }
        }
        catch (IOException ex) {
            throw new SKException(TAG, ex.getMessage(), SKExceptionErrorCode.UNKNOWN_ERROR);
        }

        // Make file visible
        //MediaScannerConnection.scanFile(getBaseContext(), new String[]{file.getAbsolutePath()}, null, null);

        return file;
    }
    @Override
    public void onDataReceived(SKSensorModuleType moduleType, SKSensorData moduleData) {
        if (mFileBuffer != null) {
//            if (moduleType==SKSensorModuleType.ACCELEROMETER){
//                SKAccelerometerData data = (SKAccelerometerData) moduleData;
//                dataLine = String.valueOf(data.getX());
//            }
//            else {
//
//            }
            // Build the data line

            dataLine = moduleData.getDataInCSV() + "\n";
            // Write in the FileBuffer
            try {
                mFileBuffer.write(dataLine.getBytes());
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

    }
}
