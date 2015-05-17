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
    private ImageButton imageButton;
    private final String EXTRA_USER_TV_MAIL = "EXTRA_USER_TV_MAIL";
    private final String EXTRA_USER_TV_DATE_NAISSANCE = "EXTRA_USER_TV_DATE_NAISSANCE";
    private final String EXTRA_USER_TV_POIDS = "EXTRA_USER_TV_POIDS";
    private final String EXTRA_USER_TV_TAILLE = "EXTRA_USER_TV_TAILLE";
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvEmail = (TextView)view.findViewById(R.id.tvEmail);
        tvDateNaissance = (TextView)view.findViewById(R.id.tvDateNaissance);
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
        }
        return view;
    }

    private void getInformationsFromServeur() {
        //do a twiiter search with a http post
        String url = "http://192.168.0.12:8080/SmartHealth---Web-App/test?useFunctionServer=getProfil";
        int idUser = 1201;
        Date date = new Date();

        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if(json!=null){
                    System.out.println("Dans aq.ajax url= " + url);
                    System.out.println("Dans aq.ajax json= " + json);
                    System.out.println("Dans aq.ajax status= " + status);
                    JSONObject jsonObj = (JSONObject)json;
                    try {
                        String mail = (String)jsonObj.get("mail");
                        String dateNaissance = (String)jsonObj.get("dateNaissance");
                        String taille = (String)jsonObj.get("taille");
                        String poids = (String)jsonObj.get("poids");
                        System.out.println("mail="+mail);
                        System.out.println("dateNaissance="+dateNaissance);
                        System.out.println("taille="+taille);
                        System.out.println("poids ="+poids);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(FragmentProfil.this.getActivity(),"Impossible de contacter le serveur",Toast.LENGTH_LONG);
                }
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager= (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void /*String*/ReadSettings(){
        FileInputStream fis = null;
        BufferedReader reader = null;
        String data = null;

        try{
            fis = this.getActivity().openFileInput("settings.dat");
            //isr = new InputStreamReader(fIn);
            reader = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            ArrayList<String> lineSetting= new ArrayList<String>();
            tvEmail = (TextView)getView().findViewById(R.id.tvEmail);
            tvDateNaissance = (TextView)getView().findViewById(R.id.tvDateNaissance);
            tvTaille =(TextView)getView().findViewById(R.id.tvTaille);
            tvPoids=(TextView)getView().findViewById(R.id.tvPoids);
            while ((line = reader.readLine()) != null){
                lineSetting.add(line);
            }
            reader.close();
            fis.close();
            System.out.println("////////////////// DATA \n");
            for (int i = 0;i<lineSetting.size();i++){
                System.out.println(new String(lineSetting.get(i)));
            }
            System.out.println("//////////////////");

            //affiche le contenu de mon fichier dans un popup surgissant
        }
        catch (Exception e) {
            Toast.makeText(this.getActivity(), "Settings not read",Toast.LENGTH_SHORT).show();
        }
        //return data;
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
                startActivity(redirectModify);
                FragmentProfil.this.getActivity().onAttachFragment(FragmentProfil.this);

            }
        });
    }




}
