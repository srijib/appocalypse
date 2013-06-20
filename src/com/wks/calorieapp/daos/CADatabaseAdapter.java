package com.wks.calorieapp.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class CADatabaseAdapter
{
	public static final String DATABASE_NAME = "calorieapp";
	public static final int DATABASE_VERSION = 1;

	private static final String CREATE_TABLE_JOURNALS = 
			"CREATE TABLE "+JournalsDataAccessObject.TABLE_JOURNALS+" ("+
			""+JournalsDataAccessObject.COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
			""+JournalsDataAccessObject.COL_TIMESTAMP+" TEXT, "+
			""+JournalsDataAccessObject.COL_FOOD_ID+" INTEGER,"+
			""+JournalsDataAccessObject.COL_IMAGE_ID+" INTEGER,"+
			"FOREIGN KEY ("+JournalsDataAccessObject.COL_FOOD_ID+") REFERENCES "+FoodsDataAccessObject.TABLE_FOODS+" ("+FoodsDataAccessObject.COL_ID+"),"+
			"FOREIGN KEY ("+JournalsDataAccessObject.COL_IMAGE_ID+") REFERENCES "+ImagesDataAccessObject.TABLE_IMAGES+" ("+ImagesDataAccessObject.COL_ID+");";
					
	private static final String CREATE_TABLE_FOODS = 
			"CREATE TABLE "+FoodsDataAccessObject.TABLE_FOODS+" ("+
			""+FoodsDataAccessObject.COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
			""+FoodsDataAccessObject.COL_NAME+" TEXT,"+
			""+FoodsDataAccessObject.COL_CALORIES+" DECIMAL(8,2);";
			
	private static final String CREATE_TABLE_IMAGES = 
			"CREATE TABLE "+ImagesDataAccessObject.TABLE_IMAGES+" ("+
			""+ImagesDataAccessObject.COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
			""+ImagesDataAccessObject.COL_FILE_NAME+" TEXT);";

	private static final String DROP_TABLE_JOURNAL = "DROP TABLE IF EXISTS "+JournalsDataAccessObject.TABLE_JOURNALS+";";
	private static final String DROP_TABLE_FOODS = "DROP TABLE IF EXISTS "+FoodsDataAccessObject.TABLE_FOODS+";";
	private static final String DROP_TABLE_IMAGES = "DROP TABLE IF EXISTS "+ImagesDataAccessObject.TABLE_IMAGES+";";
	
	private Context context;
	private CADatabaseHelper helper;
	private SQLiteDatabase db;
	
	public CADatabaseAdapter(Context context)
	{
		this.context = context;
		this.helper = new CADatabaseHelper(this.context);
		
	}
	
	public CADatabaseAdapter open() throws SQLiteException
	{
		this.db = this.helper.getWritableDatabase ();
		return this;
	}
	
	public void close()
	{
		this.helper.close ();
	}
	
	static class CADatabaseHelper extends SQLiteOpenHelper
	{
		CADatabaseHelper ( Context context )
		{
			super ( context, DATABASE_NAME, null, DATABASE_VERSION );
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
			onCreate(db);
		}

	}
}
