package com.wks.calorieapp.daos;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FoodsDataAccessObject
{

	public static final String TABLE_FOODS = "foods";
	public static final String[] COLUMNS = {Column.ID.getName (),Column.NAME.getName (),Column.CALORIES.getName ()};

	
	private SQLiteDatabase db;
	private CADatabaseHelper helper;
	
	public FoodsDataAccessObject(SQLiteDatabase db/*Context context*/)
	{
		this.db = db;
		//helper = CADatabaseHelper.getInstance ( context );
	}
	/*
	public void open()
	{
		this.db = this.helper.getWritableDatabase ();
	}
	
	public void close()
	{
		this.helper.close ();
	}*/
	
	public long create(FoodDataTransferObject food)
	{
		ContentValues values = new ContentValues();
		values.put ( Column.ID.getName (), food.getId());
		values.put ( Column.NAME.getName (), food.getName () );
		values.put ( Column.CALORIES.getName (), food.getCalories () );
		return db.insert ( TABLE_FOODS, null, values );
	}
	
	public FoodDataTransferObject read(long id)
	{
		FoodDataTransferObject food = null;
		
		Cursor c = db.query ( TABLE_FOODS, COLUMNS, Column.ID.getName ()+" = "+id, null, null, null, null );
		
		if(c != null && c.moveToFirst())
		{
			food = cursorToFoodDataTransferObject(c);
		}
		
		c.close ();
		return food;
	}
	
	public List<FoodDataTransferObject> read()
	{
		List<FoodDataTransferObject> foods = new ArrayList<FoodDataTransferObject>();
		
		Cursor c = db.query ( TABLE_FOODS, COLUMNS, null, null, null, null, null );
		
		if(c != null && c.moveToFirst())
		{
			while(!c.isAfterLast ())
			{
				foods.add ( cursorToFoodDataTransferObject(c) );
				c.moveToNext ();
			}
		}
		
		c.close ();
		return foods;
	}
	
	public int update(FoodDataTransferObject food)
	{
		ContentValues values = new ContentValues();
		values.put ( Column.ID.getName (), food.getId () );
		values.put ( Column.NAME.getName (), food.getName () );
		values.put ( Column.CALORIES.getName (), food.getCalories () );
		
		return db.update ( TABLE_FOODS, values, Column.ID.getName ()+" = "+food.getId (), null );
	}
	
	public int delete(long id)
	{
		return db.delete ( TABLE_FOODS, Column.ID.getName ()+" = "+id, null );
	}
	
	private FoodDataTransferObject cursorToFoodDataTransferObject(Cursor c)
	{
		long id = c.getLong ( Column.ID.ordinal () );
		String name = c.getString ( Column.NAME.ordinal () );
		float calories = c.getFloat ( Column.CALORIES.ordinal () );
		
		return new FoodDataTransferObject(id,name,calories);
	}
	
	public static enum Column
	{
		ID("_id"),
		NAME("name"),
		CALORIES("calories");
		
		private final String name;
		
		Column(String name)
		{
			this.name = name;
		}
		
		public String getName ()
		{
			return name;
		}
	}
}
