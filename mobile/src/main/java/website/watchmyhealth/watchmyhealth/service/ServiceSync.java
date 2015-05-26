package website.watchmyhealth.watchmyhealth.service;

/**
 * Created by Fabrice on 17/05/2015.
 */

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class ServiceSync extends Service implements LocationListener{
    public String broadcastValueDistance = "0";
    public String broadcastValueVitesse = "0";
    private LocationManager locationMgr = null;
    private final String FILENAME_LOCATION ="save_Time_Longitude_Latitude.dat";
    private FileOutputStream fOut = null;
    private OutputStreamWriter osw = null;
    private float distance = 0;
    private long startActivityTimeStamp = 0;

    private Location locationTmp = null;
    @Override
    public void onCreate() {
        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000,15, this);
        locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 15, this);
        super.onCreate();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String strLatitude = Double.toString(latitude);
        String strLongitude = Double.toString(longitude);
        int vitesse = 0;
        if(locationTmp != null){
            distance += locationTmp.distanceTo(location);
            System.out.println("locationTmp.getSpeed()====" + locationTmp.getSpeed());
        }
        if(location!=null){
            locationTmp = new Location(location);
        }

        Toast.makeText(getBaseContext(),"Latitude =" + latitude + " Longitude=" + longitude,Toast.LENGTH_LONG).show();
        try{
            fOut = this.openFileOutput(FILENAME_LOCATION, MODE_APPEND);
            osw = new OutputStreamWriter(fOut);
            String separator = System.getProperty("line.separator");
            //osw.flush();
            //osw.append(System.currentTimeMillis()+"_" + strLongitude+"_"+strLatitude+"_"+distance+"_"+vitesse); a supprimer si la  meme ligne du bas fonctionne

            if(startActivityTimeStamp == 0){
                System.out.println("Dans currentTime = 0 ");
                startActivityTimeStamp = System.currentTimeMillis();
            }
            else{
                double time = (System.currentTimeMillis()- startActivityTimeStamp)/3600;
                System.out.println((System.currentTimeMillis()- startActivityTimeStamp)+" =====Timestamp - Timestamp ========"+time);
                System.out.println("distanceKm ==="+distance);
                vitesse = (int)(distance/time);
                System.out.println("vitesse ==="+vitesse);

                broadcastValueVitesse = Integer.toString(vitesse);
                System.out.println("broadcastValueVitesse ===== "+broadcastValueVitesse);
            }
            osw.append(System.currentTimeMillis()+"_" + strLongitude+"_"+strLatitude+"_"+distance+"_"+vitesse);


            osw.append(separator);
//            osw.flush();
            osw.close();
            fOut.close();
        }
        catch (Exception e) {
        }
        //transmet les donnees a Home.java pour mettre a jour la distance sur l'UI
        Intent i = new Intent("DISTANCE_UPDATED");
        broadcastValueDistance = String.valueOf((int)distance);
        i.putExtra("distance", broadcastValueDistance);
        i.putExtra("vitesse", broadcastValueVitesse);

        sendBroadcast(i);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationMgr.removeUpdates(this);
    }
}