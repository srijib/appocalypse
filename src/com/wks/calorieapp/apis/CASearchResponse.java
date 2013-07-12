package com.wks.calorieapp.apis;

import java.util.List;


public class CASearchResponse extends CAAbstractResponse
{
	List<NutritionInfo> nutritionInfo;
	
	public CASearchResponse ( long code )
	{
		super ( code );
		// TODO Auto-generated constructor stub
	}
	
	public void setNutritionInfo ( List< NutritionInfo > nutritionInfo )
	{
		this.nutritionInfo = nutritionInfo;
	}
	
	public List< NutritionInfo > getNutritionInfo ()
	{
		return nutritionInfo;
	}
}
