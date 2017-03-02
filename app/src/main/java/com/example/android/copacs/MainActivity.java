package com.example.android.copacs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    //    private static final int REQUEST_ACCESS_FINE_LOCATION = 2;
//    private static final int REQUEST_ACCESS_COARSE_LOCATION = 3;
    private static final String[] activities = {
            "Walking",
            "Sitting",
            "Elevator_Ascend",
            "Elevator_Descend",
            "Running",
            "Driving",
            "Staircase_Ascend",
            "Staircase_descend",
            "Sleeping"};

    private static final String[] positions = {
            "Hand",
            "Trouser",
            "Upper_pocket",
            "Stationary_object"};

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    final float[] mrotationMatrix = new float[9];
    final float[] morientationAngles = new float[3];
    SensorManager sensorManager;

    Sensor
            senAccelerometer,
            senMagnetic,
            senGyroscope,
            senLinearAcceleration,
            sensGravity,
            sensLight,
            senProximity;

    int
            acceloremeterFlag = 0,
            magnetometerFlag = 0,
            gyroscopeFlag = 0,
            linearAccelerationFlag = 0,
            gravityFlag = 0,
            lightFlag = 0,
            proximityFlag = 0;

    private String[] locations = {
            "ABB1",
            "ABB2",
            "ABB3",
            "Sarojini",
            "Annapurna",
            "Gate_1",
            "Gate_2",
            "Gate_3"};

    private float[] mAccelerometerReading = new float[3];
    private float[] mMagnetometerReading = new float[3];
    private float[] mGyroscopeReading = new float[3];
    private float[] mLinearAccelerationReading = new float[3];
    private float[] mGravityReading = new float[3];
    private float mLightReading;
    private float mProximityReading;
    private TextView
            accelerometer_x_TextView,
            accelerometer_y_TextView,
            acclelerometer_z_TextView,
            magnetometer_x_TextView,
            magnetometer_y_TextView,
            magnetometer_z_TextView,
            lightTextView,
            orientation_X_TextView,
            orientation_Y_TextView,
            orientation_Z_TextView,
            proximityTextView,
    //            latitudeTextView,
//            longitudeTextView,
//            batteryLevelTextView,
//            speedTextView,
//            batteryTemperatureTextView,
    gravity_x_TextView,
            gravity_y_TextView,
            gravity_z_TextView,
            gyroscope_x_TextView,
            gyroscope_y_TextView,
            gyroscope_z_TextView,
            gravityHeader_TextView,
            gyroHeader_TextView,
            linear_x_TextView,
            linear_y_TextView,
            linear_z_TextView,
            linear_acc_header_TextView,
            notPresentTextView,
            resultantLinearAccelerationReadingTextView,
            resultantLinearAccelerationHeaderTextView,
            resultantGravityHeaderTextView,
            resultantGravityreadingTextView,
            resultantAccelerationHeaderTextView,
            resultantAcceleratiobReadingTextView,
    //            altitudeTextView,
    logTextView;

    private Button
            mainButton,
            writeButton;
    private
    MenuItem
            logButton,
            deleteButton;

    private String
            myTag = "",
            filename,
            fileName1;
    private int
//            batteryLevel,
            buttonFlag = 0;

//    private float batteryTemperature;
//    private LocationManager locationManager;
//    private double
//            latitude,
//            longitude,
//            speed;

    //    private Location lastKnownLocation;
//    private BroadcastReceiver mBatInfoReceiver;
    private String notPresent = "";
    private ArrayAdapter<String> adapter1;
    private String myTagpos;
    private Spinner
            activitiesSpinner,
            phonePositionSpinner;

    private double
            resultantAccelerometer,
            resultantLinearAcceleration,
            resultantGravity;

    private int currentInterval;
    //    private LocationListener locationListener;
//    private double altitude;
    private AutoCompleteTextView autoCompleteTextView;
    private String manualLocation;
    private Menu menu;
    private int checkUniformity = 0;
    private SoundPool mySoundPool;
    private int
            mSoundIdInit,
            mSoundIdtart,
            mSoundIdStop;
    private float proximityRange;

    private AudioManager audioManager;
    private Calendar calender;
    private int day;
    private String month;
    private ArrayList<String[]> dataArray;
    private View passView;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private CheckBox checkBox;

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
//        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
//        if (permission1 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_ACCESS_FINE_LOCATION
//            );
//        }

