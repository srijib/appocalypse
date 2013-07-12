package com.wks.calorieapp.apis;

import java.util.List;
import java.util.Map;


public class CARecognizeResponse extends CAAbstractResponse
{
	Map<String,List<NutritionInfo>> nutritionInfo;

	public CARecognizeResponse ( long code )
	{
		super ( code );
		
	}

	public Map< String, List< NutritionInfo >> getNutritionInfo ()
	{
		return nutritionInfo;
	}
	
	public void setNutritionInfo ( Map< String, List< NutritionInfo >> nutritionInfo )
	{
		this.nutritionInfo = nutritionInfo;
	}
	
	
}
