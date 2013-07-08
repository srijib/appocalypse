package com.wks.calorieapp.adapters;

import java.io.File;
import java.util.List;

import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.pojos.JournalEntry;
import com.wks.calorieapp.utils.FileSystem;

import com.wks.calorieapp.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	
	private Context context;
	private List<JournalEntry> items;
	private LayoutInflater inflater;
	private SQLiteDatabase db;
	
	public DateCaloriesListAdapter(Context context, List<JournalEntry> items)
	{
		this.context = context;
		this.inflater = LayoutInflater.from ( context );
		this.setItems ( items );
		
		DatabaseManager manager = DatabaseManager.getInstance ( context);
		this.db = manager.open ();
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
	public JournalEntry getItem ( int position )
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
			
			if(entry.getImageEntry () != null)
			{
				File file = new File(FileSystem.getPicturesDirectory ( context )+entry.getImageEntry ().getFileName ());
				if(file.exists ())
				{	
					Bitmap photo = BitmapFactory.decodeFile ( file.getAbsolutePath () );
					if(photo != null)
					holder.imageMeal.setImageBitmap ( photo );
				}
			}
			
			holder.textMealName.setText ( entry.getNutritionInfo ().getName () );
			holder.textMealCalories.setText ( String.format ( "%.1f cal", entry.getNutritionInfo ().getCaloriesPer100g () ));
		}catch(NullPointerException npe)
		{
			String s = entry == null? "unknown date" : entry.getDateAsString ();
			Log.e(TAG,"Null pointer exception for "+s+" : "+npe.toString ());
		}
		
		
		return resultView;
	}
	
	public boolean remove ( int position )
	{
		boolean success = false;
		JournalEntry entry = this.getItem ( position );
		if(this.db != null)
		{
			JournalDAO journalDao = new JournalDAO(this.db);
			if(journalDao.delete ( entry.getId () ) > 0)
			{
				//if item has been deleted in db, remove from list
				this.items.remove ( position );
				success = true;
			}
		}
		this.notifyDataSetChanged ();
		return success;
	}
	
	class ViewHolder
	{
		ImageView imageMeal;
		TextView textMealName;
		TextView textMealCalories;
	}

	
	
}
