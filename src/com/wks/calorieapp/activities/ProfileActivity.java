package com.wks.calorieapp.activities;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.ActivityLifestyleSpinnerAdapter;
import com.wks.calorieapp.models.Profile;
import com.wks.calorieapp.models.Profile.Sex;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class ProfileActivity extends Activity
{
	private RadioGroup radiogroupSex;
	private RadioButton radioMale;
	private RadioButton radioFemale;

	private ActivityLifestyleSpinnerAdapter activityLifestyleAdapter;
	private Spinner spinnerActivityLifestyle;


	private EditText editWeightLossGoal;
	private EditText editAge;
	private EditText editHeight;
	private EditText editWeight;

	private TextView textRecommendedCalories;

	private Profile profile;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_profile );

		this.profile = new Profile ();

		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
		this.bindView ();

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
			Intent homeIntent = new Intent ( this, HomeActivity.class );
			homeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			startActivity(homeIntent);
			return true;

		case R.id.profile_menu_done:
			//TODO
			//-store in file
			//-set as application variable.
			//-implement in journalActivity.
			return true;
			
		default:
			return super.onOptionsItemSelected ( item );
		}
	}

	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		actionBar.setDisplayHomeAsUpEnabled ( true );

		Drawable d = this.getResources ().getDrawable ( R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( d );
	}

	private void setupView ()
	{
		this.radiogroupSex = ( RadioGroup ) this.findViewById ( R.id.profile_radiogroup_sex );
		this.radioMale = ( RadioButton ) this.findViewById ( R.id.profile_radio_male );
		this.radioFemale = ( RadioButton ) this.findViewById ( R.id.profile_radio_female );

		this.spinnerActivityLifestyle = ( Spinner ) this.findViewById ( R.id.profile_spinner_activity_lifestyle );
		this.activityLifestyleAdapter = new ActivityLifestyleSpinnerAdapter ( this );
		this.spinnerActivityLifestyle.setAdapter ( this.activityLifestyleAdapter );

		this.editWeightLossGoal = ( EditText ) this.findViewById ( R.id.profile_edit_weight_loss_goal );
		this.editWeight = ( EditText ) this.findViewById ( R.id.profile_edit_weight );
		this.editHeight = ( EditText ) this.findViewById ( R.id.profile_edit_height );
		this.editAge = ( EditText ) this.findViewById ( R.id.profile_edit_age );

		this.textRecommendedCalories = ( TextView ) this.findViewById ( R.id.profile_text_recommended_calories );
	}

	private void setupListeners ()
	{
		this.radiogroupSex.setOnCheckedChangeListener ( new OnSexToggled () );

		this.spinnerActivityLifestyle.setOnItemSelectedListener ( new onActivityLifestyleSelected () );

		OnTextChanged onTextChanged = new OnTextChanged ();

		this.editWeight.addTextChangedListener ( onTextChanged );
		this.editWeight.addTextChangedListener ( onTextChanged );
		this.editHeight.addTextChangedListener ( onTextChanged );
		this.editAge.addTextChangedListener ( onTextChanged );
	}

	private void bindView ()
	{
		this.radioMale.setChecked ( profile.getSex ().equals ( Profile.Sex.MALE ) );
		this.editAge.setText ( "" + this.profile.getAge () );
		this.editWeight.setText ( "" + this.profile.getWeight () );
		this.editHeight.setText ( "" + this.profile.getHeight () );
		this.editWeightLossGoal.setText ( "" + this.profile.getWeightLossGoal () );

		int position = this.activityLifestyleAdapter.getPositionForActivityFactor ( this.profile.getActivityFactor () );
		this.spinnerActivityLifestyle.setSelection ( position );

	}

	private void updateTextRecommendedCalories ()
	{
		String templateText = this.getResources ().getString ( R.string.profile_layout_text_recommended_calories );
		float recommendedCalories = this.profile.getDailyCaloricNeeds ();
		this.textRecommendedCalories.setText ( String.format ( templateText, recommendedCalories ) );
	}

	class OnSexToggled implements RadioGroup.OnCheckedChangeListener
	{

		public void onCheckedChanged ( RadioGroup group, int checkedId )
		{
			Sex sex = checkedId == R.id.profile_radio_male ? Sex.MALE : Sex.FEMALE;
			ProfileActivity.this.profile.setSex ( sex );
			ProfileActivity.this.updateTextRecommendedCalories ();
		}
	}

	class onActivityLifestyleSelected implements AdapterView.OnItemSelectedListener
	{

		public void onItemSelected ( AdapterView< ? > parent, View v, int position, long id )
		{
			float activityFactor = ProfileActivity.this.activityLifestyleAdapter.getItem ( position );
			ProfileActivity.this.profile.setActivityFactor ( activityFactor );
			ProfileActivity.this.updateTextRecommendedCalories ();
		}

		@Override
		public void onNothingSelected ( AdapterView< ? > parent )
		{
			ProfileActivity.this.profile.setActivityFactor ( Profile.MIN_ACTIVITY_FACTOR );
			ProfileActivity.this.updateTextRecommendedCalories ();
		}
	}

	class OnTextChanged implements TextWatcher
	{

		public void afterTextChanged ( Editable editable )
		{
			try
			{
				String sAge = ProfileActivity.this.editAge.getText ().toString ();
				String sWeight = ProfileActivity.this.editWeight.getText ().toString ();
				String sHeight = ProfileActivity.this.editHeight.getText ().toString ();
				String sWeightLossGoal = ProfileActivity.this.editWeightLossGoal.getText ().toString ();

				ProfileActivity.this.profile.setAge ( sAge.isEmpty () ? 0 : Integer.parseInt ( sAge ) );
				ProfileActivity.this.profile.setHeight ( sHeight.isEmpty () ? 0 : Float.parseFloat ( sHeight ) );
				ProfileActivity.this.profile.setWeight ( sWeight.isEmpty () ? 0 : Float.parseFloat ( sWeight ) );
				ProfileActivity.this.profile.setWeightLossGoal ( sWeightLossGoal.isEmpty () ? 0 : Integer.parseInt ( sWeightLossGoal ) );
			
				ProfileActivity.this.updateTextRecommendedCalories ();
			}
			catch ( NumberFormatException e )
			{
				// do nothing
			}
		}

		public void beforeTextChanged ( CharSequence arg0, int arg1, int arg2, int arg3 )
		{

		}

		public void onTextChanged ( CharSequence arg0, int arg1, int arg2, int arg3 )
		{

		}

	}
}
