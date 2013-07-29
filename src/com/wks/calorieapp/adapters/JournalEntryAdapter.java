package com.wks.calorieapp.adapters;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import com.wks.android.utils.BitmapUtils;
import com.wks.android.utils.FileSystem;
import com.wks.calorieapp.entities.JournalEntry;
import com.wks.calorieapp.models.JournalEntryModel;

import com.wks.calorieapp.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JournalEntryAdapter extends BaseAdapter implements Observer
{
	private static final String TAG = JournalEntryAdapter.class.getCanonicalName ();

	private Context context;
	private JournalEntryModel model;
	private LayoutInflater inflater;

	public JournalEntryAdapter ( Context context, JournalEntryModel model )
	{
		this.context = context;
		this.model = model;
		this.inflater = LayoutInflater.from ( context );
	}

	@Override
	public int getCount ()
	{
		return this.model.getJournalEntries ().size ();
	}

	@Override
	public JournalEntry getItem ( int position )
	{
		return this.model.getJournalEntries ().get ( position );
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

		if ( resultView == null )
		{
			resultView = inflater.inflate ( R.layout.activity_date_calories_list_item, null );

			holder = new ViewHolder ();
			holder.imageMeal = ( ImageView ) resultView.findViewById ( R.id.date_calories_image_meal );
			holder.textMealName = ( TextView ) resultView.findViewById ( R.id.date_calories_text_meal_name );
			holder.textMealCalories = ( TextView ) resultView.findViewById ( R.id.date_calories_text_meal_calories );

			resultView.setTag ( holder );
		}else
		{
			holder = ( ViewHolder ) resultView.getTag ();
		}

		JournalEntry entry = this.getItem ( position );
		if ( entry != null )
		{
			try
			{

				if ( entry.getImageEntry () != null )
				{
					File file = new File ( FileSystem.getPicturesDirectory ( context ) + entry.getImageEntry ().getFileName () );
					if ( file.exists () )
					{
						
						Bitmap photo = BitmapFactory.decodeFile ( file.getAbsolutePath () );
						photo = BitmapUtils.rotate ( photo, 90 );
						if ( photo != null ) holder.imageMeal.setImageBitmap ( photo );
					}
				}

				holder.textMealName.setText ( entry.getNutritionInfo ().getName () );
				holder.textMealCalories.setText ( String.format ( "%.1f cal", entry.getNutritionInfo ().getCalories () ) );
			}
			catch ( NullPointerException npe )
			{
				String s = entry == null ? "unknown date" : entry.getDateAsString ();
				Log.e ( TAG, "Null pointer exception for " + s + " : " + npe.toString () );
			}
		}

		return resultView;
	}

	@Override
	public void update ( Observable observable, Object data )
	{
		this.model = ( JournalEntryModel ) data;
		this.notifyDataSetChanged ();
	}

	class ViewHolder
	{
		ImageView imageMeal;
		TextView textMealName;
		TextView textMealCalories;
	}

}
