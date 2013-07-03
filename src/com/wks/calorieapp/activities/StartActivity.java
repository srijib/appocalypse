package com.wks.calorieapp.activities;

import java.io.IOException;

import com.wks.calorieapp.pojos.Profile;
import com.wks.calorieapp.pojos.ProfileException;
import com.wks.calorieapp.pojos.ProfileFactory;
import com.wks.calorieapp.utils.FileUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class StartActivity extends Activity
{
	private static final String TAG = StartActivity.class.getCanonicalName ();

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onCreate ( savedInstanceState );
		// you might want to set content view over here.
		Log.e ( TAG, "BEGIN" );
		String profileCsv = this.loadProfileCsv ();

		if ( profileCsv != null && !profileCsv.isEmpty () )
		{

			if ( this.loadProfile ( profileCsv ) )
			{

				Intent homeIntent = new Intent ( this, HomeActivity.class );
				homeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity ( homeIntent );
				return;
			}
		}

		// assume first launch. Direct user to profile activity with welcome
		// view.

		Intent welcomeIntent = new Intent ( this, ProfileActivity.class );
		welcomeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		welcomeIntent.putExtra ( Key.PROFILE_MODE.key (), ProfileActivity.ViewMode.WELCOME.toString () );
		startActivity ( welcomeIntent );
		return;
	}

	@Override
	protected void onPause ()
	{
		// TODO Auto-generated method stub
		super.onPause ();
		this.finish ();
	}

	private String loadProfileCsv ()
	{
		String profileCsv = null;

		try
		{
			profileCsv = FileUtil.readFromFile ( this, CalorieApplication.FILENAME_PROFILE_CSV);

		}
		catch ( IOException e )
		{
			Log.e ( TAG, e.getMessage () );
		}

		return profileCsv;
	}

	private boolean loadProfile ( String profileCSV )
	{
		try
		{

			Profile profile = ProfileFactory.createProfileFromCSV ( profileCSV );
			if(profile!=null)
			{
				CalorieApplication app = ( CalorieApplication ) this.getApplication ();
				app.setProfile ( profile );
			}
			return true;
			
		}
		catch ( ProfileException e )
		{
			Log.e ( TAG, "" + e.getMessage () );

		}
		return false;
	}
}
