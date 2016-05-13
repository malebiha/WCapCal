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

import cal.WCapCal.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * Classe OnSeekBarClickListener implements OnSeekBarChangeListener
 * Réaction au clic sur la bar du pourcentage de la batterie
 * Enregistre le nouveau pourcentrage de la batterie
 * Modifie l'affichage du pourcentage choisit avec le nouveau
 * @author WCap'Cal Company
 * @version 2.0
 */
public class OnSeekBarClickListener implements OnSeekBarChangeListener {

	private Activity activity;

	public OnSeekBarClickListener(Activity activity){
		this.activity = activity;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		TextView textview = (TextView) 	this.activity.findViewById(R.id.textView6);
		textview.setText(""+progress+" %");
		SharedPreferences settings = this.activity.getSharedPreferences("preferencescreen.xml", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("batterie", progress);
		editor.putBoolean("modificationSync" , true);
		editor.commit();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
