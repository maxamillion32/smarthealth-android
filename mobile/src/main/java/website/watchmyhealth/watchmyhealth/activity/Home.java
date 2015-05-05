package website.watchmyhealth.watchmyhealth.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import website.watchmyhealth.watchmyhealth.NavigationDrawerFragment;
import website.watchmyhealth.watchmyhealth.R;
import website.watchmyhealth.watchmyhealth.fragment.FragmentMap;
import website.watchmyhealth.watchmyhealth.fragment.FragmentProfile;
import website.watchmyhealth.watchmyhealth.fragment.FragmentTest;
import website.watchmyhealth.watchmyhealth.fragment.FragmentTest2;


/**
 * Created by Yoann on 26/04/2015.
 */

public class Home extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private AQuery aq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        if( intent.getExtras() !=null){
            //if(intent.getExtras().containsKey("go_to_fragment")){
                System.out.println("intent ===========================================================================  go to fragment !");

                onNavigationDrawerItemSelected(intent.getIntExtra("go_to_fragment", 3));
                intent.getExtras().remove("go_to_fragment");
            //}
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));
        aq = new AQuery(this);

        /*A mettre dans des boutons*/
        //asyncJson();
        async_post();
        System.out.println("asynch POST");

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
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                fragment = new FragmentTest2();
                mTitle = getString(R.string.title_section3);
                break;
            case 2:
                fragment = new FragmentMap();
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                fragment = new FragmentProfile();
                mTitle = getString(R.string.title_section4);
                break;
            case 4:
                fragment = new FragmentTest();
                mTitle = getString(R.string.title_section1);
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack("tag").commit();

    }



    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
        }
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
        ((Chronometer) findViewById(R.id.chronometer1)).start();

    }

    public void stopChronometer(View view) {
        timeWhenStopped = ((Chronometer) findViewById(R.id.chronometer1)).getBase() - SystemClock.elapsedRealtime();
        ((Chronometer) findViewById(R.id.chronometer1)).stop();
    }

    public void resetChronometer(View view) {
        ((Chronometer) findViewById(R.id.chronometer1)).setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
    }

    // Button to be redirected to ProfilModif.java
    public void goToModifyUserProfile(View view) {
        Intent redirectModify = new Intent(this, ProfilModif.class);
        startActivity(redirectModify);
    }

    // Button to be redirected to FragmentProfile.java
    // and to update all the data
//    public void confirmModifyUserProfile(View view) {
//        /*Intent confirmModifications = new Intent(this, FragmentProfile.class);
//        startActivity(confirmModifications);
//        this.finish();
//        *//*
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = (Fragment)new FragmentProfile();
//        fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack("tag").commit();*/
//    }
    /**
     * A placeholder fragment containing a simple view.
     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
//            return rootView;
//        }
//
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((Home) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
//    }

    public void asyncJson(){

        //perform a Google search in just a few lines of code

        String url = "http://192.168.0.46:8080/smartwatchproject/test";
        aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if(json != null){
                    Map<String, Object> params = new HashMap<String, Object>();

                    params.get("utilisateur");
                    System.out.println(params.get("utilisateur"));
                    //successful ajax call, show status code and json content
                    Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();
                }else{
                    //ajax error, show error code
                    Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void async_post(){
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

    }

}
