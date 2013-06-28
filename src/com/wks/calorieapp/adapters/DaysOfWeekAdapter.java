package com.wks.calorieapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DaysOfWeekAdapter extends BaseAdapter
{
	private Context context;
	private static final String[] daysOfWeek = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	
	
	public DaysOfWeekAdapter (Context context)
	{
		this.context = context;
	}
	
	@Override
	public int getCount ()
	{
		return daysOfWeek.length;
	}

	@Override
	public String getItem ( int position )
	{
		return daysOfWeek[position];
	}
	

	public long getItemId ( int position )
	{
		return position;
	}


	@Override
	public View getView ( int position, View convertView, ViewGroup parent )
	{
		TextView text;
		if(convertView == null)
		{
			text = new TextView(this.context);
			text.setTypeface ( null, Typeface.BOLD );
			text.setGravity ( Gravity.CENTER );
			
		}else
		{
			text = (TextView) convertView;
		}
		
		text.setText ( this.getItem ( position ) );
		return text;
	}

}
