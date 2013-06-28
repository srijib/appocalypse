package com.wks.calorieapp.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.CalendarAdapter;
import com.wks.calorieapp.adapters.DaysOfWeekAdapter;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.FoodDAO;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.pojos.FoodEntry;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

public class JournalActivity extends Activity
{
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
	
	private CalendarAdapter adapter;


	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_journal );

		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
		this.initCalendar();
		
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

	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		Drawable drawable = this.getResources ().getDrawable ( R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( drawable );

		actionBar.setDisplayHomeAsUpEnabled ( true );
	}

	private void setupView ()
	{
		textDateHeader = (TextView) this.findViewById ( R.id.journal_text_date_header );
		gridDaysOfWeek = (GridView) this.findViewById ( R.id.journal_grid_days_of_week );
		gridCalendar = (GridView) this.findViewById ( R.id.journal_grid_calendar );
		buttonLastYear = ( ImageButton ) this.findViewById ( R.id.journal_button_last_year );
		buttonLastMonth = ( ImageButton ) this.findViewById ( R.id.journal_button_last_month );
		buttonToday = ( ImageButton ) this.findViewById ( R.id.journal_button_today );
		buttonNextMonth = ( ImageButton ) this.findViewById ( R.id.journal_button_next_month );
		buttonNextYear = ( ImageButton ) this.findViewById ( R.id.journal_button_next_year );

		buttonsDateControl = new ImageButton [] { buttonLastYear, buttonLastMonth, buttonToday, buttonNextMonth, buttonNextYear };
	
		this.setupCalendar();
	}

	private void setupCalendar()
	{
		this.gridDaysOfWeek.setAdapter ( new DaysOfWeekAdapter(this) );
		this.adapter = new CalendarAdapter(this);
		this.gridCalendar.setAdapter ( adapter );
		this.updateDateHeader ( this.adapter.getDate () );
	}
	
	private void setupListeners ()
	{
		for ( ImageButton button : buttonsDateControl )
			button.setOnClickListener ( new OnDateControlButtonClicked () );

	}
	
	@SuppressWarnings ( "unused" )
	private void initCalendar()
	{
		
		DatabaseManager manager = DatabaseManager.getInstance ( this );
		SQLiteDatabase db = manager.open ();
		JournalDAO journalDAO = new JournalDAO(db);
		//Map< String, Float > cow = journalDAO.getCaloriesForEachDay ( Calendar.getInstance () );
		//Log.e ( "shit", ""+cow.size () );
		//journalDAO.test ();
		FoodDAO foodDao = new FoodDAO(db);
		List<FoodEntry> list = foodDao.read ();
		Log.e ( "r", ""+list.toString () );
	}
		
	private void updateDateHeader(long newDate)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE_HEADER);
		String dateText = formatter.format ( newDate );
		this.textDateHeader.setText ( dateText );
	}

	private Calendar getCalendarForDate ( int day, int month, int year )
	{
		Calendar calendar = Calendar.getInstance ();
		calendar.set ( Calendar.DAY_OF_MONTH, day );
		calendar.set ( Calendar.MONTH, month );
		calendar.set ( Calendar.YEAR, year );

		return calendar;
	}

	private Calendar getCalendarForPeriod ( CalendarPeriod period )
	{
		Calendar cal = Calendar.getInstance ();
		cal.setTimeInMillis ( this.adapter.getDate () );
		int day = cal.get ( Calendar.DAY_OF_MONTH );
		int month = cal.get ( Calendar.MONTH );
		int year = cal.get ( Calendar.YEAR );

		switch ( period )
		{
		case LAST_YEAR:
			return getCalendarForDate ( day, month, ( year - 1 ) );
		case LAST_MONTH:
			return getCalendarForDate ( day, ( month - 1 ), year );
			
		case NEXT_MONTH:
			return getCalendarForDate ( day, ( month + 1 ), year );
			
		case NEXT_YEAR:
			return getCalendarForDate ( day, month, ( year + 1 ) );
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
			// to avoid date change events when user is navigating through
			// calendar.
			
			Calendar calendar = Calendar.getInstance ();
			
			switch ( v.getId () )
			{
			case R.id.journal_button_last_year:
				calendar = (Calendar) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.LAST_YEAR ).clone ();
				break;
			case R.id.journal_button_last_month:
				calendar = (Calendar) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.LAST_MONTH ).clone();
				break;
			case R.id.journal_button_today:
				calendar = (Calendar) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.TODAY ).clone();
				break;
			case R.id.journal_button_next_month:
				calendar = (Calendar) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.NEXT_MONTH ).clone();
				break;
			case R.id.journal_button_next_year:
				calendar = (Calendar) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.NEXT_YEAR ).clone();
				break;
			default:
				calendar = (Calendar) JournalActivity.this.getCalendarForPeriod ( CalendarPeriod.TODAY ).clone();
				break;
			}
			JournalActivity.this.adapter.setDate ( calendar );
			JournalActivity.this.updateDateHeader ( calendar.getTimeInMillis () );
			
			
		}

	}

}
