package com.wks.calorieapp.entities;

import android.graphics.drawable.Drawable;

public class CalendarEvent
{
	private String description = "";
	private Drawable drawable = null;
	private int backgroundColor = 0;
	
	public String getDescription ()
	{
		return description;
	}
	
	public void setDescription ( String description )
	{
		this.description = description;
	}
	
	public int getBackgroundColor ()
	{
		return backgroundColor;
	}
	
	public void setBackgroundColor ( int backgroundColor )
	{
		this.backgroundColor = backgroundColor;
	}
	
	public Drawable getDrawable ()
	{
		return drawable;
	}
	
	public void setDrawable ( Drawable drawable )
	{
		this.drawable = drawable;
	}
	
	
}
