package com.wks.calorieapp.daos;

import java.util.ArrayList;
import java.util.List;

import com.wks.calorieapp.apis.NutritionInfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NutritionInfoDAO implements DataAccessObject<NutritionInfo>
{

	public static final String TABLE_NUTRITION = "nutrition";
	public static final String[] COLUMNS = {Column.ID.getName (),Column.NAME.getName (),Column.TYPE.getName (),Column.URL.getName (),Column.CALORIES.getName (),Column.FAT.getName (),Column.CARBS.getName (),Column.PROTEINS.getName ()};

	
	private SQLiteDatabase db;
	
	public NutritionInfoDAO(SQLiteDatabase db)
	{
		this.db = db;
	}
	
	public long create(NutritionInfo nutrInfo)
	{
		ContentValues values = new ContentValues();
		values.put ( Column.ID.getName (), nutrInfo.getId () );
		values.put ( Column.NAME.getName (), nutrInfo.getName () );
		values.put ( Column.TYPE.getName (), nutrInfo.getType () );
		values.put ( Column.URL.getName (), nutrInfo.getUrl () );
		values.put ( Column.CALORIES.getName (), nutrInfo.getCalories () );
		values.put ( Column.FAT.getName (), nutrInfo.getGramFat () );
		values.put ( Column.CARBS.getName (), nutrInfo.getGramCarbs () );
		values.put ( Column.PROTEINS.getName (), nutrInfo.getGramProteins () );
		return db.insert ( TABLE_NUTRITION, null, values );
	}
	
	public NutritionInfo read(long id)
	{
		NutritionInfo food = null;
		
		Cursor c = db.query ( TABLE_NUTRITION, COLUMNS, Column.ID.getName ()+" = "+id, null, null, null, null );
		
		if(c != null && c.moveToFirst())
		{
			food = cursorToNutritionInfoObject(c);
		}
		
		c.close ();
		return food;
	}
	
	public List<NutritionInfo> read()
	{
		List<NutritionInfo> foods = new ArrayList<NutritionInfo>();
		
		Cursor c = db.query ( TABLE_NUTRITION, COLUMNS, null, null, null, null, null );
		
		if(c != null && c.moveToFirst())
		{
			while(!c.isAfterLast ())
			{
				foods.add ( cursorToNutritionInfoObject(c) );
				c.moveToNext ();
			}
		}
		
		c.close ();
		return foods;
	}
	
	public int update(NutritionInfo nutrInfo)
	{
		ContentValues values = new ContentValues();
		values.put ( Column.ID.getName (), nutrInfo.getId () );
		values.put ( Column.NAME.getName (), nutrInfo.getName () );
		values.put ( Column.TYPE.getName (), nutrInfo.getType () );
		values.put ( Column.URL.getName (), nutrInfo.getUrl () );
		values.put ( Column.CALORIES.getName (), nutrInfo.getCalories () );
		values.put ( Column.FAT.getName (), nutrInfo.getGramFat () );
		values.put ( Column.CARBS.getName (), nutrInfo.getGramCarbs () );
		values.put ( Column.PROTEINS.getName (), nutrInfo.getGramProteins () );
		
		return db.update ( TABLE_NUTRITION, values, Column.ID.getName ()+" = "+nutrInfo.getId (), null );
	}
	
	public int delete(long id)
	{
		return db.delete ( TABLE_NUTRITION, Column.ID.getName ()+" = "+id, null );
	}
	
	private NutritionInfo cursorToNutritionInfoObject(Cursor c)
	{
		NutritionInfo info = new NutritionInfo();
		info.setId ( c.getLong ( Column.ID.ordinal () ) );
		info.setName ( c.getString ( Column.NAME.ordinal () ) );
		info.setType ( c.getString ( Column.TYPE.ordinal () ) );
		info.setUrl ( c.getString ( Column.URL.ordinal () ) );
		info.setCalories ( c.getFloat ( Column.CALORIES.ordinal () ) );
		info.setGramFat ( c.getFloat ( Column.FAT.ordinal () ) );
		info.setGramCarbs ( c.getFloat ( Column.CARBS.ordinal () ) );
		info.setGramProteins ( c.getFloat ( Column.PROTEINS.ordinal () ) );
	
		return info;
	}
	
	public static enum Column
	{
		ID("_id"),
		NAME("name"),
		TYPE("type"),
		URL("url"),
		CALORIES("calories"),
		FAT("fat"),
		CARBS("carbs"),
		PROTEINS("proteins");
		
		private final String name;
		
		Column(String name)
		{
			this.name = name;
		}
		
		public String getName ()
		{
			return name;
		}
		
		public String getFullName()
		{
			return NutritionInfoDAO.TABLE_NUTRITION+"."+name;
		}
	}
}
