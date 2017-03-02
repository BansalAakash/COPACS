package com.example.android.copacs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aakas on 23-02-2017.
 */
class BigComputationTask extends AsyncTask {
    CSVWriter csvWriter, csvWriter1;
    FileWriter mFileWriter, mFileWriter1;
    ArrayList<String[]> dataArray;
    String filename, fileName1;
    Context mainContext;
    String path1;
    int temp = 0, temp1 = 0;
    View view;
    MainActivity activity1;

    public BigComputationTask(String fileName, String fileName1, ArrayList<String[]> inData, Context context, View view, MainActivity activity2) {
        dataArray = new ArrayList<>();
        this.dataArray = inData;
        this.filename = fileName;
        this.fileName1 = fileName1;
        this.mainContext = context;
        this.path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Copacs Data").toString();
        this.view = view;
        this.activity1 = activity2;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        File file = new File(path1, filename);
        File file1 = new File(path1, fileName1);
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Copacs Data");
        if (!folder.exists()) {
            folder.mkdir();
            Log.d(BigComputationTask.class.toString(), "Folder created!");
        }
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Log.d(this.toString(), "Created file " + filename);
                    temp = 1;
                }
            } catch (Exception e) {
                Log.d(BigComputationTask.class.toString(), "File creation error!");
                e.printStackTrace();
            }
        }
        if (!file1.exists()) {
            try {
                if (file1.createNewFile()) {
                    Log.d(this.toString(), "Created file " + fileName1);
                    temp1 = 1;
                }
            } catch (Exception e) {
                Log.d(BigComputationTask.class.toString(), "File creation error!");
                e.printStackTrace();
            }
        }
        try {
            mFileWriter = new FileWriter(file, true);
            mFileWriter1 = new FileWriter(file1, true);
        } catch (IOException e) {
            Log.d(this.toString(), "heythis");
            e.printStackTrace();
            Log.d(BigComputationTask.class.toString(), "filewriter error!");
        }
        csvWriter = new CSVWriter(mFileWriter);
        csvWriter1 = new CSVWriter(mFileWriter1);
        String[] header = {
                "Timestamp",
                "Date_Time",
                "Accelerometer_x",
                "Accelerometer_y",
                "Accelerometer_z",
                "Resultant Accelerometer",
                "Magnetometer_x",
                "Magnetometer_y",
                "Magnetometer_z",
                "Light",
                "Orientation(Azimuth)",
                "Orientation(Pitch)",
                "Orientation(Roll)",
                "Proximity",
//                "Latitude",
//                "Longitude",
//                "Speed(GPS)",
//                "Altitude",
//                "Battery level(percent)",
//                "Battery Temperature(celsius)",
                "Gravity_x",
                "Gravity_y",
                "Gravity_z",
                "Resultant_gravity",
                "Gyroscope_x",
                "Gyroscope_y",
                "Gyroscope_z",
                "Linear_acceleration_x",
                "Linear_acceleration_y",
                "Linear_acceleration_z",
                "Resultant_linear_acceleration",
                "Activity",
                "Device position",
                "Location",
        };

        if (temp == 1)
            csvWriter.writeNext(header);

        if (temp1 == 1)
            csvWriter1.writeNext(header);

        csvWriter.writeAll(dataArray);
        csvWriter1.writeAll(dataArray);
        try {
            csvWriter.flush();
            csvWriter.close();
            mFileWriter.close();
            csvWriter1.flush();
            csvWriter1.close();
            mFileWriter1.close();
        } catch (Exception e) {
            Log.d(mainContext.toString(), "Close or flush error!");
            e.printStackTrace();
        }
        return null;
    }
}
