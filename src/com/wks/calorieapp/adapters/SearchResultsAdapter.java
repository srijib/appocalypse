package com.wks.calorieapp.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import com.wks.calorieapp.apis.NutritionInfo;
import com.wks.calorieapp.models.SearchResultsModel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class SearchResultsAdapter extends BaseExpandableListAdapter implements Observer
{
	private static final String FORMAT_CALORIE = "%.2f cal";
	private List< GenericFoodItem > genericFoodList;
	private LayoutInflater inflater;
	private SearchResultsModel model;
	
	
	public SearchResultsAdapter ( Context context,  SearchResultsModel model )
	{
		this.inflater = LayoutInflater.from ( context );
		this.model = model;
		this.model.addObserver ( this );
		this.setItems ( model.getSearchResults () );
	}
	
	public void setItems(Map<String,List<NutritionInfo>> items)
	{	
		if(items == null) return;
		Log.e("WKS","Setting Items" );
		this.genericFoodList = new ArrayList< GenericFoodItem > ();
		for ( Entry< String, List< NutritionInfo >> e : items.entrySet () )
		{
			GenericFoodItem food = new GenericFoodItem ( e.getKey () );
			List< NutritionInfo > nutrinfoList = e.getValue ();

			for ( NutritionInfo info : nutrinfoList )
				food.getNutritionInfoList ().add ( info );

			this.genericFoodList.add ( food );
		}
		this.notifyDataSetChanged ();
	}

	public GenericFoodItem getGroup ( int groupPosition )
	{
		return genericFoodList == null? null : genericFoodList.get ( groupPosition );
	}

	@Override
	public long getGroupId ( int groupPosition )
	{
		return groupPosition;
	}

	@Override
	public int getGroupCount ()
	{
		return genericFoodList == null? 0 : genericFoodList.size ();
	}

	@Override
	public View getGroupView ( int groupPosition, boolean isExpanded, View convertView, ViewGroup parent )
	{
		View resultView = convertView;
		ParentViewHolder holder;

		if ( resultView == null )
		{
			resultView = inflater.inflate ( com.wks.calorieapp.R.layout.expandable_row_parent, null );
			holder = new ParentViewHolder ();
			holder.textFoodCategory = ( TextView ) resultView.findViewById ( com.wks.calorieapp.R.id.expandable_row_parent_text_food_name );
			resultView.setTag ( holder );
		}else
		{
			holder = ( ParentViewHolder ) resultView.getTag ();
		}

		GenericFoodItem foodCategory = getGroup ( groupPosition );
		// holder.textFoodName.setTypeface ( CalorieApplication.getFont (
		// Font.CANTARELL_REGULAR ) );
		if(foodCategory != null)
		{
			holder.textFoodCategory.setText ( foodCategory == null? "null" : foodCategory.getGenericFoodName () );
		}
		
		return resultView;
	}

	@Override
	public NutritionInfo getChild ( int groupPosition, int childPosition )
	{
		return genericFoodList == null? null : genericFoodList.get ( groupPosition ).getNutritionInfoList ().get ( childPosition );
	}

	@Override
	public long getChildId ( int groupPosition, int childPosition )
	{
		return childPosition;
	}

	@Override
	public int getChildrenCount ( int groupPosition )
	{
		return genericFoodList == null? 0 : genericFoodList.get ( groupPosition ).getNutritionInfoList ().size ();
	}

	@Override
	public View getChildView ( int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent )
	{
		View resultView = convertView;
		ChildViewHolder holder;

		if ( resultView == null )
		{
			holder = new ChildViewHolder ();
			resultView = inflater.inflate ( com.wks.calorieapp.R.layout.expandable_row_child, null );
			holder.textFoodName = ( TextView ) resultView.findViewById ( com.wks.calorieapp.R.id.expandable_row_child_food_name );
			holder.textCalories = ( TextView ) resultView.findViewById ( com.wks.calorieapp.R.id.expandable_row_child_calories );
			resultView.setTag ( holder );
		}else
		{
			holder = ( ChildViewHolder ) resultView.getTag ();
		}

		NutritionInfo info = getChild ( groupPosition, childPosition );

		if(info != null)
		{
			holder.textFoodName.setText ( info.getName () );
			holder.textCalories.setText ( String.format ( FORMAT_CALORIE, info.getCalories () ) );
		}
		return resultView;
	}

	@Override
	public boolean hasStableIds ()
	{
		return true;
	}

	@Override
	public boolean isChildSelectable ( int groupPosition, int childPosition )
	{
		return true;
	}

	static class ParentViewHolder
	{
		TextView textFoodCategory;
	}

	static class ChildViewHolder
	{
		TextView textFoodName;
		TextView textCalories;
	}

	public class GenericFoodItem
	{
		private String genericFoodName = "";
		private List< NutritionInfo > nutritionInfoList;

		public GenericFoodItem ( String genericFoodName )
		{
			this.genericFoodName = genericFoodName;
			this.nutritionInfoList = new ArrayList< NutritionInfo > ();
		}

		public String getGenericFoodName ()
		{
			return genericFoodName;
		}

		public List< NutritionInfo > getNutritionInfoList ()
		{
			return this.nutritionInfoList;
		}

	}

	@Override
	public void update ( Observable observable, Object object )
	{
	
		SearchResultsModel model = (SearchResultsModel) object;
		this.setItems ( model.getSearchResults () );
		
	
	}
}