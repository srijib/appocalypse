package com.wks.calorieapp.apis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class CASearchResponseFactory extends CAAbstractResponseFactory
{
	
	List<NutritionInfo> nutritionInfoList;
	
	@Override
	public CASearchResponse createResponseFromJSON ( String json ) throws ParseException
	{

		
		JSONParser parser = new JSONParser();
		JSONObject searchJson = (JSONObject) parser.parse ( json );
		
		long code = ((Number) searchJson.get ( CAAbstractResponse.KEY_CODE )).longValue ();
		
		
		List<NutritionInfo> nutritionInfoList = null;
		if(code == StatusCode.OK.getCode ())
		{
			nutritionInfoList = new ArrayList<NutritionInfo>();
			
			JSONArray nutritionInfoJson = (JSONArray) parser.parse((String)searchJson.get ( CAAbstractResponse.KEY_MESSAGE ));
			Iterator<JSONObject> iterator = nutritionInfoJson.iterator ();
			while(iterator.hasNext ())
			{
				JSONObject foodJson = iterator.next ();
				NutritionInfo foodInfo = CANutritionInfoFactory.createNutritionInfoFromJson ( foodJson.toJSONString () );
				nutritionInfoList.add ( foodInfo );
			}
		}
		
		CASearchResponse response = new CASearchResponse(code);
		response.setNutritionInfo ( nutritionInfoList );
		return response;
	}
}
