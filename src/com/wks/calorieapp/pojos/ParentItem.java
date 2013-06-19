package com.wks.calorieapp.pojos;

import java.util.ArrayList;
import java.util.List;


public class ParentItem
{
	private String foodName;
	private List<NutritionInfo> nutritionInfoList;
	
	public ParentItem()
	{
		this("");
	}
	
	public ParentItem(String foodName)
	{
		this.foodName = foodName;
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
