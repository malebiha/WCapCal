/**
 * WCap'cal
 * Développé par Simon BRUNOU, François DUCLOS, Quentin GUILLOU et Mehdi SABIR
 * © 2014 - WCap'Cal Company
 * L'usage d'une partie du code demande la citation des auteurs de l'application
 * Application de synchronisation d'agenda SUN Java Calendar sur téléphone android
 * Réalisé dans le cadre du projet de synthèse 2013-2014 de l'IUT de Vannes
 * Par des étudiants en DUT informatique de deuxième année 
 */

package cal.WCapCal;

import connection.Connection;
import datasBases.AgendaList;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;

/**
 * Classe MyService extends Service
 * Permet le fonctionnement de l'application en fond de tâche
 * Permet la synchronisation automatique
 * @author WCap'Cal Company
 * @version 2.0
 */
public class MyService extends Service{

	//used for getting the handler from other class for sending messages
	public static Handler 		mMyServiceHandler 			= null;
	//used for keep track on Android running status
	public static Boolean 		mIsServiceRunning 			= false;
	protected boolean syncAuto = true;
	protected int temps = 0;
	private String[][] listAgenda;
	private Thread thread;
	private int batterie;

	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	/**
	 * Méthode onCreate
	 * Est appelée lors de la création du Service
	 */
	public void onCreate() {
		try {
			//On récupére les informations de paramètres avancés
			SharedPreferences settings = getApplicationContext().getSharedPreferences("preferencescreen.xml", 0);
			syncAuto = settings.getBoolean("switch1", true);
			int heure = settings.getInt("numberpicker1", 1);
			int minute = settings.getInt("numberpicker2", 0);
			temps = (heure-1)*3600000+(minute-1)*60000;
			batterie = settings.getInt("batterie", 0);
			listAgenda = AgendaList.instance(getApplicationContext()).getAgenda();
		}
		catch(NullPointerException e){
			
		}
		mIsServiceRunning= true;
		mMyServiceHandler = new Handler(){

			@Override
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				//On effectue la synchronisation
				
				String[][] agenda = new String[1][5];
				int i = 0;
				for(i = 0; i < listAgenda.length; i++){
					agenda[0][0] = listAgenda[i][0];
					agenda[0][1] = listAgenda[i][1];
					agenda[0][2] = listAgenda[i][2];
					agenda[0][3] = listAgenda[i][3];
					agenda[0][4] = listAgenda[i][4];
					Connection connection = new Connection(agenda);
					connection.makeConnection(getApplicationContext(), false);
				}
			}
		};
		//On crée un Thread pour la synchronisation automatique
		thread = new Thread(new Runnable(){
			public void run() {
				if(syncAuto){
					while(mIsServiceRunning){
						try {
							if(checkConnectivity()){
								mMyServiceHandler.sendEmptyMessage(0);
							}
							if(temps < 60000){
								temps = 60000;
							}
							if(!checkBattery() || !checkConnectivity()){
								onDestroy();
							}
							Thread.currentThread().sleep(temps);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} 
					}
				}
			}
		});
	}

	@Override
	/**
	 * Méthode onStart
	 * Appelé après onCreate
	 * Permet le lancement de la synchronisation automatique si l'état du téléphone
	 * correspond au condition définies dans les paramètres avancés
	 */
	public void onStart(Intent intent, int startId) {
		if(MyService.checkParametersBeforeStart(getApplicationContext())){
			//lancement du thread
			thread.start();
		}
	}

	@Override
	/**
	 * Méthode onDestroy
	 * Permet la destruction du service
	 */
	public void onDestroy() {
		//On arrête le service et interrompt le thread
		mIsServiceRunning = false;
		thread.interrupt();
	}
	
	/**
	 * Méthoe checkParametersBeforeStart
	 * Permet de vérifier les conditions de lancement de la synchronisation automatique
	 * @param context le context de l'application
	 * @return un boolean, true s'il peut être lancé, false sinon
	 */
	public static boolean checkParametersBeforeStart(Context context){
		//On récupére les paramètres avancés
		SharedPreferences settings = context.getSharedPreferences("preferencescreen.xml", 0);
		boolean syncAuto = settings.getBoolean("switch1", true);
		boolean network = settings.getBoolean("checkbox1", false);
		boolean wifi = settings.getBoolean("checkbox2", true);
		
		//On vérifie s'il y a des agendas à synchroniser
		String[][] listAgenda = AgendaList.instance(context).getAgenda();
		if(listAgenda.length == 0){
    		return false;
    	}
		
		//On véréfie l'état synchronisation auto ou non
		if(!syncAuto){
			return false;
		}
		
		//On vérifie que l'un des modes de récéptions est validé
	    if(!wifi && !network){
	    	AlertDialog.Builder adb = new AlertDialog.Builder(context);
       	 	adb.setTitle("Erreur de synchronisation");
       	 	adb.setMessage("Pour pouvoir synchroniser, veuillez activer au moins un mode de récéption des données.");
       	 	adb.show();
       	 	return false;
	    }
	    
	    //On vérifie l'état de la wifi
	    if(wifi){
	    	WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		    if(wifiMgr.isWifiEnabled()){
		    	return true;
		    }
	    }
	    
	    //On vérifie l'état du réseau (3g/4g/h+)
	    if(network){
	    	ConnectivityManager conMgr =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		    if ( conMgr.getNetworkInfo(0).getState() == State.CONNECTED ||  conMgr.getNetworkInfo(1).getState() == State.CONNECTING  ) {
		    	return true;
		    }
		    else if ( conMgr.getNetworkInfo(0).getState() == State.DISCONNECTED ||  conMgr.getNetworkInfo(1).getState() == State.DISCONNECTED) {
		    	return false;
		    }
	    }
	    
	    return true;
	}
	
	/**
	 * Méthode checkConnectivity
	 * Permet de vérifier les paramètres de connection du téléphone
	 * @return un boolean, true si la connection est ok, false sinon
	 */
	public boolean checkConnectivity(){
		SharedPreferences settings = getApplicationContext().getSharedPreferences("preferencescreen.xml", 0);
		boolean network = settings.getBoolean("checkbox1", false);
		boolean wifi = settings.getBoolean("checkbox2", true);
		if(wifi){
	    	WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	    	if(wifiMgr.isWifiEnabled()){
		    	return true;
		    }
	    }
		if(network){
			ConnectivityManager conMgr =  (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		    if ( conMgr.getNetworkInfo(0).getState() == State.CONNECTED ||  conMgr.getNetworkInfo(1).getState() == State.CONNECTING  ) {
		    	return true;
		    }
		    else if ( conMgr.getNetworkInfo(0).getState() == State.DISCONNECTED ||  conMgr.getNetworkInfo(1).getState() == State.DISCONNECTED) {
		    	return false;
		    }
	    }
		return false;
	}
	
	/**
	 * Méthode checkBattery
	 * Permet de vérifier l'état de la batterie
	 * @return un boolean, true si la batterie est assez chargée, false sinon
	 */
	public boolean checkBattery(){
		//On récupére le niveau actuel de la batterie
		Intent batteryIntent = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	    int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
	    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
	    float value = 0;
	    if(level == -1 || scale == -1) {
	        value = 50.0f;
	    }
	    else {
	    	value = ((float)level / (float)scale) * 100.0f;
	    }
	    //On vérifi que le niveau de la batterie est supérieur à celui indiqué dans les paramètres 
	    //Et que le téléphone ne charge pas actuellement
	    if(value <= batterie && !isCharging){
	    	//Le niveau de la batterie n'est pas bon
	    	return false;
		}
	    //Le niveau de la batterie est correct
	    return true;
	}
}
