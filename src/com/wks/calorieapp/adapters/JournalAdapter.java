package com.wks.calorieapp.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import com.wks.calorieapp.R;
import com.wks.calorieapp.entities.CalendarEvent;
import com.wks.calorieapp.models.JournalModel;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JournalAdapter extends BaseAdapter implements Observer
{

	private static final int DAYS_PER_WEEK = 7;
	private static final int WEEKS_PER_MONTH = 6;// do not change
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private Context context;
	private JournalModel journalModel;
	private Calendar calendar;
	private int height;
	private Map< String, CalendarEvent > events;
	private SimpleDateFormat formatter;

	public JournalAdapter ( Context context, Calendar calendar, JournalModel model )
	{
		if ( context == null ) throw new IllegalStateException ( "Application context must not be null" );
		if ( model == null ) throw new IllegalStateException ( "Model must not be null" );

		this.context = context;
		this.journalModel = model;
		this.journalModel.addObserver ( this );

		this.calendar = calendar == null ? Calendar.getInstance () : ( Calendar ) calendar.clone ();
		this.formatter = new SimpleDateFormat ( DATE_FORMAT );

		DisplayMetrics metrics = new DisplayMetrics ();
		WindowManager manager = ( WindowManager ) context.getSystemService ( Context.WINDOW_SERVICE );
		manager.getDefaultDisplay ().getMetrics ( metrics );
		this.height = metrics.heightPixels / WEEKS_PER_MONTH;

		this.setItems ( this.journalModel.getCalorieCalendar () );
	}

	public void setItems ( Map< Calendar, CalendarEvent > events )
	{

		if ( events != null )
		{
			this.events = new HashMap< String, CalendarEvent > ();

			for ( Entry< Calendar, CalendarEvent > entry : events.entrySet () )
			{
				String date = formatter.format ( entry.getKey ().getTimeInMillis () );
				this.events.put ( date, entry.getValue () );

			}

			this.notifyDataSetChanged ();
		}

	}

	public void clearItems ()
	{
		this.events.clear ();
		this.notifyDataSetChanged ();
	}

	/**
	 * DO NOT CHANGE
	 * 
	 * @return number of days in month.
	 */
	public int getCount ()
	{
		return DAYS_PER_WEEK * WEEKS_PER_MONTH;
	}

	/**
	 * @return the event at the given day. you can override this method and
	 *         return a value relevant to your own implementation.
	 * 
	 */
	public CalendarEvent getItem ( int position )
	{
		int date = this.getDateAtPosition ( position );
		if ( date != -1 )
		{
			Calendar cal = ( Calendar ) this.calendar.clone ();
			cal.set ( Calendar.DAY_OF_MONTH, date );
			String currentDate = formatter.format ( cal.getTimeInMillis () );

			return this.events.get ( currentDate );

		}
		return null;
	}

	@Override
	public long getItemId ( int position )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView ( int position, View convertView, ViewGroup parent )
	{
		View resultView = convertView;
		ViewHolder holder;
		LayoutInflater inflater;

		if ( resultView == null )
		{
			inflater = LayoutInflater.from ( context );
			resultView = inflater.inflate ( R.layout.activity_journal_calendar_cell, null );

			holder = new ViewHolder ( resultView );

			resultView.setMinimumHeight ( this.height );
			resultView.setTag ( holder );
		}else
		{
			holder = ( ViewHolder ) resultView.getTag ();
		}

		holder.clear ();

		int date = this.getDateAtPosition ( position );
		if ( date != -1 )
		{
			// show the date in the calendar.
			holder.setTextDate ( "" + date );
			if ( this.events != null )
			{
				Calendar currentDay = ( Calendar ) this.calendar.clone ();
				currentDay.set ( Calendar.DAY_OF_MONTH, date );
				String eventDay = formatter.format ( currentDay.getTimeInMillis () );

				CalendarEvent event = events.get ( eventDay );
				if ( event != null )
				{
					holder.setCalendarEvent ( event );
				}
			}

		}else
		{

			holder.textDate.setText ( "" );
			resultView.setEnabled ( false );
			resultView.setClickable ( false );
			resultView.setFocusable ( false );

		}

		return resultView;
	}

	// DO NOT CHANGE
	public int getDateAtPosition ( int position )
	{
		int date = position - getPositionFirstDayOfMonth () + 1;

		if ( date <= 0 || date > this.calendar.getActualMaximum ( Calendar.DAY_OF_MONTH ) ) return -1;

		return date;
	}

	// DO NOT CHANGE
	private int getPositionFirstDayOfMonth ()
	{
		return this.getFirstDayOfMonth () - 1;
	}

	// DO NOT CHANGE
	// Days of week are numbered the same as Calendar.DAY_OF_WEEK i.e.
	// Sunday = 1;
	// Monday = 2;
	// Tuesday = 3;
	// Wednesday = 4;
	// Thursday = 5;
	// Friday = 6;
	// Saturday = 7;
	private int getFirstDayOfMonth ()
	{
		Calendar cal = ( Calendar ) this.calendar.clone ();
		cal.set ( Calendar.DAY_OF_MONTH, 1 );

		int i = cal.get ( Calendar.DAY_OF_WEEK );

		return i;
	}

	public long getDate ()
	{
		return this.calendar.getTimeInMillis ();
	}

	// update calendar dates
	public void setDate ( Calendar calendar )
	{
		this.calendar = ( Calendar ) calendar.clone ();
		this.notifyDataSetChanged ();
	}

	// update calendar events
	@Override
	public void update ( Observable observable, Object data )
	{
		JournalModel model = ( JournalModel ) data;
		Map< Calendar, CalendarEvent > calorieCalendar = model.getCalorieCalendar ();

		if ( calorieCalendar != null )
		{
			this.setItems ( calorieCalendar );
			this.notifyDataSetChanged ();
		}

	}

	private static class ViewHolder
	{
		private LinearLayout cellCalendar;
		private TextView textDate;
		private ImageView image;
		private TextView textDescription;

		public ViewHolder ( View view )
		{
			this.cellCalendar = ( LinearLayout ) view.findViewById ( R.id.calendar_cell_layout );
			this.textDate = ( TextView ) view.findViewById ( R.id.calendar_cell_date );
			this.image = ( ImageView ) view.findViewById ( R.id.calendar_cell_image );
			this.textDescription = ( TextView ) view.findViewById ( R.id.calendar_cell_description );

		}

		public void clear ()
		{
			this.cellCalendar.setBackgroundColor ( 0 );
			this.textDate.setText ( "" );
			this.image.setImageDrawable ( null );
			this.textDescription.setText ( "" );
		}

		public void setTextDate ( String date )
		{
			this.textDate.setText ( date );
		}

		public void setCalendarEvent ( CalendarEvent event )
		{
			this.image.setImageDrawable ( event.getDrawable () );
			this.textDescription.setText ( event.getDescription () );
			this.cellCalendar.setBackgroundColor ( event.getBackgroundColor () );
		}
	}

}
