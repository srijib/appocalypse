package com.wks.calorieapp.adapters;

import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.wks.calorieapp.entities.JournalEntry;
import com.wks.calorieapp.models.DateCaloriesModel;
import com.wks.calorieapp.utils.AndroidBitmap;
import com.wks.calorieapp.utils.FileSystem;

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

public class DateCaloriesAdapter extends BaseAdapter implements Observer
{
	private static final String TAG = DateCaloriesAdapter.class.getCanonicalName ();

	private Context context;
	private List< JournalEntry > items;
	private LayoutInflater inflater;

	public DateCaloriesAdapter ( Context context )
	{
		this.context = context;
		this.inflater = LayoutInflater.from ( context );
	}

	public void setItems ( List< JournalEntry > items )
	{
		this.items = items;
		this.notifyDataSetChanged ();
	}

	public void clear ()
	{
		this.items.clear ();
		this.notifyDataSetChanged ();
	}

	@Override
	public int getCount ()
	{
		return items == null ? 0 : items.size ();
	}

	@Override
	public JournalEntry getItem ( int position )
	{
		return this.items == null ? null : items.get ( position );
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

		JournalEntry entry = this.items.get ( position );
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
						if ( photo != null ) holder.imageMeal.setImageBitmap ( photo );
					}
				}

				holder.textMealName.setText ( entry.getNutritionInfo ().getName () );
				holder.textMealCalories.setText ( String.format ( "%.1f cal", entry.getNutritionInfo ().getCaloriesPer100g () ) );
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
		DateCaloriesModel model = ( DateCaloriesModel ) data;
		List< JournalEntry > mealEntries = model.getMealEntries ();
		this.setItems ( mealEntries );
	}

	class ViewHolder
	{
		ImageView imageMeal;
		TextView textMealName;
		TextView textMealCalories;
	}

}
