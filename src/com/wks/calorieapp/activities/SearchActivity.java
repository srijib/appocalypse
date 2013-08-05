package com.wks.calorieapp.activities;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wks.android.utils.KeyboardUtils;
import com.wks.android.utils.NetworkUtils;
import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.SearchResultsAdapter;
import com.wks.calorieapp.apis.CAWebService;
import com.wks.calorieapp.apis.NutritionInfo;
import com.wks.calorieapp.daos.DataAccessObject;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.entities.ImageEntry;
import com.wks.calorieapp.entities.JournalEntry;
import com.wks.calorieapp.models.SearchResultsModel;

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

public class SearchActivity extends Activity implements Observer
{
	//DEBUGGING
	private static final String TAG = SearchActivity.class.getCanonicalName ();

	//EXTRAS
	public static final String EXTRAS_PHOTO_NAME = "image";

	//UI Elements
	private EditText editSearch;
	private TextView textConfirm;
	private ImageButton buttonAddToJournal;
	private ViewSwitcher viewSwitcher;
	private RelativeLayout viewLoading;
	private RelativeLayout viewResults;
	private TextView textLoading;
	private ProgressBar progressLoading;
	private ExpandableListView listNutritionInfo;

	//Members
	private String photoName;
	private NutritionInfo selectedItem;
	private SearchResultsAdapter adapter;
	private SearchResultsModel searchResultsModel;
	private ViewMode viewMode;
	
	private enum ViewMode
	{
		VIEW_IDLE, VIEW_LOADING, VIEW_RESULTS
	};

