package com.wks.calorieapp.apis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		String url = WEBAPP_URL + SERVLET_NUTRITION_INFO + "/" + foodName;
		String json = HttpClient.get ( url );
		Log.i(TAG, json );
		
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
