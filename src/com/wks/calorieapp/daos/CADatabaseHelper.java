package com.wks.calorieapp.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//http://stackoverflow.com/questions/4063510/multiple-table-sqlite-db-adapters-in-android
public class CADatabaseHelper extends SQLiteOpenHelper
{
	public static final String DATABASE_NAME = "calorieapp";
	public static final int DATABASE_VERSION = 3;

	private static final String CREATE_TABLE_JOURNALS = 
			"CREATE TABLE IF NOT EXISTS "+JournalsDataAccessObject.TABLE_JOURNALS+" ("+
			""+JournalsDataAccessObject.Column.ID.getName ()+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
			""+JournalsDataAccessObject.Column.DATE.getName ()+" TEXT, "+
			""+JournalsDataAccessObject.Column.TIME.getName()+" TEXT,"+
			""+JournalsDataAccessObject.Column.FOOD_ID.getName ()+" INTEGER,"+
			""+JournalsDataAccessObject.Column.IMAGE_ID.getName ()+" INTEGER,"+
			"FOREIGN KEY ("+JournalsDataAccessObject.Column.FOOD_ID.getName ()+") REFERENCES "+FoodsDataAccessObject.TABLE_FOODS+" ("+FoodsDataAccessObject.Column.ID.getName ()+"),"+
			"FOREIGN KEY ("+JournalsDataAccessObject.Column.IMAGE_ID.getName ()+") REFERENCES "+ImagesDataAccessObject.TABLE_IMAGES+" ("+ImagesDataAccessObject.Column.ID.getName ()+"));";
					
	private static final String CREATE_TABLE_FOODS = 
			"CREATE TABLE IF NOT EXISTS "+FoodsDataAccessObject.TABLE_FOODS+" ("+
			""+FoodsDataAccessObject.Column.ID.getName ()+" INTEGER PRIMARY KEY,"+
			""+FoodsDataAccessObject.Column.NAME.getName ()+" TEXT,"+
			""+FoodsDataAccessObject.Column.CALORIES.getName ()+" DECIMAL(8,2));";
			
	private static final String CREATE_TABLE_IMAGES = 
			"CREATE TABLE IF NOT EXISTS "+ImagesDataAccessObject.TABLE_IMAGES+" ("+
			""+ImagesDataAccessObject.Column.ID.getName ()+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
			""+ImagesDataAccessObject.Column.FILE_NAME.getName ()+" TEXT);";
	
	private static final String DROP_TABLE_JOURNAL = "DROP TABLE IF EXISTS " + JournalsDataAccessObject.TABLE_JOURNALS + ";";
	private static final String DROP_TABLE_FOODS = "DROP TABLE IF EXISTS " + FoodsDataAccessObject.TABLE_FOODS + ";";
	private static final String DROP_TABLE_IMAGES = "DROP TABLE IF EXISTS " + ImagesDataAccessObject.TABLE_IMAGES + ";";

	private static CADatabaseHelper instance = null;
	private static SQLiteDatabase db = null;
	
	private CADatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME,null,DATABASE_VERSION);
		
	}
	
	public static CADatabaseHelper getInstance(Context context)
	{
		if(instance == null)
			instance = new CADatabaseHelper(context);
		
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
