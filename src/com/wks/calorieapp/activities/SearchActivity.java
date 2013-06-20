package com.wks.calorieapp.activities;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.parser.ParseException;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.NutritionInfoExpandableListAdapter;
import com.wks.calorieapp.pojos.NutritionInfo;
import com.wks.calorieapp.pojos.ParentItem;
import com.wks.calorieapp.pojos.Response;
import com.wks.calorieapp.pojos.ResponseFactory;
import com.wks.calorieapp.utils.HttpClient;
import com.wks.calorieapp.utils.WebServiceUrlFactory;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;


public class SearchActivity extends Activity
{
	private static final String TAG = SearchActivity.class.getCanonicalName ();
	
	private EditText editSearch;
	private Button buttonSearch;
	private ViewSwitcher viewSwitcher;
	private RelativeLayout viewLoading;
	private LinearLayout viewResults;
	private TextView textLoading;
	private ProgressBar progressLoading;
	private ExpandableListView listNutritionInfo;
	
	private enum SearchActivityView{VIEW_IDLE,VIEW_LOADING,VIEW_RESULTS};
	private SearchActivityView searchActivityView;
	
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
		
		viewSwitcher = (ViewSwitcher) this.findViewById ( R.id.search_viewswitcher );
		viewLoading = (RelativeLayout) this.findViewById ( R.id.search_view_loading );
		viewResults = (LinearLayout) this.findViewById ( R.id.search_view_results );
		
		textLoading = (TextView) this.findViewById ( R.id.search_text_loading_activity );
		progressLoading = (ProgressBar) this.findViewById ( R.id.search_spinner_loading );
		
		listNutritionInfo = (ExpandableListView) this.findViewById ( R.id.search_expandlist_nutrition_info );
	
		setSearchActivityView(SearchActivityView.VIEW_IDLE);
	}
	
	private void setupListeners()
	{
		buttonSearch.setOnClickListener ( new OnButtonSearchClicked() );
	}

	private void setSearchActivityView(SearchActivityView view)
	{
		this.searchActivityView = view;
		
		switch(view)
		{
		case VIEW_IDLE:
			setLoadingProgressVisible(false);
			if(viewSwitcher.getCurrentView () != viewLoading)
			{
				viewSwitcher.showPrevious ();
			}
		case VIEW_LOADING:
			setLoadingProgressVisible(true);
			if(viewSwitcher.getCurrentView () != viewLoading)
			{
				viewSwitcher.showPrevious ();
			}
		case VIEW_RESULTS:
			setLoadingProgressVisible(false);
			if(viewSwitcher.getCurrentView () != viewResults)
			{
				viewSwitcher.showNext ();
			}
		}
	}
	
	private SearchActivityView getSearchActivityView()
	{
		return this.searchActivityView;
	}
	
	private void setLoadingProgressVisible(boolean visible)
	{
		this.progressLoading.setVisibility ( visible? View.VISIBLE : View.GONE );
		this.textLoading.setVisibility ( visible? View.VISIBLE : View.GONE );
	}
	
	private void setLoadingText(String text)
	{
		this.textLoading.setText ( text );
	}
	
	@SuppressWarnings ( "unused" )
	private String getLoadingText()
	{
		return this.textLoading.getText ().toString ();
	}
	
	private void setupList(Map<String,List<NutritionInfo>> nutritionInfoDictionary)
	{
		if(nutritionInfoDictionary != null)
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
	
	class OnButtonSearchClicked implements View.OnClickListener
	{

		public void onClick ( View v )
		{
			String foodName = editSearch.getText ().toString ();
			if(foodName != null && !foodName.isEmpty ())
			{
				if( SearchActivity.this.getSearchActivityView () != SearchActivityView.VIEW_LOADING)
					SearchActivity.this.setSearchActivityView ( SearchActivityView.VIEW_LOADING );
					
				new GetNutritionInfoTask().execute ( foodName );
			}
		}
		
	}
	
	class GetNutritionInfoTask extends AsyncTask<String,String,Response>
	{

		@Override
		protected Response doInBackground ( String... params )
		{
			Response response = null;
			try
			{
				String foodName = params[0];
				
				//do REST call to get nutrition information for food.
				publishProgress("Fetching Nutrition Information");
				String json = HttpClient.get ( WebServiceUrlFactory.getNutritionInfo ( foodName ) );
				response = ResponseFactory.createResponseForNutritionInfoRequest ( json );
			}
			catch ( IOException e )
			{
				Log.e ( TAG, "IOException occured while fetching results."+e.toString () );
				e.printStackTrace ();
			}
			catch ( ParseException e )
			{
				Log.e (TAG, "ParseException while reading results."+e.toString ());
				e.printStackTrace();
			}
			
			return response;
		}
		
		@Override
		protected void onProgressUpdate ( String... values )
		{
			SearchActivity.this.setLoadingText ( values[0] );
		}
		
		@Override
		protected void onPostExecute ( Response response )
		{
			if(response == null || !response.isSuccessful ())
			{
				if(response!= null)
					Log.e(TAG, response.getMessage ());
				
				SearchActivity.this.setLoadingText ( SearchActivity.this.getString ( R.string.search_error_null_response ) );
			}else
			{
				if(response.getData () != null && response.isSuccessful () && response.getData () instanceof HashMap)
				{
					@SuppressWarnings ( "unchecked" )
					Map<String,List<NutritionInfo>> nutritionInfoDictionary = (Map<String,List<NutritionInfo>>) response.getData ();
					setupList(nutritionInfoDictionary);
					SearchActivity.this.setSearchActivityView ( SearchActivityView.VIEW_RESULTS );
					return;
				}else
				{
					SearchActivity.this.setLoadingText (SearchActivity.this.getString ( R.string.search_error_no_results_found  ));
				}
			}
		}
	}

}
