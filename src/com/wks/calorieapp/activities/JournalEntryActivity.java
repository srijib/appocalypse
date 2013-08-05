package com.wks.calorieapp.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.JournalEntryAdapter;
import com.wks.calorieapp.daos.DataAccessObject;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.entities.JournalEntry;
import com.wks.calorieapp.entities.Profile;
import com.wks.calorieapp.listeners.SwipeDismissListViewTouchListener;
import com.wks.calorieapp.models.JournalEntryModel;

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

/**Activity that displays the meals logged in a given day and thier calories.
 * 
 * @author Waqqas
 *
 */
public class JournalEntryActivity extends Activity implements Observer
{
	//DEBUGGING TAG
	private static final String TAG = JournalEntryActivity.class.getCanonicalName ();
	
	//CONSTANTS
	private static final String DATE_FORMAT_ACTION_BAR = "EEEE, d MMMM yyyy";
	public static final String EXTRAS_DATE = "date";
	public static final int COLOR_GOOD = R.color.sap_green;
	public static final int COLOR_BAD = R.color.brick_red;

	//UI Componenets
	private ListView listviewMeals;
	private TextView textTotalCalories;
	
	//MEMBERS
	private Calendar calendar;
	private Profile profile;
	private JournalEntryModel model;
	private JournalEntryAdapter adapter;
	
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
		
		this.model = new JournalEntryModel();
		this.calendar = Calendar.getInstance ();
		this.calendar.setTimeInMillis ( extras.getLong ( EXTRAS_DATE ) );
		
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
		
		//instantiate model
		this.model.setMealEntries ( this.getJournalEntriesForDate ( calendar ) );
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

	/**
	 * - Applies background on Action Bar
	 * - Sets ActionBar Title to Date of Journal Entry
	 */
	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		actionBar.setDisplayHomeAsUpEnabled ( true );

		Drawable backgroundActionBar = getResources ().getDrawable ( R.drawable.bg_actionbar );
		Drawable iconActionBar = getResources().getDrawable ( R.drawable.ic_actionbar );
		
		actionBar.setBackgroundDrawable ( backgroundActionBar );
		actionBar.setIcon ( iconActionBar );

		SimpleDateFormat formatter = new SimpleDateFormat ( DATE_FORMAT_ACTION_BAR );
		actionBar.setTitle ( formatter.format ( this.calendar.getTimeInMillis () ) );
	}

	/**
	 * - Instantiates UI elements
	 */
	private void setupView ()
	{
		this.listviewMeals = ( ListView ) this.findViewById ( R.id.date_calories_list_meals );
		this.textTotalCalories = ( TextView ) this.findViewById ( R.id.date_calories_text_total_calories);

		this.adapter = new JournalEntryAdapter ( this,this.model );
		this.listviewMeals.setAdapter ( this.adapter );
	}

	/**
	 * Registers Subscribers to Publishers.
	 */
	private void setupListeners ()
	{
		this.model.addObserver ( adapter );
		this.model.addObserver ( this );
		
		OnListItemSwipe onListItemSwipe = new OnListItemSwipe ( this.listviewMeals, new OnListItemDismissed () );
		this.listviewMeals.setOnTouchListener ( onListItemSwipe );
		this.listviewMeals.setOnScrollListener ( onListItemSwipe.makeScrollListener () );
	}
	
	/**Loads journal entries for given date from the DB.
	 * @param calendar date for which journal entries are to be loaded.
	 * @return List of JournalEntries for provided date.
	 */
	private List< JournalEntry > getJournalEntriesForDate ( Calendar calendar )
	{
		SQLiteDatabase db = this.manager.open ();
		
		DataAccessObject<JournalEntry> journalDao = new JournalDAO ( db );
		List< JournalEntry > entriesList = new ArrayList< JournalEntry > ();
		try
		{
			entriesList = ((JournalDAO) journalDao).read ( calendar );
		}
		catch ( ParseException e )
		{
			Log.e ( TAG, "Parse error while parsing date: " + e.toString () );
		}

		return entriesList;
	}
	
	/**
	 * - Shows the total calories consumed on the given day in the textTotalCalories textView.
	 * - Background of textview is set to red if more than recommended amount of calories.
	 * - Background of textview is set to green if less than or equal to reocmmended amount of calories. 
	 */
	private void displayTotalCaloriesConsumed()
	{
		String templateCaloriesConsumed = this.getResources ().getString ( R.string.date_calories_template_calories_consumed );
		templateCaloriesConsumed = String.format ( templateCaloriesConsumed, this.getTotalCaloriesConsumed () );
		
		this.textTotalCalories.setText ( templateCaloriesConsumed );
		this.textTotalCalories.setBackgroundColor ( this.getResources ().getColor (
				this.getCaloriesInExcess () <= 0 ? COLOR_GOOD : COLOR_BAD ) );

	}
	
	/**
	 * 
	 * @return total calories consumed on the given day.
	 */
	private float getTotalCaloriesConsumed ()
	{
		float totalCaloriesConsumed = 0;
		for ( JournalEntry entry : this.model.getJournalEntries () )
		{
			totalCaloriesConsumed += entry.getNutritionInfo ().getCalories ();
		}

		return totalCaloriesConsumed;
	}

	/**
	 * 
	 * @return the differennce between calories consumed and recommended calories per day.
	 */
	private int getCaloriesInExcess ()
	{
		int extra = 0;
		if ( this.profile != null )
		{
			extra = ( int ) ( this.getTotalCaloriesConsumed () - this.profile.getRecommendedDailyCalories () );
		}
		return extra;
	}

	//Not sure if its good design to have this method here. 

	/**
	 * - Removes Journal Entry from database.
	 * - Updates Journal Entry model.
	 * @param entry Journal entry to be removed.
	 */
	private void removeEntry(JournalEntry entry)
	{
		SQLiteDatabase db = manager.open ();
		DataAccessObject<JournalEntry> journalDao = new JournalDAO(db);
		if(journalDao.delete ( entry.getId () ) == 1)
		{
			this.model.setMealEntries ( this.getJournalEntriesForDate ( this.calendar ) );
		}
	}

	//OBSERVER INTERFACE
	/**
	 * - Updates the totalCaloriesConsumed in a day when the JournalEntriesModel is changed.
	 */
	@Override
	public void update ( Observable observable, Object data )
	{
		this.displayTotalCaloriesConsumed ();
	}

	/**Swipe Listener for ListView Items
	 * 
	 * @author Waqqas
	 *
	 */
	class OnListItemSwipe extends SwipeDismissListViewTouchListener
	{

		public OnListItemSwipe ( ListView listView, DismissCallbacks callbacks )
		{
			super ( listView, callbacks );
		}

	}

	/**Callback when user swipes on list item to delete.
	 * 
	 * @author Waqqas
	 *
	 */
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
				JournalEntry entry = JournalEntryActivity.this.adapter.getItem ( position );
				JournalEntryActivity.this.removeEntry ( entry );

			}

		}

	}
}
