package com.wks.calorieapp.activities;

import com.wks.android.utils.TypefaceUtils;
import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.ActivityLifestyleAdapter;
import com.wks.calorieapp.entities.Profile;
import com.wks.calorieapp.entities.Profile.Sex;
import com.wks.calorieapp.entities.ProfileException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity
{
	// private static final String TAG = ProfileActivity.class.getCanonicalName

	public static final String EXTRAS_VIEW_MODE = "mode";

	private RadioGroup radiogroupSex;
	private RadioButton radioMale;

	private TextView textProfileLogo;
	
	private ActivityLifestyleAdapter activityLifestyleAdapter;
	private Spinner spinnerActivityLifestyle;

	private EditText editWeightLossGoal;
	private EditText editAge;
	private EditText editHeight;
	private EditText editWeight;

	private Button buttonCalculateRecommendedCalories;

	public enum ViewMode
	{
		REGULAR, WELCOME
	};

	private ViewMode mode;

	private Profile profile;
	private float selectedActivityFactor;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );

		Bundle extras = this.getIntent ().getExtras ();

		this.mode = ViewMode.REGULAR;

		if ( extras != null )
		{
			this.mode = ViewMode.valueOf ( extras.getString ( EXTRAS_VIEW_MODE ) );
		}

		this.setContentView ( this.mode == ViewMode.REGULAR ? R.layout.activity_profile : R.layout.activity_profile_welcome );

		CalorieApplication app = ( CalorieApplication ) this.getApplication ();
		this.profile = app.getProfile ();

		if ( this.profile == null ) this.profile = new Profile ();

		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
	}

	@Override
	protected void onPause ()
	{
		super.onPause ();
		// remove from activity stack.
		if ( this.mode == ViewMode.WELCOME ) this.finish ();
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{
		MenuInflater inflater = this.getMenuInflater ();
		inflater.inflate ( R.menu.activity_profile, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent homeIntent = new Intent ( this, MainMenuActivity.class );
			homeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			startActivity ( homeIntent );
			return true;

		case R.id.profile_menu_done:

			this.saveProfile();
			
			Intent homeIntent2 = new Intent ( this, MainMenuActivity.class );
			homeIntent2.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			this.startActivity ( homeIntent2 );
			return true;

		default:
			return super.onOptionsItemSelected ( item );
		}
	}

	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		if ( this.mode == ViewMode.WELCOME )
		{
			actionBar.setTitle ( this.getResources ().getString ( R.string.title_activity_profile_welcome ) );
		}

		actionBar.setDisplayHomeAsUpEnabled ( this.mode == ViewMode.REGULAR );

		Drawable backgroundActionBar = getResources ().getDrawable ( R.drawable.bg_actionbar );
		Drawable iconActionBar = getResources().getDrawable ( R.drawable.ic_actionbar );
		
		actionBar.setBackgroundDrawable ( backgroundActionBar );
		actionBar.setIcon ( iconActionBar );
	}

	private void setupView ()
	{
		if(this.mode == ViewMode.WELCOME)
		{
			textProfileLogo = (TextView) this.findViewById ( R.id.profile_text_logo );
			TypefaceUtils.setFont ( textProfileLogo, "Hattori_Hanzo.otf" );
		}
		

		this.radiogroupSex = ( RadioGroup ) this.findViewById ( R.id.profile_radiogroup_sex );
		this.radioMale = ( RadioButton ) this.findViewById ( R.id.profile_radio_male );

		this.spinnerActivityLifestyle = ( Spinner ) this.findViewById ( R.id.profile_spinner_activity_lifestyle );
		this.activityLifestyleAdapter = new ActivityLifestyleAdapter ( this );
		this.spinnerActivityLifestyle.setAdapter ( this.activityLifestyleAdapter );

		this.editWeightLossGoal = ( EditText ) this.findViewById ( R.id.profile_edit_weight_loss_goal );
		this.editWeight = ( EditText ) this.findViewById ( R.id.profile_edit_weight );
		this.editHeight = ( EditText ) this.findViewById ( R.id.profile_edit_height );
		this.editAge = ( EditText ) this.findViewById ( R.id.profile_edit_age );

		this.buttonCalculateRecommendedCalories = ( Button ) this.findViewById ( R.id.profile_button_calculate_recommended_calories );
		this.bindView ();

	}

	private void setupListeners ()
	{
		this.radiogroupSex.setOnCheckedChangeListener ( new OnSexToggled () );
		this.spinnerActivityLifestyle.setOnItemSelectedListener ( new onActivityLifestyleSelected () );
		this.buttonCalculateRecommendedCalories.setOnClickListener ( new OnCalculateRecommendedCaloriesClicked () );

		OnTextChanged onTextChanged = new OnTextChanged ();

		this.editWeightLossGoal.addTextChangedListener ( onTextChanged );
		this.editWeight.addTextChangedListener ( onTextChanged );
		this.editHeight.addTextChangedListener ( onTextChanged );
		this.editAge.addTextChangedListener ( onTextChanged );
	}

	private void bindView ()
	{

		this.radioMale.setChecked ( profile.getSex ().equals ( Profile.Sex.MALE ) );
		this.editAge.setText ( "" + profile.getAge () );
		this.editWeight.setText ( "" + profile.getWeight () );
		this.editHeight.setText ( "" + profile.getHeight () );
		this.editWeightLossGoal.setText ( "" + profile.getWeightLossGoal () );

		int position = this.activityLifestyleAdapter.getPositionForActivityFactor ( profile.getActivityFactor () );
		this.spinnerActivityLifestyle.setSelection ( position );

	}

	private void toggleButtonCalculateRecommendedCaloriesText ()
	{
		this.buttonCalculateRecommendedCalories.setText ( this.getResources ().getString (
				R.string.profile_layout_button_get_recommended_calories_update ) );
	}
	
	/**
	 * Saves changes to user profile
	 */
	private void saveProfile()
	{
		//create Profile object from user input
		Profile profile = this.createProfile ();

		if ( profile != null ) 
		{
			//save 
			CalorieApplication app = ( CalorieApplication ) this.getApplication ();
			app.saveProfile ( profile );
		}		
	}

	private Profile createProfile ()
	{
		try
		{
			Sex sex = this.radioMale.isChecked () ? Sex.MALE : Sex.FEMALE;
			float activityFactor = this.selectedActivityFactor;
			String sAge = ProfileActivity.this.editAge.getText ().toString ();
			String sHeight = ProfileActivity.this.editHeight.getText ().toString ();
			String sWeight = ProfileActivity.this.editWeight.getText ().toString ();
			String sWeightLossGoal = ProfileActivity.this.editWeightLossGoal.getText ().toString ();

			Profile profile = new Profile ();

			profile.setSex ( sex );
			profile.setActivityFactor ( activityFactor );
			profile.setAge ( Integer.parseInt ( sAge ) );
			profile.setHeight ( Float.parseFloat ( sHeight ) );
			profile.setWeight ( Float.parseFloat ( sWeight ) );
			profile.setWeightLossGoal ( Integer.parseInt ( sWeightLossGoal ) );

			return profile;
		}
		catch ( NumberFormatException e )
		{
			Toast.makeText ( this, this.getResources ().getString ( R.string.profile_error_data_missing ), Toast.LENGTH_LONG ).show ();
			return null;
		}
		catch ( ProfileException e )
		{
			Toast.makeText ( this, e.getMessage (), Toast.LENGTH_LONG ).show ();

			return null;
		}
	}

	class OnSexToggled implements RadioGroup.OnCheckedChangeListener
	{

		public void onCheckedChanged ( RadioGroup group, int checkedId )
		{
			ProfileActivity.this.toggleButtonCalculateRecommendedCaloriesText ();
		}
	}

	class onActivityLifestyleSelected implements AdapterView.OnItemSelectedListener
	{

		public void onItemSelected ( AdapterView< ? > parent, View v, int position, long id )
		{
			ProfileActivity.this.selectedActivityFactor = ProfileActivity.this.activityLifestyleAdapter.getItem ( position );
			ProfileActivity.this.toggleButtonCalculateRecommendedCaloriesText ();
		}

		@Override
		public void onNothingSelected ( AdapterView< ? > parent )
		{
			ProfileActivity.this.selectedActivityFactor = Profile.MIN_ACTIVITY_FACTOR;
			ProfileActivity.this.toggleButtonCalculateRecommendedCaloriesText ();
		}
	}

	class OnCalculateRecommendedCaloriesClicked implements View.OnClickListener
	{
		public void onClick ( View v )
		{

			Profile profile = ProfileActivity.this.createProfile ();

			if ( profile != null )
			{
				String template = ProfileActivity.this.getResources ().getString ( R.string.profile_template_recommended_calories );
				template = String.format ( template, profile.getRecommendedDailyCalories () );

				ProfileActivity.this.buttonCalculateRecommendedCalories.setText ( template );
			}
		}

	}

	class OnTextChanged implements TextWatcher
	{
		@Override
		public void afterTextChanged ( Editable arg0 )
		{
		}

		@Override
		public void beforeTextChanged ( CharSequence arg0, int arg1, int arg2, int arg3 )
		{
		}

		@Override
		public void onTextChanged ( CharSequence s, int start, int before, int count )
		{
			ProfileActivity.this.toggleButtonCalculateRecommendedCaloriesText ();
		}

	}
}