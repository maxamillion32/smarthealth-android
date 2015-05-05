package website.watchmyhealth.watchmyhealth.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import website.watchmyhealth.watchmyhealth.R;
import website.watchmyhealth.watchmyhealth.fragment.FragmentProfile;


public class ProfilModif extends ActionBarActivity {

    //UI References
    private EditText modifDateNaissance;
    private EditText modifPoids;
    private EditText modifTaille;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Dialog dialogPoids;
    private Dialog dialogTaille;
    private NumberPicker npTaille;
    private NumberPicker nbPoids;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_modif);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
        modifTaille = (EditText) findViewById(R.id.modifTaille);
        modifTaille.setInputType(InputType.TYPE_NULL);

        modifDateNaissance = (EditText) findViewById(R.id.modifDateNaissance);
        modifDateNaissance.setInputType(InputType.TYPE_NULL);

        modifPoids = (EditText) findViewById(R.id.modifPoids);
        modifPoids.setInputType(InputType.TYPE_NULL);
        // modifDateNaissance.requestFocus();

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
                modifDateNaissance.setText(dateFormatter.format(newDate.getTime()));
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
                if(hasFocusTaile){
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
        Intent intent = new Intent(ProfilModif.this, Home.class);
        intent.putExtra("go_to_fragment", 3);
        startActivity(intent);
    }
}
