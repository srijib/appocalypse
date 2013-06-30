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
	public static final String TAG = JournalDAO.class.getCanonicalName ();
	
	public static final String TABLE_JOURNALS = "journals";
	public static final String [] COLUMNS =
	{
			Column.ID.getName (), Column.DATE.getName (), Column.TIME.getName (), Column.FOOD_ID.getName (), Column.IMAGE_ID.getName ()
	};
	

	public static final String TOTAL_CALORIES = "total_calories";
	public static final String queryCaloriesEachDayOfParameterMonth = "SELECT "+JournalDAO.Column.DATE.getFullName ()+", SUM("+FoodDAO.Column.CALORIES.getFullName ()+") FROM "+JournalDAO.TABLE_JOURNALS+", "+FoodDAO.TABLE_FOODS+"  WHERE ("+JournalDAO.Column.ID.getFullName ()+" = "+FoodDAO.Column.ID.getFullName ()+")"
			+ " AND ("+JournalDAO.Column.DATE.getFullName ()+" LIKE %s) GROUP BY "+JournalDAO.Column.DATE.getFullName ()+";";
	
	public static final String queryFoodEntriesForParameterDate = "SELECT "+FoodDAO.Column.ID.getFullName ()+", "+FoodDAO.Column.NAME.getFullName ()+", "+FoodDAO.Column.CALORIES.getFullName ()+", "+ ImageDAO.Column.ID.getFullName ()+", "+ImageDAO.Column.FILE_NAME.getFullName ()+", "+JournalDAO.Column.ID.getFullName()+","+JournalDAO.Column.TIME.getFullName()+"  FROM "+FoodDAO.TABLE_FOODS+", "+JournalDAO.TABLE_JOURNALS+", "+ImageDAO.TABLE_IMAGES+" WHERE ("+JournalDAO.Column.FOOD_ID.getFullName ()+" = "+FoodDAO.Column.ID.getFullName ()+") AND ("+JournalDAO.Column.DATE.getFullName ()+" LIKE %s)";
	
	
	private SQLiteDatabase db;

	public JournalDAO ( SQLiteDatabase db )
	{
		this.db = db;
	}


	public long addToJournal ( JournalEntry journal)
	{
		//if journal entry or food entry are null, throw exception
		if ( journal == null || journal.getFoodEntry () == null) throw new IllegalStateException ( "A Journal entry can not be null or have a null food entry" );

		//begin transaction
		this.db.beginTransaction ();
		
		//check if food item exists in database
		long foodId = journal.getFoodEntry ().getId ();
		FoodDAO foodDao = new FoodDAO ( this.db);
		FoodEntry foodDto = foodDao.read ( journal.getFoodEntry ().getId () );

		//if food item does not exist in db, add to db.
		if ( foodDto == null )
		{
			foodId = foodDao.create ( journal.getFoodEntry () );
			if ( foodId == -1 )
			{
				db.endTransaction ();
				return -1;
			}
		}

		//if journal entry contains an image entry, add image entry
		long imageId = -1;
		if ( journal.getImageEntry () != null )
		{

			ImageDAO imageDao = new ImageDAO ( this.db);

			imageId = imageDao.create ( journal.getImageEntry () );
			if ( imageId == -1 )
			{
				db.endTransaction ();
				return -1;
			}

		}
		
		long journalId = -1;
		ContentValues values = new ContentValues ();
		values.put ( Column.DATE.getName (), journal.getDateAsString () );
		values.put ( Column.TIME.getName (), journal.getTimeAsString () );
		values.put ( Column.FOOD_ID.getName (), foodId );
		
		if ( imageId == -1 ) 
		{
			journalId = db.insert ( TABLE_JOURNALS, Column.IMAGE_ID.getName (), values ); 
		}else
		{
			values.put ( Column.IMAGE_ID.getName (), journal.getImageEntry ().getId ());
			journalId = db.insert ( TABLE_JOURNALS, null, values );
		}
		
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
		values.put ( Column.FOOD_ID.getName (), journal.getFoodEntry ().getId () );
		values.put ( Column.IMAGE_ID.getName (), journal.getImageEntry ().getId () );

		return db.update ( TABLE_JOURNALS, values, Column.ID.getName () + " = " + journal.getId (), null );
	}

	public int delete ( long id )
	{
		return db.delete ( TABLE_JOURNALS, Column.ID.getName () + " = " + id, null );
	}

	
	public Map< Calendar, Float > getCaloriesForEachDay ( long timeInMillis )
	{
		Calendar calendar = Calendar.getInstance ();
		calendar.setTimeInMillis ( timeInMillis );
		return getCaloriesForMonth(calendar);
	}

	public Map< Calendar, Float > getCaloriesForMonth ( Calendar cal )
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		String monthRegex = formatter.format ( cal.getTimeInMillis () ) + "-__";
		
		Map< Calendar, Float > dayCaloriesMap = new HashMap< Calendar, Float > ();

		//This works. DO NOT TOUCH!!!!!!!!!!
		String unparameterQuery = "SELECT journals.date,SUM(foods.calories) FROM journals, foods WHERE (journals.food_id = foods._id) AND (journals.date LIKE %s) GROUP BY journals.date;";
		String query = String.format ( unparameterQuery, "'"+monthRegex+"'");
		Cursor c = this.db.rawQuery ( query, null);

		if ( c != null && c.moveToFirst () )
		{

			while ( !c.isAfterLast () )
			{
				String _date = c.getString ( 0 );
				formatter.applyPattern ( JournalEntry.DATE_FORMAT );
				long date = 0;
				
				try
				{
					
					date = formatter.parse ( _date ).getTime ();
					
				}
				catch ( ParseException e )
				{
					Log.e(TAG,"Could not parse date: "+_date+"Exception: "+e.toString ());
					continue;
				}
				

				Calendar calendar = Calendar.getInstance ();
				calendar.setTimeInMillis ( date );
				
				float calories = c.getFloat ( 1 );
				dayCaloriesMap.put ( calendar, calories );
				c.moveToNext ();
			}
		}

		return dayCaloriesMap;
	}

	public List< JournalEntry > getEntriesForDay ( Calendar cal ) throws ParseException
	{
		//TODO sort this out.
		SimpleDateFormat formatter = new SimpleDateFormat(JournalEntry.DATE_FORMAT);
		String monthRegex = formatter.format ( cal.getTimeInMillis () );
		
		List<JournalEntry> entries = new ArrayList<JournalEntry>();
		
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
				
				ImageEntry imageEntry = new ImageEntry();
				imageEntry.setId ( c.getLong ( 3 ) );
				imageEntry.setFileName ( c.getString ( 4 ) );
				
				JournalEntry journalEntry = new JournalEntry();
				journalEntry.setId ( c.getLong ( 5 ) );
				journalEntry.setTime ( c.getString ( 6 ) );
				journalEntry.setDate ( monthRegex );
				journalEntry.setFoodEntry ( foodEntry );
				journalEntry.setImageEntry ( imageEntry );
				entries.add ( journalEntry );
			}
		}
		
		return entries;
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

		FoodDAO foodDAO = new FoodDAO(this.db);
		FoodEntry foodEntry = foodDAO.read ( foodId );
		
		ImageDAO imageDAO = new ImageDAO(this.db);
		ImageEntry imageEntry = imageDAO.read ( imageId );
		
		return new JournalEntry ( id, date, time, foodEntry, imageEntry );
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
