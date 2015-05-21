package website.watchmyhealth.watchmyhealth.activity;

import android.os.Handler;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayerListenerService extends WearableListenerService {

    private static final String LOG_TAG = "WearableListener";
    public static final String HEARTBEAT = "HEARTBEAT";

    private static Handler handler;
    private static int currentValue=0;
    private static int currentValueHeart=0;
    private static int currentValueStep=0;

    public static Handler getHandler() {
        return handler;
    }

    public static void setHandler(Handler handler) {
        DataLayerListenerService.handler = handler;
        // send current value as initial value.
        if(handler!=null)
            handler.sendEmptyMessage(1);

    }
    public static int getCurrentValueHeart() {
        return currentValueHeart;
    }
    public static int getCurrentValueStep() {
        return currentValueStep;
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        String id = peer.getId();
        String name = peer.getDisplayName();

        Log.d(LOG_TAG, "Connected peer name & ID: " + name + "|" + id);
    }
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d(LOG_TAG, "received a message from wear: " + messageEvent.getPath());
        // save the new heartbeat value
        if(handler!=null) {
            // if a handler is registered, send the value as new message

            if(messageEvent.getPath().contains("step")) {

                Log.d(LOG_TAG, "received a message from wear: " + messageEvent.getPath());
                currentValueStep = Integer.parseInt(messageEvent.getPath().substring(4));
            }
            else if(messageEvent.getPath().contains("heart")) {
                currentValueHeart = Integer.parseInt(messageEvent.getPath().substring(5));
            }

            handler.sendEmptyMessage(1);
        }
    }

}