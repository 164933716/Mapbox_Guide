package com.mapbox.global;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;

import androidx.annotation.NonNull;


/**
 * Change the camera's bearing and tilt based on device movement while viewing building extrusions
 */
public class RotationExtrusionActivity extends SimpleMapViewActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private SensorControl sensorControl;
    private float[] gravityArray;
    private float[] magneticArray;
    private float[] inclinationMatrix = new float[9];
    private float[] rotationMatrix = new float[9];
    private static final int PITCH_AMPLIFIER = -90;
    private static final int BEARING_AMPLIFIER = 90;


    @Override
    protected void onMapReady(Style style) {
        setupBuildingExtrusionPlugin(style);
    }

    private void setupBuildingExtrusionPlugin(@NonNull Style style) {
        BuildingPlugin buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style);
        buildingPlugin.setColor(Color.LTGRAY);
        buildingPlugin.setOpacity(0.6f);
        buildingPlugin.setMinZoomLevel(15);
        buildingPlugin.setVisibility(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorControl = new SensorControl(sensorManager);
        registerSensorListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, sensorControl.getGyro());
        sensorManager.unregisterListener(this, sensorControl.getMagnetic());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravityArray = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticArray = event.values;
        }
        if (gravityArray != null && magneticArray != null) {
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, gravityArray, magneticArray);
            if (success) {
                if (mapboxMap != null) {
                    int mapCameraAnimationMillisecondsSpeed = 100;
                    mapboxMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(createNewCameraPosition()), mapCameraAnimationMillisecondsSpeed
                    );
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Intentionally left empty
    }

    private CameraPosition createNewCameraPosition() {
        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);
        float pitch = orientation[1];
        float roll = orientation[2];

        CameraPosition position = new CameraPosition.Builder()
                .tilt(pitch * PITCH_AMPLIFIER)
                .bearing(roll * BEARING_AMPLIFIER)
                .build();

        return position;
    }

    private void registerSensorListeners() {
        int sensorEventDeliveryRate = 200;
        if (sensorControl.getGyro() != null) {
            sensorManager.registerListener(this, sensorControl.getGyro(), sensorEventDeliveryRate);
        } else {
            Toast.makeText(this, "no accelerometer sensor", Toast.LENGTH_SHORT).show();
        }
        if (sensorControl.getMagnetic() != null) {
            sensorManager.registerListener(this, sensorControl.getMagnetic(), sensorEventDeliveryRate);
        } else {
            Toast.makeText(this, "no magnetic sensor", Toast.LENGTH_SHORT).show();
        }
    }

    private class SensorControl {
        private Sensor gyro;
        private Sensor magnetic;

        SensorControl(SensorManager sensorManager) {
            this.gyro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            this.magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        Sensor getGyro() {
            return gyro;
        }

        Sensor getMagnetic() {
            return magnetic;
        }
    }
}

