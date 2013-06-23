package com.wks.calorieapp.adapters;

public class GridItem
{
	private String text;
	private int resourceId;
	
	public GridItem ()
	{
		// TODO Auto-generated constructor stub
	}
	
	public GridItem(String text, int resourceId)
	{
		this.text = text;
		this.resourceId = resourceId;
	}
	
	public String getText ()
	{
		return text;
	}
	
	public void setText ( String text )
	{
		this.text = text;
	}
	
	public int getResourceId ()
	{
		return resourceId;
	}
	
	public void setResourceId ( int resourceId )
	{
		this.resourceId = resourceId;
	}
}

