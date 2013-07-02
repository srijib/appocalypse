package com.wks.calorieapp.activities;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.DateCaloriesListAdapter;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.pojos.JournalEntry;

import android.app.ActionBar;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class DateCaloriesActivity extends Activity
{
	private static final String TAG = DateCaloriesActivity.class.getCanonicalName ();
	
	private ListView listMeals;
	private TextView textTotalCalories;
	private DateCaloriesListAdapter adapter;
	
	private Calendar calendar = Calendar.getInstance ();
	
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_date_calories );
		
		Bundle extras = this.getIntent ().getExtras ();
		if(extras != null)
		{
			long date = extras.getLong ( "date" );
			this.calendar.setTimeInMillis ( date );
		}else
		{
			Log.e(TAG, "Date was not received by activity.");
			this.finish ();
		}
		
		
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
		
		
		List<JournalEntry> mealList = this.getListOfMeals ();
		this.adapter = new DateCaloriesListAdapter(this,mealList);
		this.listMeals.setAdapter ( this.adapter );
	}
	
	private void setupListeners()
	{
		
	}
	
	private List<JournalEntry> getListOfMeals()
	{
		DatabaseManager manager = DatabaseManager.getInstance ( this);
		SQLiteDatabase db = manager.open ();
		
		JournalDAO journalDao = new JournalDAO(db);
		List<JournalEntry> mealList = new ArrayList<JournalEntry>();
		try
		{
			mealList = journalDao.getEntriesForDay ( this.calendar );
		}
		catch ( ParseException e )
		{
			Log.e(TAG,"Parse error while parsing date: "+e.toString ());
		}
		
		Log.e ( TAG, "sizee: "+mealList.size () );
		
		
		return mealList;
	}
}
