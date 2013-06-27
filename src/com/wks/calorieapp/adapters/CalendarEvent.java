package com.wks.calorieapp.adapters;

import android.graphics.drawable.Drawable;

public class CalendarEvent
{
	private String eventDescription = "";
	private Drawable eventImage = null;
	private int eventBackgroundColor = 0;
	
	public String getEventDescription ()
	{
		return eventDescription;
	}
	
	public void setEventDescription ( String eventDescription )
	{
		this.eventDescription = eventDescription;
	}
	
	public int getEventBackgroundColor ()
	{
		return eventBackgroundColor;
	}
	
	public void setEventBackgroundColor ( int eventBackgroundColor )
	{
		this.eventBackgroundColor = eventBackgroundColor;
	}
	
	public Drawable getEventImage ()
	{
		return eventImage;
	}
	
	public void setEventImage ( Drawable eventImage )
	{
		this.eventImage = eventImage;
	}
	
	
}
