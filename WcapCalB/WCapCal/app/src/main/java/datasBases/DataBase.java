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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Classe DataBase
 * Base de donnée de WCap'Cal
 * @author WCap'Cal Company
 * @version 2.0
 */
public class DataBase extends SQLiteOpenHelper{

	private static DataBase instance;
	
	private static final String TABLE_AGENDAS = "table_agendas";
	private static final String COL_ID = "ID";
	private static final String COL_NAME = "name";
	private static final String COL_URL = "url";
	private static final String COL_IDENTIFIANT = "identifiant";
	private static final String COL_PASSWORD = "password";
 
	private static final String CREATE_BDD = "CREATE TABLE " + TABLE_AGENDAS + " ("
	+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME + " TEXT NOT NULL, "
	+ COL_URL + " TEXT NOT NULL, " + COL_IDENTIFIANT + " TEXT NOT NULL, "+ COL_PASSWORD + " TEXT NOT NULL);";
 
	/**
	 * Constructeur de la classe DataBase
	 * @param context le contexte de l'application
	 * @param name le nom de la base de données
	 * @param factory
	 * @param version la version de la base de données
	 */
	private DataBase(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	/**
	 * Implémentation du pattern Singleton
	 * Méthode utilisée pour vérifier qu'il n'y a qu'une seule instance de la
	 * classe DataBase
	 * @param context
	 * @param name le nom de la base de données
	 * @param factory
	 * @param version la version de la base de données
	 * @return l'instance de DataBase
	 */
	public static DataBase instance(Context context, String name, CursorFactory factory, int version){
		if(instance == null){
			instance = new DataBase(context, name, factory, version);
		}
		return instance;
	}
 
	@Override
	/**
	 * Réaction de la base de données à sa création
	 * @param db la base de données de type SQLite
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BDD);
	}
 
	@Override
	/**
	 * Réaction de la base de données lors de modifications
	 * @param bd la base de données
	 * @param oldVersion l'ancienne version
	 * @param newVersion la nouvelle version
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + TABLE_AGENDAS + ";");
		onCreate(db);
	}

}
