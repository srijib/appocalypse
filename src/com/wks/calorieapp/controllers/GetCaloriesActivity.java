package com.wks.calorieapp.controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.simple.parser.ParseException;

import com.wks.calorieapp.R;
import com.wks.calorieapp.models.FoodSimilarity;
import com.wks.calorieapp.models.NutritionInfo;
import com.wks.calorieapp.models.Response;
import com.wks.calorieapp.models.ResponseFactory;
import com.wks.calorieapp.utils.FileSystem;
import com.wks.calorieapp.utils.HttpClient;
import com.wks.calorieapp.utils.Uploader;
import com.wks.calorieapp.utils.WebServiceUrlFactory;
import com.wks.calorieapp.views.GetCaloriesView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class GetCaloriesActivity extends Activity
{
	private static final String TAG = GetCaloriesActivity.class.getCanonicalName ();

	private static final int NUM_TRIES_GET_NUTR_INFO = 3;

	private GetCaloriesView view;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_get_calories );
		this.view = new GetCaloriesView ( findViewById ( android.R.id.content ) );

		Bundle extras = this.getIntent ().getExtras ();
		String fileName = extras.getString ( "image" );

		if ( fileName == null )
		{
			Toast.makeText ( this, R.string.get_calories_error_image_not_found, Toast.LENGTH_LONG ).show ();
			this.finish ();
			return;
		}
		// Toast.makeText ( this, fileName, Toast.LENGTH_LONG ).show ();
		new GetCaloriesTask ().execute ( fileName );
	}

	class GetCaloriesTask extends AsyncTask< String, String, Response >
	{

		@Override
		protected void onPostExecute ( Response response )
		{
			if(response != null && response.isSuccessful () && response.getData () instanceof HashMap)
			{
				HashMap<String,List<NutritionInfo>> nutritionInfoForFoods = (HashMap<String,List<NutritionInfo>>) response.getData ();
				for(Entry<String,List<NutritionInfo>> entry : nutritionInfoForFoods.entrySet ())
				{
					List<NutritionInfo> ni = entry.getValue ();
					String foodname = entry.getKey ();
					String calories = ni.get ( 0 ).toString ();
					view.setProgressBarText ( foodname + "="+calories );
					break;
				}
				
			}else
				view.setProgressBarText ( "No results found" );
		}

		/**
		 * I just want to go on record saying that I am definitely not pleased with the quality of code in this method.
		 * but i'm really frustrated so i jsut want it to work cuz i've got a deadline and a dissertation to wrtie. 
		 */
		@Override
		protected Response doInBackground ( String... params )
		{

			if ( !HttpClient.isConnectedToNetwork ( GetCaloriesActivity.this ) )
			{
				Toast.makeText ( GetCaloriesActivity.this, R.string.get_calories_error_no_internet_connection, Toast.LENGTH_LONG ).show ();
				return null;
			}

			String picturesDir = FileSystem.getPicturesDirectory ( GetCaloriesActivity.this );
			String fileName = params[0];
			File imageFile = new File ( picturesDir + fileName );

			String json = "";
			Response response = null;

			try
			{
				// upload image
				publishProgress ( "Uploading Image..." );
				json = Uploader.uploadFile ( imageFile, WebServiceUrlFactory.upload () );
				response = ResponseFactory.createResponseForUploadRequest ( json );

				if ( response == null || !response.isSuccessful () ) return response;

				// get matches for image
				publishProgress ( "Identifying Food..." );
				json = HttpClient.get ( WebServiceUrlFactory.identify ( "35937204245399320130616225827.jpg" ) );
				response = ResponseFactory.createResponseForIdentifyRequest ( json );

				//if response not received or matching foods not found, return 
				if ( response == null || !response.isSuccessful () ) return response;

				//if matching foods found
				if ( response.getData () != null )
				{
					//assert that data is a list of matching foods before casting.
					if ( response.getData () instanceof List )
					{
						List< FoodSimilarity > foodSimilarity = ( List< FoodSimilarity > ) response.getData ();

						publishProgress ( "Getting Nutrition Information..." );
						//create hashmap containing food name => nutrtion info
						HashMap< String, List< NutritionInfo >> nutritionInfoForFoods = new HashMap< String, List< NutritionInfo >> ();
						
						//get nutrition info for each food in list.
						for ( int i = 0 ; i < foodSimilarity.size () ; i++ )
						{
							String foodName = foodSimilarity.get ( i ).getFoodName ();

							//nutrition info request doesn't always work, so if it fails, 
							//make NUM_TRIES_GET_NUTR_INFO tries before giving up.
							for ( int j = 0 ; j < NUM_TRIES_GET_NUTR_INFO ; j++ )
							{
								json = HttpClient.get ( WebServiceUrlFactory.getNutritionInfo ( foodName));
								response = ResponseFactory.createResponseForNutritionInfoRequest ( json );
								
								//when response is received with data, move on to next step.
								if ( response != null && response.isSuccessful () ) break;
							}
							
							publishProgress ( "Proccessing Information" );
							//double check that the response is not null and that data is received.
							if ( response != null && response.isSuccessful () )
							{
								//asseert tht data is  list of nutrition info
								if ( response.getData () instanceof List )
								{
									
									List< NutritionInfo > nutritionInfoForFood = ( List< NutritionInfo > ) response.getData ();
									
									//put nutrition info into map with food name as key.
									nutritionInfoForFoods.put ( foodName, nutritionInfoForFood );
								}
							}

						}
						//replace list of food similarity with hashmap foodname=>nutrinfo
						response.setData ( nutritionInfoForFoods );
					}else 
						//you might want to throw an exception over here.
						return null;

				}

			}
			catch ( IOException e )
			{

			}
			catch ( ParseException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace ();
			}
			return response;
		}

		@Override
		protected void onProgressUpdate ( String... values )
		{
			view.setProgressBarText ( values[0] );
		}

	}

}
