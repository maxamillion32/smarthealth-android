package website.watchmyhealth.watchmyhealth;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import website.watchmyhealth.watchmyhealth.activity.ProfilModif;

/**
 * Created by Fabrice on 25/05/2015.
 */
public class Save_Data_ReadWrite {
    private final String FILENAME_PROFIL = "settings_profil.dat";
    private final String FILENAME_LOCATION ="save_Time_Longitude_Latitude.dat";
    private Service service;
    private Activity activity;
    public Save_Data_ReadWrite(Activity activity){
        this.activity = activity;
    }

    public void ReadSettingsProfil(TextView tvTaille,TextView tvPoids,TextView tvDateNaissance,TextView tvEmail,TextView tvNom,TextView tvPrenom){
        FileInputStream fis = null;
        BufferedReader reader = null;
        try{
            fis = activity.openFileInput(FILENAME_PROFIL);
            //isr = new InputStreamReader(fIn);
            reader = new BufferedReader(new InputStreamReader(fis));
            String strLine = "";
            String[] tmpStr;
            while ((strLine = reader.readLine()) != null) {
                System.out.println("ReadSettings ======= "+strLine);
                tmpStr = strLine.split("_");
                String libelle = tmpStr[0];
                switch (libelle){
                    case "taille":
                        tvTaille.setText(tmpStr[1]);
                        break;
                    case "poids":
                        tvPoids.setText(tmpStr[1]);
                        break;
                    case "dateNaissance":
                        tvDateNaissance.setText(tmpStr[1]);
                        break;
                    case "mail":
                        tvEmail.setText(tmpStr[1]);
                        break;
                    case "nom":
                        tvNom.setText(tmpStr[1]);
                        break;
                    case "prenom":
                        tvPrenom.setText(tmpStr[1]);
                        break;
                }
            }
            reader.close();
            fis.close();
        }
        catch (Exception e) {
            Toast.makeText(activity, "Settings not read", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Permet de sauvegarder les donnees du profil dans un fichier texte pour les recuperer sans connexion(Dans Home.java)
     */
    public void saveDataProfilModifInFile(String modifNom,String modifPrenom,String modifTaille,String modifPoids,String modifDateNaissance,String modifMail){
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;
        activity.deleteFile(FILENAME_PROFIL);
        try{
            fOut = activity.openFileOutput(FILENAME_PROFIL, activity.MODE_APPEND);
            osw = new OutputStreamWriter(fOut);
            String separator = System.getProperty("line.separator");
            osw.append("nom_" + modifNom);
            osw.append(separator);
            osw.append("prenom_" + modifPrenom);
            osw.append(separator);
            osw.append("taille_" + modifTaille);
            osw.append(separator);
            osw.append("poids_" + modifPoids);
            osw.append(separator);
            osw.append("dateNaissance_" + modifDateNaissance);
            osw.append(separator);
            osw.append("mail_" + modifMail);
            osw.close();
            fOut.close();
        }
        catch (Exception e) {
        }
    }
    public void write_location(String strLongitude,String strLatitude){

    }

}
