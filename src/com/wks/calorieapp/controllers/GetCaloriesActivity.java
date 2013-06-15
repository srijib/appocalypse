package com.wks.calorieapp.controllers;

import java.io.File;
import java.io.IOException;

import org.json.simple.parser.ParseException;

import com.wks.calorieapp.R;
import com.wks.calorieapp.models.Response;
import com.wks.calorieapp.utils.FileSystem;
import com.wks.calorieapp.utils.HttpClient;
import com.wks.calorieapp.utils.Uploader;
import com.wks.calorieapp.views.GetCaloriesView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GetCaloriesActivity extends Activity
{
	private static final String TAG = GetCaloriesActivity.class.getCanonicalName ();
	
	private static final String WEBAPP_URL = "http://uploadte-wksheikh.rhcloud.com/calorieapp/";
	private static final String SERVLET_UPLOAD = "upload";
	private static final String SERVLET_IDENTIFY = "identify";
	private static final String SERVLET_NUTRITION_INFO = "nutrition_info";
	
	
	private GetCaloriesView view;
	private String imageFileName;
	
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_get_calories );
		this.view = new GetCaloriesView( findViewById(android.R.id.content) );
		
		Bundle extras = this.getIntent ().getExtras ();
		String fileName = extras.getString ( "image" );
	
		if(fileName == null)
		{
			Toast.makeText ( this, R.string.get_calories_error_image_not_found, Toast.LENGTH_LONG ).show();
			this.finish();
			return;
		}
		//Toast.makeText ( this, fileName, Toast.LENGTH_LONG ).show ();
		new GetCaloriesTask().execute ( fileName );
	}
	
	class GetCaloriesTask extends AsyncTask<String,String,Response>
	{
		@Override
		protected void onPostExecute (Response result )
		{
			if(result == null || !result.isSuccessful ())
			{
				String log = (result != null)? result.getMessage () : "fuck me";
				Log.e ( TAG, log );
				view.setProgressBarText ( GetCaloriesActivity.this.getString ( R.string.get_calories_error_operation_failed ) );
			}else
			{
				view.setProgressBarText ( result.getMessage () );
			}
			
		}

		@Override
		protected Response doInBackground ( String... params )
		{
			
			if(!HttpClient.isConnectedToNetwork ( GetCaloriesActivity.this ))
			{
				Toast.makeText ( GetCaloriesActivity.this, R.string.get_calories_error_no_internet_connection, Toast.LENGTH_LONG).show ();
				return null;
			}
			
			String json = "";
			Response response = null;
			
			publishProgress ( "Uploading Image..." );
			String picturesDir = FileSystem.getPicturesDirectory ( GetCaloriesActivity.this );
			String fileName = params[0];
			File imageFile = new File(picturesDir + fileName);
			
			try
			{
				//upload image
				
				json = Uploader.uploadFile(imageFile, WEBAPP_URL+SERVLET_UPLOAD);
				Log.e(TAG,json);
				response = com.wks.calorieapp.models.ResponseFactory.createResponseForUploadRequest ( json );
				
				//if(response == null || !response.isSuccessful ()) return response;
				
				//get matches for image
				//publishProgress("Identifying Food...");
				//json = HttpClient.get ( WEBAPP_URL + SERVLET_IDENTIFY +"/"+"tree.jpg");
				
				//TODO check response for succes and put images with match greater than 40% in list.
				
				//get nutrition information for each of the three items.
				//publishProgress("Getting Nutrition Information...");
				//response = HttpClient.get ( WEBAPP_URL + SERVLET_NUTRITION_INFO + "/"+"chicken" );
				//TODO check response for success. if false, try again 3 times
				//TODO prepare response for list.
				
			}catch(IOException e)
			{
				
			}
			catch ( ParseException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
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
