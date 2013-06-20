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
	private static Typeface cantarellRegular;
	private static Typeface cantarellBold;
	
	@Override
	public void onCreate ()
	{
		super.onCreate ();
		CalorieApplication.instance = this;
		
		cantarellRegular = Typeface.createFromAsset (this.getAssets (), "Cantarell-Regular.ttf" );
		cantarellBold = Typeface.createFromAsset(this.getAssets (), "Cantarell-Bold.ttf");
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
	
	public static Typeface getFont(Font typeface)
	{
		switch(typeface)
		{
		case CANTARELL_REGULAR: return cantarellRegular;
		case CANTARELL_BOLD: return cantarellBold;
		default: return cantarellRegular;
		}
	}
	
	public enum Font
	{
		CANTARELL_REGULAR("Cantarell-Regular.ttf"),
		CANTARELL_BOLD("Cantarell-Bold.ttf");
		
		private final String font;
		
		private Font(String font)
		{
			this.font = font;
		}
		
		public String getFont ()
		{
			return font;
		}
	}
}
