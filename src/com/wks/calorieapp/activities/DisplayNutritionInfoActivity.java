package com.wks.calorieapp.activities;

import java.util.List;
import java.util.Map;
import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.NutritionInfoListAdapter;
import com.wks.calorieapp.pojos.NutritionInfo;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class DisplayNutritionInfoActivity extends Activity
{
	private ExpandableListView listNutritionInfo;
	private Button buttonAddToJournal;
	private Button buttonNoMatch;
	private Button buttonDone;
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
		
		setupActionBar();
		setupView();
		setupListeners();
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch(item.getItemId ())
		{
		case android.R.id.home:
			Intent cameraIntent = new Intent(DisplayNutritionInfoActivity.this, CameraActivity.class);
			cameraIntent.addFlags ( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
			startActivity(cameraIntent);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
		
	}
	
	private void setupActionBar()
	{
		ActionBar actionBar = this.getActionBar ();
		
		Drawable d = this.getResources ().getDrawable (R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( d );
		
		actionBar.setDisplayHomeAsUpEnabled ( true );
	}
	
	private void setupView()
	{
		
		//this.buttonNext = (Button) this.findViewById ( R.id.display_nutriton_info_button_next );
		this.listNutritionInfo = (ExpandableListView) this.findViewById ( R.id.display_nutrition_info_expandlist_nutrition_info );
		this.buttonAddToJournal = (Button) this.findViewById ( R.id.display_nutrition_info_button_add_to_journal);
		this.buttonNoMatch = (Button) this.findViewById ( R.id.display_nutrition_info_button_no_match );
		this.buttonDone = (Button) this.findViewById ( R.id.display_nutrition_info_button_done );
		setupList();
	}
	
	private void setupList()
	{
		if(this.nutritionInfoDictionary != null)
		{
			NutritionInfoListAdapter adapter = new NutritionInfoListAdapter(this,nutritionInfoDictionary);
			this.listNutritionInfo.setAdapter ( adapter );
		}
	}
	
	private void setupListeners()
	{
		//
	}
	
	
}
