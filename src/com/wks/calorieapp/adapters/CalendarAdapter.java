package com.wks.calorieapp.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.wks.calorieapp.R;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter
{

	private static final int DAYS_PER_WEEK = 7;
	private static final int WEEKS_PER_MONTH = 6;// do not change
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private int textSizeDate = 12;
	private int textSizeDescription = 12;
	private int textColorDate = Color.BLACK;
	private int textColorDescription = Color.BLACK;

	private Context context;
	private Calendar calendar;
	private int height;
	private Map< Long, CalendarEvent > events;

	public CalendarAdapter(Context context)
	{
		this(context,null,null);
	}
	
	public CalendarAdapter (Context context, Calendar calendar)
	{
		this(context, calendar, null);
	}
	
	public CalendarAdapter ( Context context, Calendar calendar, Map< String, CalendarEvent > events )
	{
		if ( context == null ) throw new IllegalStateException ( "Application context must not be null" );

		this.context = context;

		this.calendar = calendar == null ? Calendar.getInstance () : ( Calendar ) calendar.clone ();

		DisplayMetrics metrics = new DisplayMetrics ();
		WindowManager manager = ( WindowManager ) context.getSystemService ( Context.WINDOW_SERVICE );
		manager.getDefaultDisplay ().getMetrics ( metrics );
		this.height = metrics.heightPixels / WEEKS_PER_MONTH;

		this.setItems ( events );
	}
	
	public void setItems(Map<String,CalendarEvent> events)
	{
		if ( events == null ) return;

		this.events = new HashMap< Long, CalendarEvent > ();
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat ( DATE_FORMAT );
			for ( Entry< String, CalendarEvent > entry : events.entrySet () )
			{
				String _date = entry.getKey ();
				CalendarEvent event = entry.getValue ();

				long date = formatter.parse ( _date ).getTime ();
				this.events.put ( date, event );
			}
		}
		catch ( ParseException e )
		{
			throw new IllegalArgumentException ( "Date String must match format: " + DATE_FORMAT );
		}
	}
	
	public void clearItems()
	{
		this.events.clear ();
	}

	/**
	 * DO NOT CHANGE
	 * 
	 * @return number of days in month.
	 */
	public int getCount ()
	{
		return CalendarAdapter.DAYS_PER_WEEK * CalendarAdapter.WEEKS_PER_MONTH;
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
			return this.events.get ( cal.getTimeInMillis () );

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
			resultView = inflater.inflate ( R.layout.calendar_cell, null );

			holder = new ViewHolder ();
			holder.cellCalendar = ( LinearLayout ) resultView.findViewById ( R.id.calendar_cell_layout );
			holder.textDate = ( TextView ) resultView.findViewById ( R.id.calendar_cell_date );
			holder.image = ( ImageView ) resultView.findViewById ( R.id.calendar_cell_image );
			holder.textDescription = ( TextView ) resultView.findViewById ( R.id.calendar_cell_description );

			holder.textDate.setTextSize ( this.textSizeDate );
			holder.textDate.setTextColor ( this.textColorDate );

			holder.textDescription.setTextSize ( this.textSizeDate );
			holder.textDescription.setTextColor ( this.textColorDescription );

			resultView.setMinimumHeight ( this.height );
			resultView.setTag ( holder );
		}else
		{
			holder = ( ViewHolder ) resultView.getTag ();
		}

		int date = this.getDateAtPosition ( position );
		if ( date != -1 )
		{
			// show the date in the calendar.
			holder.textDate.setText ( "" + date );

			if ( this.events != null )
			{
				// if there is an event on this day in the calendar, display
				// details.
				CalendarEvent event = events.get ( this.getDateAsLong ( date ) );
				if ( event != null )
				{
					holder.image.setImageDrawable ( event.getEventImage () );
					holder.textDescription.setText ( event.getEventDescription () );
					holder.cellCalendar.setBackgroundColor ( event.getEventBackgroundColor () );
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

	private long getDateAsLong ( int date )
	{
		Calendar cal = ( Calendar ) this.calendar.clone ();
		cal.set ( Calendar.DAY_OF_MONTH, date );
		return cal.getTimeInMillis ();
	}

	private static class ViewHolder
	{
		private LinearLayout cellCalendar;
		private TextView textDate;
		private ImageView image;
		private TextView textDescription;
	}

	public void setDate ( Calendar calendar )
	{
		this.calendar = ( Calendar ) calendar.clone ();
		this.notifyDataSetChanged ();
	}
	
	
	public long getDate()
	{
		return this.calendar.getTimeInMillis ();
	}


	public int getTextSizeDate ()
	{
		return textSizeDate;
	}

	public void setTextSizeDate ( int textSizeDate )
	{
		this.textSizeDate = textSizeDate;
	}

	public int getTextSizeDescription ()
	{
		return textSizeDescription;
	}

	public void setTextSizeDescription ( int textSizeDescription )
	{
		this.textSizeDescription = textSizeDescription;
	}

	public int getTextColorDate ()
	{
		return textColorDate;
	}

	public void setTextColorDate ( int textColorDate )
	{
		this.textColorDate = textColorDate;
	}

	public int getTextColorDescription ()
	{
		return textColorDescription;
	}

	public void setTextColorDescription ( int textColorDescription )
	{
		this.textColorDescription = textColorDescription;
	}

}