//        if (permission2 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_ACCESS_COARSE_LOCATION
//            );
//        }
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
                                deleteFileClick(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Copacs Data Lite"));
                                Toast.makeText(MainActivity.this, "Folder deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Copacs Data Lite").mkdir();
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
        accelerometer_x_TextView = (TextView) findViewById(R.id.Accelerometer_x);
        accelerometer_y_TextView = (TextView) findViewById(R.id.Accelerometer_y);
        acclelerometer_z_TextView = (TextView) findViewById(R.id.Accelerometer_z);
        magnetometer_x_TextView = (TextView) findViewById(R.id.Magnetometer_x);
        magnetometer_y_TextView = (TextView) findViewById(R.id.Magnetometer_y);
        magnetometer_z_TextView = (TextView) findViewById(R.id.Magnetometer_z);
        lightTextView = (TextView) findViewById(R.id.lightTextView);
        orientation_X_TextView = (TextView) findViewById(R.id.orientation_x);
        orientation_Y_TextView = (TextView) findViewById(R.id.orientation_y);
        orientation_Z_TextView = (TextView) findViewById(R.id.orientation_z);
        proximityTextView = (TextView) findViewById(R.id.proximityTextView);
//        latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
//        longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
//        batteryLevelTextView = (TextView) findViewById(R.id.batteryLevelTextView);
//        speedTextView = (TextView) findViewById(R.id.speedTextView);
//        batteryTemperatureTextView = (TextView) findViewById(R.id.batteryTemperatureTextView);
//        altitudeTextView = (TextView) findViewById(R.id.altitudeTextView);
        gravity_x_TextView = (TextView) findViewById(R.id.gravity_x);
        gravity_y_TextView = (TextView) findViewById(R.id.gravity_y);
        gravity_z_TextView = (TextView) findViewById(R.id.gravity_z);
        gyroscope_x_TextView = (TextView) findViewById(R.id.gyro_x);
        gyroscope_y_TextView = (TextView) findViewById(R.id.gyro_y);
        gyroscope_z_TextView = (TextView) findViewById(R.id.gyro_z);
        gravityHeader_TextView = (TextView) findViewById(R.id.gravity_header);
        gyroHeader_TextView = (TextView) findViewById(R.id.gyro_header);
        linear_x_TextView = (TextView) findViewById(R.id.linear_x);
        linear_y_TextView = (TextView) findViewById(R.id.linear_y);
        linear_z_TextView = (TextView) findViewById(R.id.linear_z);
        linear_acc_header_TextView = (TextView) findViewById(R.id.linear_acc_header);
        notPresentTextView = (TextView) findViewById(R.id.notPresentTextView);
        logTextView = (TextView) findViewById(R.id.logTextView);
        resultantAccelerationHeaderTextView = (TextView) findViewById(R.id.raHeader);
        resultantAcceleratiobReadingTextView = (TextView) findViewById(R.id.raReading);
        resultantLinearAccelerationHeaderTextView = (TextView) findViewById(R.id.rla_header);
        resultantLinearAccelerationReadingTextView = (TextView) findViewById(R.id.rla_reading);
        resultantGravityHeaderTextView = (TextView) findViewById(R.id.rgheader);
        resultantGravityreadingTextView = (TextView) findViewById(R.id.rgReading);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        checkBox = (CheckBox) findViewById(R.id.sound);
        calender = Calendar.getInstance(TimeZone.getDefault());
        month = calender.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        day = calender.get(Calendar.DAY_OF_MONTH);
        dataArray = new ArrayList<>();
        activitiesSpinner = (Spinner) findViewById(R.id.spinner1);
        phonePositionSpinner = (Spinner) findViewById(R.id.spinner2);
        currentInterval = 50000;
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, locations);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        writeButton = (Button) findViewById(R.id.fileWrite);
        if (volumeCheck() == 1)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (checkBox.isChecked()) {
                                                        if (volumeCheck() == 1)
                                                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                                                    } else {
                                                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
                                                    }
                                                }
                                            }
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mySoundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .build();
        } else {
            mySoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        }
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Copacs Data Lite");
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
        writeButton.setEnabled(false);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigComputationTask t1 = new BigComputationTask(filename, fileName1, dataArray, MainActivity.this, passView, MainActivity.this);
                t1.execute();
                Toast.makeText(MainActivity.this, "Write successful !", Toast.LENGTH_SHORT).show();
                writeButton.setEnabled(false);
            }
        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if (!enabled) {
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
//        }
//        locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                // Called when a new location is found by the network location provider.
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//                speed = location.getSpeed();
//                latitudeTextView.setText("Latitude : " + String.valueOf(latitude));
//                longitudeTextView.setText("Longitude : " + String.valueOf(longitude));
//                speedTextView.setText("Speed : " + String.valueOf(speed));
//                altitudeTextView.setText("Altitude : " + String.valueOf(altitude));
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//            }
//
//            public void onProviderEnabled(String provider) {
//                Toast.makeText(MainActivity.this, "Provider is enabled", Toast.LENGTH_SHORT).show();
//            }
//
//            public void onProviderDisabled(String provider) {
//                Toast.makeText(MainActivity.this, "COPACS needs location services to be up and running!", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        };
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0.5f, locationListener);
//        Location location = locationManager
//                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//            altitude = location.getAltitude();
//            speed = location.getSpeed();
//        }
//        Criteria crta = new Criteria();
//        crta.setAccuracy(Criteria.ACCURACY_FINE);
//        crta.setAltitudeRequired(true);
//        crta.setSpeedRequired(true);
//        crta.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
//        crta.setPowerRequirement(Criteria.POWER_LOW);
//
//        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        latitude = 0;
//        longitude = 0;
//        speed = 0;
//        altitude = 0;
        resultantAccelerometer = 0;
        resultantGravity = 0;
        resultantLinearAcceleration = 0;
