package de.ur.mi.android.demos.wasserwaage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Die Wasserwaage: Eine interkative Wasserwage für Android-Smartphones
 * <p>
 * Diese App stellt eine digitale Wasserwage zur Verfügung. Im User Interface werden zwei separate
 * Elemente für die horizontale und vertikale Ausrichtung des Geräts angezeigt. Auf Basis der
 * aktuellen Werte des Beschleunigungssensors werden die jeweiligen Inidikatoren der beiden Elemente
 * positioniert, um die aktuelle Lage des Geräts zu visualisieren.
 * <p>
 * Die Genauigkeit der Anzeige wird durch eine Möglichkeit zur Kalibrierung der Werte erhöht:
 * Nutzer*innen können das Gerät auf einer planen Fläche ablegen und über einen Klick auf den
 * Kalibrierungs-Button die aktuellen Messwerte speichern. Diese werden bei allen nachfolgenden
 * Messungen zur Normalisierung der Sensordaten verwendet.
 * <p>
 * Für die Darstellung der aktuellen Geräteausrichtung sind bereits zwei passende CustomViews im
 * Layout der Activity ergänzt. Diese basieren auf der Klasse SpiritLevel, werden in der XML-Datei
 * des Layouts eingefügt und konfiguriert und können hier in der Activity wie alle anderen bekannten
 * Views verwendet werden.
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}