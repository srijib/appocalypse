package com.wks.calorieapp.controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.parser.ParseException;

import com.wks.calorieapp.R;
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
import android.util.Log;
import android.widget.Toast;

public class GetCaloriesActivity extends Activity
{
	private static final String TAG = GetCaloriesActivity.class
			.getCanonicalName ();

	private static final int NUM_TRIES_GET_NUTR_INFO = 3;

	private GetCaloriesView view;
	private String imageFileName;

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
			Toast.makeText ( this, R.string.get_calories_error_image_not_found,
					Toast.LENGTH_LONG ).show ();
			this.finish ();
			return;
		}
		// Toast.makeText ( this, fileName, Toast.LENGTH_LONG ).show ();
		new GetCaloriesTask ().execute ( fileName );
	}

	class GetCaloriesTask extends AsyncTask< String, String, Response >
	{

		@Override
		protected void onPostExecute ( Response result )
		{
			if ( result == null || !result.isSuccessful () )
			{
				String log = ( result != null ) ? result.getMessage ()
						: "fuck me";
				Log.e ( TAG, log );
				view.setProgressBarText ( GetCaloriesActivity.this
						.getString ( R.string.get_calories_error_operation_failed ) );
			}else
			{

			}

		}

		@Override
		protected Response doInBackground ( String... params )
		{

			if ( !HttpClient.isConnectedToNetwork ( GetCaloriesActivity.this ) )
			{
				Toast.makeText ( GetCaloriesActivity.this,
						R.string.get_calories_error_no_internet_connection,
						Toast.LENGTH_LONG ).show ();
				return null;
			}

			String picturesDir = FileSystem
					.getPicturesDirectory ( GetCaloriesActivity.this );
			String fileName = params[0];
			File imageFile = new File ( picturesDir + fileName );

			String json = "";
			Response response = null;

			try
			{
				// upload image
				publishProgress ( "Uploading Image..." );
				json = Uploader.uploadFile ( imageFile,WebServiceUrlFactory.upload ());
				response = ResponseFactory
						.createResponseForUploadRequest ( json );

				if ( response == null || !response.isSuccessful () )
				{
					if ( response == null )
					{
						response = new Response();
						response.setSuccessful ( false );
						response.setMessage ( GetCaloriesActivity.Status.IMAGE_UPLOAD_FAILURE
								.getMessage () );
						response.setData ( null );
					}

					return response;
				}

				// get matches for image
				publishProgress ( "Identifying Food..." );
				json = HttpClient.get (WebServiceUrlFactory.identify ("35937204245399320130616225827.jpg"));
				response = ResponseFactory
						.createResponseForIdentifyRequest ( json );

				// check response for succes and put images
				if ( response == null || !response.isSuccessful () )
				{
					if ( response == null )
					{
						response = new Response();
						response.setSuccessful ( false );
						response.setMessage ( GetCaloriesActivity.Status.FOOD_NOT_IDENTIFIED.getMessage() );
						response.setData ( null );

					}
					
					return response;
				}

				if ( response.getData () != null
						&& response.getData () instanceof HashMap )
				{
					@SuppressWarnings ( "unchecked" )
					HashMap< Float, String > similarityFoodName = ( HashMap< Float, String > ) response
							.getData ();

					if ( similarityFoodName.size () == 0 )
					{
						Response failResponse = new Response ();
						failResponse.setSuccessful ( false );
						failResponse
								.setMessage ( GetCaloriesActivity.Status.FOOD_NOT_IDENTIFIED
										.getMessage () );
						failResponse.setData ( null );

						return failResponse;
					}

					publishProgress ( "Getting Nutrition Information..." );
					Set<String> foodNames = new HashSet<String>(similarityFoodName.values ());
					
					for(int i=0;i < foodNames.size ();i++)
					{
						for(int j=0;j < NUM_TRIES_GET_NUTR_INFO;j++)
						{
							
						}
					}

				}else
				{

				}

				// get nutrition information for each of the three items.
				//

				// TODO check response for success. if false, try again 3 times
				// TODO prepare response for list.

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

	enum Status
	{
		FOOD_NOT_IDENTIFIED (
				"The food in the picture could not be identified." ), IMAGE_UPLOAD_FAILURE (
				"This image failed to upload." );

		private final String message;

		private Status ( String message )
		{
			this.message = message;
		}

		public String getMessage ()
		{
			return message;
		}
	}
}
