package com.wks.calorieapp.activities;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.NutritionInfoAdapter;
import com.wks.calorieapp.apis.CAWebService;
import com.wks.calorieapp.apis.NutritionInfo;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.entities.ImageEntry;
import com.wks.calorieapp.entities.JournalEntry;
import com.wks.calorieapp.utils.FileSystem;
import com.wks.calorieapp.utils.NetworkUtils;

public class GetCaloriesActivity extends Activity
{
	private static final String TAG = GetCaloriesActivity.class.getCanonicalName ();

	private static final int MAX_HITS = 10;
	private static final float MIN_SIMILARITY = 0.1F;

	public static final String KEY_IMAGE = "image";
	private RelativeLayout viewLoading;
	private RelativeLayout viewResults;
	private ViewSwitcher viewSwitcher;

	private ProgressBar progressLoading;
	private TextView textLoading;
	private GetCaloriesTask getCaloriesTask;

	private ExpandableListView listNutritionInfo;
	private ImageButton buttonAddToJournal;
	private TextView textConfirm;

	private enum ViewMode
	{
		VIEW_LOADING, VIEW_RESULTS;
	}

	private enum TextConfirmGist
	{
		DEFAULT, CONFIRM_ADD, ADDED, NOT_ADDED;
	}

	private ViewMode viewMode;
	private String cameraPhotoName;
	private NutritionInfoAdapter adapter;

