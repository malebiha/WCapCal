/**
 * WCap'cal
 * Développé par Simon BRUNOU, François DUCLOS, Quentin GUILLOU et Mehdi SABIR
 * © 2014 - WCap'Cal Company
 * L'usage d'une partie du code demande la citation des auteurs de l'application
 * Application de synchronisation d'agenda SUN Java Calendar sur téléphone android
 * Réalisé dans le cadre du projet de synthèse 2013-2014 de l'IUT de Vannes
 * Par des étudiants en DUT informatique de deuxième année 
 */

package parser;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.util.Patterns;
import android.view.WindowManager.BadTokenException;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import cal.WCapCal.Fenetre;
import cal.WCapCal.Main;
import cal.WCapCal.NotificationErreur;
import cal.WCapCal.R;
import datasBases.AgendaList;

import android.util.Log;

/**
 * Classe Parsing
 * Cette classe permet la lecture du fichier xml
 * pour permettre l'ajout à l'agenda du téléphone
 * Exécuté après récéption du fichier xml
 * @author WCap'cal company
 * @version 2.0
 */
public class Parsing {
	//lien sur le contentResolver de WCap'cal
	private ContentResolver contentResolver;
	//l'id de l'agena sur le téléphone
	private long calId;
	//le nombre d'événement synchronisé
	private int nbEvent;
	//Affichage = true si les évènements doivent être affichés dans l'application
	//Quand le lancement de parsing est du à un clic sur l'agenda à synchronisé
	private boolean affichage;
	

	/**
	 * Parsing
	 * Constructeur de Parsing, initialise les attributs
	 * @param activity
	 */
	public Parsing(ContentResolver contentResolver, boolean affichage) {
		this.contentResolver = contentResolver;
		this.affichage = affichage;
	}
	
