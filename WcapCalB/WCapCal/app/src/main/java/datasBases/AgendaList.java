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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Classe AgendaList
 * Base de données de WCap'cal 
 * @author WCap'Cal Company
 * @version 2.0
 */
public class AgendaList {
	private static AgendaList instance = null;
	private static final int VERSION_BDD = 1;
	private static final String NOM_BDD = "agendas.db";
	private static final String TABLE_AGENDAS = "table_agendas";
	private static final String COL_ID = "ID";
	private static final int NUM_COL_ID = 0;
	private static final String COL_NAME = "name";
	private static final int NUM_COL_NAME = 1;
	private static final String COL_URL = "url";
	private static final int NUM_COL_URL = 2;
	private static final String COL_IDENTIFIANT = "identifiant";
	private static final int NUM_COL_IDENTIFIANT = 3;
	private static final String COL_PASSWORD = "password";
	private static final int NUM_COL_PASSWORD = 4;
 	private DataBase maBaseSQLite;
	private SQLiteDatabase bdd;
 
	/**
	 * Constructeur de la classe AgendaList
	 * @param context le contexte de l'application
	 */
	private AgendaList(Context context){
		maBaseSQLite = DataBase.instance(context, NOM_BDD, null, VERSION_BDD);
		this.open();
	}
	
	/**
	 * Implémentation du pattern Singleton
	 * Vérifie qu'il n'y a qu'une seule instance d'AgendaList
	 * @param context le contexte de l'application
	 * @return instance l'instance de l'AgendaList
	 */
	public static AgendaList instance(Context context){
		if(instance == null){
			instance = new AgendaList(context);
		}
		return instance;
	}
	
	/**
	 * Permet de récupérer l'instance d'AgendaList
	 * @return instance l'instance actuelle d'AgendaList
	 */
	public static AgendaList instance(){
		return instance;
	}
 
	/**
	 * Ouvre la connexion avec la base de données
	 */
	public void open(){
		bdd = maBaseSQLite.getWritableDatabase();
	}
 
	/**
	 * Ferme la connexion avc la base de données
	 */
	public void close(){
		bdd.close();
	}
 
	/**
	 * Accesseur de la base de données
	 * @return bdd la base de données
	 */
	public SQLiteDatabase getBDD(){
		return bdd;
	}
	
	/**
	 * Méthode utilisée pour obtenir le nombre d'Agendas
	 * @return le nombre d'objets Agenda présents
	 */
	public int getNbAgenda(){
		Cursor c = bdd.query(TABLE_AGENDAS, new String[] {COL_ID, COL_NAME, COL_URL, COL_IDENTIFIANT, COL_PASSWORD}, null, null, null, null, null);
		int nbAgenda = c.getCount();
		return nbAgenda;
	}
	
	/**
	 * Methode isEmpty
	 * Pour vérifier si des calendrier ont déjà été configuré 
	 * @return false si des calendrier ont été configuré, true sinon.
	 */
	public boolean isEmpty(){
		boolean ret = false;
		if(this.getNbAgenda() == 0){
			ret = true;
		}
		return ret;
	}
 
	/**
	 * Méthode d'insertion d'un Agenda dans la base de données
	 * @param name le nom de l'Agenda
	 * @param url l'adresse de l'Agenda
	 * @param identifiant l'ID correspondant à l'Agenda
	 * @param password le password correspondant à l'Agenda
	 * @return
	 */
	public long insertAgenda(String name, String url, String identifiant, String password){
		ContentValues values = new ContentValues();
		values.put(COL_NAME, name);
		values.put(COL_URL, url);
		values.put(COL_IDENTIFIANT, identifiant);
		values.put(COL_PASSWORD, password);
		return bdd.insert(TABLE_AGENDAS, null, values);
	}
 
	/**
	 * Méthode de mise à jour la liste des Agendas en cas de modification
	 * @param id l'ID de l'Agenda dans la bdd
	 * @param name le nom de l'Agenda
	 * @param url l'adresse de l'Agenda
	 * @param identifiant l'ID correspondant à l'Agenda
	 * @param password le password correspondant à l'Agenda
	 * @return
	 */
	public int updateAgenda(int id, String name, String url, String identifiant, String password){
		ContentValues values = new ContentValues();
		values.put(COL_NAME, name);
		values.put(COL_URL, url);
		values.put(COL_IDENTIFIANT, identifiant);
		values.put(COL_PASSWORD, password);
		return bdd.update(TABLE_AGENDAS, values, COL_ID + " = " +id, null);
	}
 
	/**
	 * Méthode utilisée pour effacer un Agenda à partir de l'ID
	 * @param id l'ID de l'Agenda à effacer
	 * @return
	 */
	public int removeAgendaWithID(int id){
		return bdd.delete(TABLE_AGENDAS, COL_ID + " = " +id, null);
	}
 
	/**
	 * Méthode utilisée pour obtenir les Agendas dans la table
	 * sous forme de tableau
	 * @return la liste des Agendas
	 */
	public String[][] getAgenda(){
		Cursor c = bdd.rawQuery("SELECT * FROM table_agendas", null);
		return cursorToAgenda(c);
	}
	
	/**
	 * méthode pour récupérer le nom de l'agenda
	 */
	public String getAgendaName(){
		String [][] agenda = this.getAgenda();
		String name = agenda[0][1];
		return name;
	}
	
	/**
	 * Méthode utilisée pour effacer un Agenda depuis son URL et son nom
	 * @param name le nom de l'Agenda à effacer
	 * @param url l'URl de l'Agneda à effacer
	 * @return
	 */
	public int removeAgenda(String name, String url){
		return bdd.delete(TABLE_AGENDAS, COL_NAME +" LIKE '" + name +"' AND "+ COL_URL +" LIKE '" + url+"'", null);
	}
	
	/**
	 * Méthode utilisée pour selectionner un Agenda particulier depuis son nom
	 * @param name le nom de l'Agenda
	 * @return l'agenda dont le nom correspond
	 */
	public String[][] select(String name){
		Cursor c = bdd.rawQuery("SELECT * FROM table_agendas WHERE name LIKE '"+name+"'", null);
		return cursorToAgenda(c);
	}
	
	/**
	 * Détruit tous les Agendas de la liste
	 */
	public void removeAll(){
		bdd.rawQuery("DROP TABLE table_agendas", null);
	}
 
	/**
	 * Si j'ai bien compris, ça récupère la liste des Agendas pour la mettre
	 * dans un tableau. Comme je suis pas trop sur, je laisse Quentin la spécifier.
	 * @param c
	 * @return
	 */
	private String[][] cursorToAgenda(Cursor c){
		String[][] agenda = new String[this.getNbAgenda()][5];
		if (c.getCount() == 0){
			return agenda;
		}
 
		c.moveToFirst();
		agenda[0][0] = c.getString(NUM_COL_ID);
		agenda[0][1] = c.getString(NUM_COL_NAME);
		agenda[0][2] = c.getString(NUM_COL_URL);
		agenda[0][3] = c.getString(NUM_COL_IDENTIFIANT);
		agenda[0][4] = c.getString(NUM_COL_PASSWORD);
		int i = 1;
		while(c.moveToNext()){
			agenda[i][0] = c.getString(NUM_COL_ID);
			agenda[i][1] = c.getString(NUM_COL_NAME);
			agenda[i][2] = c.getString(NUM_COL_URL);
			agenda[i][3] = c.getString(NUM_COL_IDENTIFIANT);
			agenda[i][4] = c.getString(NUM_COL_PASSWORD);
			i++;
		}
		c.close();
 
		return agenda;
	}
}
