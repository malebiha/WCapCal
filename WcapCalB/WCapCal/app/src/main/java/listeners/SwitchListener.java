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


import cal.WCapCal.Main;
import cal.WCapCal.R;
import android.content.SharedPreferences;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Classe SwitchListener implements OnCheckedChangeListener
 * Réaction au changement d'état du switch
 * Enregistre les nouveaux paramètres
 * @author WCap'Cal Company
 * @version 2.0
 */
public class SwitchListener implements OnCheckedChangeListener{
	
	private Main activity;
	
	/**
	 * Constructeur de la classe SwitchListener
	 * @param main
	 */
	public SwitchListener(Main main){
		super();
		this.activity = main;
	}

	@Override
	/**
	 * Override la méthode onCheckedChanged de l'interface onCheckedChangeListener
	 * S'occupe de l'apparition du NumberPicker en fonction de la position du Switch
	 * Si le Switch est sur "Checked", le NumberPicker apparaitra
	 * Sinon il sera invisible
	 */
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		//On affiche ou pas plus certain paramètres avancés car liés à la synchronisation automatique
		TextView textView = (TextView) activity.findViewById(R.id.textView3);
		TextView textView2 = (TextView) activity.findViewById(R.id.textView4);
		TextView textView3 = (TextView) activity.findViewById(R.id.textView5);
		TextView textView4 = (TextView) activity.findViewById(R.id.textView6);
		NumberPicker numberPicker = (NumberPicker) activity.findViewById(R.id.numberPicker1);
		NumberPicker numberPicker2 = (NumberPicker) activity.findViewById(R.id.numberPicker2);
		CheckBox checkbox = (CheckBox) activity.findViewById(R.id.checkBox1);
		CheckBox checkbox2 = (CheckBox) activity.findViewById(R.id.checkBox2);
		SeekBar seekBar = (SeekBar) activity.findViewById(R.id.seekBar1);
		ImageButton imageButton = (ImageButton) activity.findViewById(R.id.imageButton1);
		if(!isChecked){
			//On n'affiche pas les champs inutile quand pas de synchronisation automatique
			textView.setVisibility(4);
			numberPicker2.setVisibility(4);
			numberPicker.setVisibility(4);
			textView2.setVisibility(4);
			textView3.setVisibility(4);
			textView4.setVisibility(4);
			checkbox.setVisibility(4);
			checkbox2.setVisibility(4);
			seekBar.setVisibility(4);
			imageButton.setVisibility(4);
		}
		else {
			//On affiche les champs inutile quand synchronisation automatique
			textView.setVisibility(0);
			numberPicker2.setVisibility(0);
			numberPicker.setVisibility(0);
			textView2.setVisibility(0);
			textView3.setVisibility(0);
			textView4.setVisibility(0);
			checkbox.setVisibility(0);
			checkbox2.setVisibility(0);
			seekBar.setVisibility(0);
			imageButton.setVisibility(0);
		}
		SharedPreferences settings = this.activity.getSharedPreferences("preferencescreen.xml", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("switch1", isChecked);
		editor.putBoolean("modificationSync" , true);
		editor.commit();
	}

}
