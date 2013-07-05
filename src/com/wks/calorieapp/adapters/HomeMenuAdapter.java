package com.wks.calorieapp.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.wks.calorieapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeMenuAdapter extends BaseAdapter
{
	private Context context;
	private List< Activity > items;
	private LayoutInflater inflater;

	public HomeMenuAdapter ( Context context )
	{
		this.context = context;
		this.inflater = LayoutInflater.from ( context );

		this.items = new ArrayList< Activity > ();

		this.items = new ArrayList<Activity>(Arrays.asList ( Activity.values () ));
		
	}

	@Override
	public int getCount ()
	{
		return this.items.size ();
	}

	@Override
	public Activity getItem ( int position )
	{
		return items.get ( position );
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
			resultView = inflater.inflate ( R.layout.activity_home_list_item, null );

			holder = new ViewHolder ();
			holder.image = ( ImageView ) resultView.findViewById ( R.id.list_item_image );
			holder.text = ( TextView ) resultView.findViewById ( R.id.list_item_text );
			resultView.setTag ( holder );

		}else
		{
			holder = ( ViewHolder ) resultView.getTag ();
		}

		Activity item = getItem ( position );

		holder.image.setImageDrawable ( context.getResources ().getDrawable ( item.getResourceId () ) );
		holder.text.setText ( item.getText () );

		return resultView;
	}

	class ViewHolder
	{
		ImageView image;
		TextView text;
	}

	public enum Activity
	{
		CAMERA ( "Calorie Camera", R.drawable.ic_calorie_camera ), SEARCH ( "Food Search", R.drawable.search ), JOURNAL ( "Calorie Journal",
				R.drawable.ic_launcher ), GALLERY ( "Gallery", R.drawable.ic_gallery ), PROFILE ( "Profile", R.drawable.ic_launcher );

		private final String text;
		private final int resourceId;

		Activity ( String text, int resourceId )
		{
			this.text = text;
			this.resourceId = resourceId;
		}

		public String getText ()
		{
			return text;
		}

		public int getResourceId ()
		{
			return resourceId;
		}

	}
}
