package com.wks.calorieapp.adapters;

import java.util.List;
import com.wks.calorieapp.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationListAdapter extends BaseAdapter
{	
	private Context context;
	private List<GridItem> items;
	private LayoutInflater inflater;
	
	public NavigationListAdapter (Context context, List<GridItem> items, int numRows)
	{
		this.context = context;
		this.items = items;
		this.inflater = LayoutInflater.from ( context );
	
	}
	
	@Override
	public int getCount ()
	{
		return this.items.size ();
	}

	@Override
	public GridItem getItem ( int position )
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
		
		if(resultView == null)
		{
			resultView = inflater.inflate ( R.layout.activity_home_list_item, null );
			
			
			holder = new ViewHolder();
			holder.image = (ImageView) resultView.findViewById ( R.id.list_item_image );
			holder.text = (TextView) resultView.findViewById ( R.id.list_item_text );
			resultView.setTag ( holder );
			
		}else
		{
			holder = (ViewHolder) resultView.getTag ();
		}
		
		GridItem item = getItem(position);
		
		holder.image.setImageDrawable ( context.getResources().getDrawable ( item.getResourceId () ));
		holder.text.setText ( item.getText () );
		
		return resultView;
	}

	class ViewHolder
	{
		ImageView image;
		TextView text;
	}
	
	
}
