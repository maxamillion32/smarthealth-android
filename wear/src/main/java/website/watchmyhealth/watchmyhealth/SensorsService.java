package website.watchmyhealth.watchmyhealth;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class SensorsService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private int currentValueHeart=0;
    private int currentValueStep=0;
    private static final String LOG_TAG = "MyHeart";
    private IBinder binder = new SensorsServiceBinder();
    private OnChangeListener onChangeListener;
    private GoogleApiClient mGoogleApiClient;

    // interface to pass a heartbeat value to the implementing class
    public interface OnChangeListener {
        void onValueChanged(int newValueHeart, int newValueStep);
    }

    /**
     * Binder for this service. The binding activity passes a listener we send the heartbeat to.
     */
    public class SensorsServiceBinder extends Binder {
        public void setChangeListener(OnChangeListener listener) {
            onChangeListener = listener;
            // return currently known value
            listener.onValueChanged(currentValueHeart, currentValueStep);
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // register us as a sensor listener
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        Sensor mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        // delay SENSOR_DELAY_UI is sufficiant
        boolean resHeart = mSensorManager.registerListener(this, mHeartRateSensor,  SensorManager.SENSOR_DELAY_FASTEST);
        boolean resStep = mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d(LOG_TAG, " sensor heart registered: " + (resHeart ? "yes" : "no"));
        Log.d(LOG_TAG, " sensor step registered: " + (resStep ? "yes" : "no"));

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();
        mGoogleApiClient.connect();
    }

  /*  @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        Log.d(LOG_TAG," sensor unregistered");
    }*/

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // is this a heartbeat event and does it have data?
        if(sensorEvent.sensor.getType()==Sensor.TYPE_HEART_RATE && sensorEvent.values.length>0 ) {
            int newValueHeart = (int) sensorEvent.values[0];
            //Log.d(LOG_TAG,sensorEvent.sensor.getName() + " changed to: " + newValue);
            // only do something if the value differs from the value before and the value is not 0.
            if(currentValueHeart != newValueHeart && newValueHeart!=0) {
                // save the new value
                currentValueHeart = newValueHeart;
                // send the value to the listener
                if(onChangeListener!=null) {
                    Log.d(LOG_TAG,"sending new Heart value to listener: " + newValueHeart);
                    onChangeListener.onValueChanged(newValueHeart, currentValueStep);
                    sendMessageToHandheld("heart"+Integer.toString(newValueHeart));
                }
            }
        }

        if(sensorEvent.sensor.getType()==Sensor.TYPE_STEP_COUNTER && sensorEvent.values.length>0 ) {
            int newValueStep = (int) sensorEvent.values[0];
            //Log.d(LOG_TAG,sensorEvent.sensor.getName() + " changed to: " + newValue);
            // only do something if the value differs from the value before and the value is not 0.
            if(currentValueStep != newValueStep && newValueStep!=0) {
                // save the new value
                currentValueStep = newValueStep;
                // send the value to the listener
                if(onChangeListener!=null) {
                    Log.d(LOG_TAG,"sending new Step value to listener: " + newValueStep);
                    onChangeListener.onValueChanged(currentValueHeart, newValueStep);
                    sendMessageToHandheld("step"+Integer.toString(newValueStep));
                }
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


/**
 * sends a string message to the connected handheld using the google api client (if available)
 * @param message
 */
private void sendMessageToHandheld(final String message) {

    if (mGoogleApiClient == null)
        return;

    Log.d(LOG_TAG,"sending a message to handheld: "+message);

    // use the api client to send the heartbeat value to our handheld
    final PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
    nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
        @Override
        public void onResult(NodeApi.GetConnectedNodesResult result) {
            final List<Node> nodes = result.getNodes();
            if (nodes != null) {
                for (int i=0; i<nodes.size(); i++) {
                    final Node node = nodes.get(i);
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), message, null);
                }
            }
        }
    });

}
}