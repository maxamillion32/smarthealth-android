package website.watchmyhealth.watchmyhealth.fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import website.watchmyhealth.watchmyhealth.R;
import website.watchmyhealth.watchmyhealth.ServerSync;
import website.watchmyhealth.watchmyhealth.activity.ProfilModif;

/**
 * Created by Yoann on 26/04/2015.
 */

public class FragmentProfil extends Fragment {
    private AQuery aq;
    private TextView tvEmail;
    private TextView tvDateNaissance;
    private TextView tvTaille;
    private TextView tvPoids;
    private TextView tvNom;
    private TextView tvPrenom;
    private ImageButton imageButton;
    private final String EXTRA_USER_TV_MAIL = "EXTRA_USER_TV_MAIL";
    private final String EXTRA_USER_TV_DATE_NAISSANCE = "EXTRA_USER_TV_DATE_NAISSANCE";
    private final String EXTRA_USER_TV_POIDS = "EXTRA_USER_TV_POIDS";
    private final String EXTRA_USER_TV_TAILLE = "EXTRA_USER_TV_TAILLE";
    private final String EXTRA_USER_TV_PRENOM = "EXTRA_USER_TV_PRENOM";
    private final String EXTRA_USER_TV_NOM = "EXTRA_USER_TV_NOM";
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvEmail = (TextView)view.findViewById(R.id.tvEmail);
        tvDateNaissance = (TextView)view.findViewById(R.id.tvDateNaissance);
        tvNom = (TextView)view.findViewById(R.id.tvNom);
        tvPrenom = (TextView)view.findViewById(R.id.tvPrenom);
        tvTaille =(TextView)view.findViewById(R.id.tvTaille);
        tvPoids=(TextView)view.findViewById(R.id.tvPoids);
        imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        aq = new AQuery(this.getActivity());
        setImageButtonProfilModif();
        if(isNetworkAvailable()){
            getInformationsFromServeur();
        }else{
            ReadSettings();
        }
        Intent intentFromProfilModif = getActivity().getIntent();
        if( intentFromProfilModif.getExtras() !=null) {
            tvEmail.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_EMAIL"));
            tvDateNaissance.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_DATE_NAISSANCE"));
            tvTaille.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_TAILLE"));
            tvPoids.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_POIDS"));
            tvNom.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_NOM"));
            tvPrenom.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_PRENOM"));
        }
        return view;
    }

    private void getInformationsFromServeur() {
       ServerSync serverSync = new ServerSync(this.getActivity());
        serverSync.async_get_profil(tvNom,tvPrenom,tvEmail,tvDateNaissance,tvTaille,tvPoids);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager= (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void /*String*/ReadSettings(){
        FileInputStream fis = null;
        BufferedReader reader = null;
        try{
            fis = this.getActivity().openFileInput("settings_profil.dat");
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
            Toast.makeText(this.getActivity(), "Settings not read",Toast.LENGTH_SHORT).show();
        }
    }

    // Button to be redirected to ProfilModif.java
    public void setImageButtonProfilModif() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent redirectModify = new Intent(FragmentProfil.this.getActivity(), ProfilModif.class);
                redirectModify.putExtra(EXTRA_USER_TV_MAIL, tvEmail.getText().toString());
                redirectModify.putExtra(EXTRA_USER_TV_DATE_NAISSANCE, tvDateNaissance.getText());
                redirectModify.putExtra(EXTRA_USER_TV_POIDS, tvPoids.getText());
                redirectModify.putExtra(EXTRA_USER_TV_TAILLE, tvTaille.getText());
                redirectModify.putExtra(EXTRA_USER_TV_NOM, tvNom.getText());
                redirectModify.putExtra(EXTRA_USER_TV_PRENOM, tvPrenom.getText());
                startActivity(redirectModify);
                FragmentProfil.this.getActivity().onAttachFragment(FragmentProfil.this);

            }
        });
    }




}
