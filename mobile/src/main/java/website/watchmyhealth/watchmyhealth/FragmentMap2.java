package website.watchmyhealth.watchmyhealth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Yoann on 26/04/2015.
 */

public class FragmentMap2 extends Fragment implements LocationListener {

    private GoogleMap map;
    private MapView mapView;
    private Location location = new Location("My Location");
    private double latitude;
    private double longitude;
    private LatLng positionUser;
    private LocationManager lm;
    private boolean bMoveCamera = false;
    private Marker markerUser = null;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private CameraPosition cameraPosition;
//    private MapController mc;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, null, false);
        System.out.println("onCreateView");

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);//false de base
        map.setMyLocationEnabled(true);

//        lm = (LocationManager) this.getActivity().getSystemService(this.getActivity().LOCATION_SERVICE);
//        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            createGpsDisabledAlert();
//        }
        lm = (LocationManager) this.getActivity().getSystemService(this.getActivity().LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());
        return v;
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((MapView) getView().findViewById(R.id.map)).getMap();
            if (map != null) {
                MapsInitializer.initialize(this.getActivity());
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mapView.onResume();
        if (cameraPosition != null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            cameraPosition = null;
        }
        System.out.println("onResume");

//        lm = null;
//        lm = (LocationManager) this.getActivity().getSystemService(this.getActivity().LOCATION_SERVICE);
//        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//            Toast.makeText(this.getActivity(), "onResume -> GPS_PROVIDER", Toast.LENGTH_SHORT).show();
//        }
//        else if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//            Toast.makeText(this.getActivity(), "onResume -> Dans NETWORK_PROVIDER", Toast.LENGTH_SHORT).show();
//        }
//        else if(lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
//            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//            Toast.makeText(this.getActivity(), "onResume -> PASSIVE_PROVIDER", Toast.LENGTH_SHORT).show();
//        }

    }
    @Override
    public void onPause() {
        super.onPause();

        cameraPosition = map.getCameraPosition();
        map = null;
        //lm.removeUpdates(this);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        positionUser= new LatLng(latitude,longitude);
            System.out.println("onLocationChanged -> bMoveCamera");
            markerUser = map.addMarker(new MarkerOptions().position(positionUser).title("fabrice126").snippet("Salut les gars !").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_img_profil)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionUser, 17));
            Toast.makeText(this.getActivity(), "onLocationChanged bMoveCamera", Toast.LENGTH_SHORT).show();

            map.animateCamera(CameraUpdateFactory.newLatLng(positionUser));
            markerUser.setPosition(positionUser);


        //map.moveCamera(CameraUpdateFactory.newLatLng(positionUser));
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
        Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String s) {
        String msg = String.format(getResources().getString(R.string.provider_enabled), s);
        Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        String msg = String.format(getResources().getString(R.string.provider_disabled), s);
        Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    private void createGpsDisabledAlert() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.getActivity());
        localBuilder.setMessage("Le GPS est inactif, voulez-vous l'activer ?").setCancelable(false).setPositiveButton("Activer GPS ",
                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                FragmentMap2.this.showGpsOptions();
                            }
                        }
                );
        localBuilder.setNegativeButton("Ne pas l'activer ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.cancel();
                        FragmentMap2.this.getActivity().finish();
                    }
                }
        );
        localBuilder.create().show();
    }

    private void showGpsOptions() {
        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        this.getActivity().finish();
    }
}