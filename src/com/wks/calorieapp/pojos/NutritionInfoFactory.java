package com.wks.calorieapp.pojos;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NutritionInfoFactory
{
	private static final String KEY_ID = "id";
	private static final String KEY_FAT = "fat";
	private static final String KEY_NAME = "name";
	private static final String KEY_CARBS = "carbohydrates";
	private static final String KEY_CALORIES = "calories";
	private static final String KEY_TYPE = "type";
	private static final String KEY_PROTEINS = "proteins";
	
	public static NutritionInfo createNutritionInfoFromJsonString(String json) throws ParseException
	{
		JSONParser parser = new JSONParser();
		Object object = parser.parse ( json );
		
		JSONObject jsonObj =  ( JSONObject ) object;
		// get values
		long id = (Long) jsonObj.get ( KEY_ID );
		String name = ( String ) jsonObj.get ( KEY_NAME );
		String type = ( String ) jsonObj.get ( KEY_TYPE );
		float fat = ( ( Double ) jsonObj.get ( KEY_FAT ) )
				.floatValue ();
		float carbs = ( ( Double ) jsonObj.get ( KEY_CARBS ) )
				.floatValue ();
		float proteins = ( ( Double ) jsonObj.get ( KEY_PROTEINS ) )
				.floatValue ();
		float calories = ( ( Double ) jsonObj.get ( KEY_CALORIES ) )
				.floatValue ();

		NutritionInfo nutrInfo = new NutritionInfo ();
		nutrInfo.setId ( id );
		nutrInfo.setName ( name );
		nutrInfo.setType ( type );
		nutrInfo.setGramFatPer100g ( fat );
		nutrInfo.setGramCarbsPer100g ( carbs );
		nutrInfo.setCaloriesPer100g ( calories );
		nutrInfo.setGramProteinsPer100g ( proteins );
		
		return nutrInfo;
	}

}