	private enum TextConfirmState
	{
		DEFAULT, CONFIRM_ADD, ADDED, NOT_ADDED;
	}

	

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_search );

		Bundle extras = this.getIntent ().getExtras ();
		if ( extras != null )
		{
			this.photoName = extras.getString ( EXTRAS_PHOTO_NAME );
			Log.i(TAG,"CameraPhotoName: "+this.photoName);
		}

		this.searchResultsModel = new SearchResultsModel ();

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
			this.finish ();
			return true;

		case R.id.search_menu_done:
			Calendar calendar = Calendar.getInstance ();

			Intent dateCaloriesIntent = new Intent ( this, JournalEntryActivity.class );
			dateCaloriesIntent.putExtra ( JournalEntryActivity.EXTRAS_DATE, calendar.getTimeInMillis () );
			startActivity ( dateCaloriesIntent );
			return true;
		default:
			return super.onOptionsItemSelected ( item );
		}
	}

	@Override
	public boolean onPrepareOptionsMenu ( Menu menu )
	{
		menu.findItem ( R.id.search_menu_done ).setVisible ( this.viewMode.equals ( ViewMode.VIEW_RESULTS ) );
		return true;
	}

	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		Drawable backgroundActionBar = getResources ().getDrawable ( R.drawable.bg_actionbar );
		Drawable iconActionBar = getResources().getDrawable ( R.drawable.ic_actionbar );
		
		actionBar.setBackgroundDrawable ( backgroundActionBar );
		actionBar.setIcon ( iconActionBar );

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

		this.adapter = new SearchResultsAdapter ( this,this.searchResultsModel );
		this.listNutritionInfo.setAdapter ( adapter );

		setViewMode ( ViewMode.VIEW_IDLE );
	}

	private void setupListeners ()
	{
		this.searchResultsModel.addObserver ( this );
		this.editSearch.setOnKeyListener ( new OnEditSearchSubmitted () );
		this.listNutritionInfo.setOnChildClickListener ( new OnListItemClicked () );
		this.buttonAddToJournal.setOnClickListener ( new OnAddToJournalClicked () );
	}

	private void setViewMode ( ViewMode view )
	{
		this.viewMode = view;
		this.invalidateOptionsMenu ();
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

	private void setTextConfirm ( TextConfirmState state )
	{
		String confirmMessage = "";
		switch ( state )
		{
		case ADDED:
			if ( this.selectedItem != null )
			{
				String confirmTemplate = this.getString ( R.string.search_layout_text_confirm_template_added );
				confirmMessage = String.format ( confirmTemplate, this.selectedItem.getName () );
			}
			break;
		case NOT_ADDED:
			if ( this.selectedItem != null )
			{
				String confirmTemplate = this.getString ( R.string.search_layout_text_confirm_template_not_added );
				confirmMessage = String.format ( confirmTemplate, this.selectedItem.getName () );
			}
			break;
		case CONFIRM_ADD:
			if ( this.selectedItem != null )
			{
				String confirmTemplate = this.getString ( R.string.search_layout_text_confirm_template_confirm_add );
				confirmMessage = String.format ( confirmTemplate, this.selectedItem.getName () );
			}
			break;
		default:
			confirmMessage = this.getString ( R.string.search_layout_text_confirm_default );
			break;
		}

		this.textConfirm.setText ( confirmMessage );
	}
	
	private long addToJournal ()
	{

		if ( this.selectedItem == null ) return -1;

		ImageEntry photo = null;
		if ( this.photoName != null && !this.photoName.isEmpty () )
		{
			photo = new ImageEntry ();
			photo.setFileName ( this.photoName );
			Log.e ( TAG, "Image Set: "+photo.getFileName ());
		}

		Calendar cal = Calendar.getInstance ();

		JournalEntry journal = new JournalEntry ();
		journal.setTimestamp ( cal.getTimeInMillis () );
		journal.setNutritionInfo ( this.selectedItem );
		journal.setImageEntry ( photo );

		DatabaseManager manager = DatabaseManager.getInstance ( this );
		SQLiteDatabase db = manager.open ();

		DataAccessObject< JournalEntry > journalDao = new JournalDAO ( db );
		long journalId = journalDao.create ( journal );

		db.close ();
		return journalId;

	}

	/**Initiates Link Task and links the picture name with the search term.
	 * 
	 */
	private void linkPhotoWithGenericFoodName ()
	{
		String genericFoodName = estimateGenericFoodNameFromSearchTerm();
		if ( this.photoName != null)
		{
			String [] params =
			{
					this.photoName, genericFoodName
			};
			new LinkTask ( this ).execute ( params );
		}
	}

	private String estimateGenericFoodNameFromSearchTerm()
	{
		
		String searchTerm = this.searchResultsModel.getSearchTerm ();
		String selectedFood = this.selectedItem.getName ();
		
		
		//Each part of the food name that contains the search term 
		//should be added to final name
		//e.g. Search Term: Ch
		// Result: Cheddar Cheese Chips
		// Generic Name: Cheddar Cheese Chips
		//BAD!
		
		String sPattern =  "(?i)\\b("+searchTerm+"(?:.+?)?)\\b";
		Pattern pattern = Pattern.compile ( sPattern );
		Matcher matcher = pattern.matcher ( selectedFood );
		String result = "";
		while(matcher.find ())
		{
				result+= matcher.group ( ) + " ";
		}
				
		return result.isEmpty ()?selectedFood:result;
	}
	
	@Override
	public void update ( Observable arg0, Object arg1 )
	{
		this.setViewMode ( ViewMode.VIEW_RESULTS );
	}

	class OnEditSearchSubmitted implements OnKeyListener
	{

		public boolean onKey ( View view, int keyCode, KeyEvent event )
		{
			if ( event.getAction () == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER )
			{

				// validate search data
				String foodName = editSearch.getText ().toString ();
				if ( foodName != null && !foodName.isEmpty () )
				{
					// clear previous data
					SearchActivity.this.selectedItem = null;
					SearchActivity.this.setTextConfirm ( TextConfirmState.DEFAULT );

					KeyboardUtils.hideKeyboard ( SearchActivity.this.editSearch );

					SearchActivity.this.searchResultsModel.setSearchTerm ( foodName );
					SearchActivity.this.setViewMode ( ViewMode.VIEW_LOADING );
					new GetNutritionInfoTask ().execute ( foodName );

				}else
				{
					Toast.makeText ( SearchActivity.this, R.string.search_error_empty_search_field, Toast.LENGTH_LONG ).show ();
				}
			}
			return false;
		}

	}

	class OnListItemClicked implements ExpandableListView.OnChildClickListener
	{

		public boolean onChildClick ( ExpandableListView parent, View v, int groupPosition, int childPosition, long id )
		{
			NutritionInfo info = SearchActivity.this.adapter.getChild ( groupPosition, childPosition );

			SearchActivity.this.selectedItem = info;
			SearchActivity.this.setTextConfirm ( TextConfirmState.CONFIRM_ADD );

			return true;
		}

	}

	class GetNutritionInfoTask extends AsyncTask< String, String, List< NutritionInfo > >
	{
		private String foodName;

		@Override
		protected void onPreExecute ()
		{
			if ( !NetworkUtils.isConnectedToNetwork ( SearchActivity.this ) )
			{
				publishProgress ( "No Internet Connection." );
				this.cancel ( true );
			}
		}

		@Override
		protected List< NutritionInfo > doInBackground ( String... params )
		{
			List< NutritionInfo > nutritionInfo = null;
			try
			{
				this.foodName = params[0];

				publishProgress ( "Fetching Nutrition Information for " + foodName );
				for ( int i = 0 ; i < 3 ; i++ )
				{
					nutritionInfo = CAWebService.getNutritionInfo ( foodName );
					if ( nutritionInfo != null ) break;
				}

			}
			catch ( IOException e )
			{
				Log.e ( TAG, "IOException occured while fetching results." + e.toString () );
				e.printStackTrace ();
			}

			return nutritionInfo;
		}

		@Override
		protected void onProgressUpdate ( String... values )
		{
			SearchActivity.this.textLoading.setText ( values[0] );
		}

		@Override
		protected void onPostExecute ( List< NutritionInfo > response )
		{
			if ( response != null )
			{
				Map< String, List< NutritionInfo >> nutritionInfoDictionary = new HashMap< String, List< NutritionInfo >> ();
				nutritionInfoDictionary.put ( this.foodName, response );

				SearchActivity.this.searchResultsModel.setSearchResults ( nutritionInfoDictionary );

			}else
			{
				this.publishProgress ( "No Results Found" );
			}
		}
	}

	class OnAddToJournalClicked implements View.OnClickListener
	{
		@Override
		public void onClick ( View v )
		{
			boolean success = ( SearchActivity.this.addToJournal () > 0 );
			SearchActivity.this.setTextConfirm ( success ? TextConfirmState.ADDED : TextConfirmState.NOT_ADDED );

			SearchActivity.this.linkPhotoWithGenericFoodName ();

		}
	}

}