	//public String selectedFoodCategory;
	private NutritionInfo selectedFood;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_get_calories );

		this.init ();
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();

		getCaloriesTask = new GetCaloriesTask ();
		getCaloriesTask.execute ( cameraPhotoName );
	}

	@Override
	protected void onPause ()
	{
		super.onPause ();
		getCaloriesTask.cancel ( true );
		this.finish ();
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{
		MenuInflater inflater = this.getMenuInflater ();
		inflater.inflate ( R.menu.activity_get_calories, menu );
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu ( Menu menu )
	{
		boolean isDisplayingResults = this.viewMode == ViewMode.VIEW_RESULTS;
		menu.findItem ( R.id.get_calories_menu_done ).setVisible ( isDisplayingResults );
		menu.findItem ( R.id.get_calories_menu_no_match ).setVisible ( isDisplayingResults );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			// return to previous activity.
			this.finish ();
			return true;

		case R.id.get_calories_menu_done:
			Calendar calendar = Calendar.getInstance ();

			Intent dateCaloriesIntent = new Intent ( this, DateCaloriesActivity.class );
			dateCaloriesIntent.putExtra ( DateCaloriesActivity.KEY_DATE, calendar.getTimeInMillis () );
			startActivity ( dateCaloriesIntent );

			return true;

		case R.id.get_calories_menu_no_match:
			Intent searchIntent = new Intent ( this, SearchActivity.class );
			searchIntent.putExtra ( SearchActivity.KEY_IMAGE, this.cameraPhotoName );
			startActivity ( searchIntent );
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
			this.cameraPhotoName = extras.getString ( KEY_IMAGE );
		}else
		{
			Toast.makeText ( this, R.string.get_calories_error_image_not_found, Toast.LENGTH_LONG ).show ();
			this.finish ();
			return;
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
		this.viewSwitcher = ( ViewSwitcher ) this.findViewById ( R.id.get_calories_view_switcher );

		this.viewLoading = ( RelativeLayout ) this.findViewById ( R.id.get_calories_relativelayout_loading );
		this.viewResults = ( RelativeLayout ) this.findViewById ( R.id.get_calories_relativelayout_results );

		this.progressLoading = ( ProgressBar ) this.findViewById ( R.id.get_calories_spinner_loading );
		this.textLoading = ( TextView ) this.findViewById ( R.id.get_calories_text_loading );
		this.listNutritionInfo = ( ExpandableListView ) this.findViewById ( R.id.get_calories_expandlist_nutrition_info );
		this.buttonAddToJournal = ( ImageButton ) this.findViewById ( R.id.get_calories_button_add_to_journal );
		this.textConfirm = ( TextView ) this.findViewById ( R.id.get_calories_text_confirm );

		this.setViewMode ( ViewMode.VIEW_LOADING );
	}

	private void setupListeners ()
	{
		this.listNutritionInfo.setOnChildClickListener ( new OnListItemClicked () );
		this.buttonAddToJournal.setOnClickListener ( new OnAddToJournalClicked () );
	}

	private void setListContents ( Map< String, List< NutritionInfo >> nutritionInfoDictionary )
	{
		if ( nutritionInfoDictionary != null )
		{
			this.adapter = new NutritionInfoAdapter ( this, nutritionInfoDictionary );
			this.listNutritionInfo.setAdapter ( adapter );
		}
	}

	private void setViewMode ( ViewMode view )
	{
		this.viewMode = view;
		this.invalidateOptionsMenu ();

		switch ( view )
		{
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

	private void setTextConfirm ( TextConfirmGist gist )
	{
		String confirmMessage = "";
		switch ( gist )
		{
		case ADDED:
			if ( this.selectedFood != null )
			{
				String confirmTemplate = this.getString ( R.string.get_calories_template_added );
				confirmMessage = String.format ( confirmTemplate, this.selectedFood.getName () );
			}
			break;
		case NOT_ADDED:
			if ( this.selectedFood != null )
			{
				String confirmTemplate = this.getString ( R.string.get_calories_template_not_added );
				confirmMessage = String.format ( confirmTemplate, this.selectedFood.getName () );
			}
			break;
		case CONFIRM_ADD:
			if ( this.selectedFood != null )
			{
				String confirmTemplate = this.getString ( R.string.get_calories_template_confirm_add );
				confirmMessage = String.format ( confirmTemplate, this.selectedFood.getName () );
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

		if ( this.selectedFood == null ) return -1;

		ImageEntry image = null;
		if ( this.cameraPhotoName != null && !this.cameraPhotoName.isEmpty () )
		{
			image = new ImageEntry ();
			image.setFileName ( this.cameraPhotoName );
			
		}

		Calendar cal = Calendar.getInstance ();

		JournalEntry journal = new JournalEntry ();
		journal.setTimestamp ( cal.getTimeInMillis () );
		journal.setNutritionInfo ( this.selectedFood );
		journal.setImageEntry ( image );
		Log.e(TAG,"GCA: "+journal.getImageEntry ());
		
		DatabaseManager manager = DatabaseManager.getInstance ( this );
		SQLiteDatabase db = manager.open ();

		JournalDAO journalDao = new JournalDAO ( db );
		long journalId = journalDao.create ( journal );

		db.close ();
		return journalId;

	}

	private void linkPhotoWithFoodCategory ()
	{
		if ( this.cameraPhotoName != null && this.selectedFood != null/*this.selectedFoodCategory != null && !this.selectedFoodCategory.isEmpty () */)
		{
			String [] params =
			{
					this.cameraPhotoName, this.selectedFood.getName ()
			};
			new LinkImageWithFoodTask ( this ).execute ( params );
		}
	}

	class OnListItemClicked implements ExpandableListView.OnChildClickListener
	{

		@Override
		public boolean onChildClick ( ExpandableListView parent, View v, int groupPosition, int childPosition, long id )
		{
			//GetCaloriesActivity.this.selectedFoodCategory = GetCaloriesActivity.this.adapter.getGroup ( groupPosition ).getFoodCategory ();
			NutritionInfo info = GetCaloriesActivity.this.adapter.getChild ( groupPosition, childPosition );

			GetCaloriesActivity.this.selectedFood = info;
			GetCaloriesActivity.this.setTextConfirm ( TextConfirmGist.CONFIRM_ADD );

			return true;
		}

	}

	class OnAddToJournalClicked implements View.OnClickListener
	{

		@Override
		public void onClick ( View v )
		{
			boolean success = ( GetCaloriesActivity.this.addToJournal () > 0 );
			GetCaloriesActivity.this.setTextConfirm ( success ? TextConfirmGist.ADDED : TextConfirmGist.NOT_ADDED );

			GetCaloriesActivity.this.linkPhotoWithFoodCategory ();

		}
	}

	class GetCaloriesTask extends AsyncTask< String, String, Map< String, List< NutritionInfo >> >
	{
		private String fileName;

		@Override
		protected void onPreExecute ()
		{
			if ( !NetworkUtils.isConnectedToNetwork ( GetCaloriesActivity.this ) )
			{
				publishProgress ( "No Internet Connection." );
				this.cancel ( true );
			}
		}

		@Override
		protected void onPostExecute ( Map< String, List< NutritionInfo >> response )
		{
			if ( response != null )
			{
				// a really stupid way of checking that the result is not empty.
				for ( Entry< String, List< NutritionInfo >> entry : response.entrySet () )
				{
					// there should be atleast one list.
					if ( !entry.getValue ().isEmpty () )
					{
						GetCaloriesActivity.this.setListContents ( response );
						GetCaloriesActivity.this.setViewMode ( ViewMode.VIEW_RESULTS );
						return;
					}
				}

			}

			Toast.makeText ( GetCaloriesActivity.this, "No Matches Found", Toast.LENGTH_LONG ).show ();
			Intent searchFoodIntent = new Intent ( GetCaloriesActivity.this, SearchActivity.class );
			searchFoodIntent.putExtra ( SearchActivity.KEY_IMAGE, cameraPhotoName );
			startActivity ( searchFoodIntent );

		}

		@Override
		protected Map< String, List< NutritionInfo >> doInBackground ( String... params )
		{
			String picturesDir = FileSystem.getPicturesDirectory ( GetCaloriesActivity.this );
			this.fileName = params[0];
			File imageFile = new File ( picturesDir + this.fileName );

			Map< String, List< NutritionInfo >> foodNutritionInfoMap = null;

			try
			{

				publishProgress ( "Uploading Image..." );
				boolean fileUploaded = CAWebService.upload ( imageFile );

				if ( !fileUploaded ) return null;

				if ( this.isCancelled () ) this.cancel ( true );

				publishProgress ( "Getting Nutrition Information..." );

				foodNutritionInfoMap = CAWebService.recognize ( fileName, MIN_SIMILARITY, MAX_HITS );

				return foodNutritionInfoMap;

			}
			catch ( IOException e )
			{
				Log.e ( TAG, "" + e.getMessage () );
			}

			return foodNutritionInfoMap;
		}

		@Override
		protected void onProgressUpdate ( String... values )
		{
			GetCaloriesActivity.this.textLoading.setText ( values[0] );
		}

	}

}
