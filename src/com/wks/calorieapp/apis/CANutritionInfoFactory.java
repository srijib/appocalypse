package com.wks.calorieapp.apis;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class CANutritionInfoFactory
{
	private static final String KEY_ID = "id";
	private static final String KEY_FAT = "fat";
	private static final String KEY_NAME = "name";
	private static final String KEY_CARBS = "carbohydrates";
	private static final String KEY_CALORIES = "calories";
	private static final String KEY_TYPE = "type";
	private static final String KEY_PROTEINS = "proteins";
	
	public static NutritionInfo createNutritionInfoFromJson(String json) throws ParseException
	{
		JSONParser parser = new JSONParser();
		Object object = parser.parse ( json );
		
		JSONObject jsonObj =  ( JSONObject ) object;
		// get values
		long id = (Long) jsonObj.get ( KEY_ID );
		String name = ( String ) jsonObj.get ( KEY_NAME );
		String type = ( String ) jsonObj.get ( KEY_TYPE );
		float fat = ( ( Number ) jsonObj.get ( KEY_FAT ) )
				.floatValue ();
		float carbs = ( ( Number ) jsonObj.get ( KEY_CARBS ) )
				.floatValue ();
		float proteins = ( ( Number ) jsonObj.get ( KEY_PROTEINS ) )
				.floatValue ();
		float calories = ( ( Number ) jsonObj.get ( KEY_CALORIES ) )
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
