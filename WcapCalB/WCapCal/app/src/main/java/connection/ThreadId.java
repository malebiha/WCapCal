/**
 * WCap'cal
 * Développé par Simon BRUNOU, François DUCLOS, Quentin GUILLOU et Mehdi SABIR
 * © 2014 - WCap'Cal Company
 * L'usage d'une partie du code demande la citation des auteurs de l'application
 * Application de synchronisation d'agenda SUN Java Calendar sur téléphone android
 * Réalisé dans le cadre du projet de synthèse 2013-2014 de l'IUT de Vannes
 * Par des étudiants en DUT informatique de deuxième année 
 */

package connection;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import cal.WCapCal.MyService;
import cal.WCapCal.NotificationErreur;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.WindowManager.BadTokenException;

/**
 * Classe ThreadId implements Runnable
 * Permet de récupérer l'id de session sur sun java calendar
 * @author WCap'Cal Company
 * @version 2.0
 */
public class ThreadId implements Runnable{
	
	private ResponseHandler<String> responseHandler;
	private RecuperationDonnees recupDonnees;
	private String url;
	private Context context;

	/**
	 * Constructeur ThreadId
	 * Thread permettant la récupération de l'identifiant de session
	 * @param activity
	 * @param recupDonnees
	 * @param url: l'url où se trouve le fichie contenant l'id de session
	 */
	public ThreadId(RecuperationDonnees recupDonnees, String url, Context context) {
		this.makeResponseHandler();
		this.recupDonnees = recupDonnees;
		this.url = url;
		this.context = context;
	}
	
	/**
	 * Methode makeResponseHandler
	 */
	private void makeResponseHandler(){
		this.responseHandler = new ResponseHandler<String>() {
            public String handleResponse(final HttpResponse response) {
            	try {
            		int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    }
            	}
            	catch(ClientProtocolException e){
                		
            	}
            	catch(IOException e){
	                		
            	}
            	return null;
            }

        };
        
	}

	/**
	 * Methode run
	 * Permet le lancement du thread de récupération de l'id
	 */
	@Override
	public void run(){
		Looper.prepare();
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(this.url);
	    
        try {
        	String responseBody = httpclient.execute(httpget, responseHandler);
        	int start = responseBody.lastIndexOf("X-NSCP-WCAP-SESSION-ID:");
            int stop = responseBody.lastIndexOf("X-NSCP-WCAP-USER-ID:");
            String idLine = (String) responseBody.subSequence(start, stop);
            String[] idTab = idLine.split(":");
            this.recupDonnees.setId(idTab[1]);
        } catch(ClientProtocolException e){
        	System.out.println(e.getMessage());
        } catch(IOException e){
        	System.out.println(e.getMessage());
        } catch(IllegalStateException e){
        	this.recupDonnees.setId("erreur");
        	AlertDialog.Builder adb = new AlertDialog.Builder(context);
       	 	adb.setTitle("Erreur");
       	 	adb.setMessage("Veuillez rentrer une adresse valide.");
       	 	try {
    	 		adb.show();
    	 	}
    	 	catch(BadTokenException b){
    	 		this.recupDonnees.setId("erreur");
    	 		context.stopService(new Intent(context, MyService.class));
    	 		//Il s'agit de la synchronisation auto. on affiche une notification
    	 		NotificationErreur notification = new NotificationErreur(context);
       	 		notification.createNotification("L'adresse (URL) est invalide.\nImpossible de synchroniser les agendas"); 	       	    			
    	 	}
        } catch(StringIndexOutOfBoundsException e){
        	this.recupDonnees.setId("erreur");
        	AlertDialog.Builder adb = new AlertDialog.Builder(context);
       	 	adb.setTitle("Erreur");
       	 	adb.setMessage("Veuillez rentrer un mot de passe et identifiant valide.");
       	 	try {
       	 		adb.show();
       	 	}
       	 	catch(BadTokenException b){
       	 		this.recupDonnees.setId("erreur");
       	 		context.stopService(new Intent(context, MyService.class));
       	 		//Il s'agit de la synchronisation auto. on affiche une notification
       	 		NotificationErreur notification = new NotificationErreur(context);
       	 		notification.createNotification("Le mot de passe ou l'identifiant est invalide.\nImpossible de synchroniser les agendas");
       	 	}
        } 
        Looper.loop();
    }

}
