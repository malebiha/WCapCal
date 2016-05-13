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

//Liste des imports

import datasBases.AgendaList;
import datasBases.Parametre;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Classe Main extends Activity
 * Classe principale de l'application
 * Permet de démarrer l'application
 * @author WCap'Cal Company
 * @version 2.0
 */
public class Main extends Activity{

	private static Main instance;
	public boolean addAgenda = false;

	public static Main instance(){
		return instance;
	}
	
	/**
	 * @Override
	 * Méthode onCreate
	 * Appelé à l'ouverture de l'application
	 * Permet de définir la vue à afficher en première et l'initialisation des attributs
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parametre.instance(this);
		AgendaList.instance(this);
		Fenetre.instance(this);
		Agenda.instance(this);
		instance = this;
	}
	
	@Override
	/**
	 * Méthode onResume
	 * Appelé lors du retour au premier plan de WCap'Cal
	 */
	protected void onResume(){
		super.onResume();
		if(!addAgenda){
			Fenetre.instance().changeViewToAccueil();
		}
		else {
			Fenetre.instance().changeViewToParametre();
		}
		addAgenda = false;
	}
	
	/**
	 * @Override
	 * Méthode onCreateOptionsMenu
	 * Appelé à l'ouverture de l'application
	 * Permet la définition du menu de l'application
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.first_page, menu);
		return true;
	}
	
	/**
	 * Methode nouveauAgenda
	 * Réaction au clic sur l'image d'ajout d'un agenda
	 * Dirige vers la page d'ajout d'un agenda
	 * @param view
	 */
	public void nouveauAgenda(View view){
		Fenetre.instance().changeViewToAjouterAgenda();
	}
	
	/**
	 * Methode ajouterAgenda
	 * Réaction au clic sur le bouton ajouter
	 * Enregistre les paramtères du nouveau agenda puis dirige vers l'accueil
	 * @param view
	 */
	public void ajouterAgenda(View view){
		Agenda.instance().ajouterAgenda();
		//Lancement de la synchronisation
		ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                this.stopService(new Intent(this, MyService.class));
            }
        }
        if(MyService.checkParametersBeforeStart(this)){
        	this.startService(new Intent(this, MyService.class));
    	}
	}
	
	/**
	 * Méthode annulerAjout
	 * Réaction au clic sur le bouton annuler
	 * Annule l'ajout d'un calendrier et dirige vers l'accueil ou page 1ere connexion
	 * @param view
	 */
	public void annulerAjout(View view){
		Agenda.instance().setModification(-1);
		Fenetre.instance().changeViewToAccueil();
	}
	
	/**
	 * Methode changerParametre
	 * Permet de changer la vue vers les paramètres avancés
	 * @param item: l'item qui a été cliqué
	 */
	public void changerParametre(MenuItem item){
		Fenetre.instance().changeViewToParametre();
	}

	/**
	 * Methode voirInformation
	 * Permet de changer la vue vers les informations de l'application
	 * @param item: l'item qui a été cliqué
	 */
	public void voirInformation(MenuItem item){
		Fenetre.instance().changeViewToInformation();
	}
	
	/**
	 * @Override
	 * Methode onKeyDown
	 * Permet la gestion du clic sur le bouton back
	 * Permet le retour à l'accueil
	 */
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	int maView = Fenetre.instance().getView();
        	if(maView == R.layout.parametre){
        		Parametre.instance().modifierParametre();
        	}
        	if(maView == R.layout.accueil || maView == R.layout.firstpage ){
        		return super.onKeyDown(keyCode, event);
        	}
        	else {
        		Fenetre.instance().changeViewToAccueil();
        	}
        }
        return true;
    }
    
    /**
     * permet de récupérer la couleur sélectionnée par l'utilisateur
     * une fois que celui-ci a cliqué sur valider dans le ColorPicker
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 0) {
    		if (resultCode == 1) {
    			Bundle b = data.getExtras();
    			SharedPreferences settings = this.getSharedPreferences("preferencescreen.xml", 0);
    			SharedPreferences.Editor editor = settings.edit();
    			editor.putInt("color" , Color.rgb(b.getInt("red"), b.getInt("green"), b.getInt("blue")));
    			editor.commit();
    			addAgenda = true;
    		}
    	}
    }
}