	/**
	 * Méthode parser
	 * permet de parser le fichier de l'agenda, pour l'ajouter dans google agenda
	 * @param agenda le contenu du fichier à parser
	 */
	public void parser(String agenda, String nomAgenda, Context context){
		//Récupération de l'id de l'agenda du téléphone et création s'il n'existe pas
		this.calId = this.getCalendarId(nomAgenda, context);
		
		ListView listeEvenement = null;
		if(affichage){
			//On change de vue vers la liste des évènements synchronisés.
			Fenetre.instance().changeViewToEvenement();
			//On récupére la liste où les évènements seront ajoutés.
			listeEvenement = (ListView) Main.instance().findViewById(R.id.evenements);
		}
		
		ArrayList<String> items = new ArrayList<String>();
		ArrayList<String> subItems = new ArrayList<String>();
		items.add(""+AgendaList.instance().getNbAgenda());
		subItems.add("");
				
		final List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
	    HashMap<String, String> elementHashMap;
	    
		try {
			//DocumentBuilder et Document permettent la lecture du fichier XML
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = parser.parse(new InputSource(new StringReader(agenda)));
			
			//On récupére le contenu de la balise X-NSCP-WCAP-ERRNO 0: il y a pas d'erreur sinon différent de 
			NodeList erreur = (NodeList)document.getElementsByTagName("X-NSCP-WCAP-ERRNO");
			String error = erreur.item(0).getFirstChild().getTextContent();
			if(error.equals("1")){
				//On stop l'éxécution de la méthode car il y a une erreur dans le fichier
				return;
			}
			
			NodeList nom = (NodeList)document.getElementsByTagName("EVENT");
			
			//On supprime tous les évènements de l'agenda
			this.deleteAgenda(nomAgenda);
			//On parcourt le fichier tant qu'il y a des balises EVENT à lire
			for(int i = 0; i < nom.getLength(); i++){
				try {
					Element element = (Element)nom.item(i);
					elementHashMap = new HashMap<String, String>();
					//Lecture de la balise SUMMARY = titre de l'évènement
					NodeList n = element.getElementsByTagName("SUMMARY");
					Node child = n.item(0).getFirstChild();

					String titre = child.getTextContent();
					titre = this.replace(titre);

					elementHashMap.put("text1",  titre);
					//Décalage par défaut de '1' car GMT+1
					int decalage = 0; // Version originale 1
					try {
						//Lecture de la balise X-NSCP-DTSTART-TZID = heure d'hiver si présent
						n = element.getElementsByTagName("X-NSCP-DTSTART-TZID");
						child = n.item(0).getFirstChild();
					}catch(NullPointerException e){
						//L'évènement a lieu pendant l'été (en période d'heure d'été)
						//On rajoute un décalage d'une heure supplémentaire 
						decalage++;
					}
					decalage=1;
					//On récupére les informations de la balise START
					//date de début de l'événement au format (YYYYmmddTHHMMSS)
					n = element.getElementsByTagName("START");
					child = n.item(0).getFirstChild();

					try {
						//On créer un nouveau Calendar avec la date de début de l'évènement
						Calendar calStart = new GregorianCalendar();
						SimpleDateFormat  format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'"); 
						Date date = format.parse(child.getTextContent());


						calStart.setTime(date);
						calStart.set(Calendar.HOUR_OF_DAY, calStart.get(Calendar.HOUR_OF_DAY)+decalage);

						String minute = Integer.toString(calStart.get(Calendar.MINUTE));
						if(minute.length() == 1){
							minute = "0"+minute;
						}

						String information = "Du "+calStart.get(Calendar.DAY_OF_MONTH)+"/"+(calStart.get(Calendar.MONTH)+1)+"/"+calStart.get(Calendar.YEAR);
						information = information+" à "+calStart.get(Calendar.HOUR_OF_DAY)+":"+minute;
						
						//On récupére les informations de la balise END
						n = element.getElementsByTagName("END");
						child = n.item(0).getFirstChild();

						//On créer un nouveau Calendar avec la date de début de l'évènement
						Calendar calEnd = new GregorianCalendar();
						date = format.parse(child.getTextContent());
						calEnd.setTime(date);
						calEnd.set(Calendar.HOUR_OF_DAY, calEnd.get(Calendar.HOUR_OF_DAY)+decalage);

						minute = Integer.toString(calEnd.get(Calendar.MINUTE));
						if(minute.length() == 1){
							//pour éviter d'avoir 8:0 comme heure pour 8:00
							minute = "0"+minute;
						}

						information = information+" au "+calEnd.get(Calendar.DAY_OF_MONTH)+"/"+(calEnd.get(Calendar.MONTH)+1)+"/"+calEnd.get(Calendar.YEAR);
						information = information+" à "+calEnd.get(Calendar.HOUR_OF_DAY)+":"+minute;
						String lieu = "";
						try {
							//On récupére les informations de la balise LOCATION
							//Certains évènements n'ont pas les informations LOCATION d'où le catch 
							n = element.getElementsByTagName("LOCATION");
							child = n.item(0).getFirstChild();
							information = information+"\n"+child.getTextContent();
							lieu = child.getTextContent();
						}
						catch(NullPointerException e){
							e.printStackTrace();
						}
						finally {
							elementHashMap.put("text2", information);
							//On ajoute l'évènement dans l'interface graphique
							liste.add(elementHashMap);
							//On crée l'évènements dans l'agenda du téléphone
							this.ecrireEvent(calStart, calEnd, titre, information, i, lieu);
							//On incrémente le nombre d'évènement
							this.nbEvent++;
						}
					}
					catch(ParseException e){
						e.printStackTrace();
					}
				}
				catch(NullPointerException e){
					e.printStackTrace();
				}
				
			}
			if(affichage){
				//On modifi le contenu du TextView01 par le nombre d'évènement synchronisé.
				TextView compteur = (TextView) Main.instance().findViewById(R.id.TextView01);
				compteur.setText("Nombre d'événements : "+ this.nbEvent);
				ListAdapter adapter = new SimpleAdapter(Main.instance(), liste, android.R.layout.simple_list_item_2,new String[] {"text1", "text2"}, new int[] {android.R.id.text1, android.R.id.text2 });  
				listeEvenement.setAdapter(adapter);	
			}
		}
		catch(Exception e) {
			if(Fenetre.instance() != null){
				//Un heure imprévue est survenue, on affiche un message d'erreur
				Fenetre.instance().changeViewToAccueil();
				AlertDialog.Builder adb = new AlertDialog.Builder(context);
	       	 	adb.setTitle("Erreur de synchronisation");
	       	 	adb.setMessage("Un problème est survenu lors de la synchronisation, veuillez réessayer ultérieurement.");
		       	 try {
		       		 adb.show();
		       	 }
		       	 catch(BadTokenException b){
		       		 //Il s'agit de la synchronisation auto. on affiche une notification
		       		 NotificationErreur notification = new NotificationErreur(context);
		       		 notification.createNotification("Les évènements n'ont pas pu être actualisés, il est possible qu'ils aient été supprimés."); 	       	    			
		       	 }
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * Methode deleteAgenda
	 * Permet de supprimer tous les événements d'un agenda
	 * @param titre nom de l'agenda où l'on veux supprimer les événements
	 */
	public void deleteAgenda(String titre){
		//On récupére la liste des agenda
		Uri eventUri = Uri.parse("content://com.android.calendar/events");
		String[] projection = 
			      new String[]{
			            Calendars._ID, 
			            Calendars.NAME, 
			            Calendars.ACCOUNT_NAME, 
			            Calendars.ACCOUNT_TYPE};
		Cursor cursor = contentResolver.query(Calendars.CONTENT_URI, projection, null, null, null);
		//On parcourt tous les agendas du téléphone
		while(cursor.moveToNext()) {
			//l'id de l'agenda
			long id = cursor.getLong(0);
			//le nom de l'agenda
			String displayName = cursor.getString(1);
			//On vérifi qu'il s'agit bien de l'agenda recherché
			if(titre.equals(displayName)){
				//On est dans l'agenda recherché
				Uri deleteUri = ContentUris.withAppendedId(eventUri, cursor.getInt(0));
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				//On supprime les évènements de l'agenda
				contentResolver.delete(Uri.parse("content://com.android.calendar/events"),"calendar_id=? and dtstart>?", new String[]{String.valueOf(id), ""+cal.getTimeInMillis()});
			    contentResolver.delete(deleteUri, null, null);
			}
		    
		}
		//On referme le curseur
		cursor.close();
	}

	/**
	 * Méthode replace
	 * Permet l'affichage correctement des caractères spéciaux
	 * @param titre le String ou il faut modifier les caractère spéciaux
	 * @return le String avec les caractère spéciaux correct.
	 */
	private String replace(String titre){
		//Méthode pour éviter les erreurs d'affichage de caractères spéciaux
		titre = titre.replace("Ã¢", "â");
		titre = titre.replace("Ã©", "é");
		titre = titre.replace("Ã¨", "è");
		titre = titre.replace("Ãª", "ê"); 
		titre = titre.replace("Ã«", "ë");
		titre = titre.replace("Ã®", "î");
		titre = titre.replace("Ã¯", "ï");
		titre = titre.replace("Ã´", "ô");
		titre = titre.replace("Ã¶", "ö");
		titre = titre.replace("Ã¹", "ù");
		titre = titre.replace("Ã", "à");
		titre = titre.replace("Ã»", "û");
		titre = titre.replace("Ã¼", "ü");
		titre = titre.replace("Ã§", "ç");
		titre = titre.replace("Å", "œ");
		titre = titre.replace("â¬", "€");		
		return titre;
	}
	
	/**
	 * Methode ecrireEvent
	 * méthode qui permet d'écrire l'agenda, dans l'agenda du téléphone.
	 * @param start début de l'event
	 * @param end fin de l'event
	 * @param titre titre de l'event
	 * @param description petite description de l'event (lieu, ...)
	 */
	private void ecrireEvent(Calendar start, Calendar end, String titre, String description, int i, String lieu){
		TimeZone timeZone = TimeZone.getDefault();
		//Création d'un ContentValues avec les informations de l'évènements à ajouter  
		ContentValues event = new ContentValues();
		//id de l'agenda
		event.put(CalendarContract.Events.CALENDAR_ID, calId);
		//titre de l'évènement
		event.put(CalendarContract.Events.TITLE, titre);
		//description de l'évènement
		event.put(CalendarContract.Events.DESCRIPTION, description);
		//lieu de l'évènement
		event.put(CalendarContract.Events.EVENT_LOCATION, lieu);
		//date de début en milliseconde
		event.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());
		//date de fin en milliseconde
		event.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
		event.put(CalendarContract.Events.STATUS, 1);
		//pour désactiver l'alarme sur l'évènement
		event.put(CalendarContract.Events.HAS_ALARM, 0);
		//timeZone de l'évènement pour ajouter avec la bonne heure
		event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
		Log.v("Date Start",start.toString());
		Log.v("Date End  ",end.toString());
		Log.v("Time Zone", timeZone.getID());

		//insertion de l'évènement dans l'agenda
		Uri eventsUri = Uri.parse("content://com.android.calendar/events");
		contentResolver.insert(eventsUri, event);
		
	}
	
	/**
	 * Methode privée getCalendarId
	 * Permet de récupérer l'id de l'agenda (du téléphone) à modifier 
	 * @param titre de l'agenda à modifier
	 * @return int l'id de l'agenda
	 */
	private long getCalendarId(String titre, Context context){
		SharedPreferences settings = context.getSharedPreferences("preferencescreen.xml", 0);
		//On récupére la couleur choisie pour l'agenda (par défaut rouge)
		int color = settings.getInt("color", Color.RED);
		
		//On récupére la liste des agendas du téléphone
		String[] projection = 
			      new String[]{
			            Calendars._ID, 
			            Calendars.NAME,
			            Calendars.ACCOUNT_NAME, 
			            Calendars.ACCOUNT_TYPE};
		Cursor calCursor = contentResolver.query(Calendars.CONTENT_URI, projection, null, null, null);
		//On parcourt cette liste
		while(calCursor.moveToNext()){
			//l'id de l'agenda
			long id = calCursor.getLong(0);
			//le nom de l'agenda
			String displayName = calCursor.getString(1);
			//on vérifi qu'il s'agit de l'agenda recherché
			if(titre.equals(displayName)){
				//on récupére le nom de l'agenda par défaut l'identifiant de l'utilisateur
				titre = settings.getString("agendaName", titre);
				//création d'un ContentValues pour modifer le titre et la couleur de l'agenda
				ContentValues values = new ContentValues();
				values.put(Calendars.CALENDAR_DISPLAY_NAME, titre);
				values.put(Calendars.CALENDAR_COLOR,  color );
				//On update l'agenda avec le nouveau titre et couleur paramètré
			    Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
			    contentResolver.update(builder.build(), values, "_ID=?", new String[]{String.valueOf(id)});
				//on ferme le curseur
			    calCursor.close();
			    //on retourne l'id de l'agenda modifié
				return id;
			}
		}
		calCursor.close();
		
		//On execute le code qui suit, si et seulement si, il n'y a pas d'agenda crée avec ce nom
		//Création de l'agenda
		
		//on récupére les adresses mails google liées au téléphone 
		ArrayList<String> email = new ArrayList<String>();
		try {
			Pattern emailPattern = Patterns.EMAIL_ADDRESS;
			Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
			if(accounts.length ==0){
				accounts = AccountManager.get(context).getAccounts();
			}
			
			for (Account account : accounts) {
			    if (emailPattern.matcher(account.name).matches()) {
			        email.add(account.name);
			    }
			}
		}
		catch(IllegalArgumentException e){
			//Aucun email n'a été trouvé
			e.printStackTrace();
			email.add("");
		}
		
		//création d'un ContentValues pour ajouter le nouvel agenda
		ContentValues values = new ContentValues();
		//Adresse mail pour l'agenda
		values.put(Calendars.ACCOUNT_NAME,  email.get(0));
		//le type d'agenda
	    values.put(Calendars.ACCOUNT_TYPE,  "com.google");
	    //le titre de l'agenda
	    values.put(Calendars.NAME,  titre);
	    values.put(Calendars.CALENDAR_DISPLAY_NAME,  titre);
	    //la couleur de l'agenda
	    values.put(Calendars.CALENDAR_COLOR,  color );
	    //les autorisations d'accès de l'agenda
	    values.put(Calendars.CALENDAR_ACCESS_LEVEL,  Calendars.CAL_ACCESS_OWNER);
	    //l'adresse du propriétaire
	    values.put(Calendars.OWNER_ACCOUNT,email.get(0));
	    //la timezone de l'événement
	    values.put(Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
	    values.put(Calendars.VISIBLE, 1);
	    values.put(Calendars.SYNC_EVENTS, 1);
	    values.put(Calendars.CAN_PARTIALLY_UPDATE, 1);
	    String adresse = email.get(0).replace("@", "%40");
	    values.put(Calendars.CAL_SYNC1, "https://www.google.com/calendar/feeds/" + adresse + "/private/full");
        values.put(Calendars.CAL_SYNC2, "https://www.google.com/calendar/feeds/default/allcalendars/full/" + adresse);
        values.put(Calendars.CAL_SYNC3, "https://www.google.com/calendar/feeds/default/allcalendars/full/" + adresse);
        values.put(Calendars.CAL_SYNC4, 1);
        values.put(Calendars.CAL_SYNC5, 0);
        values.put(Calendars.CAL_SYNC8, System.currentTimeMillis());
	    Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon(); 
	    builder.appendQueryParameter(Calendars.ACCOUNT_NAME, email.get(0));
	    builder.appendQueryParameter(Calendars.ACCOUNT_TYPE,  "com.google");
	    builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,  "true");
	    //On insert le nouvel agenda dans le téléphone
	    contentResolver.insert(builder.build(), values);
	    //On rappel cette méthode une fois l'agenda crée
		return this.getCalendarId(titre, context);
	}
}
