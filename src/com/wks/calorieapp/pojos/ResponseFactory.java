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

	private static final JSONParser parser = new JSONParser ();

	public static Response createResponseForUploadRequest ( String json ) throws ParseException
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

	public static Response createResponseForIdentifyRequest ( String json ) throws ParseException
	{
		if ( json == null || json.isEmpty () ) return null;

		JSONObject jsonObject = jsonify ( json );
		int statusCode = getStatusCode ( jsonObject );
		String message = getMessage ( jsonObject );
		Object data = null;

		if ( statusCode == StatusCode.OK.getCode () )
		{
			// if request was successful, the call will return a JSON Map
			Set< FoodSimilarity > uniqueData = new HashSet< FoodSimilarity > ();

			Object object = parser.parse ( message );
			// Message contains an array so cast it to array.
			JSONArray array = ( JSONArray ) object;

			// for wach item in the array.
			for ( int i = 0 ; i < array.size () ; i++ )
			{
				String foodSimilarityJSON = ( String ) array.get ( i ) ;
				FoodSimilarity foodSimilarity = FoodSimilarityFactory.createFoodSimilarityFromJsonString ( foodSimilarityJSON );
				
				uniqueData.add ( foodSimilarity );

			}
			List< FoodSimilarity > foodSimilarity = new ArrayList< FoodSimilarity > ();
			foodSimilarity.addAll ( uniqueData );
			data = foodSimilarity;
		}

		Response response = new Response ();
		response.setStatusCode ( statusCode );
		response.setMessage ( message );
		response.setData ( data );

		return response;

	}

	public static Response createResponseForNutritionInfoRequest ( String json ) throws ParseException
	{
		if ( json == null || json.isEmpty () ) return null;

		JSONObject jsonObject = jsonify ( json );
		int statusCode = getStatusCode ( jsonObject );
		String message = getMessage ( jsonObject );
		Object data = null;

		if ( statusCode == StatusCode.OK.getCode () )
		{
			Object object = parser.parse ( message );
			// message contains array so cast to array.
			JSONArray array = ( JSONArray ) object;

			List< NutritionInfo > nutrInfoList = new ArrayList< NutritionInfo > ();
			for ( int i = 0 ; i < array.size () ; i++ )
			{

				String nutritionInfoJson = ( String ) array.get ( i );
				NutritionInfo nutrInfo = NutritionInfoFactory.createNutritionInfoFromJsonString ( nutritionInfoJson );

				nutrInfoList.add ( nutrInfo );

			}
			data = nutrInfoList;
		}

		Response response = new Response ();
		response.setStatusCode ( statusCode );
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
		return ( ( Long ) jsonObject.get ( KEY_STATUS_CODE ) ).intValue ();
	}
}
