package com.example.android.copacs;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 3;
    private static final String[] activities = {"Walking", "Sitting", "Elevator_Ascend", "Elevator_Descend", "Running",
            "Driving", "Staircase_Ascend", "Staircase_descend", "Sleeping"};
    private static final String[] positions = {"Hand", "Trouser", "Upper_pocket", "Stationary_object"};
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    final float[] mrotationMatrix = new float[9];
    final float[] morientationAngles = new float[3];
    SensorManager sensorManager;
    Sensor senAccelerometer, senMagnetic, senGyroscope, senLinearAcceleration, sensGravity, sensLight, senProximity;
    int acceloremeterFlag = 0, magnetometerFlag = 0, gyroscopeFlag = 0, linearAccelerationFlag = 0,
            gravityFlag = 0, lightFlag = 0, proximityFlag = 0;
    private String[] locations = {"ABB1", "ABB2", "ABB3", "Sarojini", "Annapurna", "Gate_1", "Gate_2", "Gate_3"};
    private float[] mAccelerometerReading = new float[3];
    private float[] mMagnetometerReading = new float[3];
    private float[] mGyroscopeReading = new float[3];
    private float[] mLinearAccelerationReading = new float[3];
    private float[] mGravityReading = new float[3];
    private float mLightReading;
    private float mProximityReading;
    private TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8, textView9,
            textView10, textView11, textView12, textView13, textView14, textView15, textView16, textView17, textView18,
            textView19, textView20, textView21, textView22, textView23, textView24, textView25, textView26, textView27,
            textView28, textView29, rlareading, rlaheader, rgheader, rgreading, raheader, rareading, altiTextView, logTextView;
    private Button button;
    private MenuItem logButton, deleteButton;
    private String myTag = "";
    private int buttonFlag = 0, batteryLevel;
    private float batteryTemperature;
    private LocationManager locationManager;
    private double latitude, longitude, speed;
    private Location lastKnownLocation;
    private BroadcastReceiver mBatInfoReceiver;
    private String notPresent = "";
    private ArrayAdapter<String> adapter, adapter1;
    private String myTagpos;
    private Spinner activitiesSpinner, phonePositionSpinner;
    private double resultantLinearAcceleration, resultantAccelerometer, resultantGravity;
    private int currentInterval;
    private LocationListener locationListener;
    private double altitude;
    private AutoCompleteTextView autoCompleteTextView;
    private String manualLocation;
    private Menu menu;
    private int checkUniformity = 0;
    private SoundPool mySoundPool;
    private int mSoundIdInit, mSoundIdtart, mSoundIdStop;
    private AudioManager audioManager;
    private Calendar calender;
    private int day;
    private String month;
    private ArrayList<String[]> dataArray;
    private View passView;

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_ACCESS_FINE_LOCATION
            );
        }

        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_ACCESS_COARSE_LOCATION
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.logFileButton:
                logFileClick();
                break;

            case R.id.deleteFileButton:
                new AlertDialog.Builder(this)
                        .setMessage("Do you really want to delete the log folder?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteFileClick(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Copacs Data"));
                                Toast.makeText(MainActivity.this, "Folder deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Copacs Data").mkdir();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        passView = getLayoutInflater().inflate(R.layout.activity_main, new LinearLayout(this), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(MainActivity.this);
        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView3 = (TextView) findViewById(R.id.text3);
        textView4 = (TextView) findViewById(R.id.text4);
        textView5 = (TextView) findViewById(R.id.text5);
        textView6 = (TextView) findViewById(R.id.text6);
        textView7 = (TextView) findViewById(R.id.lightTextView);
        textView8 = (TextView) findViewById(R.id.orientation_x);
        textView9 = (TextView) findViewById(R.id.orientation_y);
        textView10 = (TextView) findViewById(R.id.orientation_z);
        textView11 = (TextView) findViewById(R.id.proximityTextView);
        textView12 = (TextView) findViewById(R.id.latitudeTextView);
        textView13 = (TextView) findViewById(R.id.longitudeTextView);
        textView14 = (TextView) findViewById(R.id.batteryLevelTextView);
        textView15 = (TextView) findViewById(R.id.speedTextView);
        textView16 = (TextView) findViewById(R.id.batteryTemperatureTextView);
        textView17 = (TextView) findViewById(R.id.gravity_x);
        textView18 = (TextView) findViewById(R.id.gravity_y);
        textView19 = (TextView) findViewById(R.id.gravity_z);
        textView20 = (TextView) findViewById(R.id.gyro_x);
        textView21 = (TextView) findViewById(R.id.gyro_y);
        textView22 = (TextView) findViewById(R.id.gyro_z);
        textView23 = (TextView) findViewById(R.id.gravity_header);
        textView24 = (TextView) findViewById(R.id.gyro_header);
        textView25 = (TextView) findViewById(R.id.linear_x);
        textView26 = (TextView) findViewById(R.id.linear_y);
        textView27 = (TextView) findViewById(R.id.linear_z);
        textView28 = (TextView) findViewById(R.id.linear_acc_header);
        textView29 = (TextView) findViewById(R.id.notPresentTextView);
        logTextView = (TextView) findViewById(R.id.logTextView);
        raheader = (TextView) findViewById(R.id.raHeader);
        rareading = (TextView) findViewById(R.id.raReading);
        rlaheader = (TextView) findViewById(R.id.rla_header);
        rlareading = (TextView) findViewById(R.id.rla_reading);
        rgheader = (TextView) findViewById(R.id.rgheader);
        rgreading = (TextView) findViewById(R.id.rgReading);
        altiTextView = (TextView) findViewById(R.id.altitudeTextView);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (volumeCheck() == 1)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
        calender = Calendar.getInstance(TimeZone.getDefault());
        month = calender.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        day = calender.get(Calendar.DAY_OF_MONTH);
        dataArray = new ArrayList<>();
        activitiesSpinner = (Spinner) findViewById(R.id.spinner1);
        phonePositionSpinner = (Spinner) findViewById(R.id.spinner2);
        currentInterval = 10;
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, locations);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mySoundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .build();
        } else {
            mySoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        }
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Copacs Data");
        if (!folder.exists()) {
            folder.mkdir();
        }
        mSoundIdInit = mySoundPool.load(this, R.raw.startsoundinit, 1);
        mSoundIdtart = mySoundPool.load(this, R.raw.startsound, 1);
        mSoundIdStop = mySoundPool.load(this, R.raw.stopsound, 1);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                autoCompleteTextView.showDropDown();
                return false;
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                speed = location.getSpeed();
                textView12.setText("Latitude : " + String.valueOf(latitude));
                textView13.setText("Longitude : " + String.valueOf(longitude));
                textView15.setText("Speed : " + String.valueOf(speed));
                altiTextView.setText("Altitude : " + String.valueOf(altitude));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(MainActivity.this, "Provider is enabled", Toast.LENGTH_SHORT).show();
            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(MainActivity.this, "COPACS needs location services to be up and running!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.5f, locationListener);
        Location location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
            speed = location.getSpeed();
        }
        Criteria crta = new Criteria();
        crta.setAccuracy(Criteria.ACCURACY_FINE);
        crta.setAltitudeRequired(true);
        crta.setSpeedRequired(true);
        crta.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        crta.setPowerRequirement(Criteria.POWER_LOW);

        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitude = 0;
        longitude = 0;
        speed = 0;
        altitude = 0;
        resultantAccelerometer = 0;
        resultantGravity = 0;
        resultantLinearAcceleration = 0;
        if (lastKnownLocation != null) {
            latitude = lastKnownLocation.getLatitude();
            longitude = lastKnownLocation.getLongitude();
            speed = lastKnownLocation.getSpeed();
            altitude = lastKnownLocation.getAltitude();
        }
        textView12.setText("Latitude(Last known) : " + String.valueOf(latitude));
        textView13.setText("Longitude(Last known) : " + String.valueOf(longitude));
        textView15.setText("Speed(Last known) : " + String.valueOf(speed));
        altiTextView.setText("Altitude(last known) : " + String.valueOf(altitude));

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            Log.d("Sensors", "" + sensor.getName());
        }

        senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        senGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        senLinearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        senProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, activities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitiesSpinner.setAdapter(adapter);

        adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, positions);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phonePositionSpinner.setAdapter(adapter1);

        Arrays.fill(mMagnetometerReading, 0);
        Arrays.fill(mAccelerometerReading, 0);
        Arrays.fill(mGyroscopeReading, 0);
        Arrays.fill(mLinearAccelerationReading, 0);
        Arrays.fill(mGravityReading, 0);
        mLightReading = 0;
        mProximityReading = 0;

        if (senMagnetic != null) {
            sensorManager.registerListener(this, senMagnetic, currentInterval);
            magnetometerFlag = 1;
        } else
            notPresent += "No magnetic field sensor!\n";

        if (senProximity != null) {
            sensorManager.registerListener(this, senProximity, currentInterval);
            proximityFlag = 1;
        } else
            notPresent += "No Proximity sensor!\n";

        if (senAccelerometer != null) {
            sensorManager.registerListener(this, senAccelerometer, currentInterval);
            acceloremeterFlag = 1;
        } else {
            notPresent += "No Accelerometer!\n";
            rareading.setVisibility(View.GONE);
            raheader.setVisibility(View.GONE);
        }

        if (senGyroscope != null) {
            gyroscopeFlag = 1;
            sensorManager.registerListener(this, senGyroscope, currentInterval);
        } else {
            textView20.setVisibility(View.GONE);
            textView21.setVisibility(View.GONE);
            textView22.setVisibility(View.GONE);
            textView24.setVisibility(View.GONE);
            notPresent += "No gyroscope sensor!\n";
        }

        if (senLinearAcceleration != null) {
            linearAccelerationFlag = 1;
            sensorManager.registerListener(this, senLinearAcceleration, currentInterval);
        } else {
            textView25.setVisibility(View.GONE);
            textView26.setVisibility(View.GONE);
            textView27.setVisibility(View.GONE);
            textView28.setVisibility(View.GONE);
            rlaheader.setVisibility(View.GONE);
            rlareading.setVisibility(View.GONE);
            notPresent += "No linear acceleration sensor!\n";
        }
        if (sensGravity != null) {
            gravityFlag = 1;
            sensorManager.registerListener(this, sensGravity, currentInterval);
        } else {
            textView17.setVisibility(View.GONE);
            textView18.setVisibility(View.GONE);
            textView19.setVisibility(View.GONE);
            textView23.setVisibility(View.GONE);
            rgheader.setVisibility(View.GONE);
            rgreading.setVisibility(View.GONE);
            notPresent += "No gravity sensor!\n";
        }
        if (sensLight != null) {
            lightFlag = 1;
            sensorManager.registerListener(this, sensLight, currentInterval);
        } else
            notPresent += "No light sensor!\n";


        textView29.setText(notPresent);

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        button = (Button) findViewById(R.id.button1);
        button.setText("Start logging!");

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctxt, Intent intent) {
                batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                batteryTemperature = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10);
                textView14.setText("Battery Level : " + String.valueOf(batteryLevel) + "%");
                textView16.setText("Battery Temperature : " + String.valueOf(batteryTemperature) + " °C");
            }
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER || sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            SensorManager.getRotationMatrix(mrotationMatrix, null,
                    mAccelerometerReading, mMagnetometerReading);
            SensorManager.getOrientation(mrotationMatrix, morientationAngles);
            morientationAngles[0] = (float) Math.toDegrees(morientationAngles[0]);
            morientationAngles[1] = (float) Math.toDegrees(morientationAngles[1]);
            morientationAngles[2] = (float) Math.toDegrees(morientationAngles[2]);
            textView8.setText(String.valueOf(morientationAngles[0]));
            textView9.setText(String.valueOf(morientationAngles[1]));
            textView10.setText(String.valueOf(morientationAngles[2]));
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometerReading[0] = sensorEvent.values[0];
            mAccelerometerReading[1] = sensorEvent.values[1];
            mAccelerometerReading[2] = sensorEvent.values[2];
            resultantAccelerometer = Math.sqrt(Math.pow(mAccelerometerReading[0], 2) + Math.pow(mAccelerometerReading[1], 2)
                    + Math.pow(mAccelerometerReading[2], 2));
            rareading.setText(String.valueOf(resultantAccelerometer));
            textView1.setText(String.valueOf(mAccelerometerReading[0]));
            textView2.setText(String.valueOf(mAccelerometerReading[1]));
            textView3.setText(String.valueOf(mAccelerometerReading[2]));

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            mLinearAccelerationReading[0] = sensorEvent.values[0];
            mLinearAccelerationReading[1] = sensorEvent.values[1];
            mLinearAccelerationReading[2] = sensorEvent.values[2];
            resultantLinearAcceleration = Math.sqrt(Math.pow(mLinearAccelerationReading[0], 2) +
                    Math.pow(mLinearAccelerationReading[1], 2) + Math.pow(mLinearAccelerationReading[2], 2));
            if (linearAccelerationFlag == 1) {
                textView25.setText(String.valueOf(mLinearAccelerationReading[0]));
                textView26.setText(String.valueOf(mLinearAccelerationReading[1]));
                textView27.setText(String.valueOf(mLinearAccelerationReading[2]));
                rlareading.setText(String.valueOf(resultantLinearAcceleration));
            }

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
            mGravityReading[0] = sensorEvent.values[0];
            mGravityReading[1] = sensorEvent.values[1];
            mGravityReading[2] = sensorEvent.values[2];
            resultantGravity = Math.sqrt(Math.pow(mGravityReading[0], 2) + Math.pow(mGravityReading[1], 2)
                    + Math.pow(mGravityReading[2], 2));
            if (gravityFlag == 1) {
                textView17.setText(String.valueOf(mGravityReading[0]));
                textView18.setText(String.valueOf(mGravityReading[1]));
                textView19.setText(String.valueOf(mGravityReading[2]));
                rgreading.setText(String.valueOf(resultantGravity));
            }

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            mGyroscopeReading[0] = sensorEvent.values[0];
            mGyroscopeReading[1] = sensorEvent.values[1];
            mGyroscopeReading[2] = sensorEvent.values[2];
            if (gyroscopeFlag == 1) {
                textView20.setText(String.valueOf(mGyroscopeReading[0]));
                textView21.setText(String.valueOf(mGyroscopeReading[1]));
                textView22.setText(String.valueOf(mGyroscopeReading[2]));
            }

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagnetometerReading[0] = sensorEvent.values[0];
            mMagnetometerReading[1] = sensorEvent.values[1];
            mMagnetometerReading[2] = sensorEvent.values[2];

            textView4.setText(String.valueOf(mMagnetometerReading[0]));
            textView5.setText(String.valueOf(mMagnetometerReading[1]));
            textView6.setText(String.valueOf(mMagnetometerReading[2]));

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            mLightReading = sensorEvent.values[0];
            textView7.setText(String.valueOf(mLightReading));
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            mProximityReading = sensorEvent.values[0];
            textView11.setText(String.valueOf(mProximityReading));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (senMagnetic != null)
            sensorManager.registerListener(this, senMagnetic, currentInterval);

        if (senAccelerometer != null)
            sensorManager.registerListener(this, senAccelerometer, currentInterval);

        if (sensLight != null)
            sensorManager.registerListener(this, sensLight, currentInterval);

        if (senGyroscope != null)
            sensorManager.registerListener(this, senGyroscope, currentInterval);

        if (senLinearAcceleration != null)
            sensorManager.registerListener(this, senLinearAcceleration, currentInterval);

        if (sensGravity != null)
            sensorManager.registerListener(this, sensGravity, currentInterval);

        if (senProximity != null)
            sensorManager.registerListener(this, senProximity, currentInterval);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        this.unregisterReceiver(this.mBatInfoReceiver);
    }

    public void logFileClick() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Copacs Data/").toString();

        Uri selectedUri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");
        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
            startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(path);
            intent.setDataAndType(uri, "text/csv");
            startActivity(Intent.createChooser(intent, "Open folder"));
        }
    }

    public void deleteFileClick(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteFileClick(child);

        fileOrDirectory.delete();
    }

    public void mainClick(View arg0) {
        if (buttonFlag == 0) {
            mySoundPool.play(mSoundIdInit, 1, 1, 1, 0, 1);
            checkUniformity = 0;
            logButton = menu.findItem(R.id.logFileButton);
            deleteButton = menu.findItem(R.id.deleteFileButton);
            logButton.setEnabled(false);
            deleteButton.setEnabled(false);
            activitiesSpinner.setEnabled(false);
            phonePositionSpinner.setEnabled(false);
            autoCompleteTextView.setEnabled(false);
            myTag = activitiesSpinner.getSelectedItem().toString();
            myTagpos = phonePositionSpinner.getSelectedItem().toString();
            if (autoCompleteTextView.getText().toString().equals(""))
                autoCompleteTextView.setText("Not Specified");
            buttonFlag = 1;
            button.setEnabled(false);
            button.setText("Just a sec!");
            final String filename = day + "_" + month + "_" + myTag + "_" + myTagpos + ".csv";
            final String fileName1 = "AllData.csv";
            Handler handler4 = new Handler();
            final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            manualLocation = autoCompleteTextView.getText().toString();
            if (manualLocation.equals("") || manualLocation == null)
                manualLocation = "Not_specified";
            handler4.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mySoundPool.play(mSoundIdtart, 1, 1, 1, 0, 1);
                    if (volumeCheck() == 1)
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);

                    new CountDownTimer(120000, 1000) {
                        public void onTick(long millis) {
                            long seconds = (millis / 1000) % 60;
                            long minutes = ((millis - seconds) / 1000) / 60;
                            String output = "Remaining Time   ";
                            if (minutes >= 1)
                                output += String.valueOf(minutes) + " : " + String.valueOf(seconds);
                            else
                                output += String.valueOf(seconds) + " seconds";
                            button.setText(output);
                        }

                        public void onFinish() {
                            Toast.makeText(MainActivity.this, "Stopped Logging!", Toast.LENGTH_SHORT).show();
                            button.setEnabled(true);
                        }

                    }.start();
                    dataArray.clear();
                    new CountDownTimer(120000, 100) {

                        public void onTick(long millis) {
                            performTick(currentDateTimeString, manualLocation);
                        }

                        public void onFinish() {
                            mySoundPool.play(mSoundIdStop, 1, 1, 1, 1, 1);
                            if (volumeCheck() == 1)
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                            Toast.makeText(MainActivity.this, String.valueOf("Size is " + dataArray.size()), Toast.LENGTH_LONG).show();
                            BigComputationTask t1 = new BigComputationTask(filename, fileName1, dataArray, MainActivity.this, passView, MainActivity.this);
                            t1.execute();
                            findViewById(R.id.button1).performClick();
                        }
                    }.start();
                }
            }, 6000);

        } else {
            char temp = logTextView.getText().toString().charAt(0);
            if (temp == '0') {
                logTextView.setText(String.valueOf(checkUniformity));
            } else {
                String temp1 = logTextView.getText().toString();
                temp1 = String.valueOf(checkUniformity) + "," + temp1;
                logTextView.setText(temp1);
            }
            logButton = menu.findItem(R.id.logFileButton);
            deleteButton = menu.findItem(R.id.deleteFileButton);
            logButton.setEnabled(true);
            deleteButton.setEnabled(true);
            activitiesSpinner.setEnabled(true);
            phonePositionSpinner.setEnabled(true);
            autoCompleteTextView.setEnabled(true);
            autoCompleteTextView.setText("");
            buttonFlag = 0;
        }
    }

    public void performTick(String currentDateTimeString, String manualLocation) {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        String[] data = {ts
                , currentDateTimeString
                , String.valueOf(mAccelerometerReading[0])
                , String.valueOf(mAccelerometerReading[1])
                , String.valueOf(mAccelerometerReading[2])
                , String.valueOf(resultantAccelerometer)
                , String.valueOf(mMagnetometerReading[0])
                , String.valueOf(mMagnetometerReading[1])
                , String.valueOf(mMagnetometerReading[2])
                , String.valueOf(mLightReading)
                , String.valueOf(morientationAngles[0])
                , String.valueOf(morientationAngles[1])
                , String.valueOf(morientationAngles[2])
                , String.valueOf(mProximityReading)
                , String.valueOf(latitude)
                , String.valueOf(longitude)
                , String.valueOf(speed)
                , String.valueOf(altitude)
                , String.valueOf(batteryLevel)
                , String.valueOf(batteryTemperature)
                , String.valueOf(mGravityReading[0])
                , String.valueOf(mGravityReading[1])
                , String.valueOf(mGravityReading[2])
                , String.valueOf(resultantGravity)
                , String.valueOf(mGyroscopeReading[0])
                , String.valueOf(mGyroscopeReading[1])
                , String.valueOf(mGyroscopeReading[2])
                , String.valueOf(mLinearAccelerationReading[0])
                , String.valueOf(mLinearAccelerationReading[1])
                , String.valueOf(mLinearAccelerationReading[2])
                , String.valueOf(resultantLinearAcceleration)
                , myTag
                , myTagpos
                , manualLocation
                , String.valueOf(currentInterval)};
        checkUniformity += 1;
        dataArray.add(data);
    }

    public int volumeCheck() {
        int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxvol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (vol < maxvol)
            return 1;
        else return 0;
    }

}
