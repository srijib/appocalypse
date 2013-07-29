package com.wks.calorieapp.daos;

import java.util.ArrayList;
import java.util.List;

import com.wks.calorieapp.entities.ImageEntry;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ImageDAO implements DataAccessObject<ImageEntry>
{

	public static final String TABLE_IMAGES = "images";
	public static final String [] COLUMNS = { Column.ID.name, Column.FILE_NAME.name };

	private SQLiteDatabase db;

	public ImageDAO ( SQLiteDatabase db )
	{
		this.db = db;
		
	}


	public long create ( ImageEntry image )
	{
		ContentValues values = new ContentValues ();
		values.put ( Column.FILE_NAME.getName (), image.getFileName () );
		return db.insert ( TABLE_IMAGES, null, values );
	}

	public ImageEntry read ( long id )
	{
		ImageEntry image = null;

		Cursor c = db.query ( TABLE_IMAGES, COLUMNS, Column.ID.getName () + " = " + id, null, null, null, null );

		if ( c != null && c.moveToFirst () )
		{
			image = cursorToImageDataTransferObject ( c );
		}

		c.close ();
		return image;
	}

	public List< ImageEntry > read ()
	{
		List< ImageEntry > images = new ArrayList< ImageEntry > ();

		Cursor c = db.query ( TABLE_IMAGES, COLUMNS, null, null, null, null, null );

		if ( c != null && c.moveToFirst () )
		{
			while ( !c.isAfterLast () )
			{
				images.add ( cursorToImageDataTransferObject ( c ) );
				c.moveToNext ();
			}
		}

		c.close ();
		return images;
	}

	public int update ( ImageEntry image )
	{
		ContentValues values = new ContentValues ();
		values.put ( Column.ID.getName (), image.getId () );
		values.put ( Column.FILE_NAME.getName (), image.getFileName () );

		return db.update ( TABLE_IMAGES, values, Column.ID.getName () + " = " + image.getId (), null );
	}

	public int delete ( long id )
	{
		return db.delete ( TABLE_IMAGES, Column.ID.getName () + " = " + id, null );
	}

	private ImageEntry cursorToImageDataTransferObject ( Cursor c )
	{
		long id = c.getLong ( Column.ID.ordinal () );
		String fileName = c.getString ( Column.FILE_NAME.ordinal () );

		return new ImageEntry ( id, fileName );
	}

	public static enum Column
	{
		ID ( "_id" ), FILE_NAME ( "file_name" );

		private final String name;

		Column ( String name )
		{
			this.name = name;
		}

		public String getName ()
		{
			return name;
		}
		
		public String getFullName()
		{
			return ImageDAO.TABLE_IMAGES+"."+name;
		}
	}


}
