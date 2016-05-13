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

import cal.WCapCal.Agenda;
import cal.WCapCal.Fenetre;
import cal.WCapCal.Main;
import cal.WCapCal.MyService;
import cal.WCapCal.R;
import datasBases.AgendaList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Classe AgendaListenerLongCLick implements nItemLongClickListener
 * Réaction au clic long sur un agenda
 * Pour le supprimer ou le modifier
 * @author WCap'Cal Company
 * @version 2.0
 */
public class AgendaListenerLongClick implements OnItemLongClickListener {

	private Main activity;
	private List<HashMap<String, String>> liste;

	/**
	 * Constructeur de la classe AgendaListener
	 * @param main
	 * @param liste
	 */
	public AgendaListenerLongClick(Main main, List<HashMap<String, String>> liste) {
		super();
		this.activity = main;
		this.liste = liste;
	}

	/**
	 * Permet de gérer la réaction au clic sur un Agenda
	 * @return 
	 */
	public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id){
		//Affichage du sous menu
		AlertDialog.Builder adb = new AlertDialog.Builder(this.activity);
		//on attribut un titre à notre boite de dialogue
		final String titre = liste.get(position).get("text1");
		String url = liste.get(position).get("text2");
		adb.setTitle(titre);
		adb.setMessage(url);
		//on indique que l'on veut le bouton ok à notre boite de dialogue
		adb.setNegativeButton("Modifier", new OnClickListener(){

			@Override
			/**
			 * Réaction au clic sur le bouton Modifier
			 */
			public void onClick(DialogInterface dialog, int which) {
				Fenetre.instance().changeViewToAjouterAgenda();

				EditText editTextURL = (EditText) activity.findViewById(R.id.editText1);
				EditText editTextIdentifiant = (EditText) activity.findViewById(R.id.EditText2);
				EditText editTextPassword = (EditText) activity.findViewById(R.id.editText3);
				//EditText editTextNom = (EditText) activity.findViewById(R.id.EditText4);

				String[][] liste = AgendaList.instance().select(titre);

				editTextURL.setText(liste[0][2]);
				editTextIdentifiant.setText(liste[0][3]);
				editTextPassword.setText(liste[0][4]);
				//editTextNom.setText(liste[0][1]);

				Agenda.instance().setModification(Integer.parseInt(liste[0][0]));

			}
			
		});

		adb.setPositiveButton("Supprimer", new OnClickListener(){

			@Override
			/**
			 * Réaction au clic sur le bouton Supprimer
			 */
			public void onClick(DialogInterface dialog, int which) {
				AgendaList.instance().removeAgenda(liste.get(position).get("text1"), liste.get(position).get("text2"));
				Toast.makeText(activity.getApplicationContext(), "L'agenda "+liste.get(position).get("text1")+" a été supprimé", Toast.LENGTH_SHORT).show();
				Fenetre.instance().changeViewToAccueil();
				String[][] listAgenda = AgendaList.instance().getAgenda();
		    	if(listAgenda.length == 0){
		    		activity.stopService(new Intent(activity, MyService.class));
		    	}
			}

		});
		
		//on affiche la boite de dialogue
		adb.show();
		
		return true;
	}

}
