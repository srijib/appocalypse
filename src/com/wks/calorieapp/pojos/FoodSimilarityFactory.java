package com.wks.calorieapp.pojos;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FoodSimilarityFactory
{
	private static final String KEY_SIMILARITY = "similarity";
	private static final String KEY_NAME = "name";

	public static FoodSimilarity createFoodSimilarityFromJsonString ( String json ) throws ParseException
	{
		JSONParser parser = new JSONParser ();
		Object object = parser.parse ( json );

		JSONObject jo = ( JSONObject ) object;
		String foodName = ( String ) jo.get ( KEY_NAME );// gets string
		float similarity = ( ( Double ) jo.get ( KEY_SIMILARITY ) ).floatValue ();// <--
																					// this
																					// is
																					// the
																					// stupidest
																					// convention
																					// i've
																					// ever
																					// seen!!!!
		return new FoodSimilarity ( foodName, similarity );
	}
}
