package com.wks.calorieapp.activities;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.NutritionInfoListAdapter;
import com.wks.calorieapp.pojos.NutritionInfo;
import com.wks.calorieapp.pojos.Response;
import com.wks.calorieapp.pojos.ResponseFactory;
import com.wks.calorieapp.utils.AndroidUtils;
import com.wks.calorieapp.utils.HttpClient;
import com.wks.calorieapp.utils.WebServiceUrlFactory;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class SearchActivity extends Activity
{
	private static final String TAG = SearchActivity.class.getCanonicalName ();
	private static final int NUM_TRIES = 3;

	private EditText editSearch;
	private Button buttonAddToJournal;
	private Button buttonDone;
	private ViewSwitcher viewSwitcher;
	private RelativeLayout viewLoading;
	private RelativeLayout viewResults;
	private TextView textLoading;
	private ProgressBar progressLoading;

	private ExpandableListView listNutritionInfo;

	private enum SearchActivityView
	{
		VIEW_IDLE, VIEW_LOADING, VIEW_RESULTS
	};

	private SearchActivityView searchActivityView;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_search );

		setupActionBar ();
		setupView ();
		setupListeners ();

	}


	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent homeIntent = new Intent(SearchActivity.this,HomeActivity.class);
			homeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			startActivity(homeIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		Drawable d = this.getResources ().getDrawable ( R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( d );

		actionBar.setDisplayHomeAsUpEnabled ( true );
	}

	private void setupView ()
	{
		editSearch = ( EditText ) this.findViewById ( R.id.search_edit_search_term );
	
		viewSwitcher = ( ViewSwitcher ) this.findViewById ( R.id.search_viewswitcher );
		viewLoading = ( RelativeLayout ) this.findViewById ( R.id.search_view_loading );
		viewResults = ( RelativeLayout ) this.findViewById ( R.id.search_view_results );

		textLoading = ( TextView ) this.findViewById ( R.id.search_text_loading_activity );
		progressLoading = ( ProgressBar ) this.findViewById ( R.id.search_spinner_loading );

		listNutritionInfo = ( ExpandableListView ) this.findViewById ( R.id.search_expandlist_nutrition_info );
		buttonAddToJournal = (Button) this.findViewById ( R.id.search_button_add_to_journal );
		buttonDone = (Button) this.findViewById ( R.id.search_button_done );
		
		setSearchActivityView ( SearchActivityView.VIEW_IDLE );

	}

	private void setupListeners ()
	{
		this.editSearch.setOnKeyListener ( new OnEditSearchSubmitted ());
	}

	private void setSearchActivityView ( SearchActivityView view )
	{
		this.searchActivityView = view;
		switch ( view )
		{
		case VIEW_IDLE:
			setLoadingProgressVisible ( false );
			if ( viewSwitcher.getCurrentView () != viewLoading )
			{
				viewSwitcher.showPrevious ();
			}
			return;
		case VIEW_LOADING:
			setLoadingProgressVisible ( true );
			if ( viewSwitcher.getCurrentView () != viewLoading )
			{
				viewSwitcher.showPrevious ();
			}
			return;
		case VIEW_RESULTS:
			setLoadingProgressVisible ( false );
			if ( viewSwitcher.getCurrentView () != viewResults )
			{
				viewSwitcher.showNext ();
				Toast.makeText ( this, "Displaying results", Toast.LENGTH_LONG ).show ();
			}
			return;
		}
	}

	private SearchActivityView getSearchActivityView ()
	{
		return this.searchActivityView;
	}

	private void setLoadingProgressVisible ( boolean visible )
	{
		this.progressLoading.setVisibility ( visible ? View.VISIBLE : View.GONE );
		this.textLoading.setVisibility ( visible ? View.VISIBLE : View.GONE );
	}

	private void setLoadingText ( String text )
	{
		this.textLoading.setText ( text );
	}

	@SuppressWarnings ( "unused" )
	private String getLoadingText ()
	{
		return this.textLoading.getText ().toString ();
	}

	private void setupList ( Map< String, List< NutritionInfo >> nutritionInfoDictionary )
	{
		if ( nutritionInfoDictionary != null )
		{
			NutritionInfoListAdapter adapter = new NutritionInfoListAdapter ( this, nutritionInfoDictionary );
			this.listNutritionInfo.setAdapter ( adapter );
		}
	}

	class OnEditSearchSubmitted implements OnKeyListener
	{

		public boolean onKey ( View view, int keyCode, KeyEvent event )
		{
			if(event.getAction () == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
			{
				AndroidUtils.hideKeyboard ( SearchActivity.this, SearchActivity.this.editSearch );

				String foodName = editSearch.getText ().toString ();
				if ( foodName != null && !foodName.isEmpty () )
				{
					if ( SearchActivity.this.getSearchActivityView () != SearchActivityView.VIEW_LOADING )
						SearchActivity.this.setSearchActivityView ( SearchActivityView.VIEW_LOADING );

					new GetNutritionInfoTask ().execute ( foodName );
				}else Toast.makeText ( SearchActivity.this, R.string.search_error_empty_search_field, Toast.LENGTH_LONG ).show ();	
				return true;
			}
			return false;
		}
		
	}
	
	class GetNutritionInfoTask extends AsyncTask< String, String, Response >
	{
		private String foodName;

		@Override
		protected Response doInBackground ( String... params )
		{
			Response response = null;
			try
			{
				this.foodName = params[0];

				// do REST call to get nutrition information for food.
				publishProgress ( "Fetching Nutrition Information for " + foodName );

				for ( int i = 0 ; i < SearchActivity.NUM_TRIES ; i++ )
				{
					String json = HttpClient.get ( WebServiceUrlFactory.getNutritionInfo ( foodName ) );
					response = ResponseFactory.createResponseForNutritionInfoRequest ( json );

					if ( response != null && response.isSuccessful () ) break;
				}

			}
			catch ( IOException e )
			{
				Log.e ( TAG, "IOException occured while fetching results." + e.toString () );
				e.printStackTrace ();
			}
			catch ( ParseException e )
			{
				Log.e ( TAG, "ParseException while reading results." + e.toString () );
				e.printStackTrace ();
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
			if ( response != null && response.isSuccessful () )
			{
				if ( response.getData () instanceof List )
				{
					List< NutritionInfo > nutritionInfoList = ( List< NutritionInfo > ) response.getData ();
					Map< String, List< NutritionInfo >> nutritionInfoDictionary = new HashMap< String, List< NutritionInfo >> ();
					nutritionInfoDictionary.put ( this.foodName, nutritionInfoList );

					//NutritionInfoListAdapter adapter = new NutritionInfoListAdapter ( SearchActivity.this, nutritionInfoDictionary );
					//SearchActivity.this.listNutritionInfo.setAdapter ( adapter );
					//SearchActivity.this.listNutritionInfo.invalidateViews ();
					setupList(nutritionInfoDictionary);
					SearchActivity.this.setSearchActivityView ( SearchActivityView.VIEW_RESULTS );
					return;
				}
			}else
			{
				if ( response != null )
				{
					Log.e ( SearchActivity.TAG, response.getMessage () );
				}

				SearchActivity.this.setLoadingText ( SearchActivity.this.getString ( R.string.search_error_null_response ) );
			}
		}
	}

}
