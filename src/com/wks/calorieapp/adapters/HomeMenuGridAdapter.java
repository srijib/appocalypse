package com.wks.calorieapp.adapters;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.wks.calorieapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeMenuGridAdapter extends BaseAdapter
{

	private LinkedHashMap<String,Integer> items;
	private LayoutInflater inflater;
	
	public HomeMenuGridAdapter (Context context, LinkedHashMap<String,Integer> items)
	{

		this.items = items;
		this.inflater = LayoutInflater.from ( context );
	}
	
	@Override
	public int getCount ()
	{
		return this.items.size ();
	}

	@Override
	public Entry<String,Integer> getItem ( int position )
	{
		Iterator<Entry<String,Integer>> iterator = items.entrySet ().iterator ();
		int count = 0;
		while(iterator.hasNext ())
		{
			if(count == position)
				return iterator.next ();
			
			count++;
		}
		
		return null;
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
			resultView = inflater.inflate ( R.layout.grid_item, null );
			
			holder = new ViewHolder();
			holder.image = (ImageView) resultView.findViewById ( R.id.grid_item_image );
			holder.text = (TextView) resultView.findViewById ( R.id.grid_item_text );
			resultView.setTag ( holder );
			
		}else
		{
			holder = (ViewHolder) resultView.getTag ();
		}
		
		Entry<String,Integer> item = getItem(position);
		holder.image.setImageResource ( item.getValue ());
		holder.text.setText ( item.getKey () );
		return resultView;
	}

	static class ViewHolder
	{
		ImageView image;
		TextView text;
	}
}
