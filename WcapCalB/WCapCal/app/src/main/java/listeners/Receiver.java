/**
 * WCap'cal
 * Développé par Simon BRUNOU, François DUCLOS, Quentin GUILLOU et Mehdi SABIR
 * © 2014 - WCap'Cal Company
 * L'usage d'une partie du code demande la citation des auteurs de l'application
 * Application de synchronisation d'agenda SUN Java Calendar sur téléphone android
 * Réalisé dans le cadre du projet de synthèse 2013-2014 de l'IUT de Vannes
 * Par des étudiants en DUT informatique de deuxième année 
 */

package listeners;

import cal.WCapCal.MyService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Classe Receiver extends BroadcastReceiver
 * Permet de recevoir différente information du téléphone
 * pour savoir quand lancer la synchronisation automatique
 * @author WCap'Cal Company
 * @version 2.0
 */
public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//Lors de la récéption d'une information on vérifi s'il faut lancer le service ou le stopper
		String action = intent.getAction();
	    if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
	    	boolean started = false;
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if (MyService.class.getName().equals(service.service.getClassName())) {
					started = true;
				}
			}
			if(started && !MyService.checkParametersBeforeStart(context)){
				context.stopService(new Intent(context, MyService.class));
			}
	    }
	        
	    else {
	    	intent = new Intent(context, MyService.class);
			boolean started = true;
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if (MyService.class.getName().equals(service.service.getClassName())) {
					started = false;
				}
			}
			if(MyService.checkParametersBeforeStart(context) && started){
				context.startService(new Intent(context, MyService.class));
			}
	    }
	}
}
