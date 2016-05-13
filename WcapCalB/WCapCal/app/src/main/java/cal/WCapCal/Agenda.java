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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import listeners.AgendaListenerClick;
import listeners.AgendaListenerLongClick;
import datasBases.AgendaList;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Classe Agenda
 * Permet la gestion des agendas dans WCap'Cal
 * @author WCap'Cal Company
 * @version 2.0
 */
public class Agenda {
	
	private static Agenda instance;
	
	private Main activity;
	public static final String MODIF_NAME = "modification.xml";
	private String nomAgenda;
	
	/**
	 * Constructeur de la classe Agenda.
	 * @param activity l'activité principale
	 */
	private Agenda(Main activity){
		this.activity = activity;
	}
	
	/**
	 * Implémentation du pattern Singleton
	 * Permet de vérifier qu'il n'y a qu'une seule instance d'Agenda
	 * @param main
	 * @return l'insctance d'Agenda
	 */
	public static Agenda instance(Main main){
		if(instance == null){
			instance = new Agenda(main);
		}
		return instance;
	}
	
	/**
	 * Permet de récupérer l'instance courante d'Agenda
	 * @return
	 */
	public static Agenda instance(){
		return instance;
	}
	
	/**
	 * Méthode pour ajouter un écouteur sur un Agenda
	 * @param liste
	 */
	public void ajouterListenerOnAgenda(final List<HashMap<String, String>> liste){
		ListView listAgenda = (ListView) this.activity.findViewById(R.id.evenements);
		listAgenda.setOnItemLongClickListener(new AgendaListenerLongClick(this.activity, liste));
		listAgenda.setOnItemClickListener(new AgendaListenerClick(this.activity, liste));
	}
	
	/**
	 * Méthode servant à effectuer des modifications
	 * @param id l'id des modifications
	 */
	public void setModification(int id){
		SharedPreferences settings = this.activity.getSharedPreferences(MODIF_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("modification", id);
		editor.commit();
	}
	
	/**
	 * Methode servant à récupérer les modifications effectuées
	 * @return l'id des modifications
	 */
	public int getModification(){
		SharedPreferences settings = this.activity.getSharedPreferences(MODIF_NAME, 0);
		return settings.getInt("modification", -1);
	}
	
	/**
	 * Methode permettant d'afficher la liste des Agendas enregistrés
	 */
	public void voirAgenda(){
		ListView listAgenda = (ListView) this.activity.findViewById(R.id.evenements);
		ArrayList<String> items = new ArrayList<String>();
		ArrayList<String> subItems = new ArrayList<String>();
		items.add(""+AgendaList.instance().getNbAgenda());
		subItems.add("");
		String[][] agenda = AgendaList.instance().getAgenda();
		
		final List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
	    HashMap<String, String> element;
	     
		for(int i = 0;i < agenda.length; i++){
			element = new HashMap<String, String>();
			
			element.put("text1", agenda[i][1]);
			element.put("text2", agenda[i][2]);
			
			
			liste.add(element);
		}
		ListAdapter adapter = new SimpleAdapter(this.activity, liste, android.R.layout.simple_list_item_2,new String[] {"text1", "text2"}, new int[] {android.R.id.text1, android.R.id.text2 });  
		
		listAgenda.setAdapter(adapter);
		this.ajouterListenerOnAgenda(liste);
	}
	
	/**
	 * Permet d'ajouter un Agenda lors du clic sur le bouton Ajouter
	 */
	public void ajouterAgenda(){
		
		
		EditText textUrl = (EditText)  this.activity.findViewById(R.id.editText1);
		String url = textUrl.getText().toString();
		EditText textIdentifiant = (EditText)  this.activity.findViewById(R.id.EditText2);
		String identifiant = textIdentifiant.getText().toString();
		EditText textPassword = (EditText)  this.activity.findViewById(R.id.editText3);
		String password = textPassword.getText().toString();
		
		String name = textIdentifiant.getText().toString();
		this.nomAgenda = name;
		
		if(!url.equals("") && !identifiant.equals("") && !password.equals("")){
			if(Agenda.instance().getModification() != -1){
				AgendaList.instance().updateAgenda(Agenda.instance().getModification(), name, url, identifiant, password);
				Agenda.instance().setModification(-1);
			}
			else {
				AgendaList.instance().insertAgenda(name, url, identifiant, password);
			}
			Fenetre.instance().changeViewToAccueil();
			this.voirAgenda();
		}
		else {
			AlertDialog.Builder adb = new AlertDialog.Builder(this.activity);
       	 	adb.setTitle("Erreur");
       	 	adb.setMessage("Veuillez remplir les champs obligatoires (URL, identifiant et mot de passe).");
       	 	adb.show();
		}
	}
	
	/**
	 * Accesseur nom agenda
	 */
	public String getNameAgenda () {
		return this.nomAgenda;
	}
	
}