//        if (lastKnownLocation != null) {
//            latitude = lastKnownLocation.getLatitude();
//            longitude = lastKnownLocation.getLongitude();
//            speed = lastKnownLocation.getSpeed();
//            altitude = lastKnownLocation.getAltitude();
//        }
//        latitudeTextView.setText("Latitude(Last known) : " + String.valueOf(latitude));
//        longitudeTextView.setText("Longitude(Last known) : " + String.valueOf(longitude));
//        speedTextView.setText("Speed(Last known) : " + String.valueOf(speed));
//        altitudeTextView.setText("Altitude(last known) : " + String.valueOf(altitude));

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

        Arrays.fill(mAccelerometerReading, 0);
        Arrays.fill(mMagnetometerReading, 0);
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
            proximityRange = senProximity.getMaximumRange();
            proximityFlag = 1;
        } else
            notPresent += "No Proximity sensor!\n";

        if (senAccelerometer != null) {
            sensorManager.registerListener(this, senAccelerometer, currentInterval);
            acceloremeterFlag = 1;
        } else {
            notPresent += "No Accelerometer!\n";
            resultantAcceleratiobReadingTextView.setVisibility(View.GONE);
            resultantAccelerationHeaderTextView.setVisibility(View.GONE);
        }

        if (senGyroscope != null) {
            gyroscopeFlag = 1;
            sensorManager.registerListener(this, senGyroscope, currentInterval);
        } else {
            gyroscope_x_TextView.setVisibility(View.GONE);
            gyroscope_y_TextView.setVisibility(View.GONE);
            gyroscope_z_TextView.setVisibility(View.GONE);
            gyroHeader_TextView.setVisibility(View.GONE);
            notPresent += "No gyroscope sensor!\n";
        }

        if (senLinearAcceleration != null) {
            linearAccelerationFlag = 1;
            sensorManager.registerListener(this, senLinearAcceleration, currentInterval);
        } else {
            linear_x_TextView.setVisibility(View.GONE);
            linear_y_TextView.setVisibility(View.GONE);
            linear_z_TextView.setVisibility(View.GONE);
            linear_acc_header_TextView.setVisibility(View.GONE);
            resultantLinearAccelerationHeaderTextView.setVisibility(View.GONE);
            resultantLinearAccelerationReadingTextView.setVisibility(View.GONE);
            notPresent += "No linear acceleration sensor!\n";
        }
        if (sensGravity != null) {
            gravityFlag = 1;
            sensorManager.registerListener(this, sensGravity, currentInterval);
        } else {
            gravity_x_TextView.setVisibility(View.GONE);
            gravity_y_TextView.setVisibility(View.GONE);
            gravity_z_TextView.setVisibility(View.GONE);
            gravityHeader_TextView.setVisibility(View.GONE);
            resultantGravityHeaderTextView.setVisibility(View.GONE);
            resultantGravityreadingTextView.setVisibility(View.GONE);
            notPresent += "No gravity sensor!\n";
        }
        if (sensLight != null) {
            lightFlag = 1;
            sensorManager.registerListener(this, sensLight, currentInterval);
        } else
            notPresent += "No light sensor!\n";


        notPresentTextView.setText(notPresent);

