package seto.ca.thedabometer;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    SensorManager manager;
    Sensor sensor;
    DabEventListener listener;
    public TextView dabCountTextView;
    double[][] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        double [] values = new double [3];
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //listener = new AccelerometerSensorEventListener(output, graph, values);
        manager.registerListener(listener, sensor, manager.SENSOR_DELAY_GAME);
        dabCountTextView = (TextView) findViewById(R.id.dabcounttextview);




    }


}
