package com.wks.calorieapp.activities;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import com.wks.calorieapp.pojos.Profile;
import com.wks.calorieapp.pojos.ProfileException;
import com.wks.calorieapp.pojos.ProfileFactory;
import com.wks.calorieapp.utils.FileUtils;

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
		super.onCreate ( savedInstanceState );
		
		Log.e ( TAG, "BEGIN" );
		String profileJson = this.loadProfileJson ();

		if ( profileJson != null && !profileJson.isEmpty () )
		{

			if ( this.loadProfile ( profileJson ) )
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
		welcomeIntent.putExtra ( ProfileActivity.KEY_VIEW_MODE, ProfileActivity.ViewMode.WELCOME.toString () );
		startActivity ( welcomeIntent );
		return;
	}

	@Override
	protected void onPause ()
	{
		super.onPause ();
		this.finish ();
	}

	private String loadProfileJson ()
	{
		String profileJson = null;

		try
		{
			profileJson = FileUtils.readFromFile ( this, CalorieApplication.FILENAME_PROFILE_JSON);

		}
		catch ( IOException e )
		{
			Log.e ( TAG, e.getMessage () );
		}

		return profileJson;
	}

	private boolean loadProfile ( String profileJson )
	{
		try
		{
		
			Profile profile = ProfileFactory.createProfileFromJson ( profileJson );
			if(profile!=null)
			{
				CalorieApplication app = ( CalorieApplication ) this.getApplication ();
				app.setProfile ( profile );
				return true;
			}
			
			
		}
		catch ( ProfileException e )
		{
			Log.e ( TAG, "" + e.getMessage () );

		}
		catch ( ParseException e )
		{
			Log.e ( TAG, "" + e.getMessage () );
		}
		return false;
	}
}
