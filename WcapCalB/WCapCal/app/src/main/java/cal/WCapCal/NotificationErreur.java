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


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Classe NotificationErreur
 * Permet l'affichage d'une notification quand il y a une erreur lors de la synchronisation automatique
 * @author WCap'Cal Company
 * @version 2.0
 */
public class NotificationErreur {

	private Context context;
	
	/**
	 * Consructeur de NotificationErreur
	 * @param context de l'application WCap'Cal
	 */
	public NotificationErreur(Context context){
		this.context = context;
	}
	
	/**
	 * Méthode createNotification
	 * Permet la création de la notification
	 * @param text de la notification
	 */
	public void createNotification(String text){
		//Création d'un NotificationManager
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//Création d'un intent pour réagir au clic sur la notification
		Intent notificationIntent = new Intent(context, Main.class);
	    PendingIntent notifAlreadyExists = PendingIntent.getActivity(context, 42, notificationIntent, PendingIntent.FLAG_NO_CREATE);
	    //S'il y a déjà une notification on la supprime
	    if(notifAlreadyExists != null){
	    	notificationManager.cancel(42);
	    }
	    Intent intent = new Intent(context, Main.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    
	    //On construit la notification avec les informations nécessaire
	    Builder notification = new Notification.Builder(context)
	    	.setWhen(System.currentTimeMillis())
			.setContentTitle("WCap'cal - Erreur")
			.setTicker("WCap'cal - Erreur - Synchronisation interrompue - Mauvaise saisie des informations de l'agenda")
			.setContentText(text)
			.setAutoCancel(true)
			.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
			.setSmallIcon(R.drawable.ic_launcher);
		//Si l'api est inférieur à 16
	    if(android.os.Build.VERSION.SDK_INT < 16){
			notificationManager.notify(42, notification.getNotification());
		}
	    //Sinon l'api est supérieur à 16
		else {
			notificationManager.notify(42, showNotificationForNewSDK(notification));
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	/**
	 * Méthode showNotifiationForNewSDK
	 * Permet d'afficher la notification pour les téléphone d'API supérieur à 16
	 * @param notification
	 * @return
	 */
	private Notification showNotificationForNewSDK(Builder notification){
		//Affichage de la notification pour les API supérieurs à 16
		return notification.build();
	}
}
