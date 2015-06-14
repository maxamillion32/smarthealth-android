package website.watchmyhealth.watchmyhealth.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import website.watchmyhealth.watchmyhealth.R;
import website.watchmyhealth.watchmyhealth.Save_Data_ReadWrite;
import website.watchmyhealth.watchmyhealth.ServerSync;


public class LoginPwd extends ActionBarActivity {

    //UI References
    private EditText modifMail;
    private EditText modifPwd;
    private Dialog dialogTaille;


    private NumberPicker npTaille;
    private Intent intent;
    private final String EXTRA_USER_MODIF_EMAIL = "EXTRA_USER_MODIF_EMAIL";
    private final String EXTRA_USER_MODIF_PWD = "EXTRA_USER_MODIF_PWD";
    private final String GO_TO_ACTIVITY_HOME = "GO_TO_ACTIVITY_HOME";
    private TextView tvId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //recuperation du mail de l'utilisateur
        modifMail = (EditText) findViewById(R.id.editTextMail);
        //recuperation de la taille de l'utilisateur
        modifPwd = (EditText) findViewById(R.id.editTextPwd);

        /*
        //instanciation de la class AQuery permettant de faire des requete ajax sur un serveur
        Intent intentFromProfil = getIntent();
        if( intentFromProfil.getExtras() !=null){
            //On r&eacute;cup&eacute;re les donn&eacute;es de la page de profil afin de les mettre dans des EditText afin de les modifier
            this.modifMail.setText(intentFromProfil.getStringExtra("EXTRA_USER_TV_MAIL"));
            this.modifPwd.setText(intentFromProfil.getStringExtra("EXTRA_USER_TV_PWD"));
        }
        setModifPwd();*/
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

    public void setModifPwd(){

        dialogTaille = new Dialog(this);
        dialogTaille.setTitle("Quelle est votre taille ?");
        dialogTaille.setContentView(R.layout.number_picker_dialog);
        npTaille = (NumberPicker)dialogTaille.findViewById(R.id.np);
        npTaille.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npTaille.setMaxValue(260);
        npTaille.setMinValue(70);
        npTaille.setWrapSelectorWheel(false);

        if(this.modifPwd.getText().toString().equals("")){ npTaille.setValue(170);}
        else{npTaille.setValue(Integer.parseInt(this.modifPwd.getText().toString()));}
        this.modifPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocusTaile) {
                if (hasFocusTaile) {
                    Button buttonOK;
                    buttonOK = (Button) dialogTaille.findViewById(R.id.buttonOK);
                    buttonOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LoginPwd.this.modifPwd.setText(String.valueOf(npTaille.getValue()));
                            dialogTaille.dismiss();
                        }
                    });
                    dialogTaille.show();
                }
            }
        });
    }

    public void confirmLoginPwd(View view) {

        //Save_Data_ReadWrite save_data_readWrite= new Save_Data_ReadWrite(this);

        ServerSync serverSync = new ServerSync(this);
        serverSync.async_post_login_pwd(
                this.modifMail.getText().toString(),
                this.modifPwd.getText().toString()
        );

        intent = new Intent(this, Home.class);
        startActivity(intent);
        this.finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
