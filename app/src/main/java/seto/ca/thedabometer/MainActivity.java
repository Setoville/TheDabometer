package seto.ca.thedabometer;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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


        //for initial popup instructions
        AlertDialog.Builder ADB = new AlertDialog.Builder(MainActivity.this);


        ADB.setMessage("Instructions: \n 1. Hold your phone and perform a dab. \n 2. Scroll down to see how many calories you've burned! \n 3.Be aware of your surroundings.");
        ADB.setTitle("Welcome!");


        //AlertDialog dialog = ADB.create();



        ADB.setPositiveButton("GOT IT",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface ADB, int id){
                ADB.cancel();
            }
        });



        ADB.show();

        //sensor manager and event listener init
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //elements init
        TextView output = (TextView) findViewById(R.id.dabcounttextview);
        Button resetButton = (Button) findViewById(R.id.resetButton);
        TextView dabStatus = (TextView) findViewById(R.id.dabstatus);
        TextView calories = (TextView) findViewById(R.id.caloriesBurnedTextView);
        ImageView dabPicture = (ImageView) findViewById(R.id.dabImageView);

        //set default 'image'
        dabPicture.setImageResource(R.drawable.dabneutral);


        //pass all new created elements to accel event listener
        listener = new DabEventListener(output, values, dabStatus,calories,dabPicture);
        resetButton.setOnClickListener(dabButtonEventListener);


        manager.registerListener(listener, sensor, manager.SENSOR_DELAY_GAME);



    }

    //reset button function call
    private View.OnClickListener dabButtonEventListener = new View.OnClickListener() {
        public void onClick(View v) {
            DabEventListener.resetDabCount();
        }
    };




}
