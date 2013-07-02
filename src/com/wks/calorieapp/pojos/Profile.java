package com.wks.calorieapp.pojos;

import org.json.simple.JSONObject;

import com.wks.calorieapp.utils.JSONWriteable;


public class Profile implements JSONWriteable
{	
	private static final int MIN_ZERO = 0;
	private static final int MAX_HEIGHT = 300;
	private static final int MAX_WEIGHT = 700;
	private static final int MAX_AGE = 100;
	private static final int MIN_AGE = 13;
	public static final float MIN_ACTIVITY_FACTOR = 1.200F;
	public static final float MAX_ACTIVITY_FACTOR = 1.900F;
	
	private static final float CALORIES_PER_KILOGRAM_LOST = 1000;

	public enum Sex
	{
		MALE, FEMALE
	};

	private Sex sex = Sex.MALE;
	private int age = MIN_AGE;
	private float height = MIN_ZERO;
	private float weight = MIN_ZERO;
	private float activityFactor = MIN_ACTIVITY_FACTOR;
	private int weightLossGoal = MIN_ZERO;
	
	public int getAge ()
	{
		return age;
	}

	public void setAge ( int age ) throws ProfileException
	{
		if(age <= MIN_ZERO || age > MAX_AGE)
			throw new ProfileException("Age must be between "+MIN_AGE+" and "+MAX_AGE+".");
		this.age = age;
	}

	public float getHeight ()
	{
		return height;
	}

	public void setHeight ( float height ) throws ProfileException
	{
		if(height <= MIN_ZERO)
			throw new ProfileException("Height must be between "+MIN_ZERO+" and "+MAX_HEIGHT+".");
		this.height = height;
	}

	public float getWeight ()
	{
		return weight;
	}

	public void setWeight ( float weight ) throws ProfileException
	{
		if(weight <= MIN_ZERO )
			throw new ProfileException("Weight must be between "+MIN_ZERO+" and "+MAX_WEIGHT+".");
		this.weight = weight;
	}

	public float getActivityFactor ()
	{
		return activityFactor;
	}

	public void setActivityFactor ( float activityFactor ) 
	{
		
		this.activityFactor = activityFactor;
	}

	public int getWeightLossGoal ()
	{
		return weightLossGoal;
	}

	public void setWeightLossGoal ( int weightLossGoal ) throws ProfileException
	{
		if(weightLossGoal > this.weight)
			throw new ProfileException("Weight Loss Goal can't be greater than actual weight.");
		this.weightLossGoal = weightLossGoal;
	}

	
	public Sex getSex ()
	{
		return sex;
	}
	
	public void setSex ( Sex sex )
	{
		this.sex = sex;
		
	}
	
	public float getRecommendedDailyCalories()
	{
		double bmr = 10*this.weight + 6.25*height -5*this.age;
		if(this.sex.equals ( Sex.MALE ))
			bmr += 5;
		else
			bmr -= 161;
		
		double dailyCaloricNeeds = this.activityFactor * bmr;
		double dailyCaloricNeedsFactoringWeightLoss = dailyCaloricNeeds - this.weightLossGoal*(CALORIES_PER_KILOGRAM_LOST);
		
		return (float) dailyCaloricNeedsFactoringWeightLoss;
	}

	//----------------JSONWriteable Interface-------//
	public static final String KEY_AGE = "age";
	public static final String KEY_SEX = "sex";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_WEIGHT = "weight";
	public static final String KEY_ACTIVITY_FACTOR = "activity_factor";
	public static final String KEY_WEIGHT_LOSS_GOAL = "weight_loss_goal";
	
	@SuppressWarnings ( "unchecked" )
	@Override
	public String toJSON ()
	{
		JSONObject profileJSON = new JSONObject();
		profileJSON.put ( KEY_AGE, this.age );
		profileJSON.put ( KEY_SEX, this.sex.toString () );
		profileJSON.put ( KEY_HEIGHT, this.height );
		profileJSON.put ( KEY_WEIGHT, this.weightLossGoal );
		profileJSON.put ( KEY_WEIGHT_LOSS_GOAL, this.weightLossGoal );
		profileJSON.put ( KEY_ACTIVITY_FACTOR, this.activityFactor );
		
		return profileJSON.toJSONString ();
	}
	
}
