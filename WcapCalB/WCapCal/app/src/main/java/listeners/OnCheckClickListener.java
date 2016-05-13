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

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Classe OnCheckClickListener implements OnCheckedChangeListener
 * Permet la réaction au changement de l'état de la connection
 * @author WCap'Cal Company
 * @version 2.0
 */
public class OnCheckClickListener implements OnCheckedChangeListener{
	
	private Activity activity;
	private String name;
	
	/**
	 * Constructeur de OnCheckClickListener
	 * @param activity
	 * @param name reseau ou wifi
	 */
	public OnCheckClickListener(Activity activity, String name){
		this.activity = activity;
		this.name = name;
	}

	@Override
	/**
	 * Méthode onCheckedChanged
	 * Réaction au changement
	 */
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		SharedPreferences settings = this.activity.getSharedPreferences("preferencescreen.xml", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(name, isChecked);
		editor.putBoolean("modificationSync" , true);
		editor.commit();
	}

}
