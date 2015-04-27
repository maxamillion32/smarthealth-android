package website.watchmyhealth.watchmyhealth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoann on 26/04/2015.
 */

public class FragmentTest extends Fragment {

    private AQuery aq;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("Dans Fragment !!!!");
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        aq = new AQuery(getActivity(), view);

        /*A mettre dans des boutons*/
        //asyncJson();
        //async_post();
        return view;
    }


    public void asyncJson(){

        //perform a Google search in just a few lines of code

        String url = "http://blipper.fr/person";
        aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if(json != null){
                    //successful ajax call, show status code and json content
                    Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();
                }else{
                    //ajax error, show error code
                    Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void async_post(){
        //do a twiiter search with a http post
        String url = "http://blipper.fr/person";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nom", "jonathan");
        params.put("prenom", "michel");
        params.put("sexe", "Feminin");
        params.put("telephone", "1234567890");
        params.put("email", "eamosse@gmail.com");
        params.put("password", "amosse1988");

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                //showResult(json);
                System.out.println(json);
            }
        });

    }
}