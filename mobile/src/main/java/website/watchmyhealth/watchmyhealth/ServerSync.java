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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Fabrice on 23/05/2015.
 */
public class ServerSync {
    private AQuery aq;
    private Context context;
    private String url = "http://192.168.0.13:8080/smartwatchproject";
    private String idUser="" ;//= "5548c58c07603c2ff4cf1cf7";

    public ServerSync(Context context){
        this.context = context;
        aq = new AQuery(this.context);
        Save_Data_ReadWrite save_login_readWrite= new Save_Data_ReadWrite((Activity)context);
        idUser = save_login_readWrite.ReadLoginProfil();
        System.out.println("ReadLoginProfil = "+idUser);
    }

    public void async_get_profil(final TextView tvNom,final TextView tvEmail,final TextView tvDateNaissance,final TextView tvTaille,final TextView tvPoids){

        //do a twiiter search with a http post
        String urlGet = this.url+"/test?useFunctionServer=getProfil&userId="+this.idUser;
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
                        String mail = (String)jsonObj.get("mail");
                        String dateNaissance = (String)jsonObj.get("dateNaissance");
                        String taille = (String)jsonObj.get("taille");
                        String poids = (String)jsonObj.get("poids");
                        tvNom.setText(nom);
                        tvDateNaissance.setText(dateNaissance);
                        tvEmail.setText(mail);
                        tvPoids.setText(poids);
                        tvTaille.setText(taille);
                        //On enregistre les donnees du serveur sur le telephone pour le cas ou l'utilisateur n'est plus connectee
                        Save_Data_ReadWrite save_data_readWrite= new Save_Data_ReadWrite((Activity)context);
                        save_data_readWrite.saveDataProfilModifInFile(nom,taille,poids,dateNaissance,mail);

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
    public void async_post_activite(ArrayList<String>latitude,ArrayList<String> longitude,String startTimer,String endTimer,String nbPas,String rythmeCardiaqueMoyen,String metres,String vitesse,String activityState){
        Map<String, Object> params = new HashMap<String, Object>();
        String urlPost =this.url+"/test";
        params.put("useFunctionServer","sauvegardeActivitee");
        params.put("userId", idUser);
        params.put("timeDebutActivite",startTimer);
        params.put("timeFinActivite",endTimer);
        params.put("latitude",latitude);
        params.put("longitude",longitude);
        params.put("podometre", nbPas);
        params.put("rythmeCardiaque", rythmeCardiaqueMoyen);
        params.put("metres", metres);
        params.put("vitesse", vitesse);
        params.put("type",activityState);
        aq.ajax(urlPost, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String urlPost, JSONObject json, AjaxStatus status) {
                //showResult(json);
                System.out.println("Dans aq.ajax = " + json);
            }
        });


    }
    public void async_post_modif_profil(String modifMail,String modifDateNaissance,String modifPoids,String modifTaille,String modifNom){
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

        String urlPost = this.url + "/test";
        aq.ajax(urlPost, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String urlPost, JSONObject json, AjaxStatus status) {
                System.out.println("Dans aq.ajax = " + json);
            }
        });
    }

    public void async_post_login_pwd(String modifMail,String modifPwd){
        //do a twiiter search with a http post
        //Une appelle de methode d'Async_post pour chaque jour (la date), car il faut envoyer toutes les donnees d'un jour donne en meme temps
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("useFunctionServer", "verifLogin");
        params.put("email", modifMail);
        params.put("password", modifPwd);

        String urlPost = this.url + "/test";
        aq.ajax(urlPost, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            String idUser = "";
            @Override
            public void callback(String urlPost, JSONObject json, AjaxStatus status) {
                if (json != null) {
                    System.out.println("Dans aq.ajax url= " + url);
                    System.out.println("Dans aq.ajax json= " + json);
                    System.out.println("Dans aq.ajax status= " + status);
                    JSONObject jsonObj = (JSONObject) json;
                    try {
                        idUser = (String) jsonObj.get("id");

                        //On enregistre les donnees du serveur sur le telephone pour le cas ou l'utilisateur n'est plus connectee
                        Save_Data_ReadWrite save_login_readWrite= new Save_Data_ReadWrite((Activity)context);
                        save_login_readWrite.saveDataLoginInFile(idUser);

                        System.out.println(idUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "Impossible de contacter le serveur", Toast.LENGTH_LONG);
                }
            }
        });
    }
}
