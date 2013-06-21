package com.wks.calorieapp.daos;

public class FoodDataTransferObject
{
	private long id;
	private String name;
	private float calories;
	
	public FoodDataTransferObject()
	{
		this(0,"null",0.0F);
	}
	
	public FoodDataTransferObject(long id, String name, float calories)
	{
		this.setId(id);
		this.setName(name);
		this.setCalories(calories);
	}
	
	public long getId ()
	{
		return id;
	}
	
	public void setId ( long id )
	{
		if(id < 0) throw new IllegalStateException("Id must be a positive integer");
		this.id = id;
	}
	
	public String getName ()
	{
		return name;
	}
	
	public void setName ( String name )
	{
		if(name == null || name.isEmpty ()) throw new IllegalStateException("name can not be empty");
		this.name = name;
	}
	
	public float getCalories ()
	{
		return calories;
	}
	
	public void setCalories ( float calories )
	{
		if(calories < 0F) throw new IllegalStateException("calories can not be a negative value");
		this.calories = calories;
	}
	

}
