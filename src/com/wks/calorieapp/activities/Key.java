package com.wks.calorieapp.activities;

public enum Key
{
	PROFILE_MODE("profile_mode"),
	DATE_CALORIES_DATE("date");
	
	private final String key;
	
	private Key(String key)
	{
		this.key = key;
	}
	
	public String key()
	{
		return this.key;
	}
}
