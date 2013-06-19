package com.wks.calorieapp.activities;


import java.util.List;
import java.util.Map;

import com.wks.calorieapp.pojos.NutritionInfo;

import android.app.Application;
import android.graphics.Typeface;

public class CalorieApplication extends Application
{
	private static CalorieApplication instance;
	private static Map<String,List<NutritionInfo>> nutritionInfoDictionary;
	private static Typeface cantarell;
	
	@Override
	public void onCreate ()
	{
		super.onCreate ();
		CalorieApplication.instance = this;
		
		cantarell = Typeface.createFromAsset (this.getAssets (), "Cantarell-Regular.ttf" );
	}
	
	public static CalorieApplication getInstance()
	{
		return CalorieApplication.instance;
	}
	
	public static void setNutritionInfoDictionary(Map<String,List<NutritionInfo>> dictionary)
	{
		CalorieApplication.nutritionInfoDictionary = dictionary;
	}
	
	public static Map<String,List<NutritionInfo>> getNutritionInfoDictionary()
	{
		return CalorieApplication.nutritionInfoDictionary;
	}
	
	public static Typeface getTypefaceCantarell()
	{
		return cantarell;
	}
}
