package com.wks.calorieapp.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.wks.android.utils.DisplayUtils;
import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.IdentifyResultsAdapter;
import com.wks.calorieapp.apis.NutritionInfo;
import com.wks.calorieapp.models.IdentifyResultsModel;

public class IdentifyTaskActivity extends Activity implements IdentifyTaskInvoker
{
	protected RelativeLayout layout;
	protected String photoName = "";
	
	protected LinearLayout layoutProgress;
	protected TextView textProgress;

	protected View viewResults;
	protected ListView listviewResults;
	protected RelativeLayout.LayoutParams params;
	protected IdentifyResultsModel identifyResultsModel;
	protected IdentifyResultsAdapter identifyResultsAdpater;

	public enum ViewMode
	{
		IDLE, LOADING, RESULTS
	};

	@SuppressWarnings ( "unused" )
	protected ViewMode viewMode = ViewMode.IDLE;

	protected void setViewMode ( ViewMode mode )
	{
		this.layoutProgress.setVisibility ( mode == ViewMode.LOADING ? View.VISIBLE : View.GONE );
	}

	protected void showResults ()
	{

		this.setViewMode ( ViewMode.RESULTS );
		this.setupDynamicResultsView();
		AnimatorSet set = ( AnimatorSet ) AnimatorInflater.loadAnimator ( this, R.animator.fade );
		set.setTarget ( this.viewResults );
		
		if(this.layout == null)
		{
			Log.e("fuckers","layout is null");
		}
		
		this.layout.addView ( this.viewResults, this.params );
		set.start ();
	}
	
	private void setupDynamicResultsView()
	{
		LayoutInflater inflater = LayoutInflater.from ( this );
		this.viewResults = inflater.inflate ( R.layout.identify_list_results, null );
		this.listviewResults = ( ListView ) this.viewResults.findViewById ( R.id.identify_listview_results );

		this.identifyResultsAdpater = new IdentifyResultsAdapter ( this, this.identifyResultsModel );
		this.listviewResults.setAdapter ( this.identifyResultsAdpater );

		this.params = new RelativeLayout.LayoutParams ( DisplayUtils.getScreenWidth ( this ) / 3, LayoutParams.WRAP_CONTENT );
		this.params.addRule ( RelativeLayout.CENTER_IN_PARENT );
	}

	@Override
	public void onPreExecute ()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPostExecute ( Map< String, List< NutritionInfo >> response )
	{
		// TODO Auto-generated method stub
		if ( response != null )
		{
			// a really stupid way of checking that the result is not empty.
			for ( Entry< String, List< NutritionInfo >> entry : response.entrySet () )
			{
				// there should be atleast one list.
				if ( !entry.getValue ().isEmpty () )
				{
					this.identifyResultsModel.setPossibleMatchesList ( new ArrayList< String > ( response.keySet () ) );
					this.showResults ();
					return;
				}
			}

		}

		// If no response, start searchActivity so that the user can manually
		// search for the item.

		Toast.makeText ( this, "No Matches Found", Toast.LENGTH_LONG ).show ();
		Intent searchFoodIntent = new Intent ( this, SearchActivity.class );
		searchFoodIntent.putExtra ( SearchActivity.EXTRAS_PHOTO_NAME, photoName );
		startActivity ( searchFoodIntent );
	}

	@Override
	public void onProgressUpdate ( String [] values )
	{
		this.textProgress.setText ( values[0] );

	}

	@Override
	public void onCancelled ()
	{
		this.setViewMode ( ViewMode.IDLE );

	}

}
