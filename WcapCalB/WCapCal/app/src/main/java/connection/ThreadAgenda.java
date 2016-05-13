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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/**
 * Classe ThreadAgenda implements Runnable
 * Permet de récupérer le fichier xml dans un buffer
 * @author WCap'Cal Company
 * @version 2.0
 */
public class ThreadAgenda implements Runnable{

	private ResponseHandler<String> responseHandler;
	private RecuperationDonnees recupDonnees;
	private String url;
	private String calid;
	
	/**
	 * Constructeur ThreadAgenda
	 * Thread de connection pour récupérer le contenu du fichier wcap.cal
	 * @param recupDonnees
	 * @param url: l'url où se trouve le fichier à récupérer.
	 * @param calid: l'identifiant du calendar
	 */
	public ThreadAgenda(RecuperationDonnees recupDonnees, String url, String calid) {
		this.makeResponseHandler();
		this.recupDonnees = recupDonnees;
		this.url = url;
		this.calid = calid;
	}
	
	/**
	* Methode makeResponseHandler
	* 
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
	 * Permet le lancement du thread
	 */
	@Override
	public void run() {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			
			Calendar calendar = Calendar.getInstance();
			String date = ""+calendar.get(Calendar.YEAR);
			if(calendar.get(Calendar.MONTH)+1 < 10){
				date = date+"0"+(calendar.get(Calendar.MONTH)+1);
			}
			else {
				date = date+(calendar.get(Calendar.MONTH)+1);
			}
			
			if(calendar.get(Calendar.DAY_OF_MONTH) < 10){
				date = date+"0"+(calendar.get(Calendar.DAY_OF_MONTH));
			}
			else{
				date = date+(calendar.get(Calendar.DAY_OF_MONTH));
			}
			
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("id", recupDonnees.getId()));
            formparams.add(new BasicNameValuePair("calid", this.calid));
            formparams.add(new BasicNameValuePair("dtstart", date));
            formparams.add(new BasicNameValuePair("content-out", "text/xml"));            
            
            HttpPost httppost = new HttpPost(url);
            
            httppost.setEntity(new UrlEncodedFormEntity(formparams));
            String responseBody = httpclient.execute(httppost, responseHandler);
            this.recupDonnees.setAgenda(responseBody);
            
        }
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        catch (ClientProtocolException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch(IllegalStateException e){
        	e.printStackTrace();
        }
    }
}
