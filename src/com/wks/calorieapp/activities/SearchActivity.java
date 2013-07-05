package com.wks.calorieapp.activities;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.NutritionInfoListAdapter;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.pojos.ImageEntry;
import com.wks.calorieapp.pojos.JournalEntry;
import com.wks.calorieapp.pojos.NutritionInfo;
import com.wks.calorieapp.pojos.Response;
import com.wks.calorieapp.pojos.ResponseFactory;
import com.wks.calorieapp.utils.ViewUtils;
import com.wks.calorieapp.utils.HttpClient;
import com.wks.calorieapp.utils.NetworkUtils;
import com.wks.calorieapp.utils.WebServiceUrlFactory;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
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
	private TextView textConfirm;
	private ImageButton buttonAddToJournal;
	private ViewSwitcher viewSwitcher;
	private RelativeLayout viewLoading;
	private RelativeLayout viewResults;
	private TextView textLoading;
	private ProgressBar progressLoading;

	private String currentFoodSearch;
	private String cameraPhotoName;
	private NutritionInfo selectedFoodInfo;
	private NutritionInfoListAdapter adapter;
	private ExpandableListView listNutritionInfo;

	private enum ViewMode
	{
		VIEW_IDLE, VIEW_LOADING, VIEW_RESULTS
	};

	private enum TextConfirmGist
	{
		DEFAULT, CONFIRM_ADD, ADDED, NOT_ADDED;
	}

	private ViewMode viewMode;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_search );

		this.init ();
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();

	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{
		MenuInflater inflater = this.getMenuInflater ();
		inflater.inflate ( R.menu.activity_search, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent homeIntent = new Intent ( SearchActivity.this, HomeActivity.class );
			homeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			startActivity ( homeIntent );
			return true;

		case R.id.search_menu_done:
			Calendar calendar = Calendar.getInstance ();

			Intent dateCaloriesIntent = new Intent ( this, DateCaloriesActivity.class );
			dateCaloriesIntent.putExtra ( DateCaloriesActivity.KEY_DATE, calendar.getTimeInMillis () );
			startActivity ( dateCaloriesIntent );
			return true;
		default:
			return super.onOptionsItemSelected ( item );
		}
	}

	private void init ()
	{
		Bundle extras = this.getIntent ().getExtras ();
		if ( extras != null )
		{
			this.cameraPhotoName = extras.getString ( "image" );
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
		editSearch = ( EditText ) this.findViewById ( R.id.search_edit_search );

		viewSwitcher = ( ViewSwitcher ) this.findViewById ( R.id.search_viewswitcher );
		viewLoading = ( RelativeLayout ) this.findViewById ( R.id.search_view_loading );
		viewResults = ( RelativeLayout ) this.findViewById ( R.id.search_view_results );

		textLoading = ( TextView ) this.findViewById ( R.id.search_text_loading_activity );
		progressLoading = ( ProgressBar ) this.findViewById ( R.id.search_spinner_loading );

		listNutritionInfo = ( ExpandableListView ) this.findViewById ( R.id.search_expandlist_nutrition_info );
		textConfirm = ( TextView ) this.findViewById ( R.id.search_text_confirm );
		buttonAddToJournal = ( ImageButton ) this.findViewById ( R.id.search_button_add_to_journal );

		setViewMode ( ViewMode.VIEW_IDLE );

	}

	private void setupListeners ()
	{
		this.editSearch.setOnKeyListener ( new OnEditSearchSubmitted () );
		this.listNutritionInfo.setOnChildClickListener ( new OnListItemClicked () );
		this.buttonAddToJournal.setOnClickListener ( new OnAddToJournalClicked () );
	}

	private void setListContents ( Map< String, List< NutritionInfo >> nutritionInfoDictionary )
	{
		if ( nutritionInfoDictionary != null )
		{
			this.adapter = new NutritionInfoListAdapter ( this, nutritionInfoDictionary );
			this.listNutritionInfo.setAdapter ( adapter );
		}
	}

	private void setViewMode ( ViewMode view )
	{
		this.viewMode = view;

		switch ( view )
		{
		case VIEW_IDLE:
			this.setLoadingProgressVisible ( false );
			if ( viewSwitcher.getCurrentView () != viewLoading )
			{
				viewSwitcher.showPrevious ();
			}
			return;
		case VIEW_LOADING:
			this.setLoadingProgressVisible ( true );
			if ( viewSwitcher.getCurrentView () != viewLoading )
			{
				viewSwitcher.showPrevious ();
			}
			return;
		case VIEW_RESULTS:
			this.setLoadingProgressVisible ( false );
			if ( viewSwitcher.getCurrentView () != viewResults )
			{
				viewSwitcher.showNext ();
			}
			return;
		}
	}

	private void setLoadingProgressVisible ( boolean visible )
	{
		this.progressLoading.setVisibility ( visible ? View.VISIBLE : View.GONE );
		this.textLoading.setVisibility ( visible ? View.VISIBLE : View.GONE );
	}

	//TODO Refactor
	//YUCK!! 
	private void setTextConfirm ( TextConfirmGist gist )
	{
		String confirmMessage = "";
		switch ( gist )
		{
		case ADDED:
			if ( this.selectedFoodInfo != null )
			{
				String confirmTemplate = this.getString ( R.string.search_layout_text_confirm_template_added );
				confirmMessage = String.format ( confirmTemplate, this.selectedFoodInfo.getName () );
			}
			break;
		case NOT_ADDED:
			if ( this.selectedFoodInfo != null )
			{
				String confirmTemplate = this.getString ( R.string.search_layout_text_confirm_template_not_added );
				confirmMessage = String.format ( confirmTemplate, this.selectedFoodInfo.getName () );
			}
			break;
		case CONFIRM_ADD:
			if ( this.selectedFoodInfo != null )
			{
				String confirmTemplate = this.getString ( R.string.search_layout_text_confirm_template_confirm_add );
				confirmMessage = String.format ( confirmTemplate, this.selectedFoodInfo.getName () );
			}
			break;
		default:
			confirmMessage = this.getString ( R.string.search_layout_text_confirm_default );
			break;
		}

		this.textConfirm.setText ( confirmMessage );
	}

	private void linkPhotoWithSearchTerm ()
	{
		if ( this.currentFoodSearch != null && !this.currentFoodSearch.isEmpty () )
		{
			String [] params =
			{
					this.cameraPhotoName, this.currentFoodSearch
			};
			new LinkImageWithFoodTask ().execute ( params );
		}
	}

	private long addToJournal ()
	{

		if ( this.selectedFoodInfo == null ) return -1;

		ImageEntry photo = null;
		if ( this.cameraPhotoName != null && !this.cameraPhotoName.isEmpty () )
		{
			photo = new ImageEntry ();
			photo.setFileName ( this.cameraPhotoName );
		}

		Calendar cal = Calendar.getInstance ();

		JournalEntry journal = new JournalEntry ();
		journal.setTimestamp ( cal.getTimeInMillis () );
		journal.setNutritionInfo ( this.selectedFoodInfo );
		journal.setImageEntry ( photo );

		DatabaseManager manager = DatabaseManager.getInstance ( this );
		SQLiteDatabase db = manager.open ();

		JournalDAO journalDao = new JournalDAO ( db );
		long journalId = journalDao.create ( journal );

		db.close ();
		return journalId;

	}

	class OnEditSearchSubmitted implements OnKeyListener
	{

		public boolean onKey ( View view, int keyCode, KeyEvent event )
		{
			if ( event.getAction () == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER )
			{
			
				//validate search data
				String foodName = editSearch.getText ().toString ();
				if ( foodName == null || foodName.isEmpty () )
				{
					Toast.makeText ( SearchActivity.this, R.string.search_error_empty_search_field, Toast.LENGTH_LONG ).show ();
				}else
				{
					//clear previous data
					SearchActivity.this.selectedFoodInfo = null;
					SearchActivity.this.setTextConfirm ( TextConfirmGist.DEFAULT );

					//hide keyboard
					ViewUtils.hideKeyboard (SearchActivity.this.editSearch );
					
					//put new search data
					SearchActivity.this.currentFoodSearch = foodName;
					
					//set searching mode
					if ( SearchActivity.this.viewMode != ViewMode.VIEW_LOADING ) 
						SearchActivity.this.setViewMode ( ViewMode.VIEW_LOADING );

					//invoke web service.
					new GetNutritionInfoTask ().execute ( foodName );
				}
			}
			return true;
		}

	}

	class OnListItemClicked implements ExpandableListView.OnChildClickListener
	{

		public boolean onChildClick ( ExpandableListView parent, View v, int groupPosition, int childPosition, long id )
		{
			NutritionInfo info = SearchActivity.this.adapter.getChild ( groupPosition, childPosition );

			SearchActivity.this.selectedFoodInfo = info;
			SearchActivity.this.setTextConfirm ( TextConfirmGist.CONFIRM_ADD );

			return true;
		}

	}

	class OnAddToJournalClicked implements View.OnClickListener
	{
		@Override
		public void onClick ( View v )
		{
			boolean success = ( SearchActivity.this.addToJournal () > 0 );
			SearchActivity.this.setTextConfirm ( success ? TextConfirmGist.ADDED : TextConfirmGist.NOT_ADDED );

			if ( cameraPhotoName != null )
			{
				SearchActivity.this.linkPhotoWithSearchTerm ();
			}

		}
	}

	class GetNutritionInfoTask extends AsyncTask< String, String, Response >
	{
		private String foodName;

		@Override
		protected void onPreExecute ()
		{
			if(!NetworkUtils.isConnectedToNetwork ( SearchActivity.this ))
			{
				publishProgress("No Internet Connection.");
				this.cancel ( true );
			}
		}
		
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
			SearchActivity.this.textLoading.setText ( values[0] );
		}

		@SuppressWarnings ( "unchecked" )
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

					SearchActivity.this.setListContents ( nutritionInfoDictionary );

					SearchActivity.this.setViewMode ( ViewMode.VIEW_RESULTS );

					return;
				}
			}else
			{
				if ( response != null )
				{
					Log.e ( SearchActivity.TAG, response.getMessage () );
				}

				SearchActivity.this.textLoading.setText ( SearchActivity.this.getString ( R.string.search_error_null_response ) );
			}
		}
	}

	class LinkImageWithFoodTask extends AsyncTask< String, Void, Boolean >
	{

		@Override
		protected void onPreExecute ()
		{
			if(!NetworkUtils.isConnectedToNetwork ( SearchActivity.this ))
			{
				this.cancel ( true );
			}
		}
		
		@Override
		protected Boolean doInBackground ( String... params )
		{
			boolean success = false;
			if ( params.length >= 2 )
			{
				try
				{
					String imageName = params[0];
					String foodName = params[1];

					Log.i ( TAG, "Linking " + foodName + " with " + imageName );

					String json = HttpClient.get ( WebServiceUrlFactory.update ( imageName, foodName ) );

					Log.i ( TAG, json );

					Response response = ResponseFactory.createResponseForUpdateRequest ( json );
					if ( response != null )
					{
						success = response.isSuccessful ();
					}
				}
				catch ( ParseException e )
				{
					Log.e ( TAG, e.getMessage () );
				}
				catch ( IOException e )
				{
					Log.e ( TAG, e.getMessage () );
				}

			}else
			{
				Log.e ( TAG, "Insufficient Parameters." );
			}
			return success;
		}

	}

}
