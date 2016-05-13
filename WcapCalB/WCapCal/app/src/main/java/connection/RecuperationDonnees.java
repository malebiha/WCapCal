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

import android.content.Context;

/**
 * Classe RecuperationDonnes
 * Permet de récupérer l'id et l'agenda
 * @author WCap'Cal Company
 * @version 2.0
 */
public class RecuperationDonnees {

	protected String id;
	protected String agenda;
	private Context context;
	

	
	/**
	 * Constructeur de la classe RecuperationDonnees
	 * @param context le contexte de l'application
	 * @param agenda
	 */
	public RecuperationDonnees(Context context) {
		this.context = context;
	}
	
	/**
	 * Methode setId
	 * Permet de modifier l'id de session
	 * @param id: nouveau id de session
	 */
	public void setId(String id){
		this.id = id;
	}
	
	/**
	 * Methode getId
	 * Permet de récupérer l'id de session
	 * @return String l'id de session
	 */
	public String getId(){
		return this.id;
	}
	
	/**
	 * Methode setAgenda
	 * Permet de modifier le nom de l'agenda
	 * @param agenda
	 */
	public void setAgenda(String agenda){
		this.agenda = agenda;
	}
	
	/**
	 * Methode getId
	 * Méthode de récupération d'un id de session à partir du fichier login.wcap
	 * @param url: l'url pour récupérer le fichier
	 * @return l'id de session
	 */
	public String getId(final String url){
		ThreadId threadId = new ThreadId(this, url, context);
		Thread th = new Thread(threadId);
		th.start();
		try {
			th.join(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while(this.id == null){
			if(this.id != null){
				return this.id;
			}
			try {
				th.join(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this.id;
	}
	
	/**
	 * Methode getAgenda
	 * Permet la récupération de l'agenda (du fichier wcap.cal) et mis dans un String pour le traitement
	 * @param url: l'url où se trouve le fichier calendar
	 * @param id: l'id de session
	 * @param calid: l'id du calendar
	 * @return String contenant toutes les informations sur le calendar
	 */
	public String getAgenda(final String url, final String calid){
		ThreadAgenda threadAgenda = new ThreadAgenda(this, url, calid);
		Thread th = new Thread(threadAgenda);
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this.agenda;
	}
}
