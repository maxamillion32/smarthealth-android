package website.watchmyhealth.watchmyhealth.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import website.watchmyhealth.watchmyhealth.R;


public class ProfilModif extends ActionBarActivity {

    //UI References
    private EditText modifDateNaissance;
    private EditText modifPoids;
    private EditText modifTaille;
    private EditText modifMail;

    private DatePickerDialog fromDatePickerDialog;
    private Dialog dialogPoids;
    private Dialog dialogTaille;
    private NumberPicker npTaille;
    private NumberPicker nbPoids;
    private AQuery aq;
    private Intent intent;
    private final String EXTRA_USER_MODIF_EMAIL = "EXTRA_USER_MODIF_EMAIL";
    private final String EXTRA_USER_MODIF_POIDS = "EXTRA_USER_MODIF_POIDS";
    private final String EXTRA_USER_MODIF_TAILLE = "EXTRA_USER_MODIF_TAILLE";
    private final String EXTRA_USER_MODIF_DATE_NAISSANCE = "EXTRA_USER_MODIF_DATE_NAISSANCE";
    private final String GO_TO_FRAGMENT_PROFIL = "GO_TO_FRAGMENT_PROFIL";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_modif);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //recuperation du mail de l'utilisateur
        modifMail = (EditText) findViewById(R.id.modifMail);
        //recuperation de la taille de l'utilisateur
        modifTaille = (EditText) findViewById(R.id.modifTaille);
        modifTaille.setInputType(InputType.TYPE_NULL);
        //recuperation de la date de naissance de l'utilisateur
        modifDateNaissance = (EditText) findViewById(R.id.modifDateNaissance);
        modifDateNaissance.setInputType(InputType.TYPE_NULL);
        //recuperation du poids de l'utilisateur
        modifPoids = (EditText) findViewById(R.id.modifPoids);
        modifPoids.setInputType(InputType.TYPE_NULL);
        //instanciation de la class AQuery permettant de faire des requete ajax sur un serveur
        aq = new AQuery(this);
        Intent intentFromProfil = getIntent();
        if( intentFromProfil.getExtras() !=null){
            //On r&eacute;cup&eacute;re les donn&eacute;es de la page de profil afin de les mettre dans des EditText afin de les modifier
            this.modifMail.setText(intentFromProfil.getStringExtra("EXTRA_USER_TV_MAIL"));
            this.modifDateNaissance.setText(intentFromProfil.getStringExtra("EXTRA_USER_TV_DATE_NAISSANCE"));
            this.modifPoids.setText(intentFromProfil.getStringExtra("EXTRA_USER_TV_POIDS"));
            this.modifTaille.setText(intentFromProfil.getStringExtra("EXTRA_USER_TV_TAILLE"));
        }
        setDateTimeField();
        setModifPoids();
        setModifTaille();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profil_modif, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                modifDateNaissance.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        modifDateNaissance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDatePickerDialog.show();
            }
        });
    }

    public void setModifPoids(){
        dialogPoids = new Dialog(this);
        dialogPoids.setTitle("Quel est votre poids ?");
        dialogPoids.setContentView(R.layout.number_picker_dialog);

        nbPoids = (NumberPicker)dialogPoids.findViewById(R.id.np);
        nbPoids.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        nbPoids.setMaxValue(300);
        nbPoids.setMinValue(20);
        nbPoids.setWrapSelectorWheel(false);

        if(this.modifPoids.getText().toString().equals("")){ nbPoids.setValue(70);}
        else{nbPoids.setValue(Integer.parseInt(this.modifPoids.getText().toString()));}
        this.modifPoids.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocusPoids) {
                if (hasFocusPoids) {
                    Button buttonOK = (Button) dialogPoids.findViewById(R.id.buttonOK);
                    buttonOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ProfilModif.this.modifPoids.setText(String.valueOf(nbPoids.getValue()));
                            dialogPoids.dismiss();
                        }
                    });
                    dialogPoids.show();
                }
            }
        });
    }
    public void setModifTaille(){

        dialogTaille = new Dialog(this);
        dialogTaille.setTitle("Quelle est votre taille ?");
        dialogTaille.setContentView(R.layout.number_picker_dialog);
        npTaille = (NumberPicker)dialogTaille.findViewById(R.id.np);
        npTaille.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npTaille.setMaxValue(260);
        npTaille.setMinValue(70);
        npTaille.setWrapSelectorWheel(false);

        if(this.modifTaille.getText().toString().equals("")){ npTaille.setValue(170);}
        else{npTaille.setValue(Integer.parseInt(this.modifTaille.getText().toString()));}
        this.modifTaille.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocusTaile) {
                if (hasFocusTaile) {
                    Button buttonOK;
                    buttonOK = (Button) dialogTaille.findViewById(R.id.buttonOK);
                    buttonOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ProfilModif.this.modifTaille.setText(String.valueOf(npTaille.getValue()));
                            dialogTaille.dismiss();
                        }
                    });
                    dialogTaille.show();
                }
            }
        });
    }
    public void confirmModifyUserProfile(View view) {
        System.out.println(this.modifTaille.getText().toString() + "====================Dans confirmModifyUserProfile ==========================" + this.modifTaille.getText());
        intent = new Intent(this, Home.class);

        intent.putExtra(EXTRA_USER_MODIF_TAILLE, modifTaille.getText().toString());
        intent.putExtra(EXTRA_USER_MODIF_POIDS, this.modifPoids.getText().toString());
        intent.putExtra(EXTRA_USER_MODIF_DATE_NAISSANCE, this.modifDateNaissance.getText().toString());
        intent.putExtra(EXTRA_USER_MODIF_EMAIL, this.modifMail.getText().toString());
        //permet d'indiquer quand lorsqu'on sauvegarde on veut se rendre sur la position 2 du switch dans Home.java
        intent.putExtra(GO_TO_FRAGMENT_PROFIL, 2);
        if(isNetworkAvailable()){
            async_post();
            saveDataProfilModifInFile();
        }else{
            saveDataProfilModifInFile();
        }
        startActivity(intent);
    }

    public void async_post(){
        //do a twiiter search with a http post
        String url = "http://192.168.0.12:8080/SmartHealth---Web-App/test";
        int idUser = 1201;
        Date date = new Date();
        //Une appelle de methode d'Async_post pour chaque jour (la date), car il faut envoyer toutes les donnees d'un jour donne en meme temps
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("useFunctionServer", "modificationProfil");
        params.put("userId", idUser);
        params.put("dateDuJour", date);
        params.put("userEmail", this.modifMail.getText());
        params.put("userDateNaissance", this.modifDateNaissance.getText());
        params.put("userPoids", this.modifPoids.getText());
        params.put("userTaille", this.modifTaille.getText());

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                System.out.println("Dans aq.ajax = " + json);
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Permet de sauvegarder les donnees du profil dans un fichier texte pour les recuperer sans connexion(Dans Home.java)
     */
    public void saveDataProfilModifInFile(){
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        try{
            fOut = this.openFileOutput("settings.dat", MODE_PRIVATE);
            String separator = System.getProperty("line.separator");
            osw = new OutputStreamWriter(fOut);
            osw.flush();
            osw.append("taille_" + this.modifTaille.getText().toString());
            System.out.println("===================System.getProperty(\"line.separator\")===================");
            osw.append(separator);
            osw.append("poids_" + this.modifPoids.getText().toString());
            osw.append(separator);
            osw.append("dateNaissance_" + this.modifDateNaissance.getText().toString());
            osw.append(separator);
            osw.append("mail_" + this.modifMail.getText().toString());
            System.out.println("poids_" + this.modifPoids.getText().toString());
            osw.flush();
            //popup surgissant pour le resultat
            osw.close();
            fOut.close();
        }
        catch (Exception e) {
        }
    }

}
