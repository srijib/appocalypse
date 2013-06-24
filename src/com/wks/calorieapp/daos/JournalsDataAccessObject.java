package com.wks.calorieapp.daos;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class JournalsDataAccessObject
{

	public static final String TABLE_JOURNALS = "journals";
	public static final String[] COLUMNS = {Column.ID.getName (),Column.DATE.getName (),Column.TIME.getName (),Column.FOOD_ID.getName (),Column.IMAGE_ID.getName ()};

	private SQLiteDatabase db;
	private CADatabaseHelper helper;

	public JournalsDataAccessObject ( Context context )
	{
		this.helper = CADatabaseHelper.getInstance ( context );
	}

	public void open ()
	{
		this.db = this.helper.getWritableDatabase ();
	}

	public void close ()
	{
		this.helper.close ();
	}

	public long create ( JournalDataTransferObject journal )
	{
		ContentValues values = new ContentValues();
		values.put ( Column.DATE.getName (), journal.getDateAsString () );
		values.put ( Column.TIME.getName (), journal.getTimeAsString ());
		values.put ( Column.FOOD_ID.getName (), journal.getFoodId () );
		values.put ( Column.IMAGE_ID.getName (), journal.getImageId () );
		return db.insert ( TABLE_JOURNALS, null, values );
	}
	
	public JournalDataTransferObject read(long id) throws ParseException
	{
		JournalDataTransferObject journal = null;
		
		Cursor c = db.query ( TABLE_JOURNALS, COLUMNS, Column.ID.getName ()+" = "+id, null, null, null, null );
		
		if(c != null)
		{
			c.moveToFirst ();
			journal = cursorToJournalDataTransferObject(c);
		}
		
		c.close ();
		return journal;
	}
	
	public List<JournalDataTransferObject> read() throws ParseException
	{
		List<JournalDataTransferObject> journals = new ArrayList<JournalDataTransferObject>();
		
		Cursor c = db.query ( TABLE_JOURNALS, COLUMNS, null, null, null, null, null );
		
		if(c != null)
		{
			c.moveToFirst ();
			while(!c.isAfterLast ())
			{
				journals.add ( cursorToJournalDataTransferObject(c) );
				c.moveToNext ();
			}
		}
		
		c.close ();
		return journals;
	}
	
	public int update(JournalDataTransferObject journal)
	{
		ContentValues values = new ContentValues();
		values.put ( Column.ID.getName (), journal.getId () );
		values.put ( Column.DATE.getName (), journal.getDateAsString () );
		values.put ( Column.TIME.getName (), journal.getTimeAsString () );
		values.put ( Column.FOOD_ID.getName (), journal.getFoodId () );
		values.put ( Column.IMAGE_ID.getName (), journal.getImageId () );
		
		return db.update ( TABLE_JOURNALS, values, Column.ID.getName ()+" = "+journal.getId (), null );
	}
	
	public int delete(long id)
	{
		return db.delete ( TABLE_JOURNALS, Column.ID.getName ()+" = "+id, null );
	}
	
	private JournalDataTransferObject cursorToJournalDataTransferObject(Cursor c) throws ParseException
	{
		long id = c.getLong ( Column.ID.ordinal () );
		String date = c.getString ( Column.DATE.ordinal () );
		String time = c.getString ( Column.TIME.ordinal () );
		long foodId = c.getLong ( Column.FOOD_ID.ordinal() );
		long imageId = c.getLong ( Column.IMAGE_ID.ordinal () );
		
		return new JournalDataTransferObject(id,date,time,foodId,imageId);
	}

	public static enum Column
	{
		ID ( "_id" ), DATE ( "date" ),TIME("time"), FOOD_ID ( "food_id" ), IMAGE_ID ( "image_id" );

		private final String name;

		Column ( String name )
		{

			this.name = name;
		}

		public String getName ()
		{
			return name;
		}
	}
}
