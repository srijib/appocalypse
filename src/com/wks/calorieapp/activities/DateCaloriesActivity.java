package com.wks.calorieapp.activities;

import com.wks.calorieapp.R;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class DateCaloriesActivity extends Activity
{
	private ListView listMeals;
	private TextView textTotalCalories;
	
	
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_date_calories );
		
		this.setupActionBar();
		this.setupView();
		this.setupListeners();
	}
	
	private void setupActionBar()
	{
		ActionBar actionBar = this.getActionBar ();
		
		actionBar.setDisplayHomeAsUpEnabled ( true );
		
		Drawable d = this.getResources ().getDrawable ( R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( d );
	}
	
	private void setupView()
	{
		this.listMeals = (ListView) this.findViewById ( R.id.date_calories_list_meals );
		this.textTotalCalories = (TextView) this.findViewById ( R.id.date_calories_text_total_calories );
	
		//set list adapter here
	}
	
	private void setupListeners()
	{
		
	}
}
