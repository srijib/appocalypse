package com.wks.calorieapp.daos;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class JournalDataTransferObject
{
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private long id;
	private long timestamp;
	private long foodId;
	private long imageId;
	
	public JournalDataTransferObject()
	{
		this(0,0,0,0);
	}
	
	public JournalDataTransferObject(long id, long timestamp, long foodId, long imageId)
	{
		this.setId(id);
		this.setTimestamp(timestamp);
		this.setFoodId(foodId);
		this.setImageId(imageId);
	}
	
	public JournalDataTransferObject(long id, String timestamp, long foodId, long imageId) throws ParseException
	{
		this.setId ( id );
		this.setTimestamp ( timestamp );
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
	
	public void setTimestamp ( long timestamp )
	{
		if(timestamp < 0) throw new IllegalStateException("timestamp must be a positive value");
		this.timestamp = timestamp;
	}
	
	public long getTimestamp ()
	{
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) throws ParseException
	{
		if(timestamp == null || timestamp.isEmpty ()) throw new IllegalStateException("Timestamp must match format:"+TIMESTAMP_FORMAT);
		
		SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
		this.timestamp = formatter.parse ( timestamp ).getTime ();
	}
	
	public String getTimestampString()
	{
		SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
		return formatter.format ( this.timestamp );
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
