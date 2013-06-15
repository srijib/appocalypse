package com.wks.calorieapp.models;

public class Response
{
	private boolean successful;
	private String message;
	private Object data;
	
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
	
	public Object getData ()
	{
		return data;
	}
	
	public void setData ( Object data )
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
