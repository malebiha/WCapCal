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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import parser.Parsing;

/**
 * Classe Connection
 * Permet la connection pour récupérer l'id et l'agenda 
 * @author WCap'Cal Company
 * @version 2.0
 */
public class Connection {
	
	//la liste des agendas
	private String[][] agenda;

	/**
	 * Constructeur de la classe Connection
	 * @param context le contexte de l'application
	 * @param agenda
	 */
	public Connection(String[][] agenda) {
		this.agenda = agenda;	
	}
	
	/**
	 * Methode makeConnection
	 * Permet de faire la connection avec l'agenda UBS pour récupérer le fichier agenda
	 * Lance l'ajout dans l'agenda du téléphone
	 */
	public void makeConnection(Context context, boolean affichage){
		//On crée l'url avec les données saisies lors de la création de l'agenda
		String url = agenda[0][2]+"/login.wcap?user="+agenda[0][3]+"&password="+agenda[0][4];
		RecuperationDonnees recuperation = new RecuperationDonnees(context);
		String id = "erreur";
		boolean percent = true;
		//On recharge un id tant qu'il y a le caractère '%' dedans
		//car lors du passage dans l'url ce caractère est interprété
		while(percent){
			//appel de la méthode de récupération de l'id
			id = recuperation.getId(url);
			//on vérifi qu'il n'y a pas de caractère '%'
			Pattern p = Pattern.compile("%", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(id);
			percent = m.find();
		}
		if (!id.equals("erreur")){
			//S'il n'y a pas d'erreur on lance la récupération de l'agenda et on le parse
			url = agenda[0][2]+"/export.wcap";
			String calid = agenda[0][3];
			String agenda = recuperation.getAgenda(url, calid);
			Parsing parsing = new Parsing(context.getContentResolver(), affichage);
			parsing.parser(agenda, this.agenda[0][1], context);
		}		
	}
	
}
