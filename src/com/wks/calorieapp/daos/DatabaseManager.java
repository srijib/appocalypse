package com.wks.calorieapp.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//http://stackoverflow.com/questions/4063510/multiple-table-sqlite-db-adapters-in-android
public class DatabaseManager extends SQLiteOpenHelper
{
	public static final String DATABASE_NAME = "calorieapp";
	public static final int DATABASE_VERSION = 3;

	private static final String CREATE_TABLE_JOURNALS =
			"CREATE TABLE IF NOT EXISTS "+JournalDAO.TABLE_JOURNALS+" ("+
			""+JournalDAO.Column.ID.getName ()+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
			""+JournalDAO.Column.DATE.getName ()+" TEXT, "+
			""+JournalDAO.Column.TIME.getName()+" TEXT,"+
			""+JournalDAO.Column.FOOD_ID.getName ()+" INTEGER,"+
			""+JournalDAO.Column.IMAGE_ID.getName ()+" INTEGER,"+
			"FOREIGN KEY ("+JournalDAO.Column.FOOD_ID.getName ()+") REFERENCES "+FoodDAO.TABLE_FOODS+" ("+FoodDAO.Column.ID.getName ()+"),"+
			"FOREIGN KEY ("+JournalDAO.Column.IMAGE_ID.getName ()+") REFERENCES "+ImageDAO.TABLE_IMAGES+" ("+ImageDAO.Column.ID.getName ()+"));";
					
	private static final String CREATE_TABLE_FOODS = 
			"CREATE TABLE IF NOT EXISTS "+FoodDAO.TABLE_FOODS+" ("+
			""+FoodDAO.Column.ID.getName ()+" INTEGER PRIMARY KEY,"+
			""+FoodDAO.Column.NAME.getName ()+" TEXT,"+
			""+FoodDAO.Column.CALORIES.getName ()+" DECIMAL(8,2));";
			
	private static final String CREATE_TABLE_IMAGES = 
			"CREATE TABLE IF NOT EXISTS "+ImageDAO.TABLE_IMAGES+" ("+
			""+ImageDAO.Column.ID.getName ()+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
			""+ImageDAO.Column.FILE_NAME.getName ()+" TEXT);";
	
	private static final String DROP_TABLE_JOURNAL = "DROP TABLE IF EXISTS " + JournalDAO.TABLE_JOURNALS + ";";
	private static final String DROP_TABLE_FOODS = "DROP TABLE IF EXISTS " + FoodDAO.TABLE_FOODS + ";";
	private static final String DROP_TABLE_IMAGES = "DROP TABLE IF EXISTS " + ImageDAO.TABLE_IMAGES + ";";

	private static DatabaseManager instance = null;
	private static SQLiteDatabase db = null;
	
	private DatabaseManager(Context context)
	{
		super(context, DATABASE_NAME,null,DATABASE_VERSION);
		
	}
	
	public static DatabaseManager getInstance(Context context)
	{
		if(instance == null)
			instance = new DatabaseManager(context);
		
		return instance;
	}
	
	public SQLiteDatabase open()
	{
		return this.getWritableDatabase ();
	}
	
	public void close()
	{
		this.close ();
	}

	@Override
	public void onCreate ( SQLiteDatabase db )
	{
		db.execSQL ( CREATE_TABLE_FOODS );
		db.execSQL ( CREATE_TABLE_IMAGES );
		db.execSQL ( CREATE_TABLE_JOURNALS );
	}

	@Override
	public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion )
	{
		db.execSQL ( DROP_TABLE_JOURNAL );
		db.execSQL ( DROP_TABLE_FOODS );
		db.execSQL ( DROP_TABLE_IMAGES );
		onCreate ( db );
	}

}
