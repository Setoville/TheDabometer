package seto.ca.thedabometer;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    SensorManager manager;
    Sensor sensor;
    DabEventListener listener;
    double[][] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        values = new double[100][3];

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        TextView output = (TextView) findViewById(R.id.dabcounttextview);
        Button resetButton = (Button) findViewById(R.id.resetButton);
        TextView dabStatus = (TextView) findViewById(R.id.dabstatus);
        TextView calories = (TextView) findViewById(R.id.caloriesBurnedTextView);
        ImageView dabPicture = (ImageView) findViewById(R.id.dabImageView);
        dabPicture.setImageResource(R.drawable.dabneutral);
        listener = new DabEventListener(output, values, dabStatus,calories,dabPicture);
        resetButton.setOnClickListener(dabButtonEventListener);
        manager.registerListener(listener, sensor, manager.SENSOR_DELAY_GAME);



    }
    private View.OnClickListener dabButtonEventListener = new View.OnClickListener() {
        public void onClick(View v) {
            DabEventListener.resetDabCount();
        }
    };


}
