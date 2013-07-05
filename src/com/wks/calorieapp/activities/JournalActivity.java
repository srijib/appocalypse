package com.wks.calorieapp.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.CalendarAdapter;
import com.wks.calorieapp.adapters.CalendarEvent;
import com.wks.calorieapp.adapters.DaysOfWeekAdapter;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.pojos.Profile;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

public class JournalActivity extends Activity
{
	public static final String TAG = JournalActivity.class.getCanonicalName ();

	public static final String FORMAT_DATE_HEADER = "MMMM yyyy";

	public enum CalendarPeriod
	{
		TODAY, NEXT_WEEK, NEXT_MONTH, NEXT_YEAR, LAST_WEEK, LAST_MONTH, LAST_YEAR
	};

	private TextView textDateHeader;
	private GridView gridDaysOfWeek;
	private GridView gridCalendar;
	private ImageButton buttonLastMonth;
	private ImageButton buttonLastYear;
	private ImageButton buttonToday;
	private ImageButton buttonNextMonth;
	private ImageButton buttonNextYear;
	private ImageButton [] buttonsDateControl;

	private Profile profile;

	private CalendarAdapter adapter;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_journal );

		this.init ();
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent homeIntent = new Intent ( this, HomeActivity.class );
			homeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			startActivity ( homeIntent );
			return true;

		default:
			return super.onOptionsItemSelected ( item );
		}
	}

	private void init ()
	{
		this.profile = ( ( CalorieApplication ) this.getApplication () ).getProfile ();
	}

	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		Drawable drawable = this.getResources ().getDrawable ( R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( drawable );

		actionBar.setDisplayHomeAsUpEnabled ( true );
	}

	private void setupView ()
	{
		textDateHeader = ( TextView ) this.findViewById ( R.id.journal_text_date_header );
		gridDaysOfWeek = ( GridView ) this.findViewById ( R.id.journal_grid_days_of_week );
		gridCalendar = ( GridView ) this.findViewById ( R.id.journal_grid_calendar );
		buttonLastYear = ( ImageButton ) this.findViewById ( R.id.journal_button_last_year );
		buttonLastMonth = ( ImageButton ) this.findViewById ( R.id.journal_button_last_month );
		buttonToday = ( ImageButton ) this.findViewById ( R.id.journal_button_today );
		buttonNextMonth = ( ImageButton ) this.findViewById ( R.id.journal_button_next_month );
		buttonNextYear = ( ImageButton ) this.findViewById ( R.id.journal_button_next_year );

		buttonsDateControl = new ImageButton []
		{
				buttonLastYear, buttonLastMonth, buttonToday, buttonNextMonth, buttonNextYear
		};

		this.gridDaysOfWeek.setAdapter ( new DaysOfWeekAdapter ( this ) );
		this.adapter = new CalendarAdapter ( this );
		this.gridCalendar.setAdapter ( adapter );

		this.updateCalendar ( Calendar.getInstance () );
	}

	private void setupListeners ()
	{
		for ( ImageButton button : buttonsDateControl )
			button.setOnClickListener ( new OnDateControlButtonClicked () );

		this.gridCalendar.setOnItemClickListener ( new OnDateClicked () );
	}

	private void updateCalendar ( Calendar newCalendar )
	{
		// update calendar view
		JournalActivity.this.adapter.setDate ( newCalendar );

		// update calendar view heading
		this.updateDateHeader ( this.adapter.getDate () );

		// load any calendar events for this
		Map< Calendar, CalendarEvent > caloriesForMonth = JournalActivity.this.getCalendarEvents ( newCalendar );
		if ( caloriesForMonth != null )
		{
			JournalActivity.this.adapter.setItems ( caloriesForMonth );
		}

	}

	private void updateDateHeader ( long newDate )
	{
		SimpleDateFormat formatter = new SimpleDateFormat ( FORMAT_DATE_HEADER );
		String dateText = formatter.format ( newDate );
		this.textDateHeader.setText ( dateText );
	}

	private Map< Calendar, CalendarEvent > getCalendarEvents ( Calendar cal )
	{
		DatabaseManager manager = DatabaseManager.getInstance ( this );
		SQLiteDatabase db = manager.open ();

		Map< Calendar, CalendarEvent > calorieCalendar = new HashMap< Calendar, CalendarEvent > ();
		// retrieve calories consumed for each day of selected month
		if ( db == null )
		{
			Log.e ( TAG, "Can not load calorie data - null connection to db." );
		}else
		{
			JournalDAO journalDao = new JournalDAO ( db );
			Map< Calendar, Float > caloriesForMonth = journalDao.getCaloriesForMonth ( cal );

			// create calendar events for that period.

			for ( Entry< Calendar, Float > caloriesForDate : caloriesForMonth.entrySet () )
			{
				float caloriesConsumed = caloriesForDate.getValue ();

				CalendarEvent event = new CalendarEvent ();
				event.setDescription ( String.format ( "%.f cal", caloriesConsumed ) );

				int extraCalories = ( int ) ( caloriesConsumed - this.profile.getRecommendedDailyCalories () );
				event.setBackgroundColor ( this.getResources ().getColor (
						extraCalories <= 0 ? R.color.journal_calories_consumed_good : R.color.journal_calories_consumed_bad ) );

				calorieCalendar.put ( caloriesForDate.getKey (), event );

			}

			db.close ();
		}

		return calorieCalendar;
	}

	private Calendar getCalendarForMonth ( int month, int year )
	{
		Calendar calendar = Calendar.getInstance ();
		calendar.set ( Calendar.MONTH, month );
		calendar.set ( Calendar.YEAR, year );

		return calendar;
	}

	private Calendar getCalendarForPeriod ( CalendarPeriod period )
	{
		Calendar cal = Calendar.getInstance ();
		cal.setTimeInMillis ( this.adapter.getDate () );
		int month = cal.get ( Calendar.MONTH );
		int year = cal.get ( Calendar.YEAR );

		switch ( period )
		{
		case LAST_YEAR:
			return getCalendarForMonth ( month, ( year - 1 ) );
		case LAST_MONTH:
			return getCalendarForMonth ( ( month - 1 ), year );

		case NEXT_MONTH:
			return getCalendarForMonth ( ( month + 1 ), year );

		case NEXT_YEAR:
			return getCalendarForMonth ( month, ( year + 1 ) );
		case TODAY:
		default:
			Calendar calendar = Calendar.getInstance ();
			return calendar;

		}
	}

	class OnDateControlButtonClicked implements View.OnClickListener
	{

		public void onClick ( View v )
		{
			// get time period selected by user
			Calendar calendar = Calendar.getInstance ();

			switch ( v.getId () )
			{
			case R.id.journal_button_last_year:
				calendar = ( Calendar ) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.LAST_YEAR ).clone ();
				break;
			case R.id.journal_button_last_month:
				calendar = ( Calendar ) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.LAST_MONTH ).clone ();
				break;
			case R.id.journal_button_today:
				calendar = ( Calendar ) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.TODAY ).clone ();
				break;
			case R.id.journal_button_next_month:
				calendar = ( Calendar ) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.NEXT_MONTH ).clone ();
				break;
			case R.id.journal_button_next_year:
				calendar = ( Calendar ) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.NEXT_YEAR ).clone ();
				break;
			default:
				calendar = ( Calendar ) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.TODAY ).clone ();
				break;
			}

			// update view.
			JournalActivity.this.updateCalendar ( calendar );
		}

	}

	class OnDateClicked implements AdapterView.OnItemClickListener
	{
		@Override
		public void onItemClick ( AdapterView< ? > parent, View view, int position, long id )
		{
			CalendarEvent event = JournalActivity.this.adapter.getItem ( position );
			if ( event != null )
			{
				Calendar cal = Calendar.getInstance ();
				//set month and year
				cal.setTimeInMillis ( JournalActivity.this.adapter.getDate () );
				
				//set date.
				int date = JournalActivity.this.adapter.getDateAtPosition ( position );
				cal.set ( Calendar.DAY_OF_MONTH, date );

				Intent dateCaloriesIntent = new Intent ( JournalActivity.this, DateCaloriesActivity.class );
				dateCaloriesIntent.putExtra ( DateCaloriesActivity.KEY_DATE, cal.getTimeInMillis () );
				startActivity ( dateCaloriesIntent );
			}
		}
	}

}
