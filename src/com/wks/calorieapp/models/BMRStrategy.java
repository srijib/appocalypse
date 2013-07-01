package com.wks.calorieapp.models;

public interface BMRStrategy
{
	public float getBasalMetabolicRate(int age, float weight, float height );
	public float getDailyCaloricNeeds(int age, float weight, float height, float activityFactor);
}
