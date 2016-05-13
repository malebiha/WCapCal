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

import java.util.HashMap;
import java.util.List;

import connection.Connection;

import cal.WCapCal.Main;
import datasBases.AgendaList;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Classe AgendaListenerClick implements OnItemClickListener
 * Réaction au clic sur un agenda
 * @author WCap'Cal Company
 * @version 2.0
 */
public class AgendaListenerClick implements OnItemClickListener{

	private Main activity;
	private List<HashMap<String, String>> liste;

	/**
	 * Constructeur AgendaListenerClick
	 * Permet la construction du listener d'un clic court sur un agenda
	 * @param main 
	 * @param liste la liste des agendas enregistré dans wcap'cal
	 */
	public AgendaListenerClick(Main main, List<HashMap<String, String>> liste) {
		super();
		this.activity = main;
		this.liste = liste;
	}

	/**
	 * Methode onItemClick
	 * Permet la gestion d'un clic court sur un agenda
	 * Récupére les données et les ajoutent dans l'agenda du téléphone
	 * Avant de les afficher dans l'application
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
		String[][] agenda = AgendaList.instance().select(liste.get(position).get("text1"));
		Connection connection = new Connection(agenda);
		connection.makeConnection(this.activity, true);
	}
}
