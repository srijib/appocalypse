package com.wks.calorieapp.models;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ResponseFactory
{
	private static final String KEY_SUCCESS = "success";
	private static final String KEY_MESSAGE = "message";
	
	private static final String KEY_FAT = "fat";
	private static final String KEY_NAME = "name";
	private static final String KEY_CARBS = "carbohydrates";
	private static final String KEY_CALORIES = "calories";
	private static final String KEY_TYPE = "type";
	private static final String KEY_PROTEINS = "proteins";
	
	private static final JSONParser parser = new JSONParser ();

	public static Response createResponseForUploadRequest ( String json ) throws ParseException
	{
		if(json == null || json.isEmpty ())
			return null;
		
		JSONObject jsonObject = jsonify(json);
		boolean success = isSuccessful ( jsonObject );
		String message = getMessage ( jsonObject );
		
		Response response = new Response();
		response.setSuccessful ( success );
		response.setMessage ( message );
		response.setData ( null );

		return response;
	}

	public static Response createResponseForIdentifyRequest ( String json ) throws ParseException
	{
		if(json == null || json.isEmpty ())
			return null;
		
		JSONObject jsonObject = jsonify(json);
		boolean success = isSuccessful(jsonObject);
		String message = getMessage(jsonObject);
		List<FoodSimilarity> data = null;
		
		if(success)
		{
			//if request was successful, the call will return a JSON Map
			//source: http://code.google.com/p/json-simple/wiki/DecodingExamples#Example_4_-_Container_factory
			ContainerFactory containerFactory = new ContainerFactory()
			{

				@Override
				public List creatArrayContainer ()
				{
					return new ArrayList();
				}

				@Override
				public Map createObjectContainer ()
				{
					return new HashMap<Float,String>();
				}
				
			};
			
			data = new ArrayList<FoodSimilarity>();
			Map<Float,String> foodSimilarityMap = (HashMap<Float,String>) parser.parse ( message, containerFactory );
			for(Entry< Float, String > entries : foodSimilarityMap.entrySet ())
			{
				FoodSimilarity foodSimilarity = new FoodSimilarity();
				foodSimilarity.setFoodName ( entries.getValue () );
				foodSimilarity.setSimilarity ( entries.getKey () );
				data.add ( foodSimilarity );
			}
	
		}
		
		Response response = new Response();
		response.setSuccessful ( success );
		if(!success) response.setMessage ( message );
		response.setData ( data );
		
		return response;
		
	}

	public static Response createResponseForNutritionInfoRequest ( String json ) throws ParseException
	{
		if(json == null || json.isEmpty ())
			return null;
		
		JSONObject jsonObject = jsonify(json);
		boolean success = isSuccessful(jsonObject);
		String message = getMessage(jsonObject);
		Object data = null;
		
		if(success)
		{
			Object object = parser.parse ( message );
			JSONArray foodArray = (JSONArray) object;
			
			List<NutritionInfo> nutritionInfoList = new ArrayList<NutritionInfo>();
			for(int i = 0;i < foodArray.size () ; i++)
			{
				JSONObject jsonObj = (JSONObject) foodArray.get ( i );
				String name = ( String ) jsonObj.get ( KEY_NAME );
				String type = (String) jsonObj.get ( KEY_TYPE );
				float fat = Float.parseFloat ( (String ) jsonObj.get ( KEY_FAT ) );
				float carbs = Float.parseFloat ( (String) jsonObj.get ( KEY_CARBS ) );
				float proteins = Float.parseFloat ( (String) jsonObj.get ( KEY_PROTEINS ) );
				float calories = Float.parseFloat ( (String) jsonObj.get ( KEY_CALORIES ) );
				
				NutritionInfo nutrInfo = new NutritionInfo();
				nutrInfo.setName ( name );
				nutrInfo.setType ( type );
				nutrInfo.setGramFatPer100g ( fat );
				nutrInfo.setGramCarbsPer100g ( carbs );
				nutrInfo.setCaloriesPer100g ( calories );
				nutrInfo.setGramProteinsPer100g ( proteins );
				
				nutritionInfoList.add ( nutrInfo );
				
			}
		}
		
		
		return null;
	}

	private static JSONObject jsonify ( String json ) throws ParseException
	{
		Object object =  parser.parse ( json );
		JSONObject jsonObject = (JSONObject) object;
		return jsonObject;
	}

	private static String getMessage ( JSONObject jsonObject )
	{
		return (String) jsonObject.get ( KEY_MESSAGE );
	}

	private static boolean isSuccessful ( JSONObject jsonObject )
	{
		return (Boolean) jsonObject.get ( KEY_SUCCESS );
	}
}
