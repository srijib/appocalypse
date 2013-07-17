package com.wks.calorieapp.activities;


import java.util.List;
import java.util.Map;

import com.wks.calorieapp.apis.NutritionInfo;
import com.wks.calorieapp.entities.Profile;

import android.app.Application;

public class CalorieApplication extends Application
{
	public static final String FILENAME_PROFILE_JSON = "profile.json";
	public static final String FILENAME_PROFILE_CSV = "profile.csv";
	
	private Profile profile;
	private static Map<String,List<NutritionInfo>> identifyResults;
	
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
	
	//I am sick fo androdi and I don't care how messy the code is anymore
	// i am not even going to bother refactoing this
	public Map<String,List<NutritionInfo>> getIdentifyResults()
	{
		return identifyResults;
	}
	
	public void setIdentifyResults(Map<String,List<NutritionInfo>> identifyResults)
	{
		CalorieApplication.identifyResults = identifyResults;
	}
	
}
