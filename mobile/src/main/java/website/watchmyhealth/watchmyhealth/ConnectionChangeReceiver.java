package website.watchmyhealth.watchmyhealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Fabrice on 17/05/2015.
 * Permet de recevoir un event quand la connexion internet est allumee(les envoyer sur le serveur) ou coupee (enregistrer les donnees dans des fichiers)
 */
public class ConnectionChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context, Intent intent )
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE );
        if ( activeNetInfo != null)
        {
            System.out.println("////////////////////////// Dans ConnectionChangeReceiver.java1 \"////////////////////////// ");
            //envoyer les donnees sauvegarder dans les fichiers
            Toast.makeText(context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT).show();
        }
    }
}
