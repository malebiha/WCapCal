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
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

/**
 * Classe OnNumberPickerClickListener
 * Réaction au clic sur les number pickers
 * @author WCap'Cal Company
 * @version 2.0
 */
public class OnNumberPickerClickListener implements OnValueChangeListener{

	private String name;
	private Activity activity;

	/**
	 * COnstructeur de OnNumberClickListener
	 * @param activity
	 * @param name nom du numberPicker
	 */
	public OnNumberPickerClickListener(Activity activity, String name){
		this.name = name;
		this.activity = activity;
	}
	
	@Override
	/**
	 * Méthode onValueChange
	 * Permet la réaction au changement d'état de number picker
	 * enregistre les nouveaux paramètres
	 */
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		SharedPreferences settings = this.activity.getSharedPreferences("preferencescreen.xml", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(name , newVal);
		editor.putBoolean("modificationSync" , true);
		editor.commit();
	}

}
