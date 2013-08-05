package com.wks.calorieapp.activities;

import com.wks.android.utils.TypefaceUtils;
import com.wks.calorieapp.R;
import com.wks.calorieapp.entities.Profile;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class StartActivity extends Activity
{
	private static final String TAG = StartActivity.class.getCanonicalName ();
	private static final int SPLASH_LENGTH = 1500;

	private TextView textAppName;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_start );

		this.setupView ();
		new Handler ().postDelayed ( new Runnable ()
		{

			@Override
			public void run ()
			{

				loadFirstActivity ();
			}

		}, SPLASH_LENGTH );

	}

	@Override
	protected void onPause ()
	{
		super.onPause ();
		this.finish ();
	}

	private void setupView ()
	{
		this.textAppName = ( TextView ) this.findViewById ( R.id.start_text_app_name );
		TypefaceUtils.setFont ( textAppName, "Hattori_Hanzo.otf" );
	}

	private void loadFirstActivity ()
	{
		CalorieApplication app = ( CalorieApplication ) this.getApplication ();
		Profile profile = app.getProfile ();

		Intent firstIntent = new Intent ( this, ( profile == null ) ? ProfileActivity.class : MainMenuActivity.class );
		if ( profile == null ) firstIntent.putExtra ( ProfileActivity.EXTRAS_VIEW_MODE, ProfileActivity.ViewMode.WELCOME.toString () );
		firstIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		this.startActivity ( firstIntent );
	}
}
