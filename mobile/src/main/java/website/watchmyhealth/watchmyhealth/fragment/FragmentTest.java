package website.watchmyhealth.watchmyhealth.fragment;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import website.watchmyhealth.watchmyhealth.R;

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
        asyncJson();
        //async_post();
        System.out.println("asynch POST");
        return view;
    }


    public void asyncJson(){

        //perform a Google search in just a few lines of code

        String url = "http://192.168.0.46:8080/smartwatchproject/test";
        aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if(json != null){
                    Map<String, Object> params = new HashMap<String, Object>();

                    params.get("utilisateur");
                    System.out.println(params.get("utilisateur"));
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
        String url = "http://192.168.0.46:8080/smartwatchproject/test";
        int idUser = 1201;
        int nbPas = 3000;
        ArrayList<String> latitude= new ArrayList<String>();
        latitude.add("15.23568");
        latitude.add("15.23568");latitude.add("15.23568");
        latitude.add("15.23568");

        String[] longitude= {"47.26545","47.26545","47.26545","47.26545"};
        Date date = new Date();
        //Une appelle de méthode d'Async_post pour chaque jour (la date), car il faut envoyer toutes les données d'un jour ensemble
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nom", idUser);
        params.put("dateDuJour",date);
        params.put("latitude",latitude);
        params.put("longitude",longitude);
        params.put("podometre", nbPas);



        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                //showResult(json);
                System.out.println("Dans aq.ajax = "+json);
            }
        });

    }
}