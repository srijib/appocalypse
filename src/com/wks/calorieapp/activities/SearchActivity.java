package com.wks.calorieapp.activities;
import com.wks.calorieapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;


public class SearchActivity extends Activity
{
	private EditText editSearch;
	private Button buttonSearch;
	private ViewFlipper viewFlipper;
	private TextView textLoading;
	private ProgressBar progressLoading;
	private ExpandableListView listNutritionInfo;
	
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_search );
		setupView();
		setupListeners();
	}
	
	private void setupView()
	{
		editSearch = (EditText) this.findViewById ( R.id.search_edit_search_term );
		buttonSearch = (Button) this.findViewById ( R.id.search_button_do_search );
		
		viewFlipper = (ViewFlipper) this.findViewById ( R.id.search_viewflipper );
		
		textLoading = (TextView) this.findViewById ( R.id.search_text_loading_activity );
		progressLoading = (ProgressBar) this.findViewById ( R.id.search_spinner_loading );
		
		listNutritionInfo = (ExpandableListView) this.findViewById ( R.id.search_expandlist_nutrition_info );
	}
	
	private void setupListeners()
	{
		
	}
	
}
