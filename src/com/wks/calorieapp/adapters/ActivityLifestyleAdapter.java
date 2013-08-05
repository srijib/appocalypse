package com.wks.calorieapp.adapters;



import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ActivityLifestyleAdapter extends BaseAdapter
{
	private static final int PADDING = 5;
	public Context context;

	public String[] entries = {
			"No Exercise",
			"1 - 3 days/week",
			"3 - 5 days/week",
			"6 - 7 days/week",
			"Daily Intense Exercise"};
	public float[] values = {1.200F, 1.375F, 1.550F, 1.725F,1.900F};
 	
	public ActivityLifestyleAdapter(Context context)
	{
		this.context = context;
	}
	
	@Override
	public int getCount ()
	{
		return this.entries.length;
	}

	@Override
	public Float getItem ( int position )
	{
		return  values[position];
	}
	
	public int getPositionForActivityFactor(float item)
	{
		int position = 0;
		for(int i=0;i<values.length;i++)
			if(values[i] == item)
				position = i;
		
		return position;
	}

	@Override
	public long getItemId ( int position )
	{
		return position;
	}

	@Override
	public View getView ( int position, View convertView, ViewGroup parent )
	{
		TextView textView = (convertView == null)? new TextView(this.context) : (TextView) convertView;
		textView.setText ( entries[position] );
		textView.setPadding ( PADDING, PADDING, PADDING, PADDING );
		return textView;
	}
	
	
}
