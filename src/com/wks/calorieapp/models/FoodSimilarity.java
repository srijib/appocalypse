package com.wks.calorieapp.models;

public class FoodSimilarity
{
	private String foodName;
	private float similarity;
	
	public FoodSimilarity()
	{
		
	}

	public String getFoodName ()
	{
		return foodName;
	}
	
	public void setFoodName ( String foodName )
	{
		this.foodName = foodName;
	}
	
	public float getSimilarity ()
	{
		return similarity;
	}
	
	public void setSimilarity ( float similarity )
	{
		this.similarity = similarity;
	}
}
