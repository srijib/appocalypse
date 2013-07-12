package com.wks.calorieapp.apis;

/**
 * TODO: I think the calculation of nutrients per 100g is incorrent. Double
 * check results.
 * 
 * @author Waqqas
 * 
 */
public class NutritionInfo 
{
	private long id;
	private String name;
	private String type;
	private String url;
	private float kiloCaloriesPer100g = 0;
	private float gramFatPer100g = 0;
	private float gramCarbsPer100g = 0;
	private float gramProteinsPer100g = 0;

	public NutritionInfo ()
	{

	}

	
	public long getId ()
	{
		return id;
	}

	public void setId ( long id )
	{
		this.id = id;
	}

	public String getName ()
	{
		return name;
	}

	public void setName ( String name )
	{
		this.name = name;
	}

	public String getType ()
	{
		return type;
	}

	public void setType ( String type )
	{
		this.type = type;
	}

	public float getCaloriesPer100g ()
	{
		return kiloCaloriesPer100g;
	}

	public void setCaloriesPer100g ( float caloriesPer100g )
	{
		this.kiloCaloriesPer100g = caloriesPer100g;
	}

	public float getFatPer100g ()
	{
		return gramFatPer100g;
	}

	public void setGramFatPer100g ( float fatPer100g )
	{
		this.gramFatPer100g = fatPer100g;
	}

	public float getGramCarbsPer100g ()
	{
		return gramCarbsPer100g;
	}

	public void setGramCarbsPer100g ( float carbsPer100g )
	{
		this.gramCarbsPer100g = carbsPer100g;
	}

	public float getGramProteinsPer100g ()
	{
		return gramProteinsPer100g;
	}

	public void setGramProteinsPer100g ( float proteinsPer100g )
	{
		this.gramProteinsPer100g = proteinsPer100g;
	}
	/*
	public void setDescription ( String description )
	{
		this.description = description;
		parseDescription ();
	}

	public String getDescription ()
	{
		return description;
	}
	*/
	public String getUrl ()
	{
		return url;
	}

	public void setUrl ( String url )
	{
		this.url = url;
	}
	
	@Override
	public String toString ()
	{
		return String.format ( "[id: %d,name: %s,calories: %f]",
				id, name, kiloCaloriesPer100g);
	}
}
