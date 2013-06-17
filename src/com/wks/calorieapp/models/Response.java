package com.wks.calorieapp.models;

import java.util.List;

public class Response
{
	private boolean successful;
	private String message;
	private List<?> data;
	
	public Response()
	{
		
	}
	
	public boolean isSuccessful ()
	{
		return successful;
	}
	
	public void setSuccessful ( boolean successful )
	{
		this.successful = successful;
	}
	
	public List<?> getData ()
	{
		return data;
	}
	
	public void setData ( List<?> data )
	{
		this.data = data;
	}
	
	public String getMessage ()
	{
		return message;
	}
	
	public void setMessage ( String message )
	{
		this.message = message;
	}
	
	
}
