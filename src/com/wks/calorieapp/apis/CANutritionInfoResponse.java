package com.wks.calorieapp.apis;

import java.util.List;


public class CANutritionInfoResponse extends CAAbstractResponse
{
	List<NutritionInfo> nutritionInfo;
	
	public CANutritionInfoResponse ( long code )
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