//        if (Build.VERSION.SDK_INT >= 23 &&
//                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }

        mainButton = (Button) findViewById(R.id.mainButton);
        mainButton.setText("Start logging!");
    }

//        mBatInfoReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context ctxt, Intent intent) {
//                batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//                batteryTemperature = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10);
//                batteryLevelTextView.setText("Battery Level : " + String.valueOf(batteryLevel) + "%");
//                batteryTemperatureTextView.setText("Battery Temperature : " + String.valueOf(batteryTemperature) + " °C");
//            }
//        };

// Register the listener with the Location Manager to receive location updates
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (senMagnetic != null)
//            sensorManager.registerListener(this, senMagnetic, currentInterval);
//
//        if (senAccelerometer != null)
//            sensorManager.registerListener(this, senAccelerometer, currentInterval);
//
//        if (sensLight != null)
//            sensorManager.registerListener(this, sensLight, currentInterval);
//
//        if (senGyroscope != null)
//            sensorManager.registerListener(this, senGyroscope, currentInterval);
//
//        if (senLinearAcceleration != null)
//            sensorManager.registerListener(this, senLinearAcceleration, currentInterval);
//
//        if (sensGravity != null)
//            sensorManager.registerListener(this, sensGravity, currentInterval);
//
//        if (senProximity != null)
//            sensorManager.registerListener(this, senProximity, currentInterval);
//        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        sensorManager.unregisterListener(this);
//        this.unregisterReceiver(this.mBatInfoReceiver);
//    }

    public void logFileClick() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Copacs Data Lite/").toString();

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
            writeButton.setEnabled(false);
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
            mainButton.setEnabled(false);
            mainButton.setText("Just a sec!");
            filename = day + "_" + month + "_" + myTag + "_" + myTagpos + ".csv";
            fileName1 = "AllData.csv";
            Handler handler4 = new Handler();

            manualLocation = autoCompleteTextView.getText().toString();
            handler4.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mySoundPool.play(mSoundIdtart, 1, 1, 1, 0, 1);
                    if (checkBox.isChecked()) {
                        if (volumeCheck() == 1)
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                    } else {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
                    }
                    new CountDownTimer(120000, 1000) {
                        public void onTick(long millis) {
                            long seconds = (millis / 1000) % 60;
                            long minutes = ((millis - seconds) / 1000) / 60;
                            String output = "Remaining Time   ";
                            if (minutes >= 1)
                                output += String.valueOf(minutes) + " : " + String.valueOf(seconds);
                            else
                                output += String.valueOf(seconds) + " seconds";
                            mainButton.setText(output);
                        }

                        public void onFinish() {
                            Toast.makeText(MainActivity.this, "Stopped Logging!", Toast.LENGTH_SHORT).show();
                            mainButton.setEnabled(true);
                        }

                    }.start();
                    dataArray.clear();
                    wakeLock.acquire();
                    new CountDownTimer(120000, 100) {
                        public void onTick(long millis) {
                            performTick(manualLocation);
                        }

                        public void onFinish() {
                            mySoundPool.play(mSoundIdStop, 1, 1, 1, 1, 1);
                            if (checkBox.isChecked()) {
                                if (volumeCheck() == 1)
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                            } else {
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
                            }
                            findViewById(R.id.mainButton).performClick();
                            Toast.makeText(MainActivity.this, String.valueOf("Size is " + dataArray.size()), Toast.LENGTH_LONG).show();
                            mainButton.setText("Start Logging !");
                            wakeLock.release();
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
            writeButton.setEnabled(true);
            autoCompleteTextView.setText("");
            buttonFlag = 0;
        }
    }

    public void performTick(String manualLocation) {
        final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
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
//                , String.valueOf(latitude)
//                , String.valueOf(longitude)
//                , String.valueOf(speed)
//                , String.valueOf(altitude)
//                , String.valueOf(batteryLevel)
//                , String.valueOf(batteryTemperature)
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
                , manualLocation};
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER || sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            SensorManager.getRotationMatrix(mrotationMatrix, null,
                    mAccelerometerReading, mMagnetometerReading);
            SensorManager.getOrientation(mrotationMatrix, morientationAngles);
            morientationAngles[0] = (float) Math.toDegrees(morientationAngles[0]);
            morientationAngles[1] = (float) Math.toDegrees(morientationAngles[1]);
            morientationAngles[2] = (float) Math.toDegrees(morientationAngles[2]);
            orientation_X_TextView.setText(String.valueOf(morientationAngles[0]));
            orientation_Y_TextView.setText(String.valueOf(morientationAngles[1]));
            orientation_Z_TextView.setText(String.valueOf(morientationAngles[2]));
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometerReading[0] = sensorEvent.values[0];
            mAccelerometerReading[1] = sensorEvent.values[1];
            mAccelerometerReading[2] = sensorEvent.values[2];
            resultantAccelerometer = Math.sqrt(Math.pow(mAccelerometerReading[0], 2) + Math.pow(mAccelerometerReading[1], 2)
                    + Math.pow(mAccelerometerReading[2], 2));
            resultantAcceleratiobReadingTextView.setText(String.valueOf(resultantAccelerometer));
            accelerometer_x_TextView.setText(String.valueOf(mAccelerometerReading[0]));
            accelerometer_y_TextView.setText(String.valueOf(mAccelerometerReading[1]));
            acclelerometer_z_TextView.setText(String.valueOf(mAccelerometerReading[2]));

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            mLinearAccelerationReading[0] = sensorEvent.values[0];
            mLinearAccelerationReading[1] = sensorEvent.values[1];
            mLinearAccelerationReading[2] = sensorEvent.values[2];
            resultantLinearAcceleration = Math.sqrt(Math.pow(mLinearAccelerationReading[0], 2) +
                    Math.pow(mLinearAccelerationReading[1], 2) + Math.pow(mLinearAccelerationReading[2], 2));
            if (linearAccelerationFlag == 1) {
                linear_x_TextView.setText(String.valueOf(mLinearAccelerationReading[0]));
                linear_y_TextView.setText(String.valueOf(mLinearAccelerationReading[1]));
                linear_z_TextView.setText(String.valueOf(mLinearAccelerationReading[2]));
                resultantLinearAccelerationReadingTextView.setText(String.valueOf(resultantLinearAcceleration));
            }

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
            mGravityReading[0] = sensorEvent.values[0];
            mGravityReading[1] = sensorEvent.values[1];
            mGravityReading[2] = sensorEvent.values[2];
            resultantGravity = Math.sqrt(Math.pow(mGravityReading[0], 2) + Math.pow(mGravityReading[1], 2)
                    + Math.pow(mGravityReading[2], 2));
            if (gravityFlag == 1) {
                gravity_x_TextView.setText(String.valueOf(mGravityReading[0]));
                gravity_y_TextView.setText(String.valueOf(mGravityReading[1]));
                gravity_z_TextView.setText(String.valueOf(mGravityReading[2]));
                resultantGravityreadingTextView.setText(String.valueOf(resultantGravity));
            }

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            mGyroscopeReading[0] = sensorEvent.values[0];
            mGyroscopeReading[1] = sensorEvent.values[1];
            mGyroscopeReading[2] = sensorEvent.values[2];
            if (gyroscopeFlag == 1) {
                gyroscope_x_TextView.setText(String.valueOf(mGyroscopeReading[0]));
                gyroscope_y_TextView.setText(String.valueOf(mGyroscopeReading[1]));
                gyroscope_z_TextView.setText(String.valueOf(mGyroscopeReading[2]));
            }

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagnetometerReading[0] = sensorEvent.values[0];
            mMagnetometerReading[1] = sensorEvent.values[1];
            mMagnetometerReading[2] = sensorEvent.values[2];

            magnetometer_x_TextView.setText(String.valueOf(mMagnetometerReading[0]));
            magnetometer_y_TextView.setText(String.valueOf(mMagnetometerReading[1]));
            magnetometer_z_TextView.setText(String.valueOf(mMagnetometerReading[2]));

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            mLightReading = sensorEvent.values[0];
            lightTextView.setText(String.valueOf(mLightReading));
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if ((sensorEvent.values[0] - proximityRange) >= 0)
                mProximityReading = 1;
            else
                mProximityReading = 0;
            proximityTextView.setText(String.valueOf(mProximityReading));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
