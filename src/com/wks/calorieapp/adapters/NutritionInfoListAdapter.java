package com.wks.calorieapp.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wks.calorieapp.pojos.NutritionInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class NutritionInfoListAdapter extends BaseExpandableListAdapter
{
	private static final String FORMAT_CALORIE = "%.2f cal";
	private List< ParentItem > foodNameList;
	private LayoutInflater inflater;

	public NutritionInfoListAdapter ( Context context, Map< String, List< NutritionInfo >> items )
	{
		this.inflater = LayoutInflater.from ( context );

		this.foodNameList = new ArrayList< ParentItem > ();
		for ( Entry< String, List< NutritionInfo >> e : items.entrySet () )
		{
			ParentItem food = new ParentItem ( e.getKey () );
			List< NutritionInfo > nutrinfoList = e.getValue ();

			for ( NutritionInfo info : nutrinfoList )
				food.getNutritionInfoList ().add ( info );

			this.foodNameList.add ( food );
		}

	}

	@Override
	public ParentItem getGroup ( int groupPosition )
	{
		return foodNameList.get ( groupPosition );
	}

	@Override
	public long getGroupId ( int groupPosition )
	{
		return groupPosition;
	}

	@Override
	public int getGroupCount ()
	{
		return foodNameList.size ();
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
			holder.textFoodName = ( TextView ) resultView.findViewById ( com.wks.calorieapp.R.id.expandable_row_parent_text_food_name );
			resultView.setTag ( holder );
		}else
		{
			holder = ( ParentViewHolder ) resultView.getTag ();
		}

		ParentItem foodItem = getGroup ( groupPosition );
		// holder.textFoodName.setTypeface ( CalorieApplication.getFont (
		// Font.CANTARELL_REGULAR ) );
		holder.textFoodName.setText ( foodItem.getFoodName () );
		return resultView;
	}

	@Override
	public NutritionInfo getChild ( int groupPosition, int childPosition )
	{
		return foodNameList.get ( groupPosition ).getNutritionInfoList ().get ( childPosition );
	}

	@Override
	public long getChildId ( int groupPosition, int childPosition )
	{
		return childPosition;
	}

	@Override
	public int getChildrenCount ( int groupPosition )
	{
		return foodNameList.get ( groupPosition ).getNutritionInfoList ().size ();
	}

	@Override
	public View getChildView ( int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent )
	{
		View resultView = convertView;
		NutritionInfoViewHolder holder;

		if ( resultView == null )
		{
			holder = new NutritionInfoViewHolder ();
			resultView = inflater.inflate ( com.wks.calorieapp.R.layout.expandable_row_child, null );
			holder.textFoodName = ( TextView ) resultView.findViewById ( com.wks.calorieapp.R.id.expandable_row_child_food_name );
			holder.textCalories = ( TextView ) resultView.findViewById ( com.wks.calorieapp.R.id.expandable_row_child_calories );
			resultView.setTag ( holder );
		}else
		{
			holder = ( NutritionInfoViewHolder ) resultView.getTag ();
		}

		NutritionInfo info = getChild ( groupPosition, childPosition );

		// Typeface t = CalorieApplication.getFont ( Font.CANTARELL_REGULAR );
		// holder.textFoodName.setTypeface ( t );
		// holder.textCalories.setTypeface ( t );

		holder.textFoodName.setText ( info.getName () );
		holder.textCalories.setText ( String.format ( FORMAT_CALORIE, info.getCaloriesPer100g () ) );

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
		TextView textFoodName;
	}

	static class NutritionInfoViewHolder
	{
		TextView textFoodName;
		TextView textCalories;
	}

	class ParentItem
	{
		private String foodName;
		private List< NutritionInfo > nutritionInfoList;

		public ParentItem ()
		{
			this ( "" );
		}

		public ParentItem ( String foodName )
		{
			this.foodName = foodName;
			this.nutritionInfoList = new ArrayList< NutritionInfo > ();
		}

		public String getFoodName ()
		{
			return foodName;
		}

		public List< NutritionInfo > getNutritionInfoList ()
		{
			return this.nutritionInfoList;
		}

	}
}
