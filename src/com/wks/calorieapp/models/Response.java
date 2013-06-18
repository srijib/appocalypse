package com.wks.calorieapp.models;

import java.util.List;

public class Response
{
	private int statusCode;
	private String message;
	private Object data;
	
	public Response()
	{
		
	}
	
	public Response(int statusCode,String message,List<?> data)
	{
		this.statusCode = statusCode;
		this.message = message;
		this.data = data;
	}
	
	public int getStatusCode ()
	{
		return statusCode;
	}
	
	public void setStatusCode ( int statusCode )
	{
		this.statusCode = statusCode;
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
	
	public boolean isSuccessful()
	{
		return this.statusCode == StatusCode.OK.getCode ();
	}
	
	
}
