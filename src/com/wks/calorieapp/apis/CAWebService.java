package com.wks.calorieapp.apis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.util.Log;

import com.wks.calorieapp.utils.HttpClient;

public class CAWebService
{
	private static final String TAG = CAWebService.class.getCanonicalName ();


	private static final String WEBAPP_URL = "http://uploadte-wksheikh.rhcloud.com/calorieapp/";
	private static final String SERVLET_UPLOAD = "upload";
	private static final String SERVLET_RECOGNIZE = "recognize";
	private static final String SERVLET_NUTRITION_INFO = "nutrition_info";
	private static final String SERVLET_UPDATE = "update";

	public static boolean upload ( File imageFile ) throws IOException
	{
		boolean success = false;
		String url = WEBAPP_URL + SERVLET_UPLOAD;
		String json = HttpClient.uploadFile ( imageFile, url );
		Log.e (TAG, json );

		try
		{
			CAResponseFactory factory = new CAResponseFactory ();
			CAResponse response = factory.createResponseFromJSON ( json );
			success = response.isSuccessful ();
		}
		catch ( ParseException e )
		{
			Log.e ( TAG, "" + e.getMessage () );
		}

		return success;
	}

	public static Map< String, List< NutritionInfo >> recognize ( String imageName, float minSimilarity, int maximumHits ) throws IOException
	{
		Map< String, List< NutritionInfo >> result = new HashMap<String,List<NutritionInfo>>();
		String url = WEBAPP_URL + SERVLET_RECOGNIZE + "/" + imageName + "/" + minSimilarity + "/" + maximumHits;
		
		Log.e("RECOGNIZE - URL","url: "+url);
		
		String json = HttpClient.get ( url );
		Log.e (TAG, json );

		try
		{
			CARecognizeResponseFactory factory = new CARecognizeResponseFactory ();
			CARecognizeResponse response = factory.createResponseFromJSON ( json );
			result = response.getNutritionInfo ();
		}
		catch ( ParseException e )
		{
			Log.e ( TAG, "" + e.getMessage () );
		}

		return result;
	}

	public static List< NutritionInfo > getNutritionInfo ( String foodName ) throws IOException
	{
		List< NutritionInfo > results = new ArrayList<NutritionInfo>();
		String url = WEBAPP_URL + SERVLET_NUTRITION_INFO + "/" + foodName;
		String json = HttpClient.get ( url );
		Log.e (TAG, json );
		
		try
		{
			CASearchResponseFactory factory = new CASearchResponseFactory();
			CASearchResponse response = factory.createResponseFromJSON ( json );
			results = response.getNutritionInfo ();
		}
		catch ( ParseException e )
		{
			Log.e ( TAG, "" + e.getMessage () );
		}

		return results;
	}

	public static boolean update ( String imageName, String foodName ) throws IOException
	{

		boolean success = false;
		String url = WEBAPP_URL + SERVLET_UPDATE + "/" + imageName + "/" + foodName;
		String json = HttpClient.get ( url );
		Log.e (TAG, json );

		try
		{
			CAResponseFactory factory = new CAResponseFactory ();
			CAResponse response = factory.createResponseFromJSON ( json );
			success = response.isSuccessful ();
		}
		catch ( ParseException e )
		{
			Log.e ( TAG, "" + e.getMessage () );
		}

		return success;

	}

	/*
	 * private static boolean processUploadResponse(String json) throws
	 * ParseException { JSONParser parser = new JSONParser(); Object object =
	 * parser.parse ( json ); JSONObject jsonObject = (JSONObject) object;
	 * 
	 * int statusCode = ((Number) jsonObject.get ( KEY_STATUS_CODE )).intValue
	 * (); return statusCode == StatusCode.OK.getCode (); }
	 * 
	 * 
	 * @SuppressWarnings ( "rawtypes" ) private static Map<String,Float>
	 * processIdentifyResponse(String json) throws ParseException {
	 * 
	 * JSONParser parser = new JSONParser(); Object object = parser.parse ( json
	 * ); JSONObject jsonObject = (JSONObject) object;
	 * 
	 * int statusCode = ((Number) jsonObject.get ( "code" )).intValue ();
	 * if(statusCode == StatusCode.OK.getCode ()) { String data = (String)
	 * jsonObject.get ( "message" );
	 * 
	 * Map< String, Float > foodSimilarityResponse = new HashMap< String, Float
	 * > (); ContainerFactory factory = new ContainerFactory () {
	 * 
	 * @Override public List creatArrayContainer () { return new ArrayList (); }
	 * 
	 * @Override public Map createObjectContainer () { return new HashMap (); }
	 * 
	 * };
	 * 
	 * Map foodSimilarityMap = ( Map ) parser.parse ( data, factory ); Iterator
	 * iter = foodSimilarityMap.entrySet ().iterator (); while ( iter.hasNext ()
	 * ) { Map.Entry entry = ( Map.Entry ) iter.next (); String foodName = (
	 * String ) entry.getKey (); float similarity = ((Number) entry.getValue
	 * ()).floatValue (); foodSimilarityResponse.put ( foodName, similarity ); }
	 * return foodSimilarityResponse; }
	 * 
	 * return null; }
	 * 
	 * private static List<NutritionInfo> processGetNutritionInfoResponse(String
	 * json) throws ParseException { JSONParser parser = new JSONParser();
	 * Object object = parser.parse ( json ); JSONObject jsonObject =
	 * (JSONObject) object;
	 * 
	 * int statusCode = ((Number) jsonObject.get ( KEY_STATUS_CODE )).intValue
	 * (); if(statusCode == 0) { String message = (String) jsonObject.get (
	 * KEY_MESSAGE );
	 * 
	 * Object o = parser.parse ( message ); JSONArray array = (JSONArray) o;
	 * 
	 * List< NutritionInfo > nutrInfoList = new ArrayList< NutritionInfo > ();
	 * for ( int i = 0 ; i < array.size () ; i++ ) {
	 * 
	 * String nutritionInfoJson = ( String ) array.get ( i ); NutritionInfo
	 * nutrInfo = CANutritionInfoFactory.createNutritionInfoFromJson (
	 * nutritionInfoJson ); System.out.println(""+i+": "+nutrInfo);
	 * nutrInfoList.add ( nutrInfo );
	 * 
	 * }
	 * 
	 * return nutrInfoList; }
	 * 
	 * return null; }
	 * 
	 * private static boolean processUpdateResponse(String json) throws
	 * ParseException { JSONParser parser = new JSONParser(); Object object =
	 * parser.parse ( json ); JSONObject jsonObject = (JSONObject) object;
	 * 
	 * int statusCode = ((Number)jsonObject.get ( KEY_STATUS_CODE )).intValue
	 * (); return statusCode == StatusCode.OK.getCode (); }
	 */
}
