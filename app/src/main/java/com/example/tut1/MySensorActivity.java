package com.example.tut1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.location.Criteria;


public class MySensorActivity extends Activity implements SensorEventListener {

    private TextView tvLeft;
    private TextView tvRight;
    private SensorManager mSensorManager;
    private Sensor mGyroSensor;
    private  Sensor myAccellor;
    private Sensor myLight;
    //private LocationManager myLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get an instance of the sensor service
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        myAccellor= mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        myLight= mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        PackageManager PM= this.getPackageManager();
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        final int type = event.sensor.getType();
        if(type==Sensor.TYPE_LIGHT) {
            final float currentReading = event.values[0];
            tvRight.setText("light :"+ Float.toString(currentReading));
            return;
        }
        if (type != Sensor.TYPE_ACCELEROMETER)
           return;
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
            return;
        float Gx = event.values[0];
        float Gy = event.values[1];
        float Gz = event.values[2];
        //Criteria criteria = new Criteria();
        //criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //String  bestProvider = myLocationManager.getBestProvider(criteria, false);
        //Location location = myLocationManager.getLastKnownLocation(bestProvider);

        tvLeft.setText(" x-axis :"+ Float.toString(Gx) +"\n"+
                   " y-axis :"+ Float.toString(Gy) +"\n"+
                   " z-axis :"+ Float.toString(Gz)+"\n"+
                   " ============================="+"\n"
                  // Double.toString(location.getLatitude())
        );
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, myAccellor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, myLight, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        // important to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

}