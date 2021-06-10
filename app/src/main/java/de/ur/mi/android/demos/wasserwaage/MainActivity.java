package de.ur.mi.android.demos.wasserwaage;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.ur.mi.android.demos.wasserwaage.sensors.OrientationProvider;
import de.ur.mi.android.demos.wasserwaage.ui.SpiritLevelView;

public class MainActivity extends AppCompatActivity implements OrientationProvider.OrientationListener {

    private OrientationProvider provider;
    private SpiritLevelView spiritLevelHorizontal;
    private SpiritLevelView spiritLevelVertical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initOrientationProvider();
    }

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
        spiritLevelHorizontal = findViewById(R.id.spirit_level_view_horizontal);
        spiritLevelVertical = findViewById(R.id.spirit_level_view_vertical);
    }

    private void initOrientationProvider() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        provider = new OrientationProvider(sensorManager, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        provider.calibrate();
        provider.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        provider.stop();
    }

    @Override
    public void onOrientationChanged(float x, float y, float z) {
        float bubblePositionForVerticalLevel = 0.5f - (y / 10);
        float bubblePositionForHorizontalLevel = 0.5f - (x / 10);
        spiritLevelVertical.setBubblePosition(bubblePositionForVerticalLevel);
        spiritLevelHorizontal.setBubblePosition(bubblePositionForHorizontalLevel);
    }
}