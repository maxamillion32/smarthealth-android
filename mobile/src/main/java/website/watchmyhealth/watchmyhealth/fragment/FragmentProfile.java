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

import java.util.HashMap;
import java.util.Map;

import website.watchmyhealth.watchmyhealth.R;

/**
 * Created by Yoann on 26/04/2015.
 */

public class FragmentProfile extends Fragment {

    private AQuery aq;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("Dans Fragment !!!!");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        aq = new AQuery(getActivity(), view);

        return view;
    }

}
