package com.wks.calorieapp.adapters;

import java.util.List;

import com.wks.calorieapp.pojos.JournalEntry;

import com.wks.calorieapp.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DateCaloriesListAdapter extends BaseAdapter
{
	private static final String TAG = DateCaloriesListAdapter.class.getCanonicalName ();
	
	private List<JournalEntry> items;
	private LayoutInflater inflater;
	
	public DateCaloriesListAdapter(Context context, List<JournalEntry> items)
	{
		this.inflater = LayoutInflater.from ( context );
		this.setItems ( items );
	}
	
	public void setItems ( List< JournalEntry > items )
	{
		this.items = items;
		this.notifyDataSetChanged ();
	}
	
	public void clear()
	{
		this.items.clear ();
		this.notifyDataSetChanged ();
	}

	@Override
	public int getCount ()
	{
		return items.size ();
	}

	@Override
	public Object getItem ( int position )
	{
		return this.items.get ( position );
	}

	@Override
	public long getItemId ( int position )
	{
		return position;
	}

	@Override
	public View getView ( int position, View convertView, ViewGroup parent )
	{
		View resultView = convertView;
		ViewHolder holder;
		
		if(resultView == null)
		{
			resultView = inflater.inflate ( R.layout.activity_date_calories_list_item, null );
			
			holder = new ViewHolder();
			holder.imageMeal = (ImageView) resultView.findViewById ( R.id.date_calories_image_meal );
			holder.textMealName = (TextView) resultView.findViewById ( R.id.date_calories_text_meal_name );
			holder.textMealCalories = (TextView) resultView.findViewById ( R.id.date_calories_text_meal_calories );
			
			resultView.setTag ( holder );
		}else
		{
			holder = (ViewHolder) resultView.getTag ();
		}
		
		JournalEntry entry = this.items.get ( position );
		try{
			holder.textMealName.setText ( entry.getFoodEntry ().getName () );
			holder.textMealCalories.setText ( String.format ( "%.1f cal", entry.getFoodEntry ().getCalories () ));
		}catch(NullPointerException npe)
		{
			String s = entry == null? "unknown date" : entry.getDateAsString ();
			Log.e(TAG,"Null pointer exception for "+s+" : "+npe.toString ());
		}
		
		
		return resultView;
	}
	
	class ViewHolder
	{
		ImageView imageMeal;
		TextView textMealName;
		TextView textMealCalories;
	}
	
}
