package com.wks.calorieapp.apis;

public class NutritionInfo extends CAAbstractResponse
{
	
	private long id;
	private String name;
	private String type;
	private String url;
	private float calories = 0;
	private float gramFat = 0;
	private float gramCarbs = 0;
	private float gramProteins = 0;

	public NutritionInfo ()
	{
		super(-1);
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

	public float getCalories ()
	{
		return calories;
	}

	public void setCalories ( float calories )
	{
		this.calories = calories;
	}

	public float getGramFat ()
	{
		return gramFat;
	}

	public void setGramFat ( float gramFat )
	{
		this.gramFat = gramFat;
	}

	public float getGramCarbs ()
	{
		return gramCarbs;
	}

	public void setGramCarbs ( float gramCarbs )
	{
		this.gramCarbs = gramCarbs;
	}

	public float getGramProteins ()
	{
		return gramProteins;
	}

	public void setGramProteins ( float gramProteins )
	{
		this.gramProteins = gramProteins;
	}
	
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
				id, name, calories);

	}

}
