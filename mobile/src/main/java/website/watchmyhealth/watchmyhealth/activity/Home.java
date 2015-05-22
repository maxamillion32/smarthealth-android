package website.watchmyhealth.watchmyhealth.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
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

import website.watchmyhealth.watchmyhealth.ConnectionChangeReceiver;
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
    private final String MESSAGE1_PATH = "/message1";
    private GoogleApiClient apiClient;
    private NodeApi.NodeListener nodeListener;
    private String remoteNodeId;
    private MessageApi.MessageListener messageListener;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        if( intent.getExtras() !=null){
                //Si on est sur l'activity ProfilModif et qu'on sauvegarde les modifications on doit retourner sur le FragmentProfil et non dans la page Home , on transmet donc cette info via cet Intent
                onNavigationDrawerItemSelected(intent.getIntExtra("GO_TO_FRAGMENT_PROFIL", 2));
                intent.getExtras().remove("GO_TO_FRAGMENT_PROFIL");
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));
        handler = new Handler();
        //si il y a une connexion internet alors on envoi les données sauvegardé dans les fichiers
        ConnectionChangeReceiver packageReceiver= new ConnectionChangeReceiver();
        registerReceiver(packageReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));


        // Create NodeListener that enables buttons when a node is connected and disables buttons when a node is disconnected
        nodeListener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(Node node) {
                remoteNodeId = node.getId();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Peer connecte");
                    }
                });
            }
            @Override
            public void onPeerDisconnected(Node node) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Peer deconnecte");
                    }
                });
            }
        };
        // Create MessageListener that receives messages sent from a wearable
        messageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                System.out.println(messageEvent.getPath());
            }
        };

        // Create GoogleApiClient
        apiClient = new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                // Register Node and Message listeners
                Wearable.NodeApi.addListener(apiClient, nodeListener);
                Wearable.MessageApi.addListener(apiClient, messageListener);
                // If there is a connected node, get it's id that is used when sending messages
                Wearable.NodeApi.getConnectedNodes(apiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                            remoteNodeId = getConnectedNodesResult.getNodes().get(0).getId();
                            System.out.println("Connecte");
                        }
                    }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {
                System.out.println("Connexion suspendue");
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE)
                    System.out.println("Connexion echoue");
            }
        }).addApi(Wearable.API).build();

        apiClient.connect();
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

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
                fragment = new FragmentMap();
                mTitle = getString(R.string.navigation_map);
                break;
            case 2: //attention, si on change de "case" pour FragmentProfil, le mappage ne se fera plus entre ProfilModif et FragmentProfil ctrl+f = GO_TO_FRAGMENT_PROFIL
                fragment = new FragmentProfil();
                mTitle = getString(R.string.navigation_profil);
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
        System.out.println("Envoie message");
        sync_smartwatch();
        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        //Permet de demarrer le service qui va recuperer la geolocalisation
        if(!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
            createGpsDisabledAlert();
        }
        Toast.makeText(this,"Début de la séance de sport",Toast.LENGTH_LONG).show();
    }

    public void stopChronometer(View view) {
        timeWhenStopped = ((Chronometer) findViewById(R.id.chronometer1)).getBase() - SystemClock.elapsedRealtime();
        ((Chronometer) findViewById(R.id.chronometer1)).stop();
        //Permet d'arreter le service qui recupere la geolocalisation
        this.stopService(new Intent(this, ServiceSync.class));
        Toast.makeText(this, "Fin de la sécance de sport", Toast.LENGTH_LONG).show();
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
        localBuilder.setMessage("Activer le GPS avant de commencer votre séance").setCancelable(false).setPositiveButton("Activer GPS ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                        /** Test si le gps est activé on lance le service sinon on lance un Toast pour lui indiquer d'activer le GPS*/
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

    public void sync_smartwatch() {
        System.out.println("SMARTWATCH");
        Wearable.MessageApi.sendMessage(apiClient, remoteNodeId, MESSAGE1_PATH, null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (sendMessageResult.getStatus().isSuccess())
                    System.out.println("Message envoye");
                else
                    System.out.println("Message non envoye");
            }
        });
    }

}

