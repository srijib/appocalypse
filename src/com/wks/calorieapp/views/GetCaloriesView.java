package com.wks.calorieapp.views;


import com.wks.calorieapp.R;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class GetCaloriesView
{
	private View view;
	private Context context;
	
	private ProgressBar progressbarLoading;
	private TextView textLoadingActivity;
	
	
	public GetCaloriesView(View view)
	{
		this.view = view;
		this.context = view.getContext ();
		this.setupView();
		//this.setupListeners();
	}
	
	private void setupView()
	{
		
		this.progressbarLoading = (ProgressBar) view.findViewById ( R.id.get_calories_spinner_loading );
		this.textLoadingActivity = (TextView) view.findViewById ( R.id.get_calories_text_loading_activity );
	}
	
	public String getProgressBarText()
	{
		return this.textLoadingActivity.getText ().toString ();
	}
	
	public void setProgressBarText(String text)
	{
		this.textLoadingActivity.setText ( text );
	}
	
	
}
