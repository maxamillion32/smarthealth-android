package website.watchmyhealth.watchmyhealth;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Yoann on 26/04/2015.
 */

public class FragmentMap extends Fragment implements LocationListener {

    private GoogleMap map;
    private MapView mapView;
    Location location = new Location("MyLocation");
    private double latitude = location.getLatitude();
    private double longitude = location.getLongitude();
    private double altitude = location.getAltitude();
    private float accuracy = location.getAccuracy();
    private LocationManager lm;
    boolean bMoveCamera = false;
    Marker markerUser = null;

//    private MapController mc;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, null, false);
        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);//false de base
        map.setMyLocationEnabled(true);
       // lm = (LocationManager) this.getActivity().getSystemService(this.getActivity().LOCATION_SERVICE);
        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());
        return v;
    }
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        lm = (LocationManager) this.getActivity().getApplicationContext().getSystemService(this.getActivity().LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
            Toast.makeText(this.getActivity().getApplicationContext(), "Dans GPS_PROVIDER !!!", Toast.LENGTH_SHORT).show();

        }
        else if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, this);
            Toast.makeText(this.getActivity().getApplicationContext(), "Dans NETWORK_PROVIDER !!!", Toast.LENGTH_SHORT).show();

        }
        else if(lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 20000, 0, this);
            Toast.makeText(this.getActivity().getApplicationContext(), "Dans PASSIVE_PROVIDER !!!", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onPause() {
        super.onPause();
        lm.removeUpdates(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        accuracy = location.getAccuracy();
        LatLng positionUser= new LatLng(latitude,longitude);
        if(!bMoveCamera){
            markerUser = map.addMarker(new MarkerOptions().position(positionUser).title("fabrice126").snippet("Salut les gars !").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_img_profil)));
            // Move the camera instantly to me with a zoom of 15.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionUser, 17));
            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            bMoveCamera = true;
        }
        else{
            markerUser.setPosition(positionUser);
            //String msg = String.format(getResources().getString(R.string.new_location), latitude,longitude, altitude, accuracy);
            Toast.makeText(this.getActivity().getApplicationContext(),"this.getActivity().getApplicationContext() = "+this.getActivity().getApplicationContext()+"\n"+
                    "this.getActivity() = "+this.getActivity()/*msg*/, Toast.LENGTH_LONG).show();
        }


//        GeoPoint p;
//        p = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
//        mc.animateTo(p);
//        mc.setCenter(p);
    }

    @Override
    public void onStatusChanged(String s, int statut, Bundle bundle) {
        String newStatus = "";
        switch (statut) {
            case LocationProvider.OUT_OF_SERVICE:
                newStatus = "OUT_OF_SERVICE";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                newStatus = "TEMPORARILY_UNAVAILABLE";
                break;
            case LocationProvider.AVAILABLE:
                newStatus = "AVAILABLE";
                break;
        }
        String msg = String.format(getResources().getString(R.string.provider_disabled)," s="+s);
        Toast.makeText(this.getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String s) {
        String msg = String.format(getResources().getString(R.string.provider_enabled), s);
        Toast.makeText(this.getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        String msg = String.format(getResources().getString(R.string.provider_disabled), s);
        Toast.makeText(this.getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}