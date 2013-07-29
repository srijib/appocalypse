package com.wks.calorieapp.apis;

public abstract class CAAbstractResponse
{
	public static final String KEY_CODE = "code";
	public static final String KEY_MESSAGE = "message";
	long code;
	
	public CAAbstractResponse(long code)
	{
		this.code = code;
	}
	
	public long getCode ()
	{
		return code;
	}
	
	public void setCode ( long code )
	{
		this.code = code;
	}
	
	public final boolean isSuccessful()
	{
		return this.code == 0;
	}
}
