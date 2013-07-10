package com.wks.calorieapp.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.DateCaloriesAdapter;
import com.wks.calorieapp.daos.DataAccessObject;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.entities.JournalEntry;
import com.wks.calorieapp.entities.Profile;
import com.wks.calorieapp.listeners.SwipeDismissListViewTouchListener;
import com.wks.calorieapp.models.DateCaloriesModel;

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

public class DateCaloriesActivity extends Activity implements Observer
{
	private static final String TAG = DateCaloriesActivity.class.getCanonicalName ();
	private static final String DATE_FORMAT_ACTION_BAR = "EEEE, d MMMM yyyy";

	public static final String KEY_DATE = "date";

	private ListView listviewMeals;
	private TextView textTotalCalories;
	
	private Calendar date;
	private Profile profile;
	private DateCaloriesModel model;
	private DateCaloriesAdapter adapter;
	
	private DatabaseManager manager;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_date_calories );

		this.profile = ( ( CalorieApplication ) this.getApplication () ).getProfile ();

		Bundle extras = this.getIntent ().getExtras ();
		if ( extras == null || profile == null)
		{
			Log.e ( TAG, "Date not received or profile not loaded." );
			Toast.makeText ( this, "Profile Not Found. Please Restart Application", Toast.LENGTH_LONG ).show ();
			this.finish ();
		}
		this.manager = DatabaseManager.getInstance ( this );
		
		this.model = new DateCaloriesModel(this);
		this.date = Calendar.getInstance ();
		this.date.setTimeInMillis ( extras.getLong ( KEY_DATE ) );
		
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
		
		this.model.setMealEntries ( this.getMealEntries ( date ) );
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

		SimpleDateFormat formatter = new SimpleDateFormat ( DATE_FORMAT_ACTION_BAR );
		actionBar.setTitle ( formatter.format ( this.date.getTimeInMillis () ) );
	}

	private void setupView ()
	{
		this.listviewMeals = ( ListView ) this.findViewById ( R.id.date_calories_list_meals );
		this.textTotalCalories = ( TextView ) this.findViewById ( R.id.date_calories_text_total_calories);

		this.adapter = new DateCaloriesAdapter ( this );
		this.listviewMeals.setAdapter ( this.adapter );
		
		this.model.addObserver ( adapter );
		this.model.addObserver ( this );
	}

	private void setupListeners ()
	{
		OnListItemSwipe onListItemSwipe = new OnListItemSwipe ( this.listviewMeals, new OnListItemDismissed () );
		this.listviewMeals.setOnTouchListener ( onListItemSwipe );
		this.listviewMeals.setOnScrollListener ( onListItemSwipe.makeScrollListener () );
	}
	
	private List< JournalEntry > getMealEntries ( Calendar calendar )
	{
		SQLiteDatabase db = this.manager.open ();
		
		DataAccessObject<JournalEntry> journalDao = new JournalDAO ( db );
		List< JournalEntry > mealList = new ArrayList< JournalEntry > ();
		try
		{
			mealList = ((JournalDAO) journalDao).read ( calendar );
		}
		catch ( ParseException e )
		{
			Log.e ( TAG, "Parse error while parsing date: " + e.toString () );
		}

		return mealList;
	}
	
	private void displayTotalCaloriesConsumed()
	{
		String templateCaloriesConsumed = this.getResources ().getString ( R.string.date_calories_template_calories_consumed );
		templateCaloriesConsumed = String.format ( templateCaloriesConsumed, this.getTotalCaloriesConsumed () );

		
		this.textTotalCalories.setText ( templateCaloriesConsumed );
		this.textTotalCalories.setBackgroundColor ( this.getResources ().getColor (
				this.getCaloriesInExcess () <= 0 ? R.color.date_calories_consumed_good : R.color.date_calories_consumed_bad ) );

	}
	
	private float getTotalCaloriesConsumed ()
	{
		float totalCaloriesConsumed = 0;
		for ( JournalEntry entry : this.model.getMealEntries () )
		{
			totalCaloriesConsumed += entry.getNutritionInfo ().getCaloriesPer100g ();
		}

		return totalCaloriesConsumed;
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

	private void removeEntry(JournalEntry entry)
	{
		SQLiteDatabase db = manager.open ();
		DataAccessObject<JournalEntry> journalDao = new JournalDAO(db);
		if(journalDao.delete ( entry.getId () ) == 1)
		{
			this.model.setMealEntries ( this.getMealEntries ( this.date ) );
		}
	}

	@Override
	public void update ( Observable observable, Object data )
	{
		this.displayTotalCaloriesConsumed ();
		
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
			return true;
		}

		@Override
		public void onDismiss ( ListView listView, int [] reverseSortedPositions )
		{

			for ( int position : reverseSortedPositions )
			{
				JournalEntry entry = DateCaloriesActivity.this.adapter.getItem ( position );
				DateCaloriesActivity.this.removeEntry ( entry );

			}

		}

	}
}
