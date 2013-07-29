package com.wks.calorieapp.apis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CANutriitonInfoResponseFactory extends CAAbstractResponseFactory
{

	List< NutritionInfo > nutritionInfoList;

	@SuppressWarnings ( "unchecked" )
	@Override
	public CANutritionInfoResponse createResponseFromJSON ( String json ) throws ParseException
	{
		JSONParser parser = new JSONParser ();
		JSONObject searchJson = ( JSONObject ) parser.parse ( json );

		long code = ( ( Number ) searchJson.get ( CAAbstractResponse.KEY_CODE ) ).longValue ();

		List< NutritionInfo > nutritionInfoList = null;
		if ( code == 0 )
		{
			nutritionInfoList = new ArrayList< NutritionInfo > ();

			JSONArray nutritionInfoJson = ( JSONArray ) parser.parse ( ( String ) searchJson.get ( CAAbstractResponse.KEY_MESSAGE ) );
			Iterator< JSONObject > iterator = nutritionInfoJson.iterator ();
			while ( iterator.hasNext () )
			{
				JSONObject foodJson = iterator.next ();
				CAAbstractResponseFactory factory = new CANutritionInfoFactory();
				NutritionInfo foodInfo = ( NutritionInfo ) factory.createResponseFromJSON( foodJson.toJSONString () );
				nutritionInfoList.add ( foodInfo );
			}
		}

		CANutritionInfoResponse response = new CANutritionInfoResponse ( code );
		response.setNutritionInfo ( nutritionInfoList );
		return response;
	}
}
