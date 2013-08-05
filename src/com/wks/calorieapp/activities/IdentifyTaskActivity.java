package com.wks.calorieapp.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.wks.calorieapp.models.SearchResultsModel;

public class IdentifyTaskActivity extends Activity implements IdentifyTaskInvoker
{
	protected RelativeLayout layout;
	protected String photoName = "";
	
	protected RelativeLayout.LayoutParams paramsLayoutProgress;
	protected LinearLayout layoutProgress;
	protected TextView textProgress;

	protected RelativeLayout.LayoutParams paramsViewResults;
	protected View viewResults;
	protected ListView listviewResults;
	protected Button buttonNoMatch;
	
	private SearchResultsModel searchResultsModel;
	
	protected IdentifyTask identifyTask;
	protected IdentifyResultsModel identifyResultsModel;
	protected IdentifyResultsAdapter identifyResultsAdpater;
	

	public enum ViewMode
	{
		IDLE, LOADING, RESULTS
	};

	protected ViewMode viewMode = ViewMode.IDLE;

	@Override
	protected void onPause ()
	{
		super.onPause ();
		if(this.identifyTask != null)
		{
			this.identifyTask.cancel ( true );
		}

	}
	
	
	protected void setupView()
	{
		LayoutInflater inflater = LayoutInflater.from ( this );
		//Inflate results list and progress view
		this.layoutProgress = (LinearLayout) inflater.inflate ( R.layout.identify_layout_loading, null );
		this.viewResults = inflater.inflate ( R.layout.identify_list_results, null );
		
		this.buttonNoMatch = (Button) this.viewResults.findViewById ( R.id.identify_button_no_match );
		this.listviewResults = ( ListView ) this.viewResults.findViewById ( R.id.identify_listview_results );
		
		this.identifyResultsAdpater = new IdentifyResultsAdapter ( this, this.identifyResultsModel );
		this.listviewResults.setAdapter ( this.identifyResultsAdpater );
		
		this.buttonNoMatch.setOnClickListener ( new OnButtonNoMatchClicked() );
		this.listviewResults.setOnItemClickListener ( new OnListViewResultsItemClicked() );
		
		this.textProgress = (TextView) this.layoutProgress.findViewById ( R.id.identify_text_progress );

		this.paramsViewResults = new RelativeLayout.LayoutParams ( DisplayUtils.getScreenWidth ( this ) / 3, LayoutParams.WRAP_CONTENT );
		this.paramsViewResults.addRule ( RelativeLayout.CENTER_IN_PARENT );
	
		this.paramsLayoutProgress = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		this.paramsLayoutProgress.addRule ( RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE );
	
		this.viewResults.setVisibility ( View.GONE );
		this.layoutProgress.setVisibility ( View.GONE );
		
		this.layout.addView ( this.viewResults, this.paramsViewResults );
		this.layout.addView ( this.layoutProgress, this.paramsLayoutProgress );
	}
	
	protected void setViewMode ( ViewMode mode )
	{
		this.viewMode = mode;
		if(this.layoutProgress != null) this.layoutProgress.setVisibility ( mode == ViewMode.LOADING ? View.VISIBLE : View.GONE );
		if(this.viewResults != null) this.viewResults.setVisibility ( mode == ViewMode.RESULTS? View.VISIBLE : View.GONE );
	}

	protected void showResults ()
	{

		this.setViewMode ( ViewMode.RESULTS );
		//this.setupDynamicResultsView();
		//AnimatorSet set = ( AnimatorSet ) AnimatorInflater.loadAnimator ( this, R.animator.fade );
		//set.setTarget ( this.viewResults );

		
		//this.layout.addView ( this.viewResults, this.paramsViewResults );
		//set.start ();
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
					this.searchResultsModel = new SearchResultsModel();
					this.searchResultsModel.setSearchResults ( response );
					
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

	
	class OnListViewResultsItemClicked implements AdapterView.OnItemClickListener
	{

		@Override
		public void onItemClick ( AdapterView< ? > parent, View view, int position, long id )
		{
			String selectedFoodName = IdentifyTaskActivity.this.identifyResultsAdpater.getItem ( position );
			Map<String,List<NutritionInfo>> selectedFoodInfo = new HashMap<String,List<NutritionInfo>>();
			selectedFoodInfo.put ( selectedFoodName, IdentifyTaskActivity.this.searchResultsModel.getSearchResults ().get ( selectedFoodName ) );
			
			((CalorieApplication ) IdentifyTaskActivity.this.getApplication ()).setIdentifyResults ( selectedFoodInfo );
			
			Intent resultsIntent = new Intent(IdentifyTaskActivity.this,ResultsActivity.class);
			
			resultsIntent.putExtra ( ResultsActivity.EXTRAS_PHOTO_NAME,  photoName);
			startActivity(resultsIntent);
		}
		
	}
	
	class OnButtonNoMatchClicked implements View.OnClickListener
	{
		@Override
		public void onClick ( View v )
		{
			Intent searchFoodIntent = new Intent ( IdentifyTaskActivity.this, SearchActivity.class );
			searchFoodIntent.putExtra ( SearchActivity.EXTRAS_PHOTO_NAME, photoName );
			startActivity ( searchFoodIntent );
		}	
	}
}
