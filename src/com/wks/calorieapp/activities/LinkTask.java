package com.wks.calorieapp.activities;

import java.io.IOException;
import java.net.URLEncoder;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.wks.android.utils.NetworkUtils;
import com.wks.calorieapp.apis.CAWebService;

public class LinkTask extends AsyncTask< String, Void, Boolean >
{
	private static final String TAG = LinkTask.class.getCanonicalName ();
	
	private Context context;

	public LinkTask (Context context)
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
				String foodName = URLEncoder.encode ( params[1], "UTF-8" );

				Log.e ( TAG, "Linking " + foodName + " with " + imageName );

				success = CAWebService.link ( imageName, foodName );
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