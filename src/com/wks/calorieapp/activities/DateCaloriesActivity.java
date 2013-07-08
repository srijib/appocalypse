package com.wks.calorieapp.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.DateCaloriesListAdapter;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.listeners.SwipeDismissListViewTouchListener;
import com.wks.calorieapp.pojos.JournalEntry;
import com.wks.calorieapp.pojos.Profile;

import android.app.ActionBar;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DateCaloriesActivity extends Activity
{
	private static final String TAG = DateCaloriesActivity.class.getCanonicalName ();
	private static final String DATE_FORMAT_ACTION_BAR = "EEEE, d MMMM yyyy";
	
	public static final String KEY_DATE = "date";
	
	private long date;
	
	private Profile profile;
	private ListView listMeals;
	private TextView textTotalCalories;
	private DateCaloriesListAdapter adapter;
	private List< JournalEntry > journalEntries;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_date_calories );

		this.init ();
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();

	}

	private void init ()
	{
		this.profile = ( ( CalorieApplication ) this.getApplication () ).getProfile ();
		
		Bundle extras = this.getIntent ().getExtras ();
		if ( extras == null )
		{
			Log.e ( TAG, "Date was not received by activity." );
			this.finish ();
		}
		
		this.date = extras.getLong ( KEY_DATE );
		
		Calendar calendar = Calendar.getInstance ();
		calendar.setTimeInMillis ( date );
		this.journalEntries = this.getListOfMeals (calendar);
		
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			this.finish ();
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
		
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_ACTION_BAR);
		actionBar.setTitle (formatter.format ( this.date ));
	}

	private void setupView ()
	{
		this.listMeals = ( ListView ) this.findViewById ( R.id.date_calories_list_meals );
		this.textTotalCalories = ( TextView ) this.findViewById ( R.id.date_calories_text_total_calories );

		this.adapter = new DateCaloriesListAdapter ( this, this.journalEntries );
		this.listMeals.setAdapter ( this.adapter );

		String templateCaloriesConsumed = this.getResources ().getString ( R.string.date_calories_template_calories_consumed );
		templateCaloriesConsumed = String.format ( templateCaloriesConsumed, this.getTotalCaloriesConsumed () );

		this.textTotalCalories.setText ( templateCaloriesConsumed );
		this.textTotalCalories.setBackgroundColor ( this.getResources ().getColor ( this.getCaloriesInExcess () <= 0 ? R.color.date_calories_consumed_good : R.color.date_calories_consumed_bad ) );
	}

	private void setupListeners ()
	{
		OnListItemSwipe onListItemSwipe = new OnListItemSwipe(this.listMeals,new OnListItemDismissed());
		this.listMeals.setOnTouchListener ( onListItemSwipe );
		this.listMeals.setOnScrollListener ( onListItemSwipe.makeScrollListener () );
	}

	private int getCaloriesInExcess ()
	{
		int extra = 0;
		if ( this.profile != null )
		{
			extra = ( int ) ( this.getTotalCaloriesConsumed () - this.profile.getRecommendedDailyCalories () );
		}
		return extra;
	}

	private float getTotalCaloriesConsumed ()
	{
		float totalCaloriesConsumed = 0;
		for ( JournalEntry entry : this.journalEntries )
		{
			totalCaloriesConsumed += entry.getNutritionInfo ().getCaloriesPer100g ();
		}

		return totalCaloriesConsumed;
	}

	private List< JournalEntry > getListOfMeals ( Calendar date )
	{
		DatabaseManager manager = DatabaseManager.getInstance ( this );
		SQLiteDatabase db = manager.open ();

		JournalDAO journalDao = new JournalDAO ( db );
		List< JournalEntry > mealList = new ArrayList< JournalEntry > ();
		try
		{
			
			mealList = journalDao.read ( date );
		}
		catch ( ParseException e )
		{
			Log.e ( TAG, "Parse error while parsing date: " + e.toString () );
		}

		Log.e ( TAG, "sizee: " + mealList.size () );

		return mealList;
	}
	
	class OnListItemSwipe extends SwipeDismissListViewTouchListener
	{

		public OnListItemSwipe ( ListView listView, DismissCallbacks callbacks )
		{
			super ( listView, callbacks );
		}
		
	}
	
	class OnListItemDismissed implements SwipeDismissListViewTouchListener.DismissCallbacks
	{

		@Override
		public boolean canDismiss ( int position )
		{
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void onDismiss ( ListView listView, int [] reverseSortedPositions )
		{
			
			for(int position : reverseSortedPositions)
			{

				DateCaloriesActivity.this.adapter.remove(position);
				
			}
			
			
		}
		
	}
}
