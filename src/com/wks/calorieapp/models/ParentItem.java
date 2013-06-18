package com.wks.calorieapp.models;

import java.util.ArrayList;
import java.util.List;

public class ParentItem
{
	private String foodName;
	private List<NutritionInfo> nutritionInfoList;
	
	public ParentItem()
	{
		this.nutritionInfoList = new ArrayList<NutritionInfo>();
		
	}
	
	public String getFoodName ()
	{
		return foodName;
	}
	
	public List<NutritionInfo> getNutritionInfoList()
	{
		return this.nutritionInfoList;
	}
	
	
}
