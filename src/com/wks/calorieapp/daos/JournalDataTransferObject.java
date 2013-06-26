package com.wks.calorieapp.daos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JournalDataTransferObject
{
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TIME_FORMAT = "HH:mm:ss";
	
	private long id;
	private long date;
	private long time = 0;
	private long foodId = -1;
	private long imageId = -1;
	
	public JournalDataTransferObject()
	{
		this(0,0,0,0,0);
	}
	
	public JournalDataTransferObject(long id, long date,long time, long foodId, long imageId)
	{
		this.setId(id);
		this.setDate(date);
		this.setTime(time);
		this.setFoodId(foodId);
		this.setImageId(imageId);
	}
	
	public JournalDataTransferObject(long id, String date,String time, long foodId, long imageId) throws ParseException
	{
		this.setId ( id );
		this.setDate(date);
		this.setTime(time);
		this.setFoodId ( foodId );
		this.setImageId ( imageId );
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
	
	
	public void setImageId ( long imageId )
	{
		if(id < 0 ) throw new IllegalStateException("food id must be a positive integer.");
		this.imageId = imageId;
	}
	
	public long getImageId ()
	{
		return imageId;
	}
	
	public void setFoodId ( long foodId )
	{
		if(id < 0 ) throw new IllegalStateException("image id must be a positive integer.");
		this.foodId = foodId;
	}
	
	public long getFoodId ()
	{
		return foodId;
	}
	
}
