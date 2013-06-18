package com.wks.calorieapp.models;

public class FoodSimilarity
{
	private String foodName;
	private float similarity;
	
	public FoodSimilarity()
	{
		
	}

	public FoodSimilarity ( String foodName, float similarity )
	{
		this.foodName = foodName;
		this.similarity = similarity;
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
	
	@Override
	public int hashCode ()
	{
		return foodName.hashCode ();
	}
	
	@Override
	public boolean equals ( Object o )
	{
		FoodSimilarity other = (FoodSimilarity) o;
		return foodName.equals( other.getFoodName () );
	}
}
