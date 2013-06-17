package com.wks.calorieapp.models;

/**
 * TODO: I think the calculation of nutrients per 100g is incorrent. Double
 * check results.
 * 
 * @author Waqqas
 * 
 */
public class NutritionInfo
{
	/*
	 * private static final String REGEX_SPACE = "\\s"; // private static final
	 * String REGEX_DESCRIPTION =
	 * "Per([0-9.]+?)g-Calories:([0-9.]+?)kcal|Fat:([0-9.]+?)g|Carbs:([0-9.]+?)g|Protein:([0-9.]+?)g"
	 * ; private static final int NUM_MATCH_GROUPS = 5; private static final int
	 * MATCH_GROUP_SERVING_SIZE = 0; private static final int
	 * MATCH_GROUP_CALORIES = 1; private static final int MATCH_GROUP_FAT = 2;
	 * private static final int MATCH_GROUP_CARBS = 3; private static final int
	 * MATCH_GROUP_PROTEINS = 4;
	 */
	private long id;
	private String name;
	private String type;
	// private String description;
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
	/*
	private boolean parseDescription ()
	{
		if ( this.description != null && description.length () != 0 )
		{
			// description looks like this:

			// Per101g - Calories:239kcal | Fat:13.60g|
			// Carbs:0.00g|Protein:27.30g
			String description = this.description.replace ( REGEX_SPACE, "" );

			// split description at pipe sign and hyphen
			String [] tokens = description.split ( "[-|]" );

			if ( tokens.length == NUM_MATCH_GROUPS )
			{
				// each token contains one numeric value
				float [] values = new float [tokens.length];
				// find a numeric value
				Pattern pattern = Pattern.compile ( "[0-9.]+" );

				for ( int i = 0 ; i < tokens.length ; i++ )
				{
					// store all the numeric values in the vlaues array
					Matcher matcher = pattern.matcher ( tokens[i] );
					if ( matcher.find () )
						values[i] = Float.valueOf ( matcher.group ( 0 ) );
				}
				// @formatter:off
				setCaloriesPer100g ( ( values[MATCH_GROUP_CALORIES] / values[MATCH_GROUP_SERVING_SIZE] ) * 100 );
				setGramFatPer100g ( ( values[MATCH_GROUP_FAT] / values[MATCH_GROUP_SERVING_SIZE] ) * 100 );
				setGramProteinsPer100g ( ( values[MATCH_GROUP_PROTEINS] / values[MATCH_GROUP_SERVING_SIZE] ) * 100 );
				setGramCarbsPer100g ( ( values[MATCH_GROUP_CARBS] / values[MATCH_GROUP_SERVING_SIZE] ) * 100 );
				// @formatter:on
			}

			/*
			 * Attempted to do this with Regex but it didn't work.
			 * 
			 * 
			 * Pattern descriptionPattern = Pattern.compile(REGEX_DESCRIPTION);
			 * Matcher matcher = descriptionPattern.matcher(description);
			 * 
			 * 
			 * if(matcher.find() && matcher.groupCount()==NUM_MATCH_GROUPS) {
			 * float _servingSize =
			 * Float.parseFloat(matcher.group(MATCH_GROUP_SERVING_SIZE)); float
			 * _kiloCalories =
			 * Float.parseFloat(matcher.group(MATCH_GROUP_CALORIES)); float
			 * _gramFat = Float.parseFloat(matcher.group(MATCH_GROUP_FAT));
			 * float _gramCarbs =
			 * Float.parseFloat(matcher.group(MATCH_GROUP_CARBS)); float
			 * _gramProteins =
			 * Float.parseFloat(matcher.group(MATCH_GROUP_PROTEINS));
			 * 
			 * 
			 * setCaloriesPer100g( _kiloCalories );//_kiloCalories/_servingSize
			 * * 100 ); setGramFatPer100g ( _gramFat);//_gramFat/_servingSize *
			 * 100 ); setGramCarbsPer100g (
			 * _gramCarbs);//_gramCarbs/_servingSize * 100 );
			 * setGramProteinsPer100g (
			 * _gramProteins);//_gramProteins/_servingSize * 100 );
			 * 
			 * return true; }
			 *//*
		}

		// match groups were not found so calorie information will be 0.
		return false;
	}*/
	
	@Override
	public String toString ()
	{
		return String.format ( "[id: %d,name: %s,type: %s,description: %s]",
				id, name, type/*, description */);
	}
}
