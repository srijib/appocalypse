package com.wks.calorieapp.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.NutritionInfoExpandableListAdapter;
import com.wks.calorieapp.pojos.NutritionInfo;
import com.wks.calorieapp.pojos.ParentItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class DisplayNutritionInfoActivity extends Activity
{
	private ExpandableListView listNutritionInfo;
	private Button buttonNext;
	private Map<String,List<NutritionInfo>> nutritionInfoDictionary;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.activity_display_nutrition_info );
		
		this.nutritionInfoDictionary = CalorieApplication.getNutritionInfoDictionary ();
		
		if(nutritionInfoDictionary == null)
		{
			Toast.makeText ( this, R.string.diplay_nutrition_info_error_display, Toast.LENGTH_LONG ).show ();
			this.finish ();
		}
		
		setupView();
		setupListeners();
	}
	
	private void setupView()
	{
		setActionBarDrawable(R.drawable.bg_actionbar);
		//this.buttonNext = (Button) this.findViewById ( R.id.display_nutriton_info_button_next );
		this.listNutritionInfo = (ExpandableListView) this.findViewById ( R.id.display_nutrition_info_expandlist_nutrition_info );
	
		setupList();
	}
	
	private void setupList()
	{
		if(this.nutritionInfoDictionary != null)
		{
			List<ParentItem> foodList = new ArrayList<ParentItem>();
			for(Entry<String,List<NutritionInfo>> e : nutritionInfoDictionary.entrySet ())
			{
				ParentItem food = new ParentItem(e.getKey ());
				List<NutritionInfo> nutrinfoList = e.getValue ();
				
				for(NutritionInfo info : nutrinfoList)
					food.getNutritionInfoList ().add ( info );
				
				foodList.add ( food );
			}
			
			NutritionInfoExpandableListAdapter adapter = new NutritionInfoExpandableListAdapter(this,foodList);
			this.listNutritionInfo.setAdapter ( adapter );
		}
	}
	
	private void setupListeners()
	{
		//
	}
	
	private void setActionBarDrawable(int drawable)
	{
		Drawable d = this.getResources ().getDrawable ( drawable );
		this.getActionBar ().setBackgroundDrawable ( d );
	}
}
