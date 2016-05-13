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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import datasBases.*;
import datasBases.ColorPicker.OnColorChangedListener;

/**
 * Classe Picker
 * Cette classe sert à choisir la couleur de l'agenda
 * @author WCap'Cal Company
 * @version 2.0
 */
public class Picker extends Activity implements OnColorChangedListener {
	
	private ColorPicker picker;
	private SVBar svBar;
	private Button button;
	
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.colorpicker);
		
		SharedPreferences settings = this.getSharedPreferences("preferencescreen.xml", 0);
		int color = settings.getInt("color", Color.RED);
		
		picker = (ColorPicker) findViewById(R.id.picker);
		svBar = (SVBar) findViewById(R.id.svbar);
		button = (Button) findViewById(R.id.button1);
		picker.setColor(color);
		picker.setOldCenterColor(color);
		picker.addSVBar(svBar);
		picker.setOnColorChangedListener(this);
		
		this.addOnClickListener();
	}
 
	@Override
	public void onColorChanged(int color) {
		//gives the color when it's changed.

	}
	
	public void addOnClickListener () {
			button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				picker.setOldCenterColor(picker.getColor());
				int blue = Color.blue(picker.getColor());
				int red = Color.red(picker.getColor());
				int green = Color.green(picker.getColor());
				
				Intent i = getIntent();
				Bundle b = new Bundle();
				b.putInt("blue", blue);
				b.putInt("red", red);
				b.putInt("green", green);
				i.putExtras(b);
				
				setResult(1,i); 
				finish();				
			}
		});
	}
	

}

