package com.wks.calorieapp.daos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wks.calorieapp.pojos.FoodEntry;
import com.wks.calorieapp.pojos.ImageEntry;
import com.wks.calorieapp.pojos.JournalEntry;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class JournalDAO
{
	public static final String TABLE_JOURNALS = "journals";
	public static final String [] COLUMNS =
	{
			Column.ID.getName (), Column.DATE.getName (), Column.TIME.getName (), Column.FOOD_ID.getName (), Column.IMAGE_ID.getName ()
	};
	

	public static final String TOTAL_CALORIES = "total_calories";
	public static final String queryCaloriesEachDayOfParameterMonth = "SELECT "+JournalDAO.Column.DATE.getFullName ()+", SUM("+FoodDAO.Column.CALORIES.getFullName ()+") FROM "+JournalDAO.TABLE_JOURNALS+", "+FoodDAO.TABLE_FOODS+"  WHERE ("+JournalDAO.Column.ID.getFullName ()+" = "+FoodDAO.Column.ID.getFullName ()+")"
			+ " AND ("+JournalDAO.Column.DATE.getFullName ()+" LIKE %s) GROUP BY "+JournalDAO.Column.DATE.getFullName ()+";";
	
	public static final String queryFoodEntriesForParameterDate = "SELECT "+FoodDAO.Column.ID.getFullName ()+", "+FoodDAO.Column.NAME.getFullName ()+", "+FoodDAO.Column.CALORIES.getFullName ()+"  FROM "+FoodDAO.TABLE_FOODS+", "+JournalDAO.TABLE_JOURNALS+" WHERE ("+JournalDAO.Column.FOOD_ID.getFullName ()+" = "+FoodDAO.Column.ID.getFullName ()+") AND ("+JournalDAO.Column.DATE.getFullName ()+" LIKE %s)";
	
	
	private SQLiteDatabase db;

	public JournalDAO ( SQLiteDatabase db )
	{
		this.db = db;
	}

	public long create ( JournalEntry journal )
	{
		ContentValues values = new ContentValues ();
		values.put ( Column.DATE.getName (), journal.getDateAsString () );
		values.put ( Column.TIME.getName (), journal.getTimeAsString () );
		values.put ( Column.FOOD_ID.getName (), journal.getFoodId () );

		if ( journal.getImageId () < 0 ) { return db.insert ( TABLE_JOURNALS, Column.IMAGE_ID.getName (), values ); }

		values.put ( Column.IMAGE_ID.getName (), journal.getImageId () );
		return db.insert ( TABLE_JOURNALS, null, values );
	}

	public long addToJournal ( JournalEntry journal, FoodEntry food, ImageEntry image )
	{
		if ( journal == null || food == null ) throw new IllegalStateException ( "A Journal entry requires a journalDTO and a foodDTO." );

		this.db.beginTransaction ();

		// add food item
		FoodDAO foodDao = new FoodDAO ( this.db/* this.context */);
		FoodEntry foodDto = foodDao.read ( food.getId () );

		long foodId = -1;
		if ( foodDto != null )
		{
			foodId = foodDao.create ( food );
			if ( foodId == -1 )
			{
				db.endTransaction ();
				return -1;
			}
		}

		// image may be null.
		if ( image == null )
		{
			journal.setFoodId ( foodId );
			journal.setImageId ( -1 );

		}else
		{
			// add image item
			ImageDAO imageDao = new ImageDAO ( this.db/* this.context */);

			long imageId = imageDao.create ( image );
			if ( imageId == -1 )
			{
				db.endTransaction ();
				return -1;
			}

			journal.setFoodId ( foodId );
			journal.setImageId ( imageId );
		}

		long journalId = this.create ( journal );
		if ( journalId != -1 )
		{
			db.setTransactionSuccessful ();
		}

		db.endTransaction ();
		return journalId;
	}

	public JournalEntry read ( long id ) throws ParseException
	{
		JournalEntry journal = null;

		Cursor c = db.query ( TABLE_JOURNALS, COLUMNS, Column.ID.getName () + " = " + id, null, null, null, null );

		if ( c != null && c.moveToFirst () )
		{
			journal = cursorToJournalDataTransferObject ( c );
		}

		c.close ();
		return journal;
	}

	public List< JournalEntry > read () throws ParseException
	{
		List< JournalEntry > journals = new ArrayList< JournalEntry > ();

		Cursor c = db.query ( TABLE_JOURNALS, COLUMNS, null, null, null, null, null );

		if ( c != null && c.moveToFirst () )
		{
			while ( !c.isAfterLast () )
			{
				journals.add ( cursorToJournalDataTransferObject ( c ) );
				c.moveToNext ();
			}
		}

		c.close ();
		return journals;
	}

	public int update ( JournalEntry journal )
	{
		ContentValues values = new ContentValues ();
		values.put ( Column.ID.getName (), journal.getId () );
		values.put ( Column.DATE.getName (), journal.getDateAsString () );
		values.put ( Column.TIME.getName (), journal.getTimeAsString () );
		values.put ( Column.FOOD_ID.getName (), journal.getFoodId () );
		values.put ( Column.IMAGE_ID.getName (), journal.getImageId () );

		return db.update ( TABLE_JOURNALS, values, Column.ID.getName () + " = " + journal.getId (), null );
	}

	public int delete ( long id )
	{
		return db.delete ( TABLE_JOURNALS, Column.ID.getName () + " = " + id, null );
	}

	
	public Map< String, Float > getCaloriesForEachDay ( long timeInMillis )
	{
		Calendar calendar = Calendar.getInstance ();
		calendar.setTimeInMillis ( timeInMillis );
		return getCaloriesForEachDay(calendar);
	}

	//Java is so annoying!! Calendar.DAY are 1-7 but Calendar.MONTH 0-11 WTF
	public Map< String, Float > getCaloriesForEachDay ( Calendar cal )
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		String monthRegex = formatter.format ( cal.getTimeInMillis () ) + "-__";
		
		Map< String, Float > dayCaloriesMap = new HashMap< String, Float > ();

		//This works. DO NOT TOUCH!!!!!!!!!!
		String unparameterQuery = "SELECT journals.date,SUM(foods.calories) FROM journals, foods WHERE (journals.food_id = foods._id) AND (journals.date LIKE %s) GROUP BY journals.date;";
		String query = String.format ( unparameterQuery, "'"+monthRegex+"'");
		Cursor c = this.db.rawQuery ( query, null);

		if ( c != null && c.moveToFirst () )
		{

			while ( !c.isAfterLast () )
			{
				String date = c.getString ( 0 );
				float calories = c.getFloat ( 1 );
				dayCaloriesMap.put ( date, calories );
				c.moveToNext ();
			}
		}

		return dayCaloriesMap;
	}

	public List< FoodEntry > getFoodEntriesForDay ( Calendar cal )
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String monthRegex = formatter.format ( cal.getTimeInMillis () );
		
		List<FoodEntry> foodEntries = new ArrayList<FoodEntry>();
		
		String query = String.format ( queryFoodEntriesForParameterDate, monthRegex );
		
		Cursor c = this.db.rawQuery ( query,null);
		
		if(c != null && c.moveToFirst ())
		{
			while(!c.isAfterLast ())
			{
				FoodEntry foodEntry = new FoodEntry();
				foodEntry.setId ( c.getLong ( 0 ) );
				foodEntry.setName ( c.getString ( 1 ) );
				foodEntry.setCalories ( c.getFloat ( 2 ) );
				foodEntries.add ( foodEntry );
			}
		}
		
		return foodEntries;
	}
	
	public void test()
	{
		String s = "Now:\n";
		String query = "SELECT journals.date,SUM(foods.calories) FROM journals, foods WHERE (journals.food_id = foods._id) AND (journals.date LIKE %s) GROUP BY journals.date;";
		Cursor c = db.rawQuery ( query, null );
		if(c!=null && c.moveToFirst ())
		{
			while(!c.isAfterLast ())
			{
				
				String date = c.getString ( 0 );
				float calories = c.getFloat ( 1 );
				s += "date: "+date+", calories: "+calories+"\n";
				c.moveToNext ();
			}
		}else
		{
			s+="no stuff";
		}
		Log.e("stuff",s);
		
	}

	private JournalEntry cursorToJournalDataTransferObject ( Cursor c ) throws ParseException
	{
		long id = c.getLong ( Column.ID.ordinal () );
		String date = c.getString ( Column.DATE.ordinal () );
		String time = c.getString ( Column.TIME.ordinal () );
		long foodId = c.getLong ( Column.FOOD_ID.ordinal () );
		long imageId = c.getLong ( Column.IMAGE_ID.ordinal () );

		return new JournalEntry ( id, date, time, foodId, imageId );
	}

	public static enum Column
	{
		ID ( "_id" ), DATE ( "date" ), TIME ( "time" ), FOOD_ID ( "food_id" ), IMAGE_ID ( "image_id" );

		private final String name;

		Column ( String name )
		{

			this.name = name;
		}

		public String getName ()
		{
			return name;
		}

		public String getFullName ()
		{
			return JournalDAO.TABLE_JOURNALS + "." + name;
		}
	}
}
