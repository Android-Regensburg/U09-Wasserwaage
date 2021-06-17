package de.ur.mi.android.demos.wasserwaage;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.ur.mi.android.demos.wasserwaage.sensors.OrientationProvider;
import de.ur.mi.android.demos.wasserwaage.ui.SpiritLevelView;

/**
 * Die Wasserwaage: Eine interaktive Wasserwaage für Android-Smartphones
 * <p>
 * Diese App stellt eine digitale Wasserwaage zur Verfügung. Im User Interface werden zwei separate
 * Elemente für die horizontale und vertikale Ausrichtung des Geräts angezeigt. Auf Basis der
 * aktuellen Werte des Beschleunigungssensor werden die jeweiligen Indikatoren der beiden Elemente
 * positioniert, um die aktuelle Lage des Geräts zu visualisieren.
 * <p>
 * Die Genauigkeit der Anzeige wird durch eine Möglichkeit zur Kalibrierung der Werte erhöht:
 * Nutzer*innen können das Gerät auf einer planen Fläche ablegen und über einen Klick auf den
 * Kalibrierungs-Button die aktuellen Messwerte speichern. Diese werden bei allen nachfolgenden
 * Messungen zur Normalisierung der Sensordaten verwendet.
 */
public class MainActivity extends AppCompatActivity implements OrientationProvider.OrientationListener {

    /**
     * In der selbst-erstellten Klasse OrientationProvider werden alle Funktionen gebündelt, die
     * direkt den Umgang mit dem Beschleunigungssensor betreffen. Instanzen der Klasse kommunizieren
     * die aktuellen Messwerte des Sensors über eine Listener-Schnittstelle (OrientationListener)
     * mit einer entsprechend registrierten, anderen Komponenten.
     */
    private OrientationProvider provider;
    /**
     * Die beiden UI-Elemente zur Darstellung der horizontalen und vertikalen Wasserwaagen werden
     * in zwei entsprechenden Instanzvariablen gehalten. Als Datentyp dient der Klassenname des
     * Custom View.
     */
    private SpiritLevelView spiritLevelHorizontal;
    private SpiritLevelView spiritLevelVertical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initOrientationProvider();
    }

    /**
     * Die Methode initialisiert das User Interface der Activity. Dabei wird der Floating Action
     * Button mit einem Click-Listener versehen, der bei Interaktion der Nutzer*innen mit dem Button
     * die Kalibrierungsfunktion der App auslöst. Zusätzlich werden die beiden UI-Elemente für die
     * Wasserwaagen referenziert und in den entsprechenden Instanzvariablen gespeichert.
     */
    private void initUI() {
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.calibrate_values_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (provider != null) {
                    provider.calibrate();
                }
            }
        });
        /*
         * Custom Views verhalten sich in der Regel genau so, wie "herkömmliche" Views oder
         * ViewGroups, z.B. wenn sie über die findViewById-Methode anhand ihrer ID aus der Layout-
         * Datei referenziert und in Form von Java-Objekten innerhalb der App genutzt werden.
         */
        spiritLevelHorizontal = findViewById(R.id.spirit_level_view_horizontal);
        spiritLevelVertical = findViewById(R.id.spirit_level_view_vertical);
    }

    /**
     * Die Methode initialisiert den OrientationProvider, der der Activity über eine Listener-
     * Schnittstelle die Änderungen der Messwerte des Beschleunigungssensors mitteilt. Dieser werden
     * innerhalb der Activity verwendet, um die UI-Elemente der beiden Wasserwaagen korrekt auszurichten.
     */
    private void initOrientationProvider() {
        /*
         * Innerhalb des Providers wird ein SensorManager benötigt, um auf den relevanten
         * Beschleunigungssensor zuzugreifen. Statt dem Provider die notwendigen Kontextinformationen
         * zu übergeben, um den Manager selbstständig referenzieren zu können, erhält der Konstruktor
         * den bereits erstellten bzw. referenzierten SensorManager als Parameter übergeben. Der
         * Provider erhält die notwendigen Abhängigkeiten für seine Arbeit, der restliche Kontext
         * der Activity wird aber nicht unnötig an untergeordnete Komponenten der Anwendung
         * weitergegeben.
         */
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        /*
         * Zusätzlich zum SensorManager wird ein OrientationListener übergeben, in diesem Fall
         * implementiert die Activity selbst das entsprechende Interface und registriert sich über
         * den zweiten Parameter (this) als Listener beim Provider.
         */
        provider = new OrientationProvider(sensorManager, this);
    }

    /**
     * Lifecycle-Methode der Activity, die aufgerufen wird, wenn die Activity erstmals, oder nach
     * dem Verschieben in den Hintergrund, sichtbar wird. Die Aktualisierung des UIs kann nur
     * innerhalb der aktiven Phase der App durchgeführt werden. Wir synchronisieren den Provider
     * daher mit diesem Ablauf und Starten an dieser Stelle das Lauschen auf den Beschleunigungssensor.
     */
    @Override
    protected void onResume() {
        super.onResume();
        /*
         * Vor dem Start wird ein Kalibrierungsauftrag an den Provider gestellt, so dass die ersten
         * Werte des Sensors, die vom Provider empfangen werden, direkt zur Kalibrierung der nachfolgenden
         * Messwerte verwendet werden können.
         */
        provider.calibrate();
        provider.start();
    }

    /**
     * Lifecycle-Methode der Activity, die aufgerufen wird, wenn die Activity in den Hintergrund
     * verschoben wird. Die Aktualisierung des UIs ist nach diesem Zeitpunkt nicht mehr möglich.
     * Wir synchronisieren den Provider daher mit diesem Ablauf und Stoppen an dieser Stelle das
     * Lauschen auf den Beschleunigungssensor.
     */
    @Override
    protected void onPause() {
        super.onPause();
        provider.stop();
    }

    /**
     * Callback-Methode aus dem Listener-Interface (OrientationProvider) die aufgerufen wird, wenn
     * der Provider neue Messwerte vom Beschleunigungssensor erhalten hat.
     *
     * @param x Aktuell gemessene (normalisierte) Beschleunigung auf der X-Achse
     * @param y Aktuell gemessene (normalisierte) Beschleunigung auf der Y-Achse
     * @param z Aktuell gemessene (normalisierte) Beschleunigung auf der Z-Achse
     */
    @Override
    public void onNewMeasurementAvailable(float x, float y, float z) {
        /*
         * Innerhalb des Wertebereichs des OrientationProvider markiert ein Wert um 0 die Ruheposition
         * bzw. in dieser App ein, auf der entsprechenden Achse, plan ausgerichtetes Gerät. In den
         * UI-Elementen wird dieser Zustand durch die Positionierung der Luftblase der Wasserwaage an
         * der Stelle 0.5 repräsentiert. Hier erfolgt die entsprechende Umrechnung der Werte. Der
         * Messwert des Sensors wird mit 10 multipliziert, da Unterschiede sonst nur bei Extremwerten
         * sichtbar wären.
         */
        float bubblePositionForVerticalLevel = 0.5f - (y * 10);
        float bubblePositionForHorizontalLevel = 0.5f - (x * 10);
        spiritLevelVertical.setBubblePosition(bubblePositionForVerticalLevel);
        spiritLevelHorizontal.setBubblePosition(bubblePositionForHorizontalLevel);
    }
}