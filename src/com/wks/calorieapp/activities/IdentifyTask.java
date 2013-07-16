package com.wks.calorieapp.activities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.wks.android.utils.FileSystem;
import com.wks.android.utils.NetworkUtils;
import com.wks.calorieapp.apis.CAWebService;
import com.wks.calorieapp.apis.NutritionInfo;


class IdentifyTask extends AsyncTask< String, String, Map< String, List< NutritionInfo >> >
{
	private static final String TAG = IdentifyTask.class.getCanonicalName ();
	private static final int MAX_HITS = 10;
	private static final float MIN_SIMILARITY = 0.1F;
	
	private Context context;
	private IdentifyTaskInvoker invoker;
	private String fileName;

	public IdentifyTask (IdentifyTaskInvoker invoker)
	{
		this.context = ( Context ) invoker;
		this.invoker = invoker;
	}
	
	@Override
	protected void onPreExecute ()
	{
		this.invoker.onPreExecute ();
		if ( !NetworkUtils.isConnectedToNetwork ( this.context ) )
		{
			publishProgress ( "No Internet Connection." );
			this.cancel ( true );
		}
	}

	@Override
	protected void onPostExecute ( Map< String, List< NutritionInfo >> response )
	{
		Log.i(TAG,"Identify Task Complete");
		this.invoker.onPostExecute ( response );
	}

	@Override
	protected Map< String, List< NutritionInfo >> doInBackground ( String... params )
	{
		
		this.fileName = params[0];
		String picturesDir = FileSystem.getPicturesDirectory ( this.context );
		File imageFile = new File ( picturesDir + this.fileName );

		Map< String, List< NutritionInfo >> foodNutritionInfoMap = null;

		try
		{

			publishProgress ( "Uploading Image..." );
			boolean fileUploaded = CAWebService.upload ( imageFile );

			if ( !fileUploaded ) return null;

			if ( this.isCancelled () ) this.cancel ( true );

			publishProgress ( "Getting Nutrition Information..." );

			foodNutritionInfoMap = CAWebService.recognize ( fileName, MIN_SIMILARITY, MAX_HITS );

			return foodNutritionInfoMap;

		}
		catch ( IOException e )
		{
			Log.e ( TAG, "" + e.getMessage () );
		}

		return foodNutritionInfoMap;
	}

	@Override
	protected void onProgressUpdate ( String... values )
	{
		Log.i(TAG,values[0]);
		this.invoker.onProgressUpdate (values);
	}
	
	@Override
	protected void onCancelled ()
	{
		Log.i(TAG,"Cancelled");
		this.invoker.onCancelled ();
		//this.progressDialog.dismiss ();
	}

}