package com.wks.calorieapp.activities;

public enum ExtraKey
{
	KEY_PROFILE_ACTIVITY_MODE("profile_mode");
	
	private final String key;
	
	private ExtraKey(String key)
	{
		this.key = key;
	}
	
	public String key()
	{
		return this.key;
	}
}
