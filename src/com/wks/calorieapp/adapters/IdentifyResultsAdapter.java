package com.wks.calorieapp.adapters;

import com.wks.calorieapp.R;
import com.wks.calorieapp.models.IdentifyResultsModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IdentifyResultsAdapter extends BaseAdapter
{
	private IdentifyResultsModel model;
	private LayoutInflater inflater;

	public IdentifyResultsAdapter (Context context, IdentifyResultsModel model)
	{
		this.model = model;
		this.inflater = LayoutInflater.from ( context );
	}
	
	
	@Override
	public int getCount ()
	{
		return this.model.getPossibleMatchesList ().size ();
	}

	@Override
	public String getItem ( int position )
	{
		return this.model.getPossibleMatchesList ().get ( position );
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
			resultView = inflater.inflate ( R.layout.identify_list_result_item, null );
			
			holder = new ViewHolder();
			holder.textResultItem = (TextView) resultView.findViewById ( R.id.camera_listview_results_text_result );
			resultView.setTag ( holder );
			
		}else
		{
			holder = (ViewHolder) resultView.getTag ();
		}
		
		holder.textResultItem.setText ( this.getItem ( position ) );
		return resultView;
	}

	class ViewHolder{
		TextView textResultItem;
	}
}
