package com.wks.calorieapp.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.util.Log;

public class CARecognizeResponseFactory extends CAAbstractResponseFactory
{

	@SuppressWarnings ( {
			"unused", "unchecked"
	} )
	public CARecognizeResponse createResponseFromJSON ( String json ) throws ParseException
	{
		JSONParser parser = new JSONParser();
		JSONObject recognizeJson = (JSONObject) parser.parse ( json );
		
		long statusCode = ((Number) recognizeJson.get ( CAAbstractResponse.KEY_CODE )).longValue ();
		Map<String,List<NutritionInfo>> foodNutritionInfoMap = null;
		
		Log.e("RECOGNIZE - STATUSCODE","StatusCode: "+statusCode);
		
		if(statusCode == 0)
		{	
			
			foodNutritionInfoMap = new HashMap<String,List<NutritionInfo>>();
			String message = (String) recognizeJson.get ( CAAbstractResponse.KEY_MESSAGE );
			
			Log.e("RECOGNIZE - MESSAGE","Message: "+message);
			
			ContainerFactory containerFactory = new ContainerFactory(){

				@Override
				public List<JSONObject> creatArrayContainer ()
				{
					return new ArrayList<JSONObject>();
				}

				@Override
				public Map<String,JSONArray> createObjectContainer ()
				{
					return new HashMap<String,JSONArray>();
				}};
				
			Map< String, JSONArray > foodNameInfoMap = ( Map< String, JSONArray > ) parser.parse ( message );
			
			Iterator< Entry< String, JSONArray >> iter = foodNameInfoMap.entrySet ().iterator ();
			
			String foodName = null;
			List<NutritionInfo> nutritionInfoList = null;
			
			while(iter.hasNext ())
			{
				Entry<String,JSONArray> entry = iter.next ();
				
				foodName = entry.getKey ();
				nutritionInfoList = new ArrayList<NutritionInfo>();
				
				JSONArray infoJson = entry.getValue ();
				
				Iterator<JSONObject> iterator = infoJson.iterator ();
				while(iterator.hasNext ())
				{
					JSONObject nutritionInfoJson = iterator.next ();
					CAAbstractResponseFactory factory = new CANutritionInfoFactory();
					NutritionInfo nutritionInfo = ( NutritionInfo ) factory.createResponseFromJSON ( nutritionInfoJson.toJSONString () );
					nutritionInfoList.add ( nutritionInfo );
				}
				
				if(foodName!= null && nutritionInfoList != null)
					foodNutritionInfoMap.put ( foodName, nutritionInfoList );
			}

		}
		
		CARecognizeResponse response = new CARecognizeResponse(statusCode);
		response.setNutritionInfo ( foodNutritionInfoMap );
		return response;
	}

}
