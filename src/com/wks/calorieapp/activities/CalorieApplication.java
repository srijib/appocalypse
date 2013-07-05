package com.wks.calorieapp.activities;


import com.wks.calorieapp.pojos.Profile;

import android.app.Application;

public class CalorieApplication extends Application
{
	//public static final String FILENAME_PROFILE_JSON = "profile2.json";
	public static final String FILENAME_PROFILE_CSV = "profile.csv";
	
	private Profile profile;

	
	@Override
	public void onCreate ()
	{
		super.onCreate ();
	}
	
	public Profile getProfile()
	{
		return this.profile;
	}
	
	public void setProfile(Profile profile)
	{
		if(profile == null)
			throw new IllegalStateException("Profile can not be null");
		this.profile = profile;
	}
	
	
}
