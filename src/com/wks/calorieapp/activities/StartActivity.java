package com.wks.calorieapp.activities;

import com.wks.calorieapp.entities.Profile;
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
		
		
		
		CalorieApplication app = (CalorieApplication) this.getApplication ();
		Profile profile = app.getProfile ();
		if (profile != null)
		{
			Log.i(TAG,"Profile found. Loading Main Menu");
			Intent homeIntent = new Intent ( this, MainMenuActivity.class );
			homeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			startActivity ( homeIntent );
		}else
		{
			Log.i(TAG,"Profile found. Loading Welcome Screen");
			Intent welcomeIntent = new Intent ( this, ProfileActivity.class );
			welcomeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			welcomeIntent.putExtra ( ProfileActivity.EXTRAS_VIEW_MODE, ProfileActivity.ViewMode.WELCOME.toString () );
			startActivity ( welcomeIntent );
		}

	}

	@Override
	protected void onPause ()
	{
		super.onPause ();
		this.finish ();
	}

}
