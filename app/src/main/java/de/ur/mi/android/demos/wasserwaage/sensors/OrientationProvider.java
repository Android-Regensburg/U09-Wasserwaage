package de.ur.mi.android.demos.wasserwaage.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationProvider implements SensorEventListener {

    // SensorManager für die Auswahl des Beschleunigungssensors, wird im Konstruktor übergeben und
    private final SensorManager sensorManager;
    private final OrientationListener listener;
    // Messwerte des Sensors zum Zeitpunkt der letzten Kalibrierung
    private final float[] calibrationValues = new float[3];
    // Zu letzt erhaltene Messwerte des Sensors
    private final float[] currentValues = new float[3];
    // Indikator, der aussagt, ob beim nächsten Update der Sensorwerte eine Kalibrierung durchgeführt werden soll
    private boolean calibrationRequestPending = false;

    public OrientationProvider(SensorManager sensorManager, OrientationListener listener) {
        this.sensorManager = sensorManager;
        this.listener = listener;
    }

    /**
     * Startet die Verarbeitung und Weitergabe der Sensorwerte des Beschleunigungssensors
     */
    public void start() {
        /**
         * In der Methode wird versucht, den Beschleunigungssensor des Systems auszulesen und die
         * aktuelle Instanz des OrientationProviders als Listener für Sensorupdates auf diesem zu
         * registrieren.
         */
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * Stoppt die Verarbeitung und Weitergabe der Sensorwerte des Beschleunigungssensors
     */
    public void stop() {
        // Deregistriert diese Instanz des OrientationProviders als SensorListener
        sensorManager.unregisterListener(this);
    }

    /**
     * Führt beim nächsten Update der Sensorwerte eine Kalibrierung durch, die sich auf alle danach
     * aus dem Provider weitergegebenen Messwerte auswirkt.
     */
    public void calibrate() {
        calibrationRequestPending = true;
    }

    /**
     * Callback-Methode die aufgerufen wird, wenn der (Beschleunigungs-) Sensor neue Messwerte bereitstellt
     *
     * @param event Informationen zur Messung, z.B. die genauen Werte
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Kopiert die aktuellen Messwerte in den Zwischenspeicher
        System.arraycopy(event.values, 0, currentValues, 0, currentValues.length);
        // Falls im Vorfeld eine Kalibrierung vorgemerkt wurde ...
        if (calibrationRequestPending) {
            // ... werden die aktuellen Messwerte zusätzlich als Kalibrierungswerte gespeichert ...
            System.arraycopy(currentValues, 0, calibrationValues, 0, calibrationValues.length);
            // ... und der Kalibrierungsauftrag wird als abgeschlossen markiert
            calibrationRequestPending = false;
        }
        /*
         * Der maximale Wert, der aus dem eingesetzten Sensor ausgelesen werden kann, wird über
         * die Methode getMaximumRange bestimmt. Der minimale Wert entspricht dem negierten
         * Maximalwert. Dieser Wert wird verwendet, um alle Messwerte auf die Skala -1.0 bis 1.0 zu
         * reduzieren. Der Wert wird an die Normalisierungsfunktion übergeben, in der dieser, zusammen
         * mit möglicherweise vorhandenen Kalibrierungswerten, zur Anpassung der aktuellsten Messwerte
         * des Sensors verwendet wird.
         */
        float[] normalizedValues = getNormalizedValues(currentValues, event.sensor.getMaximumRange());
        // Die normalisierten Werden werden anschließend an den Listener weitergeben (in diesem Fall: MainActivity)
        listener.onNewMeasurementAvailable(normalizedValues[0], normalizedValues[1], normalizedValues[2]);
    }

    /**
     * Normalisiert die übergebenen Messwerte anhand verschiedener Kritierien und gibt ein Array
     * mit den so umgewandelten Werten zurück.
     */
    private float[] getNormalizedValues(float[] rawValues, float normalizingDivisor) {
        // "Leeres" Array für die in dieser Methode normalisierten Messwerte
        float[] normalizedValues = new float[rawValues.length];
        // Kopieren der "rohen" Messwerte in das Zielarray
        System.arraycopy(rawValues, 0, normalizedValues, 0, normalizedValues.length);
        // Für jeden Messwert ...
        for (int i = 0; i < normalizedValues.length; i++) {
            // ... wird der entsprechende Kalibrierungswert (Ruheposition) abgezogen ...
            float calibratedValue = rawValues[i] - calibrationValues[i];
            // ... und das Ergebnis durch Division mit dem maximalen Sensorwert auf eine passende Skala runtergebrochen
            normalizedValues[i] = calibratedValue / normalizingDivisor;
        }
        return normalizedValues;
    }

    /**
     * Callback-Methode die aufgerufen wird, wenn sich die Genauigkeit des eingesetzten Sensors
     * ändert (wird hier nicht verwendet)
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Interface für Listener, die über Änderungen an den Messwerten des Beschleunigungssensors
     * informiert werden sollen.
     */
    public interface OrientationListener {
        /**
         * Die Methode wird aufgerufen, wenn neue Messwerte des Beschleunigungssensors vorliegen.
         *
         * @param x Aktuelle Beschleunigung auf der X-Achse, normalisiert für den Wertebereich -1.0 bis 1.0
         * @param y Aktuelle Beschleunigung auf der Y-Achse, normalisiert für den Wertebereich -1.0 bis 1.0
         * @param z Aktuelle Beschleunigung auf der Z-Achse, normalisiert für den Wertebereich -1.0 bis 1.0
         */
        void onNewMeasurementAvailable(float x, float y, float z);
    }
}
