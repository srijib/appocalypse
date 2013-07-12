package com.wks.calorieapp.apis;

public class CAResponse extends CAAbstractResponse
{
	private String message;
	
	public CAResponse(long code, String message)
	{
		super(code);
		this.message = message;
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
