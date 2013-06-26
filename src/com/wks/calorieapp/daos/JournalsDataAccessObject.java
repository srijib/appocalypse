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
	private Context context;

	public JournalsDataAccessObject ( SQLiteDatabase db)
	{
		this.db = db;
	}

	
	public long create ( JournalDataTransferObject journal )
	{
		ContentValues values = new ContentValues();
		values.put ( Column.DATE.getName (), journal.getDateAsString () );
		values.put ( Column.TIME.getName (), journal.getTimeAsString ());
		values.put ( Column.FOOD_ID.getName (), journal.getFoodId () );
		
		if(journal.getImageId () < 0)
		{
			return db.insert ( TABLE_JOURNALS, Column.IMAGE_ID.getName (), values );
		}
		
		values.put ( Column.IMAGE_ID.getName (), journal.getImageId () );
		return db.insert ( TABLE_JOURNALS, null, values );
	}
	
	public long create(JournalDataTransferObject journal, FoodDataTransferObject food, ImageDataTransferObject image)
	{
		if(journal == null || food == null)
			throw new IllegalStateException("A Journal entry requires a journalDTO and a foodDTO.");
		
		this.db.beginTransaction ();
		
		//add food item
		FoodsDataAccessObject foodDao = new FoodsDataAccessObject( this.db/*this.context*/);
		FoodDataTransferObject foodDto = foodDao.read ( food.getId () );

		long foodId = -1;
		if(foodDto != null)
		{
			foodId = foodDao.create ( food );
			if(foodId == -1)
			{
				db.endTransaction ();
				return -1;
			}
		}
		
		//image may be null.
		if(image == null)
		{
			journal.setFoodId ( foodId );
			journal.setImageId ( -1 );
			
		}else
		{
			//add image item
			ImagesDataAccessObject imageDao = new ImagesDataAccessObject( this.db/*this.context*/);
		
			long imageId = imageDao.create ( image );
			if(imageId == -1)
			{
				db.endTransaction ();
				return -1;
			}
			
			journal.setFoodId ( foodId );
			journal.setImageId ( imageId );
		}
		
		long journalId =  this.create ( journal );
		if(journalId != -1)
		{
			db.setTransactionSuccessful ();
		}
		
		db.endTransaction ();
		return journalId;
	}
	
	public JournalDataTransferObject read(long id) throws ParseException
	{
		JournalDataTransferObject journal = null;
		
		Cursor c = db.query ( TABLE_JOURNALS, COLUMNS, Column.ID.getName ()+" = "+id, null, null, null, null );
		
		if(c != null && c.moveToFirst ())
		{	
			journal = cursorToJournalDataTransferObject(c);
		}
		
		c.close ();
		return journal;
	}
	
	public List<JournalDataTransferObject> read() throws ParseException
	{
		List<JournalDataTransferObject> journals = new ArrayList<JournalDataTransferObject>();
		
		Cursor c = db.query ( TABLE_JOURNALS, COLUMNS, null, null, null, null, null );
		
		if(c != null && c.moveToFirst ())
		{
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
