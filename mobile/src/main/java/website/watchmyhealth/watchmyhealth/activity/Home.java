package website.watchmyhealth.watchmyhealth.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import website.watchmyhealth.watchmyhealth.NavigationDrawerFragment;
import website.watchmyhealth.watchmyhealth.R;
import website.watchmyhealth.watchmyhealth.fragment.FragmentMap;
import website.watchmyhealth.watchmyhealth.fragment.FragmentProfil;
import website.watchmyhealth.watchmyhealth.fragment.FragmentTest;
import website.watchmyhealth.watchmyhealth.fragment.FragmentTest2;
import website.watchmyhealth.watchmyhealth.service.ServiceSync;


/**
 * Created by Yoann on 26/04/2015.
 */

public class Home extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private TextView mTextViewHeart;
    private TextView mTextViewStep;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(mTextViewStep!=null)
                mTextViewStep.setText(Integer.toString(DataLayerListenerService.getCurrentValueStep()));

            if(mTextViewHeart!=null)
                mTextViewHeart.setText(Integer.toString(DataLayerListenerService.getCurrentValueHeart()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        if( intent.getExtras() !=null){
                //Si on est sur l'activity ProfilModif et qu'on sauvegarde les modifications on doit retourner sur le FragmentProfil et non dans la page Home , on transmet donc cette info via cet Intent
                onNavigationDrawerItemSelected(intent.getIntExtra("GO_TO_FRAGMENT_PROFIL", 3));
                intent.getExtras().remove("GO_TO_FRAGMENT_PROFIL");
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

    }



    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = (Fragment)new FragmentTest();
        FragmentManager fragmentManager = getSupportFragmentManager();

        //fragmentManager.beginTransaction()
        //.replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
        switch (position) {
            case 0:
                fragment = new FragmentTest();
                mTitle = getString(R.string.navigation_accueil);
                break;
            case 1:
                fragment = new FragmentTest2();
                mTitle = getString(R.string.navigation_graphique);
                break;
            case 2:
                fragment = new FragmentMap();
                mTitle = getString(R.string.navigation_map);
                break;
            case 3: //attention, si on change de "case" pour FragmentProfil, le mappage ne se fera plus entre ProfilModif et FragmentProfil ctrl+f = GO_TO_FRAGMENT_PROFIL
                fragment = new FragmentProfil();
                mTitle = getString(R.string.navigation_profil);
                break;
            case 4:
                fragment = new FragmentProfil();
                mTitle = getString(R.string.navigation_a_propos);
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack("tag").commit();

    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    long timeWhenStopped = 0;
    public void startChronometer(View view) {
        ((Chronometer) findViewById(R.id.chronometer1)).setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        //Permet de demarrer le service qui va recuperer la geolocalisation
        if(!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
            createGpsDisabledAlert();
        }
        Toast.makeText(this,"Debut de la seance de sport",Toast.LENGTH_LONG).show();
        mTextViewHeart = (TextView) findViewById(R.id.heartbeat);
        mTextViewStep = (TextView) findViewById(R.id.stepcount);
        DataLayerListenerService.setHandler(handler);
    }

    public void stopChronometer(View view) {
        timeWhenStopped = ((Chronometer) findViewById(R.id.chronometer1)).getBase() - SystemClock.elapsedRealtime();
        ((Chronometer) findViewById(R.id.chronometer1)).stop();
        //Permet d'arreter le service qui recupere la geolocalisation
        this.stopService(new Intent(this, ServiceSync.class));
        Toast.makeText(this, "Fin de la seance de sport", Toast.LENGTH_LONG).show();
        DataLayerListenerService.setHandler(null);
    }

    public void resetChronometer(View view) {
        ((Chronometer) findViewById(R.id.chronometer1)).setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
    }

/*    public void async_post(){
        //do a twiiter search with a http post
        String url = "http://172.19.150.235:8080/SmartHealth---Web-App/test";
        int idUser = 1201;
        int nbPas = 3000;
        ArrayList<String> latitude= new ArrayList<String>();
        latitude.add("15.23568");
        latitude.add("15.23568");latitude.add("15.23568");
        latitude.add("15.23568");

        String[] longitude= {"47.26545","47.26545","47.26545","47.26545"};
        Date date = new Date();
        //Une appelle de methode d'Async_post pour chaque jour (la date), car il faut envoyer toutes les donnees d'un jour ensemble
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nom", idUser);
        params.put("dateDuJour",date);
        params.put("latitude",latitude);
        params.put("longitude",longitude);
        params.put("podometre", nbPas);
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                //showResult(json);
                System.out.println("Dans aq.ajax = "+json);
            }
        });

    }*/
    private void createGpsDisabledAlert() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setMessage("Activer le GPS avant de commencer votre seance").setCancelable(false).setPositiveButton("Activer GPS ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                        /** Test si le gps est active on lance le service sinon on lance un Toast pour lui indiquer d'activer le GPS*/
                        ((Chronometer) findViewById(R.id.chronometer1)).start();
                        Home.this.startService(new Intent(Home.this, ServiceSync.class));
                    }
                }
        );
        localBuilder.create().show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

