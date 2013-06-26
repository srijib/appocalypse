package com.wks.calorieapp.activities;

import java.util.Calendar;

import com.wks.calorieapp.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;

public class JournalActivity extends Activity
{
	public enum CalendarPeriod
	{
		TODAY, NEXT_WEEK, NEXT_MONTH, NEXT_YEAR, LAST_WEEK, LAST_MONTH, LAST_YEAR
	};

	private CalendarView annoyingPieceOfShit;
	private ImageButton buttonLastMonth;
	private ImageButton buttonLastYear;
	private ImageButton buttonToday;
	private ImageButton buttonNextMonth;
	private ImageButton buttonNextYear;
	private ImageButton [] buttonsDateControl;

	private boolean userIsNavigating;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_journal );

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

	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		Drawable drawable = this.getResources ().getDrawable ( R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( drawable );

		actionBar.setDisplayHomeAsUpEnabled ( true );
	}

	private void setupView ()
	{
		annoyingPieceOfShit = ( CalendarView ) this.findViewById ( R.id.journal_calendar );
		buttonLastYear = ( ImageButton ) this.findViewById ( R.id.journal_button_last_year );
		buttonLastMonth = ( ImageButton ) this.findViewById ( R.id.journal_button_last_month );
		buttonToday = ( ImageButton ) this.findViewById ( R.id.journal_button_today );
		buttonNextMonth = ( ImageButton ) this.findViewById ( R.id.journal_button_next_month );
		buttonNextYear = ( ImageButton ) this.findViewById ( R.id.journal_button_next_year );

		buttonsDateControl = new ImageButton [] { buttonLastYear, buttonLastMonth, buttonToday, buttonNextMonth, buttonNextYear };

		Calendar fuckingCalendar = Calendar.getInstance ();
		annoyingPieceOfShit.setDate ( fuckingCalendar.getTimeInMillis () );
	}

	private void setupListeners ()
	{
		for ( ImageButton button : buttonsDateControl )
			button.setOnClickListener ( new OnDateControlButtonClicked () );

		annoyingPieceOfShit.setOnDateChangeListener ( new OnCalendarDateClicked () );
	}

	private void setCalendarDate ( int day, int month, int year )
	{
		Calendar calendar = Calendar.getInstance ();
		calendar.set ( Calendar.DAY_OF_MONTH, day );
		calendar.set ( Calendar.MONTH, month );
		calendar.set ( Calendar.YEAR, year );

		this.annoyingPieceOfShit.setDate ( calendar.getTimeInMillis () );
	}

	private void setCalendarPeriod ( CalendarPeriod period )
	{
		Calendar cal = Calendar.getInstance ();
		cal.setTimeInMillis ( this.annoyingPieceOfShit.getDate () );
		int day = cal.get ( Calendar.DAY_OF_MONTH );
		int month = cal.get ( Calendar.MONTH );
		int year = cal.get ( Calendar.YEAR );

		switch ( period )
		{
		case LAST_YEAR:
			this.setCalendarDate ( day, month, ( year - 1 ) );
			return;
		case LAST_MONTH:
			this.setCalendarDate ( day, ( month - 1 ), year );
			return;
		case NEXT_MONTH:
			this.setCalendarDate ( day, ( month + 1 ), year );
			return;
		case NEXT_YEAR:
			this.setCalendarDate ( day, month, ( year + 1 ) );
			return;
		case TODAY:
		default:
			Calendar calendar = Calendar.getInstance ();
			annoyingPieceOfShit.setDate ( calendar.getTimeInMillis () );
			return;

		}
	}

	class OnDateControlButtonClicked implements View.OnClickListener
	{

		public void onClick ( View v )
		{
			// to avoid date change events when user is navigating through
			// calendar.
			JournalActivity.this.userIsNavigating = true;

			switch ( v.getId () )
			{
			case R.id.journal_button_last_year:
				JournalActivity.this.setCalendarPeriod ( CalendarPeriod.LAST_YEAR );
				break;
			case R.id.journal_button_last_month:
				JournalActivity.this.setCalendarPeriod ( CalendarPeriod.LAST_MONTH );
				break;
			case R.id.journal_button_today:
				JournalActivity.this.setCalendarPeriod ( CalendarPeriod.TODAY );
				break;
			case R.id.journal_button_next_month:
				JournalActivity.this.setCalendarPeriod ( CalendarPeriod.NEXT_MONTH );
				break;
			case R.id.journal_button_next_year:
				JournalActivity.this.setCalendarPeriod ( CalendarPeriod.NEXT_YEAR );
				break;
			default:
				JournalActivity.this.setCalendarPeriod ( CalendarPeriod.TODAY );
				break;
			}
			JournalActivity.this.userIsNavigating = false;
		}

	}

	class OnCalendarDateClicked implements CalendarView.OnDateChangeListener
	{

		@Override
		public void onSelectedDayChange ( CalendarView view, int year, int month, int dayOfMonth )
		{
			if ( !JournalActivity.this.userIsNavigating )
			{
				Toast.makeText ( JournalActivity.this, "" + dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_SHORT ).show ();

			}
			JournalActivity.this.userIsNavigating = false;
		}

	}

}
