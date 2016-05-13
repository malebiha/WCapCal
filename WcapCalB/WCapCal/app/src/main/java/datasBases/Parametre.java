/**
 * WCap'cal
 * Développé par Simon BRUNOU, François DUCLOS, Quentin GUILLOU et Mehdi SABIR
 * © 2014 - WCap'Cal Company
 * L'usage d'une partie du code demande la citation des auteurs de l'application
 * Application de synchronisation d'agenda SUN Java Calendar sur téléphone android
 * Réalisé dans le cadre du projet de synthèse 2013-2014 de l'IUT de Vannes
 * Par des étudiants en DUT informatique de deuxième année 
 */

package datasBases;

import listeners.OnCheckClickListener;
import listeners.OnNumberPickerClickListener;
import listeners.OnSeekBarClickListener;
import listeners.SwitchListener;
import cal.WCapCal.Fenetre;
import cal.WCapCal.Main;
import cal.WCapCal.MyService;
import cal.WCapCal.R;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Classe Paramètre
 * Permet d'enregistrer et de récupérer les paramètres
 * @author WCap'Cal Company
 * @version 2.0
 */
public class Parametre  {
		
	private static Parametre instance;
	private Main activity;
	private static final String PREFS_NAME = "preferencescreen.xml";
	
	/**
	 * Constructeur de la classe Parametre
	 * @param activity l'activité correspondante
	 */
	private Parametre(final Main activity){
		this.activity = activity;
	}
	
	/**
	 * Implémentation du pattern Singleton
	 * Vérifie qu'il n'y a qu'une seule instance de Parametre
	 * @param main
	 * @return instance l'instance de Parametre
	 */
	public static Parametre instance(Main main){
		if(instance == null){
			instance = new Parametre(main);
		}
		return instance;
	}
	
	/**
	 * Permet de vérifier l'instance de Parametre
	 * @return l'instance de Parametre
	 */
	public static Parametre instance(){
		return instance;
	}
	
	/**
	 * Permet de configurer les vues des paramètres
	 */
	public void configureParametreView(){
		this.configureNumberPicker();
				
		SharedPreferences settings = this.activity.getSharedPreferences(PREFS_NAME, 0);
		
		Switch syncAuto = (Switch) this.activity.findViewById(R.id.switch1);
		syncAuto.setOnCheckedChangeListener(new SwitchListener(this.activity));
		syncAuto.setChecked(settings.getBoolean("switch1", true));
		
		NumberPicker np = (NumberPicker) this.activity.findViewById(R.id.numberPicker1);
		np.setValue(settings.getInt("numberpicker1", 1));
		np.setOnValueChangedListener(new OnNumberPickerClickListener(this.activity, "numberpicker1"));
		np = (NumberPicker) this.activity.findViewById(R.id.numberPicker2);
		np.setValue(settings.getInt("numberpicker2", 0));
		np.setOnValueChangedListener(new OnNumberPickerClickListener(this.activity, "numberpicker2"));
		
		CheckBox cb = (CheckBox) this.activity.findViewById(R.id.checkBox1);
		cb.setChecked(settings.getBoolean("checkbox1", false));
		cb.setOnCheckedChangeListener(new OnCheckClickListener(this.activity, "checkbox1"));
		cb = (CheckBox) this.activity.findViewById(R.id.checkBox2);
		cb.setChecked(settings.getBoolean("checkbox2", true));
		cb.setOnCheckedChangeListener(new OnCheckClickListener(this.activity, "checkbox2"));
		
		EditText edittext = (EditText) this.activity.findViewById(R.id.EditText4);
		if(!AgendaList.instance().isEmpty()){
			String name = settings.getString("agendaName", AgendaList.instance().getAgendaName());
			edittext.setText(name);
		}
		edittext.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		
		SeekBar seekBar = (SeekBar) this.activity.findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarClickListener(this.activity));
		int batterie = settings.getInt("batterie", 0);
		seekBar.setProgress(batterie);
		TextView textview = (TextView) this.activity.findViewById(R.id.textView6);
		textview.setText(""+batterie+" %");
		
		//Bouton pour choisir la couleur de l'agenda
		Button boutonColor = (Button) this.activity.findViewById(R.id.buttoncolor);
		OnClickListener buttonListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Fenetre.instance().changeViewToColorPicker(); 
			}
		};
		boutonColor.setOnClickListener(buttonListener);
		
		//Bouton qui fait apparaitre "l'aide"
		ImageButton boutonAide = (ImageButton) this.activity.findViewById(R.id.imageButton1);
		OnClickListener buttonListenerAide = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder adb = new AlertDialog.Builder(activity);
	       	 	adb.setTitle("Aide");
	       	 	adb.setMessage("Cette fonctionnalité vous permet de régler à partir de quel moment vous voulez que l'application ne synchronise plus pour sauver de la batterie : \n"+
	       	 					"Mettez 10% et l'application s'arretera quand votre batterie sera à 10%. \n"+
	       	 					"(Mettez 0% pour ne pas utiliser cette fonctionnalité).");
	       	 	adb.show();
			}
		};
		boutonAide.setOnClickListener(buttonListenerAide);
		
		
	}
	
	/**
	 * Permet de modifier les paramètres
	 */
	public void modifierParametre(){
		this.enregistrerParametre();
	}

	/**
	 * Enregistre les paramètres
	 */
	private void enregistrerParametre() {
		SharedPreferences settings = this.activity.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		EditText edittext = (EditText) this.activity.findViewById(R.id.EditText4);
		if(edittext.getText().length() == 0){
			try {
				editor.putString("agendaName",  AgendaList.instance().getAgendaName());
			}
			catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}
		}
		else {
			editor.putString("agendaName", ""+edittext.getText());
		}
		editor.commit();
		if(settings.getBoolean("modificationSync", false)){
			editor.putBoolean("modificationSync" , false);
			editor.commit();
			ActivityManager manager = (ActivityManager) this.activity.getSystemService(Context.ACTIVITY_SERVICE);
	        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	            if (MyService.class.getName().equals(service.service.getClassName())) {
	                this.activity.stopService(new Intent(this.activity, MyService.class));
	            }
	        }
	        if(MyService.checkParametersBeforeStart(this.activity)){
	    		this.activity.startService(new Intent(this.activity, MyService.class));
	    	}
		}		
	}

	/**
	 * Permet de configurer le NumberPicker
	 */
	public void configureNumberPicker() {
		NumberPicker np = (NumberPicker) this.activity.findViewById(R.id.numberPicker1);
		NumberPicker np2 = (NumberPicker) this.activity.findViewById(R.id.numberPicker2);
		String[] nums = new String[24];
	    for(int i=0; i<nums.length; i++)
	           nums[i] = Integer.toString(i);
		np.setMinValue(1);
	    np.setMaxValue(24);
	    np.setWrapSelectorWheel(true);
	    np.setDisplayedValues(nums);
	    np.setValue(1);
	    
	    nums = new String[60];
	    for(int i=0; i<nums.length; i++)
	           nums[i] = Integer.toString(i);
		np2.setMinValue(1);
	    np2.setMaxValue(60);
	    np2.setWrapSelectorWheel(true);
	    np2.setDisplayedValues(nums);
	    np2.setValue(1);
	}
	
	
}
