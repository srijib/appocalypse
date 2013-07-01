package com.wks.calorieapp.models;

public class MaleBMRStrategy implements BMRStrategy
{

	@Override
	public float getBasalMetabolicRate ( int age, float weight, float height )
	{
		return ( float ) ( 10*(weight)+6.25*(height)-5*(age)+5 ); 
	}

	@Override
	public float getDailyCaloricNeeds ( int age, float weight, float height, float activityFactor )
	{
		// TODO Auto-generated method stub
		return this.getBasalMetabolicRate ( age, weight, height ) * activityFactor;
	}

}
