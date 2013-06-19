package com.wks.calorieapp.pojos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ResponseFactory
{
	private static final String KEY_STATUS_CODE = "code";
	private static final String KEY_MESSAGE = "message";

	private static final String KEY_SIMILARITY = "similarity";
	private static final String KEY_FAT = "fat";
	private static final String KEY_NAME = "name";
	private static final String KEY_CARBS = "carbohydrates";
	private static final String KEY_CALORIES = "calories";
	private static final String KEY_TYPE = "type";
	private static final String KEY_PROTEINS = "proteins";

	private static final JSONParser parser = new JSONParser ();

	public static Response createResponseForUploadRequest ( String json )
			throws ParseException
	{
		if ( json == null || json.isEmpty () ) return null;

		JSONObject jsonObject = jsonify ( json );
		int statusCode = getStatusCode ( jsonObject );
		String message = getMessage ( jsonObject );

		Response response = new Response ();
		response.setStatusCode ( statusCode );
		response.setMessage ( message );
		response.setData ( null );

		return response;
	}

	public static Response createResponseForIdentifyRequest ( String json )
			throws ParseException
	{
		if ( json == null || json.isEmpty () ) return null;

		JSONObject jsonObject = jsonify ( json );
		int statusCode = getStatusCode ( jsonObject );
		String message = getMessage ( jsonObject );
		Object data = null;

		if ( statusCode == StatusCode.OK.getCode () )
		{
			// if request was successful, the call will return a JSON Map
			Set<FoodSimilarity> uniqueData = new HashSet< FoodSimilarity > ();

			Object object = parser.parse ( message );
			// Message contains an array so cast it to array.
			JSONArray array = ( JSONArray ) object;
			
			//for wach item in the array.
			for ( int i = 0 ; i < array.size () ; i++ )
			{
				System.out.println(array.get ( i ));
				JSONObject jo = jsonify ( ( String ) array.get ( i ) );
				// get values
				String foodName = ( String ) jo.get ( KEY_NAME );// gets string
				float similarity = ( ( Double ) jo.get ( KEY_SIMILARITY ) )
						.floatValue ();// <-- this is the stupidest convention
										// i've ever seen!!!!
				FoodSimilarity foodSimilarity = new FoodSimilarity ( foodName,
						similarity );
				
					uniqueData.add ( foodSimilarity );

			}
			List<FoodSimilarity> foodSimilarity = new ArrayList<FoodSimilarity>();
			foodSimilarity.addAll ( uniqueData );
			data = foodSimilarity;
		}

		Response response = new Response ();
		response.setStatusCode ( statusCode );
		response.setMessage ( message );
		response.setData ( data );

		return response;

	}

	public static Response createResponseForNutritionInfoRequest ( String json )
			throws ParseException
	{
		if ( json == null || json.isEmpty () ) return null;

		JSONObject jsonObject = jsonify ( json );
		int statusCode = getStatusCode ( jsonObject );
		String message = getMessage ( jsonObject );
		Object data = null;

		if ( statusCode == StatusCode.OK.getCode ())
		{
			Object object = parser.parse ( message );
			// message contains array so cast to array.
			JSONArray array = ( JSONArray ) object;

			List<NutritionInfo> nutrInfoList = new ArrayList< NutritionInfo > ();
			for ( int i = 0 ; i < array.size () ; i++ )
			{
				System.out.println(array.get ( i ));
				JSONObject jsonObj = jsonify ( ( String ) array.get ( i ) );
				// get values
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
				nutrInfo.setName ( name );
				nutrInfo.setType ( type );
				nutrInfo.setGramFatPer100g ( fat );
				nutrInfo.setGramCarbsPer100g ( carbs );
				nutrInfo.setCaloriesPer100g ( calories );
				nutrInfo.setGramProteinsPer100g ( proteins );

				nutrInfoList.add ( nutrInfo );

			}
			data = nutrInfoList;
		}

		Response response = new Response ();
		response.setStatusCode (statusCode);
		response.setMessage ( message );
		response.setData ( data );

		return response;

	}

	private static JSONObject jsonify ( String json ) throws ParseException
	{
		Object object = parser.parse ( json );
		JSONObject jsonObject = ( JSONObject ) object;
		return jsonObject;
	}

	private static String getMessage ( JSONObject jsonObject )
	{
		return ( String ) jsonObject.get ( KEY_MESSAGE );
	}

	private static int getStatusCode ( JSONObject jsonObject )
	{
		return (( Long ) jsonObject.get ( KEY_STATUS_CODE )).intValue ();
	}
}
