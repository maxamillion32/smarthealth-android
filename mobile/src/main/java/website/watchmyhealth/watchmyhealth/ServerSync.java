package website.watchmyhealth.watchmyhealth;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import website.watchmyhealth.watchmyhealth.fragment.FragmentProfil;

/**
 * Created by Fabrice on 23/05/2015.
 */
public class ServerSync {
    private AQuery aq;
    private Context context;
    private String urlPost = "http://192.168.0.12:8080/SmartHealth---Web-App";
    private String urlGet =  "http://192.168.0.12:8080/SmartHealth---Web-App";

    public ServerSync(Context context){
        this.context = context;
        aq = new AQuery(this.context);
    }

    public void async_get_profil(final TextView tvNom,final TextView tvPrenom,final TextView tvEmail,final TextView tvDateNaissance,final TextView tvTaille,final TextView tvPoids){

        //do a twiiter search with a http post
        String idUser = "&idUser=1201";
        String urlGet = this.urlGet+"/test?useFunctionServer=getProfil"+idUser;
        aq.ajax(urlGet, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String urlGet, JSONObject json, AjaxStatus status) {
                if(json!=null){
                    System.out.println("Dans aq.ajax url= " + urlGet);
                    System.out.println("Dans aq.ajax json= " + json);
                    System.out.println("Dans aq.ajax status= " + status);
                    JSONObject jsonObj = (JSONObject)json;
                    try {
                        String nom = (String)jsonObj.get("nom");
                        String prenom = (String)jsonObj.get("prenom");
                        String mail = (String)jsonObj.get("mail");
                        String dateNaissance = (String)jsonObj.get("dateNaissance");
                        String taille = (String)jsonObj.get("taille");
                        String poids = (String)jsonObj.get("poids");
                        tvNom.setText(nom);
                        tvPrenom.setText(prenom);
                        tvDateNaissance.setText(dateNaissance);
                        tvEmail.setText(mail);
                        tvPoids.setText(poids);
                        tvTaille.setText(taille);
                        //On enregistre les donnees du serveur sur le telephone pour le cas ou l'utilisateur n'est plus connectee
                        Save_Data_ReadWrite save_data_readWrite= new Save_Data_ReadWrite((Activity)context);
                        save_data_readWrite.saveDataProfilModifInFile(nom,prenom,taille,poids,dateNaissance,mail);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(context, "Impossible de contacter le serveur", Toast.LENGTH_LONG);
                }
            }
        });
    }
    public void async_post_activite(String idUser,ArrayList<String>latitude,ArrayList<String> longitude,String startTimer,String endTimer,String nbPas,String rythmeCardiaqueMoyen,String metres){
        Map<String, Object> params = new HashMap<String, Object>();
        String urlPost =this.urlPost+"/test";
        params.put("useFunctionServer","sauvegardeActivitee");
        params.put("userId", idUser);
        params.put("timeDebutActivite",startTimer);
        params.put("timeFinActivite",endTimer);
        params.put("latitude",latitude);
        params.put("longitude",longitude);
        params.put("podometre", nbPas);
        params.put("rythmeCardiaque", rythmeCardiaqueMoyen);
        params.put("metres", metres);

        aq.ajax(urlPost, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String urlPost, JSONObject json, AjaxStatus status) {
                //showResult(json);
                System.out.println("Dans aq.ajax = " + json);
            }
        });


    }
    public void async_post_modif_profil(String idUser,String modifMail,String modifDateNaissance,String modifPoids,String modifTaille,String modifNom,String modifPrenom){
        //do a twiiter search with a http post
        //Une appelle de methode d'Async_post pour chaque jour (la date), car il faut envoyer toutes les donnees d'un jour donne en meme temps
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("useFunctionServer", "modificationProfil");
        params.put("userId", idUser);
        params.put("userEmail", modifMail);
        params.put("userDateNaissance", modifDateNaissance);
        params.put("userPoids", modifPoids);
        params.put("userTaille", modifTaille);
        params.put("userNom", modifNom);
        params.put("userPrenom",modifPrenom);

        String urlPost = this.urlPost + "/test";
        aq.ajax(urlPost, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String urlPost, JSONObject json, AjaxStatus status) {
                System.out.println("Dans aq.ajax = " + json);
            }
        });
    }

}
