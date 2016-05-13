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



import android.content.Intent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import cal.WCapCal.Main;
import cal.WCapCal.R;
import datasBases.AgendaList;
import datasBases.Parametre;

/**
 * Classe Fenetre
 * Permet le changement de view
 * Implemente le pattern Singleton
 * @author WCap'Cal Company
 * @version 2.0
 */
public class Fenetre {

	//Instance de Fenetre 
	private static Fenetre instance;
	//id de la view actuel
	private int view;
	//lien sur l'acitivity
	private Main activity;

	/**
	 * Constructeur de la classe Fenetre
	 * @param activity l'activité principale
	 */
	private Fenetre(Main activity){
		this.activity = activity;
	}

	/**
	 * Implémentation du pattern Singleton
	 * Permet de vérifier qu'il n'y a qu'une seule instance de Fenetre
	 * @param main
	 * @return l'instance de Fenetre
	 */
	public static Fenetre instance(Main main){
		if(instance == null){
			instance = new Fenetre(main);
		}
		return instance;
	}

	/**
	 * Permet d'obtenir l'instance actuelle de Fenetre
	 * @return l'instance actuelle de Fenetre
	 */
	public static Fenetre instance(){
		return instance;
	}

	/**
	 * Méthode pour obtenir la vue actuelle
	 * @return this.view la vue actuelle
	 */
	public int getView(){
		return this.view;
	}

	/**
	 * Cette méthode sert à changer la vue pour
	 * la basculer sur la fenetre d'accueil.
	 */
	public void changeViewToAccueil(){
		if(!AgendaList.instance().isEmpty()){
			this.activity.setContentView(R.layout.accueil);
			this.view = R.layout.accueil;
			Agenda.instance().voirAgenda();
		}
		else {
			this.activity.setContentView(R.layout.firstpage);
			this.view = R.layout.firstpage;
		}
	}

	/**
	 * Cette méthode sert à changer la vue pour
	 * la basculer sur la fenetre des paramètres.
	 */
	public void changeViewToParametre(){
		this.activity.setContentView(R.layout.parametre);
		this.view = R.layout.parametre;
		Parametre.instance().configureParametreView();
		Parametre.instance().configureParametreView();


	}

	/**
	 * Cette méthode sert à changer la vue pour
	 * la basculer sur la fenetre d'ajout d'un agenda.
	 */
	public void changeViewToAjouterAgenda(){
		this.activity.setContentView(R.layout.addcalendar);
		this.view = R.layout.addcalendar;
		this.addListenerOnEditText();
	}

	/**
	 * Cette méthode sert à changer la vue pour
	 * la basculer sur la fenetre d'informations de l'application.
	 */
	public void changeViewToInformation(){
		this.activity.setContentView(R.layout.information);
		this.view = R.layout.information;
	}

	/**
	 * Cette méthode sert à démarrer l'activité du sélecteur de couleurs.
	 */
	public void changeViewToColorPicker(){
		Intent untante = new Intent(this.activity, Picker.class);
		this.activity.startActivityForResult(untante, 0);
	}

	/**
	 * Cette méthode sert à changer la vue pour
	 * la basculer sur la fenetre des événements de l'agenda
	 */
	public void changeViewToEvenement(){
		this.activity.setContentView(R.layout.evenements);
		this.view = R.layout.evenements;
	}

	/**
	 *
	 */
	public void addListenerOnEditText(){

		//Verification que la case "Adresse" n'est pas vide
		final EditText editText = (EditText) this.activity.findViewById(R.id.editText1);
		editText.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View view, boolean bool) {
				if( editText.getText().toString().trim().equalsIgnoreCase("")){
					editText.setError("L'adresse ne doit pas être vide");
				}
			}

		});

		//Verification que la case "Identifiant" n'est pas vide
		final EditText editText2 = (EditText) this.activity.findViewById(R.id.EditText2);
		editText2.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View view, boolean bool) {
				if( editText2.getText().toString().trim().equalsIgnoreCase("")){
					editText2.setError("L'identifiant ne doit pas être vide");
				}
			}

		});

		//Verification que la case "Mot de passe" n'est pas vide
		final EditText editText3 = (EditText) this.activity.findViewById(R.id.editText3);
		editText3.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View view, boolean bool) {
				if( editText3.getText().toString().trim().equalsIgnoreCase("")){
					editText3.setError("Le mot de passe ne doit pas être vide");
				}
			}

		});
	}


}
