package com.wks.calorieapp.activities;


import java.util.List;
import java.util.Map;

import com.wks.calorieapp.pojos.NutritionInfo;
import com.wks.calorieapp.pojos.Profile;

import android.app.Application;

public class CalorieApplication extends Application
{
	public static final String FILENAME_PROFILE_JSON = "profile2.json";
	public static final String FILENAME_PROFILE_CSV = "profile.csv";
	
	private Profile profile;
	private static Map<String,List<NutritionInfo>> nutritionInfoDictionary;

	
	@Override
	public void onCreate ()
	{
		super.onCreate ();
	}
	
	
	public static void setNutritionInfoDictionary(Map<String,List<NutritionInfo>> dictionary)
	{
		CalorieApplication.nutritionInfoDictionary = dictionary;
	}
	
	public static Map<String,List<NutritionInfo>> getNutritionInfoMap()
	{
		return CalorieApplication.nutritionInfoDictionary;
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
