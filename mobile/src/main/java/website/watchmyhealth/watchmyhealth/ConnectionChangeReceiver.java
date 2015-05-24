package website.watchmyhealth.watchmyhealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import website.watchmyhealth.watchmyhealth.service.ServiceSync;

/**
 * Created by Fabrice on 17/05/2015.
 * Permet de recevoir un event quand la connexion internet est allumee(les envoyer sur le serveur) ou coupee (enregistrer les donnees dans des fichiers)
 */
public class ConnectionChangeReceiver extends BroadcastReceiver
{
    private AQuery aq;
    private ArrayList<String> latitude = new ArrayList<String>();
    private ArrayList<String> longitude = new ArrayList<String>();
    private String startTimer;
    private String endTimer;
    private String nbPas;
    private FileInputStream fIut = null;
    private InputStreamReader isr = null;
    private final String FILENAME ="save_Time_Longitude_Latitude.dat";
    //afin de ne pas envoyer les donnees quand l'utilisateur se connecte au wifi pendant qu'une activite est lancee
    private String activityState = "activityFinished";

    @Override
    public void onReceive( Context context, Intent intent )
    {

        if(intent.getAction().equals("activityFinished") || intent.getAction().equals("activityStarted")){
            System.out.println("activityFinished ou activityStarted");
            activityState = intent.getAction();
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE );
        if ( activeNetInfo != null )
        {
            //permet de verifier si lorsqu'on lance l'application avec internet connecte, le fichier exite
            if(fileExists(context,FILENAME) && activityState.equals("activityFinished")){
                try {

                    fIut = context.openFileInput(FILENAME);
                    isr = new InputStreamReader(fIut);
                    BufferedReader br = new BufferedReader(isr);
                    String strLine="";
                    String [] tmpTab;
                    if((strLine = br.readLine())!=null){
                        tmpTab = strLine.split("_");
                        startTimer = tmpTab[0];
                        endTimer = tmpTab[0];
                        longitude.add(tmpTab[1]);
                        latitude.add(tmpTab[2]);
                    }
                    while ((strLine = br.readLine()) != null) { // while loop begins here
                        tmpTab = strLine.split("_");
                        endTimer = tmpTab[0];
                        longitude.add(tmpTab[1]);
                        latitude.add(tmpTab[2]);
                    }
                    isr.close();
                    br.close();
                    fIut.close();
                    //envoyer les donnees sauvegarder dans les fichiers
                    String idUser = "1251";
                    String nbPas= "3000";
                    String rythmeCardiaqueMoyen = "95";
                    ServerSync serverSync = new ServerSync(context);
                    serverSync.async_post_activite(
                            idUser,
                            latitude,
                            longitude,
                            startTimer,
                            endTimer,
                            nbPas,
                            rythmeCardiaqueMoyen
                    );
                    context.deleteFile(FILENAME);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }


}
