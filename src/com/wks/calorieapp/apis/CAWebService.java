package com.wks.calorieapp.apis;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

import android.util.Log;

import com.wks.android.utils.HttpClient;

public class CAWebService
{
	private static final String TAG = CAWebService.class.getCanonicalName ();


	private static final String WEBAPP_URL = "http://uploadte-wksheikh.rhcloud.com/calorieapp/";
	private static final String SERVLET_UPLOAD = "upload";
	private static final String SERVLET_RECOGNIZE = "recognize";
	private static final String SERVLET_NUTRITION_INFO = "nutrition_info";
	private static final String SERVLET_LINK = "link";
	
	private static final String PARAM_FOOD_NAME = "food_name";
	private static final String PARAM_IMAGE_NAME="image_name";
	private static final String PARAM_MIN_SIMILARITY="min_similarity";
	private static final String PARAM_MAX_HITS="max_hits";

	public static boolean upload ( File imageFile ) throws IOException
	{
		boolean success = false;
		String url = WEBAPP_URL + SERVLET_UPLOAD;
		String json = HttpClient.uploadFile ( imageFile, url );

		Log.i(TAG,json);
		
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

	/**Invokes the Calorie REST service recognize method.
	 * 
	 * @param imageName name of image to be recognized
	 * @param minSimilarity minimum similarity index for matching images
	 * @param maximumHits maximum number of results
	 * @return Map with key: generic food name, value: specific food items and their nutrition info 
	 * @throws IOException
	 */
	public static Map< String, List< NutritionInfo >> recognize ( String imageName, float minSimilarity, int maximumHits ) throws IOException
	{	
		Map< String, List< NutritionInfo >> result = new HashMap<String,List<NutritionInfo>>();
		
		Map<String,String> params = new HashMap<String,String>();
		params.put ( PARAM_IMAGE_NAME, imageName );
		params.put ( PARAM_MIN_SIMILARITY, ""+minSimilarity );
		params.put ( PARAM_MAX_HITS, ""+maximumHits );
		String url = HttpClient.appendGetParameters(WEBAPP_URL+SERVLET_RECOGNIZE,params);
		
		
		String json = HttpClient.get ( url );
		Log.i (TAG, json );

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
		
		Map<String,String> params = new HashMap<String,String>();
		params.put ( PARAM_FOOD_NAME, URLEncoder.encode (foodName,"UTF-8") );
		String url = HttpClient.appendGetParameters ( WEBAPP_URL + SERVLET_NUTRITION_INFO, params);
		
		String json = HttpClient.get ( url );
		Log.i(TAG, json );
		
		try
		{
			CANutriitonInfoResponseFactory factory = new CANutriitonInfoResponseFactory();
			CANutritionInfoResponse response = factory.createResponseFromJSON ( json );
			results = response.getNutritionInfo ();
		}
		catch ( ParseException e )
		{
			Log.e ( TAG, "" + e.getMessage () );
		}

		return results;
	}

	public static boolean link ( String imageName, String foodName ) throws IOException
	{

		boolean success = false;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put ( PARAM_IMAGE_NAME, imageName );
		params.put ( PARAM_FOOD_NAME, foodName );
		String url = HttpClient.appendGetParameters ( WEBAPP_URL+SERVLET_LINK, params );
		
		String json = HttpClient.get ( url );
		Log.i (TAG, json );

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

}
