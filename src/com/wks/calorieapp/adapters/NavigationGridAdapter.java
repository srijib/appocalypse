package com.wks.calorieapp.adapters;

import java.util.List;
import com.wks.calorieapp.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NavigationGridAdapter extends BaseAdapter
{
	private static final int DEFAULT_NUM_ROWS = 1;
	
	private Context context;
	private List<GridItem> items;
	private LayoutInflater inflater;
	private int height;
	
	public NavigationGridAdapter (Context context, List<GridItem> items, int numRows)
	{
		this.context = context;
		this.items = items;
		this.inflater = LayoutInflater.from ( context );
		
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager manager = (WindowManager) context.getSystemService (Context.WINDOW_SERVICE);
		manager.getDefaultDisplay ().getMetrics ( metrics );
	
		if(numRows <=0) numRows = DEFAULT_NUM_ROWS;
		this.height = metrics.heightPixels/numRows;
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
			resultView = inflater.inflate ( R.layout.activity_home_grid_item, null );
			
			
			holder = new ViewHolder();
			holder.text = (TextView) resultView.findViewById ( R.id.grid_item_text );
			resultView.setTag ( holder );
			
		}else
		{
			holder = (ViewHolder) resultView.getTag ();
		}
		
		GridItem item = getItem(position);
		
		Drawable drawable = context.getResources().getDrawable ( item.getResourceId () );
		holder.text.setCompoundDrawablesWithIntrinsicBounds ( null, drawable, null, null );
		holder.text.setText ( item.getText () );
		
		resultView.setMinimumHeight ( this.height );
		
		return resultView;
	}

	static class ViewHolder
	{
		TextView text;
	}
	
	
}
