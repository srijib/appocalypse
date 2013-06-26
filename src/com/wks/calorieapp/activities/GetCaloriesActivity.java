package com.wks.calorieapp.activities;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.R;
import com.wks.calorieapp.activities.CalorieApplication.Font;
import com.wks.calorieapp.pojos.FoodSimilarity;
import com.wks.calorieapp.pojos.NutritionInfo;
import com.wks.calorieapp.pojos.Response;
import com.wks.calorieapp.pojos.ResponseFactory;
import com.wks.calorieapp.utils.FileSystem;
import com.wks.calorieapp.utils.HttpClient;
import com.wks.calorieapp.utils.Uploader;
import com.wks.calorieapp.utils.WebServiceUrlFactory;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GetCaloriesActivity extends Activity
{
	private static final String TAG = GetCaloriesActivity.class.getCanonicalName ();

	private static final int NUM_TRIES_GET_NUTR_INFO = 3;

	private ProgressBar progressbarLoading;
	private TextView textLoadingActivity;
	private GetCaloriesTask getCaloriesTask;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_get_calories );

		setupActionBar();
		setupView ();

		Bundle extras = this.getIntent ().getExtras ();
		String fileName = extras.getString ( "image" );

		if ( fileName == null )
		{
			Toast.makeText ( this, R.string.get_calories_error_image_not_found, Toast.LENGTH_LONG ).show ();
			this.finish ();
			return;
		}

		if ( !HttpClient.isConnectedToNetwork ( GetCaloriesActivity.this ) )
		{
			setProgressBarText ( GetCaloriesActivity.this.getString ( R.string.get_calories_error_no_internet_connection ) );
		}else
		{
			getCaloriesTask = new GetCaloriesTask ();
			getCaloriesTask.execute ( fileName );
		}

	}

	@Override
	protected void onPause ()
	{
		super.onPause ();
		getCaloriesTask.cancel ( true );
		this.finish ();
	}
	
	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch(item.getItemId ())
		{
		case android.R.id.home:
			Intent intent = new Intent(GetCaloriesActivity.this,CameraActivity.class);
			intent.addFlags ( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
			startActivity(intent);
			return true;
			
		default:
			return super.onOptionsItemSelected ( item );
		}
	}

	private void setupActionBar()
	{
		ActionBar actionBar = this.getActionBar ();
		
		Drawable d = this.getResources ().getDrawable ( R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( d );
		
		actionBar.setDisplayHomeAsUpEnabled ( true );
	}
	
	private void setupView ()
	{
		this.progressbarLoading = ( ProgressBar ) findViewById ( R.id.get_calories_spinner_loading );
		this.textLoadingActivity = ( TextView ) findViewById ( R.id.get_calories_text_loading );

		this.textLoadingActivity.setTypeface ( CalorieApplication.getFont ( Font.CANTARELL_REGULAR ) );
	}



	public String getProgressBarText ()
	{
		return this.textLoadingActivity.getText ().toString ();
	}

	public void setProgressBarText ( String text )
	{
		this.textLoadingActivity.setText ( text );
	}

	class GetCaloriesTask extends AsyncTask< String, String, Response >
	{
		private String fileName;
		
		@Override
		protected void onPostExecute ( Response response )
		{
			if ( response != null && response.isSuccessful () && response.getData () instanceof HashMap )
			{
				// get data
				@SuppressWarnings ( "unchecked" )
				HashMap< String, List< NutritionInfo >> nutritionInfoForFoods = ( HashMap< String, List< NutritionInfo >> ) response.getData ();
				// pass data to Application object
				CalorieApplication.setNutritionInfoDictionary ( nutritionInfoForFoods );
				
				// move to diplay nutrition info activity.
				Intent displayNutritionInfoIntent = new Intent ( GetCaloriesActivity.this, DisplayNutritionInfoActivity.class );
				displayNutritionInfoIntent.putExtra ( "image", this.fileName );
				
				startActivity ( displayNutritionInfoIntent );

			}else
			{
				if(response != null)
				{
					Log.e ( TAG, response.getMessage () );
				}
				
				Intent searchFoodIntent = new Intent ( GetCaloriesActivity.this, SearchActivity.class );
				startActivity ( searchFoodIntent );
			}
		}

		//TODO check the cancelling thing.
		/**
		 * I just want to go on record saying that I am definitely not pleased
		 * with the quality of code in this method. but i'm really frustrated so
		 * i jsut want it to work cuz i've got a deadline and a dissertation to
		 * wrtie.
		 */
		@Override
		protected Response doInBackground ( String... params )
		{
			String picturesDir = FileSystem.getPicturesDirectory ( GetCaloriesActivity.this );
			this.fileName = params[0];
			File imageFile = new File ( picturesDir + this.fileName );

			String json = "";
			Response response = null;

			try
			{
				// upload image
				publishProgress ( "Uploading Image..." );
				json = Uploader.uploadFile ( imageFile, WebServiceUrlFactory.upload () );
				response = ResponseFactory.createResponseForUploadRequest ( json );

				Log.e("UPLOAD",json);
				
				if ( response == null || !response.isSuccessful () ) return response;
				if (this.isCancelled ()) this.cancel ( true );
				// get matches for image
				publishProgress ( "Identifying Food..." );
				json = HttpClient.get ( WebServiceUrlFactory.identify ( "lime.jpg" ) );
				response = ResponseFactory.createResponseForIdentifyRequest ( json );

				Log.e("IDENTIFY",json);
				
				// if response not received or matching foods not found, return
				if ( response == null || !response.isSuccessful () ) return response;
				if (this.isCancelled ()) this.cancel ( true );
				// if matching foods found
				if ( response.getData () != null )
				{
					// assert that data is a list of matching foods before
					// casting.
					if ( response.getData () instanceof List )
					{
						@SuppressWarnings ( "unchecked" )
						List< FoodSimilarity > foodSimilarity = ( List< FoodSimilarity > ) response.getData ();

						publishProgress ( "Getting Nutrition Information..." );
						// create hashmap containing food name => nutrtion info
						HashMap< String, List< NutritionInfo >> nutritionInfoForFoods = new HashMap< String, List< NutritionInfo >> ();

						// get nutrition info for each food in list.
						for ( int i = 0 ; i < foodSimilarity.size () ; i++ )
						{
							String foodName = "chips";//foodSimilarity.get ( i ).getFoodName ();

							// nutrition info request doesn't always work, so if
							// it fails,
							// make NUM_TRIES_GET_NUTR_INFO tries before giving
							// up.
							for ( int j = 0 ; j < NUM_TRIES_GET_NUTR_INFO ; j++ )
							{
								if (this.isCancelled ()) this.cancel ( true );
								json = HttpClient.get ( WebServiceUrlFactory.getNutritionInfo ( foodName ) );
								response = ResponseFactory.createResponseForNutritionInfoRequest ( json );
								
								Log.e("GET INFO",json);
								// when response is received with data, move on
								// to next step.
								
								if ( response != null && response.isSuccessful () ) break;
							}

							if (this.isCancelled ()) this.cancel ( true );
							publishProgress ( "Proccessing Information" );
							// double check that the response is not null and
							// that data is received.
							if ( response != null && response.isSuccessful () )
							{
								// asseert tht data is list of nutrition info
								if ( response.getData () instanceof List )
								{

									@SuppressWarnings ( "unchecked" )
									List< NutritionInfo > nutritionInfoForFood = ( List< NutritionInfo > ) response.getData ();

									// put nutrition info into map with food
									// name as key.
									nutritionInfoForFoods.put ( foodName, nutritionInfoForFood );
								}
							}

						}
						// replace list of food similarity with hashmap
						// foodname=>nutrinfo
						response.setData ( nutritionInfoForFoods );
					}else
					// you might want to throw an exception over here.
					return null;

				}

			}
			catch ( IOException e )
			{
				//TODO
			}
			catch ( ParseException e )
			{
				//TODO
				e.printStackTrace ();
			}
			
			if (this.isCancelled ()) this.cancel ( true );
			return response;
		}

		@Override
		protected void onProgressUpdate ( String... values )
		{
			setProgressBarText ( values[0] );
		}

		@Override
		protected void onCancelled ()
		{
			GetCaloriesActivity.this.textLoadingActivity.setText ( "Cancelled" );
		}
		
	}

}
