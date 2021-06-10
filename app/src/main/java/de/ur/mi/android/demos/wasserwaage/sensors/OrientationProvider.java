package de.ur.mi.android.demos.wasserwaage.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationProvider implements SensorEventListener {

    private final SensorManager sensorManager;
    private final OrientationListener listener;
    private final float[] calibrationValues = new float[3];
    private final float[] currentValues = new float[3];
    private boolean calibrationRequestPending = false;

    public OrientationProvider(SensorManager sensorManager, OrientationListener listener) {
        this.sensorManager = sensorManager;
        this.listener = listener;
    }

    public void start() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void calibrate() {
        calibrationRequestPending = true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.arraycopy(event.values, 0, currentValues, 0, currentValues.length);
        if (calibrationRequestPending) {
            System.arraycopy(currentValues, 0, calibrationValues, 0, calibrationValues.length);
            calibrationRequestPending = false;
        }
        float[] normalizedValues = getNormalizedValues(currentValues, 10);
        listener.onOrientationChanged(normalizedValues[0], normalizedValues[1], normalizedValues[2]);
    }

    private float[] getNormalizedValues(float[] rawValues, int precisionBase) {
        float[] normalizedValues = new float[rawValues.length];
        System.arraycopy(rawValues, 0, normalizedValues, 0, normalizedValues.length);
        for (int i = 0; i < normalizedValues.length; i++) {
            float calibratedValue = rawValues[i] - calibrationValues[i];
            int tmp = (int) (calibratedValue * precisionBase);
            normalizedValues[i] = ((float) tmp) / precisionBase;
        }
        return normalizedValues;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface OrientationListener {
        void onOrientationChanged(float x, float y, float z);
    }
}
