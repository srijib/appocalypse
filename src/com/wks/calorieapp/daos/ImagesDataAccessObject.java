package com.wks.calorieapp.daos;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ImagesDataAccessObject
{

	public static final String TABLE_IMAGES = "images";
	public static final String[] COLUMNS = {Column.ID.name, Column.FILE_NAME.name};
	
	private SQLiteDatabase db;
	private CADatabaseHelper helper;
	
	public ImagesDataAccessObject(Context context)
	{
		this.helper = CADatabaseHelper.getInstance ( context );
	}
	
	public void open()
	{
		this.db = this.helper.getWritableDatabase ();
	}
	
	public void close()
	{
		this.db.close ();
	}
	
	public long create(ImageDataTransferObject image)
	{
		ContentValues values = new ContentValues();
		values.put ( Column.FILE_NAME.getName (), image.getFileName () );
		return db.insert ( TABLE_IMAGES, null, values );
	}
	
	public ImageDataTransferObject read(long id)
	{
		ImageDataTransferObject image = null;
		
		Cursor c = db.query ( TABLE_IMAGES, COLUMNS, Column.ID.getName ()+" = "+id, null, null, null, null );
		
		if(c != null)
		{
			c.moveToFirst ();
			image = cursorToImageDataTransferObject(c);
		}
		
		c.close ();
		return image;
	}
	
	public List<ImageDataTransferObject> read()
	{
		List<ImageDataTransferObject> images = new ArrayList<ImageDataTransferObject>();
		
		Cursor c = db.query ( TABLE_IMAGES, COLUMNS, null, null, null, null, null );
		
		if(c != null)
		{
			c.moveToFirst ();
			while(!c.isAfterLast ())
			{
				images.add ( cursorToImageDataTransferObject(c) );
				c.moveToNext ();
			}
		}
		
		c.close ();
		return images;
	}
	
	public int update(ImageDataTransferObject image)
	{
		ContentValues values = new ContentValues();
		values.put ( Column.ID.getName (), image.getId () );
		values.put ( Column.FILE_NAME.getName (), image.getFileName () );
		
		return db.update ( TABLE_IMAGES, values, Column.ID.getName ()+" = "+image.getId (), null );
	}
	
	public int delete(long id)
	{
		return db.delete ( TABLE_IMAGES, Column.ID.getName ()+" = "+id, null );
	}
	
	private ImageDataTransferObject cursorToImageDataTransferObject(Cursor c)
	{
		long id = c.getLong ( Column.ID.ordinal () );
		String fileName = c.getString ( Column.FILE_NAME.ordinal () );
		
		return new ImageDataTransferObject(id,fileName);
	}

	
	
	
	public static enum Column
	{
		ID("_id"),
		FILE_NAME("file_name");
		
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