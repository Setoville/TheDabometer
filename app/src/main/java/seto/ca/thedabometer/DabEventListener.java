package seto.ca.thedabometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DabEventListener implements SensorEventListener{
    double values[][] = new double [100][3];
    private float[] filteredReadings;
    private float[][] historicalReadings;
    public static float value[];
    public static int dabCount = 0;
    public static double caloriesBurned = 0;
    private TextView output;
    private TextView dabstatus;
    private TextView caloriesTV;
    private ImageView dabImageView;
    public boolean leftDab = false;
    public boolean rightDab = false;


    int ctr_z;

    final int SAMPLEDEFAULT=30;

    String dab;
    String cal;
    private final int C = 16;


    enum state{WAIT,RISE_LEFTY,FALL_LEFTY,RISE_RIGHTY,FALL_RIGHTY,DETERMINED};
    state myState = state.WAIT;
    enum sig{SIG_LEFTY,SIG_RIGHTY,SIG_X};
    sig mySig = sig.SIG_X;
    double recordx = 0;
    double recordy = 0;
    double recordz = 0;

    final double[] THRESHOLDA = {0.4,2,-0.3};
    final double[] THRESHOLDB = {-0.5,-3,0.4};

    int index = 0;
    int wraps = 0;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public DabEventListener (TextView view, double[][] inValues, TextView dabStatusPass, TextView caloriesPass,ImageView dabPass){

        historicalReadings = new float[100][3];
        //set historicalReadings to 0
        for(int i = 99; i >=0; i--) {
            for(int j = 0; j<3;j++)
                historicalReadings[i][j] = 0;
        }
        dabImageView = dabPass;
        output = view;
        caloriesTV = caloriesPass;
        dabstatus = dabStatusPass;
        values=inValues;
        value = new float[3];
        ctr_z = SAMPLEDEFAULT;

        dab = "Waiting...";
        cal = "0";

        filteredReadings = new float[3];
        filteredReadings[0] = 0;
        filteredReadings[1] = 0;
        filteredReadings[2] = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            //get accel values
            value[0]=event.values[0];
            value[1]=event.values[1];
            value[2]=event.values[2];

            //filter
            for(int i = 0; i < 3; i++)
                filteredReadings[i] += (value[i] - filteredReadings[i])/C;


            for(int i = 99; i >= 1; i--) {
                historicalReadings[i-1][0] = historicalReadings[i][0];
                historicalReadings[i-1][1] = historicalReadings[i][1];
                historicalReadings[i-1][2] = historicalReadings[i][2];
            }
            historicalReadings[99][0] = filteredReadings[0];
            historicalReadings[99][1] = filteredReadings[1];
            historicalReadings[99][2] = filteredReadings[2];

            if (index == 100) {
                index = 0;
                wraps++;
            }
            if (wraps == 0 && index == 99) {
                recordx = filteredReadings[0];
                recordy = filteredReadings[1];
                recordz = filteredReadings[2];
            }

            output.setText(Integer.toString(dabCount));
            dabstatus.setText(dab);
            System.out.println("DC:" +dabCount);

            values[index][0] = filteredReadings[0];
            values[index][1] = filteredReadings[1];
            values[index][2] = filteredReadings[2];
            if (filteredReadings[0] > recordx) {
                recordx = filteredReadings[0];
            } else if (filteredReadings[1] > recordy) {
                recordy = filteredReadings[1];
            } else if (filteredReadings[2] > recordz) {
                recordz = filteredReadings[2];
            }
            index++;
            FSM_Z();
        }
    }

    static public void resetDabCount()  {
        dabCount = 0;
        for(int i = 0; i<3;i++ ) {
            value[i] = 0;
        }
    }

    public void FSM_Z()  {

        //FSM FOR Z AXIS BEGINS
        if(ctr_z >= 0) {
            double dA = historicalReadings[99][0] - historicalReadings[98][0];
            switch (myState) {
                case WAIT:
                    //out = "WAIT";
                    ctr_z = SAMPLEDEFAULT;
                    if (dA > THRESHOLDA[0]) {
                        myState = state.RISE_LEFTY;
                    } else if (dA < THRESHOLDB[0]) {
                        myState = state.FALL_RIGHTY;
                    }



                    break;
                case RISE_LEFTY:
                    if (dA <= 0) {
                        if (historicalReadings[99][0] >= THRESHOLDA[1]) {
                            myState = state.FALL_LEFTY;
                        } else {
                            myState = state.DETERMINED;
                        }
                    }
                    break;
                case FALL_LEFTY:
                    if (dA >= 0) {
                        if (historicalReadings[99][0] <= THRESHOLDA[2]) {
                            mySig = sig.SIG_LEFTY;
                        }
                        myState = state.DETERMINED;
                    }
                    break;

                case FALL_RIGHTY:
                    if (dA >= 0) {
                        if (historicalReadings[99][0] <= THRESHOLDB[1]) {
                            mySig = sig.SIG_RIGHTY;
                        }
                        myState = state.DETERMINED;
                    }
                    break;

                case RISE_RIGHTY:
                    if (dA <= 0) {
                        if (historicalReadings[99][0] >= THRESHOLDB[2]) {
                            mySig = sig.SIG_RIGHTY;
                        }
                        myState = state.DETERMINED;
                    }
                    break;
                case DETERMINED:
                    //OUTPUT UP-DOWN HERE
                    //dabCount++;


                    if (mySig == sig.SIG_LEFTY) {
                        //do we need a textview object to .setText(Zsignature);?

                        if (!rightDab){
                            dabCount++;
                            rightDab = true;
                            leftDab = false;
                        }
                        dab = "NICE DAB";

                        dabImageView.setImageResource(R.drawable.dabright);

                    } else if (mySig == sig.SIG_RIGHTY) {

                        if (!leftDab){
                            dabCount++;
                            leftDab = true;
                            rightDab = false;
                        }

                        dabImageView.setImageResource(R.drawable.dableft);

                        dab = "NICE DAB";

                    }

                    caloriesBurned = CalorieCalculator(dabCount);
                    cal = String.format("%.5f", caloriesBurned);
                    caloriesTV.setText(cal);
                    mySig = sig.SIG_X;
                    break;

                default:
                    myState = state.WAIT;
                    mySig = sig.SIG_X;

                    break;
            }
            if (ctr_z <= 0) {
                //RESET AFTER 30 SAMPLES AND NOTHING
                dabImageView.setImageResource(R.drawable.dabneutral);
                myState = state.WAIT;
                mySig = sig.SIG_X;
                leftDab=false;
                rightDab=false;
                ctr_z = 31;
                dab = "Waiting...";
            }

            ctr_z--;
        }



    }
    public double CalorieCalculator(int dabs){
        double calories;

        calories = dabs * 1.06319/2;

        return calories;
    }
}
