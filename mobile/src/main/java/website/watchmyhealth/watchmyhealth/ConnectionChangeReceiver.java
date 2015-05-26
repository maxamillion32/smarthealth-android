package website.watchmyhealth.watchmyhealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Created by Fabrice on 17/05/2015.
 * Permet de recevoir un event quand la connexion internet est allumee(les envoyer sur le serveur) ou coupee (enregistrer les donnees dans des fichiers)
 */
public class ConnectionChangeReceiver extends BroadcastReceiver
{
    private ArrayList<String> latitude = new ArrayList<String>();
    private ArrayList<String> longitude = new ArrayList<String>();
    private String startTimer;
    private String endTimer;
    private String nbPas;
    private FileInputStream fIut = null;
    private InputStreamReader isr = null;
    private BufferedReader br = null;
    private final String FILENAME_LOCATION ="save_Time_Longitude_Latitude.dat";
    private final String FILENAME_PROFIL = "settings_profil.dat";

    //afin de ne pas envoyer les donnees quand l'utilisateur se connecte au wifi pendant qu'une activite est lancee
    private String activityState = "activityFinished";
    //si les données son sauvegardé depuis ProfilModif quand le telephone est connecte
    //a internet alors il ne faut pas envoyé les donnees via ce BroadcastReceiver

    @Override
    public void onReceive( Context context, Intent intent )
    {

        if(intent.getAction().equals("activityFinished") || intent.getAction().equals("activityStarted")){
            System.out.println("activityStarted = "+intent.getAction());
            activityState = intent.getAction();
        }
        else if(intent.getAction().equals("activityFinished")){
            System.out.println("activityFinished = "+intent.getAction());
            activityState = "activityFinished";
        }
        else{
            System.out.println("android.net.conn.CONNECTIVITY_CHANGE = "+intent.getAction());
            System.out.println("activityState = "+activityState);
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE );
        if ( activeNetInfo != null )
        {
            //permet de verifier si lorsqu'on lance l'application avec internet connecte, le fichier exite
            if(fileExists(context,FILENAME_LOCATION) && activityState.equals("activityFinished")){
                try {
                    longitude = new ArrayList<String>();
                    latitude = new ArrayList<String>();
                    String distance = "0";
                    String vitesse = "0";
                    fIut = context.openFileInput(FILENAME_LOCATION);
                    isr = new InputStreamReader(fIut);
                    br = new BufferedReader(isr);
                    String strLine="";
                    String [] tmpTab;
                    if((strLine = br.readLine())!=null){
                        tmpTab = strLine.split("_");
                        System.out.println(tmpTab[0]+" "+tmpTab[1]+" "+tmpTab[2]+" "+tmpTab[3]);
                        startTimer = tmpTab[0];
                        endTimer = tmpTab[0];
                        longitude.add(tmpTab[1]);
                        latitude.add(tmpTab[2]);
                        distance = tmpTab[3];
                        vitesse = tmpTab[4];
                    }
                    while ((strLine = br.readLine()) != null) { // while loop begins here
                        tmpTab = strLine.split("_");
                        System.out.println(tmpTab[0]+" "+tmpTab[1]+" "+tmpTab[2]+" "+tmpTab[3]+" "+tmpTab[4]);
                        endTimer = tmpTab[0];
                        longitude.add(tmpTab[1]);
                        latitude.add(tmpTab[2]);
                        distance = tmpTab[3];
                        vitesse = tmpTab[4];
                        System.out.println("Distance total = "+ distance);
                    }
                    isr.close();
                    br.close();
                    fIut.close();
                    //envoyer les donnees sauvegarder dans les fichiers
                    String idUser = "1251";
                    String nbPas= "3000";
                    String rythmeCardiaqueMoyen = "95";
                    String metres= distance;
                    ServerSync serverSync = new ServerSync(context);
                    serverSync.async_post_activite(
                            idUser,
                            latitude,
                            longitude,
                            startTimer,
                            endTimer,
                            nbPas,
                            rythmeCardiaqueMoyen,
                            metres,
                            vitesse
                    );

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Suppression du fichier");
                context.deleteFile(FILENAME_LOCATION);

            }
            if(fileExists(context,FILENAME_PROFIL)){

                System.out.println("Context = "+context +"   Dedans FILENAME_PROFIL");
                FileInputStream fis = null;
                BufferedReader reader = null;
                try{
                    fis = context.openFileInput(FILENAME_PROFIL);
                    reader = new BufferedReader(new InputStreamReader(fis));
                    String strLine = "";
                    String[] tmpStr;
                    String taille="",poids="",dateNaissance="",mail="",nom="";
                    while ((strLine = reader.readLine()) != null) {
                        System.out.println("ReadSettings Receiver ======= "+strLine);
                        tmpStr = strLine.split("_");
                        String libelle = tmpStr[0];
                        switch (libelle){
                            case "taille":
                                 taille = tmpStr[1];
                                break;
                            case "poids":
                                 poids = tmpStr[1];
                                break;
                            case "dateNaissance":
                                 dateNaissance = tmpStr[1];
                                break;
                            case "mail":
                                 mail = tmpStr[1];
                                break;
                            case "nom":
                                 nom = tmpStr[1];
                                break;
                        }
                    }
                    reader.close();
                    fis.close();
                    String idUser = "1201";
                    System.out.println("Dedans FILENAME_PROFIL =" + taille + " " + poids + " " + dateNaissance + " " + mail + " " + nom );
                    context.deleteFile(FILENAME_PROFIL);

                    ServerSync serverSync = new ServerSync(context);
                    serverSync.async_post_modif_profil(
                            idUser,
                            mail,
                            dateNaissance,
                            poids,
                            taille,
                            nom
                    );
                }
                catch (Exception e) {
                }
            }
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
