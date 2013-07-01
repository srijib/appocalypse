package com.wks.calorieapp.models;

import java.util.ArrayList;
import java.util.List;

public class Profile implements Publisher
{
	public static final float MIN_ACTIVITY_FACTOR = 1.200F;
	public static final float MAX_ACTIVITY_FACTOR = 1.900F;

	public enum Sex
	{
		MALE, FEMALE
	};

	private Sex sex = Sex.MALE;
	private int age = 0;
	private float height = 0;
	private float weight = 0;
	private float activityFactor = MIN_ACTIVITY_FACTOR;
	private int weightLossGoal = 0;
	private BMRStrategy basalMetabolicRate;
	
	public int getAge ()
	{
		return age;
	}

	public void setAge ( int age )
	{
		
		this.age = age;
	}

	public float getHeight ()
	{
		return height;
	}

	public void setHeight ( float height )
	{
		this.height = height;
	}

	public float getWeight ()
	{
		return weight;
	}

	public void setWeight ( float weight )
	{
		this.weight = weight;
	}

	public float getActivityFactor ()
	{
		return activityFactor;
	}

	public void setActivityFactor ( float activityFactor )
	{
		if(activityFactor < MIN_ACTIVITY_FACTOR) this.activityFactor = MIN_ACTIVITY_FACTOR;
		if(activityFactor > MAX_ACTIVITY_FACTOR) this.activityFactor = MAX_ACTIVITY_FACTOR;
			
		
		this.activityFactor = activityFactor;
	}

	public int getWeightLossGoal ()
	{
		return weightLossGoal;
	}

	public void setWeightLossGoal ( int weightLossGoal )
	{
		if(weightLossGoal > this.weight) weightLossGoal = 0;
		this.weightLossGoal = weightLossGoal;
	}

	public float getDailyCaloricNeeds ()
	{
		float dailyCaloricNeeds = 0;
		if(this.basalMetabolicRate != null)
		{
			dailyCaloricNeeds = this.basalMetabolicRate.getDailyCaloricNeeds ( this.age, this.weight - this.weightLossGoal, this.height, this.activityFactor );
		}
		return dailyCaloricNeeds;
	}
	
	public Sex getSex ()
	{
		return sex;
	}
	
	public void setSex ( Sex sex )
	{
		this.sex = sex;
		this.basalMetabolicRate = sex==Sex.MALE? new MaleBMRStrategy(): new FemaleBMRStrategy();
	}
	
	//---------------SUBSCRIBER INTERFACE --------------//
	private List<Subscriber> subscribers = new ArrayList<Subscriber>();

	@Override
	public void addSubscriber ( Subscriber subscriber )
	{
		this.subscribers.add ( subscriber );
		
	}

	@Override
	public void removeSubscriber ( Subscriber subscriber )
	{
		this.subscribers.remove ( subscriber );
	}

	@Override
	public void notifySubscribers ()
	{
		for(Subscriber subscriber: this.subscribers)
			subscriber.update ( this );
	}
	
	
}
