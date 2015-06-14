package website.watchmyhealth.watchmyhealth.fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import website.watchmyhealth.watchmyhealth.R;
import website.watchmyhealth.watchmyhealth.Save_Data_ReadWrite;
import website.watchmyhealth.watchmyhealth.ServerSync;
import website.watchmyhealth.watchmyhealth.activity.ProfilModif;

/**
 * Created by Yoann on 26/04/2015.
 */

public class FragmentProfil extends Fragment {
    private TextView tvEmail;
    private TextView tvDateNaissance;
    private TextView tvTaille;
    private TextView tvPoids;
    private TextView tvNom;
    private ImageButton imageButton;
    private final String EXTRA_USER_TV_MAIL = "EXTRA_USER_TV_MAIL";
    private final String EXTRA_USER_TV_DATE_NAISSANCE = "EXTRA_USER_TV_DATE_NAISSANCE";
    private final String EXTRA_USER_TV_POIDS = "EXTRA_USER_TV_POIDS";
    private final String EXTRA_USER_TV_TAILLE = "EXTRA_USER_TV_TAILLE";
    private final String EXTRA_USER_TV_NOM = "EXTRA_USER_TV_NOM";
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvEmail = (TextView)view.findViewById(R.id.tvEmail);
        tvDateNaissance = (TextView)view.findViewById(R.id.tvDateNaissance);
        tvNom = (TextView)view.findViewById(R.id.tvNom);
        tvTaille =(TextView)view.findViewById(R.id.tvTaille);
        tvPoids=(TextView)view.findViewById(R.id.tvPoids);
        imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        setImageButtonProfilModif();
        Intent intentFromProfilModif = getActivity().getIntent();
        if(isNetworkAvailable() && intentFromProfilModif.getExtras() !=null){
            Save_Data_ReadWrite save_data_readWrite= new Save_Data_ReadWrite(this.getActivity());
            save_data_readWrite.ReadSettingsProfil(this.tvTaille, this.tvPoids, this.tvDateNaissance, this.tvEmail, this.tvNom);

        }
        else if(isNetworkAvailable()){
            getInformationsFromServeur();
        }else{
            Save_Data_ReadWrite save_data_readWrite= new Save_Data_ReadWrite(this.getActivity());
            save_data_readWrite.ReadSettingsProfil(this.tvTaille,this.tvPoids,this.tvDateNaissance,this.tvEmail,this.tvNom);
        }
        if( intentFromProfilModif.getExtras() !=null) {
            tvEmail.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_EMAIL"));
            tvDateNaissance.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_DATE_NAISSANCE"));
            tvTaille.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_TAILLE"));
            tvPoids.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_POIDS"));
            tvNom.setText(intentFromProfilModif.getStringExtra("EXTRA_USER_MODIF_NOM"));
        }
        return view;
    }

    private void getInformationsFromServeur() {
       ServerSync serverSync = new ServerSync(this.getActivity());
        serverSync.async_get_profil(tvNom,tvEmail,tvDateNaissance,tvTaille,tvPoids);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager= (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                startActivity(redirectModify);
                FragmentProfil.this.getActivity().onAttachFragment(FragmentProfil.this);

            }
        });
    }

}
