package com.wks.calorieapp.pojos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JournalEntry
{
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TIME_FORMAT = "HH:mm:ss";
	
	private long id;
	private long date;
	private long time = 0;
	private FoodEntry foodEntry;
	private ImageEntry imageEntry;
	
	public JournalEntry()
	{
		this(0,0,0,null,null);
	}
	
	public JournalEntry(long id, long date,long time, FoodEntry foodEntry, ImageEntry imageEntry)
	{
		this.setId(id);
		this.setDate(date);
		this.setTime(time);
		this.setFoodEntry(foodEntry);
		this.setImageEntry(imageEntry);
	}
	
	public JournalEntry(long id, String date,String time, FoodEntry foodEntry, ImageEntry imageEntry) throws ParseException
	{
		this.setId ( id );
		this.setDate(date);
		this.setTime(time);
		this.setFoodEntry(foodEntry);
		this.setImageEntry(imageEntry);
	}
	
	public void setId ( long id )
	{
		if(id < 0 ) throw new IllegalStateException("id must be a positive integer.");
		this.id = id;
	}
	
	public long getId ()
	{
		return id;
	}
	
	public void setDate ( long date )
	{
		if(date < 0) throw new IllegalStateException("timestamp must be a positive value");
		this.date = date;
	}
	
	private void setTime(long time)
	{
		if(time < 0) throw new IllegalStateException("timestamp must be a positive value");
		this.time = time;	
	}
	
	public long getDate ()
	{
		return date;
	}
	
	public long getTime()
	{
		return time;
	}
	
	public void setDate(String date) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		Date d = formatter.parse ( date );
		this.date = d.getTime ();
	}
	
	public void setTime(String time) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
		Date d = formatter.parse ( time );
		this.time = d.getTime ();
	}
	
	public String getDateAsString()
	{
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		return formatter.format ( this.date );
	}
	
	public String getTimeAsString()
	{
		SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
		return formatter.format ( this.time );
	}
	
	
	public void setFoodEntry ( FoodEntry foodEntry )
	{
		this.foodEntry = foodEntry;
	}
	
	public FoodEntry getFoodEntry ()
	{
		return foodEntry;
	}
	
	public void setImageEntry ( ImageEntry imageEntry )
	{
		this.imageEntry = imageEntry;
	}
	
	public ImageEntry getImageEntry ()
	{
		return imageEntry;
	}
	
	@Override
	public String toString ()
	{
		long foodId = this.foodEntry == null? -1:this.foodEntry.getId ();
		long imageId = this.imageEntry == null? -1: this.imageEntry.getId ();
		return String.format ( "id: %d,date: %s,time: %s,foodId: %d,imageId: %d", id,this.getDateAsString (),this.getTimeAsString (),foodId,imageId );
	}
}
