package com.wks.calorieapp.activities;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.wks.android.utils.NetworkUtils;
import com.wks.calorieapp.apis.CAWebService;

public class LinkImageWithFoodTask extends AsyncTask< String, Void, Boolean >
{
	private static final String TAG = LinkImageWithFoodTask.class.getCanonicalName ();
	
	private Context context;

	public LinkImageWithFoodTask (Context context)
	{
		this.context = context;
	}
	
	@Override
	protected void onPreExecute ()
	{
		if ( !NetworkUtils.isConnectedToNetwork ( context ) )
		{
			this.cancel ( true );
		}
	}

	@Override
	protected Boolean doInBackground ( String... params )
	{
		boolean success = false;
		if ( params.length >= 2 )
		{
			try
			{
				String imageName = params[0];
				String foodName = params[1];

				Log.e ( TAG, "Linking " + foodName + " with " + imageName );

				success = CAWebService.update ( imageName, foodName );
			}
			catch ( IOException e )
			{
				Log.e ( TAG, e.getMessage () );
			}

		}else
		{
			Log.e ( TAG, "Insufficient Parameters." );
		}
		return success;
	}

}