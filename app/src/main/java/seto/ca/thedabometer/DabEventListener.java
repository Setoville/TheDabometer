package seto.ca.thedabometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by andrewseto on 18/02/2017.
 */
public class DabEventListener implements SensorEventListener{
    double values [];
    private float maxValues[];
    private float[] filteredReadings;
    private float[][] historicalReadings;


    int ctr_z;

    final int SAMPLEDEFAULT=30;

    String dab;
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
    DabEventListener (double[] inValues){
        values=inValues;
        maxValues = new float[3];
        dab = "NO DAB";
        historicalReadings = new float[100][3];
        //set historicalReadings to 0
        for(int i = 99; i >=0; i--) {
            for(int j = 0; j<3;j++)
                historicalReadings[i][j] = 0;
        }
        filteredReadings[0] = 0;
        filteredReadings[1] = 0;
        filteredReadings[2] = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            //get accel values
            values[0]=event.values[0];
            values[1]=event.values[1];
            values[2]=event.values[2];

            //filter
            for(int i = 0; i < 3; i++)
                filteredReadings[i] += (values[i] - filteredReadings[i])/C;


            for(int i = 99; i >= 1; i--) {
                historicalReadings[i-1][0] = historicalReadings[i][0];
                historicalReadings[i-1][1] = historicalReadings[i][1];
                historicalReadings[i-1][2] = historicalReadings[i][2];
            }
            historicalReadings[99][0] = filteredReadings[0];
            historicalReadings[99][1] = filteredReadings[1];
            historicalReadings[99][2] = filteredReadings[2];


            if (filteredReadings[0] > recordx) {
                recordx = filteredReadings[0];
            } else if (filteredReadings[1] > recordy) {
                recordy = filteredReadings[1];
            } else if (filteredReadings[2] > recordz) {
                recordz = filteredReadings[2];
            }
        }


    }
    public void reset() {
        for(int i = 0; i<3;i++ ) {
            values[i] = 0;
            maxValues[i] = 0;
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
                        if (historicalReadings[99][1] >= THRESHOLDA[1]) {
                            myState = state.FALL_LEFTY;
                        } else {
                            myState = state.DETERMINED;
                        }
                    }
                    break;
                case FALL_LEFTY:
                    if (dA >= 0) {
                        if (historicalReadings[99][1] <= THRESHOLDA[2]) {
                            mySig = sig.SIG_LEFTY;
                        }
                        myState = state.DETERMINED;
                    }
                    break;

                case FALL_RIGHTY:
                    if (dA >= 0) {
                        if (historicalReadings[99][1] <= THRESHOLDB[1]) {
                            mySig = sig.SIG_RIGHTY;
                        }
                        myState = state.DETERMINED;
                    }
                    break;

                case RISE_RIGHTY:
                    if (dA <= 0) {
                        if (historicalReadings[99][1] >= THRESHOLDB[1]) {
                            mySig = sig.SIG_RIGHTY;
                        }
                        myState = state.DETERMINED;
                    }
                    break;
                case DETERMINED:
                    //OUTPUT UP-DOWN HERE
                    if (mySig == sig.SIG_LEFTY) {
                        //do we need a textview object to .setText(Zsignature);?
                        dab = "LEFT";
                    } else if (mySig == sig.SIG_RIGHTY) {
                        //do we need a textview object to .setText(Zsignature);?
                        //output
                        dab = "RIGHT";

                    } //else
                    //out = "UNDETERMINED";

                    mySig = sig.SIG_X;
                    //Log.d(Zsignature.toString());

                    break;

                default:
                    myState = state.WAIT;
                    //not z
                    //out = "UNDETERMINED";
                    break;


            }
            if (ctr_z <= 0) {
                //RESET AFTER 30 SAMPLES AND NOTHING
                myState = state.WAIT;
                mySig = sig.SIG_X;
                ctr_z = 31;
            }

            ctr_z--;
        }



    }
}
