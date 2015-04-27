package website.watchmyhealth.watchmyhealth;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Yoann on 27/04/2015.
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private TextView mTextView;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    // du site chinois
    private String checkResult = "Result:\n";
    private TextView tvResult;
    private TextView textViewf;
    private Button btnCheck;

    //de BatchStepSensor

    // State of application, used to register for sensors when app is restored
    public static final int STATE_OTHER = 0;
    public static final int STATE_COUNTER = 1;
    public static final int STATE_DETECTOR = 2;
    // Bundle tags used to store data when restoring application state
    private static final String BUNDLE_STATE = "state";
    private static final String BUNDLE_LATENCY = "latency";
    private static final String BUNDLE_STEPS = "steps";

    // max batch latency is specified in microseconds
    private static final int BATCH_LATENCY_0 = 0; // no batching
    private static final int BATCH_LATENCY_10s = 10000000;
    private static final int BATCH_LATENCY_5s = 5000000;

    // Steps counted in current session
    private int mSteps = 0;
    // Value of the step counter sensor when the listener was registered.
    // (Total steps are calculated from this value.)
    private int mCounterSteps = 0;
    // Steps counted by the step counter previously. Used to keep counter consistent across rotation
    // changes
    private int mPreviousCounterSteps = 0;

    /**
     * Resets the step counter by clearing all counting variables and lists.
     */
    private void resetCounter() {
        // BEGIN_INCLUDE(reset)
        mSteps = 0;
        mCounterSteps = 0;
        mPreviousCounterSteps = 0;
        // END_INCLUDE(reset)
    }
    /**
     * Listener that handles step sensor events for step detector and step counter sensors.
     */
    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {


            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                // A step detector event is received for each step.
                // This means we need to count steps ourselves

                mSteps += event.values.length;
                // float steps = event.values[0];
                // tvResult.setText((int) steps + "");


                Log.i(TAG,
                        "New step detected by STEP_DETECTOR sensor. Total step count: " + mSteps);

            } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

                /*
                A step counter event contains the total number of steps since the listener
                was first registered. We need to keep track of this initial value to calculate the
                number of steps taken, as the first value a listener receives is undefined.
                 */
                if (mCounterSteps < 1) {
                    // initial value
                    mCounterSteps = (int) event.values[0];
                }

                // Calculate steps taken based on first counter value received.
                mSteps = (int) event.values[0] - mCounterSteps;

                // Add the number of steps previously taken, otherwise the counter would start at 0.
                // This is needed to keep the counter consistent across rotation changes.
                mSteps = mSteps + mPreviousCounterSteps;


                Log.i(TAG, "New step detected by STEP_COUNTER sensor. Total step count: " + mSteps);
                // END_INCLUDE(sensorevent)
                textViewf.setText((int) mSteps + "");

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private final StringBuffer mDelayStringBuffer = new StringBuffer();

    /*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // BEGIN_INCLUDE(restore)
        // Fragment is being restored, reinitialise its state with data from the bundle
        if (savedInstanceState != null) {
            resetCounter();
            mSteps = savedInstanceState.getInt(BUNDLE_STEPS);
            mState = savedInstanceState.getInt(BUNDLE_STATE);
            mMaxDelay = savedInstanceState.getInt(BUNDLE_LATENCY);

            // Register listeners again if in detector or counter states with restored delay
            if (mState == STATE_DETECTOR) {
                registerEventListener(mMaxDelay, Sensor.TYPE_STEP_DETECTOR);
            } else if (mState == STATE_COUNTER) {
                // store the previous number of steps to keep  step counter count consistent
                mPreviousCounterSteps = mSteps;
                registerEventListener(mMaxDelay, Sensor.TYPE_STEP_COUNTER);
            }
        }
        // END_INCLUDE(restore)
    }*/


    // DU SITE D'ANDROID
    /*mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
        // Success! There's a Step Counter.
    }
    else {
        // Failure! No step counter.
    }*/

// AVANT
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }*/


    //du site chinois

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = (TextView) findViewById(R.id.tvResult);
        textViewf = (TextView) findViewById(R.id.textViewf);
        btnCheck = (Button) findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new onClickListenerImp());
        // sensorCheck();
    }

    class onClickListenerImp implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            sensorCheck();
        }
    }
    private void sensorCheck() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // HEART_RATE
        if ((mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null) || (mSensorManager.getDefaultSensor(65562) != null)) {
            checkResult = checkResult + "Heart Rate: YES\n";
        } else {
            checkResult = checkResult + "Heart Rate: NO\n";
        }

        // STEP_COUNTER
        if ((mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)) {
            checkResult = checkResult + "Step Counter: YES\n";
        } else {
            checkResult = checkResult + "Step Counter: NO\n";
        }

        // STEP_DETECTOR
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            checkResult = checkResult + "Step Detector: YES\n";
        } else {
            checkResult = checkResult + "Step Detector: NO\n";
        }

        tvResult.setText(checkResult);

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
