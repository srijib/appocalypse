package com.wks.calorieapp.controllers;

import java.io.File;
import java.io.IOException;

import com.wks.calorieapp.R;
import com.wks.calorieapp.utils.ApplicationEnvironment;
import com.wks.calorieapp.utils.Uploader;
import com.wks.calorieapp.views.GetCaloriesView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class GetCaloriesActivity extends Activity
{
	private static final String IMAGE_UPLOAD_URL = "http://uploadte-wksheikh.rhcloud.com/calorieapp/upload";
	
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
		
		new GetCaloriesTask().execute ( fileName );
	}
	
	class GetCaloriesTask extends AsyncTask<String,String,String>
	{
		@Override
		protected void onPostExecute (String result )
		{
			view.setProgressBarText ( result );
		}

		@Override
		protected String doInBackground ( String... params )
		{
			publishProgress ( "Uploading Image..." );
			String picturesDir = ApplicationEnvironment.getPicturesDirectory ( GetCaloriesActivity.this );
			String fileName = params[0];
			File imageFile = new File(picturesDir + fileName);
			
			String response = uploadImage(imageFile,IMAGE_UPLOAD_URL);
			if(response == null)
				response = "null";
			
			return response;
		}
		
		@Override
		protected void onProgressUpdate ( String... values )
		{
			view.setProgressBarText ( values[0] );
		}
		
		private String uploadImage(File file,String url)
		{
			try
			{
				return Uploader.uploadFile(file, url);
			}
			catch ( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
}
