package website.watchmyhealth.watchmyhealth;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.DatePickerDialog.*;

/**
 * Created by Yoann on 26/04/2015.
 */

public class FragmentProfileModif extends Fragment implements View.OnClickListener {


    TextView text;
    EditText modifDateNaissance;



    private DatePickerDialog fromDatePickerDialog;

    private SimpleDateFormat dateFormatter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("Dans Fragment !!!!");
        View view = inflater.inflate(R.layout.fragment_profile_modif, container, false);
        text=(TextView)view.findViewById(R.id.tvDateNaissance);
        modifDateNaissance=(EditText)view.findViewById(R.id.modifDateNaissance);
       // modifDateNaissance.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);

        modifDateNaissance = (EditText) view.findViewById(R.id.modifDateNaissance);
        //modifDateNaissance.setInputType(InputType.TYPE_NULL);
//        modifDateNaissance.requestFocus();


        setDateTimeField();
        return view;
    }



    private void setDateTimeField() {
        modifDateNaissance.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this.getActivity(), new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                modifDateNaissance.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getActivity().getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        fromDatePickerDialog.show();
    }
}
