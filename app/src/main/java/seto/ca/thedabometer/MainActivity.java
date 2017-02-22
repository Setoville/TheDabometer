package seto.ca.thedabometer;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    LinearLayout l;
    SensorManager manager;
    Sensor sensor;
    DabEventListener listener;
    TextView output;
    double[][] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        values = new double[100][3];

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        TextView output = (TextView) findViewById(R.id.dabcounttextview);
        //listener = new AccelerometerSensorEventListener(output, graph, values);
        listener = new DabEventListener(output, values);


        manager.registerListener(listener, sensor, manager.SENSOR_DELAY_GAME);



    }


}
