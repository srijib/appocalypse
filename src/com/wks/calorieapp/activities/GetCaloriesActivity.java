package com.wks.calorieapp.activities;

import java.io.File;
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
import com.wks.calorieapp.pojos.FoodSimilarity;
import com.wks.calorieapp.pojos.ImageEntry;
import com.wks.calorieapp.pojos.JournalEntry;
import com.wks.calorieapp.pojos.NutritionInfo;
import com.wks.calorieapp.pojos.Response;
import com.wks.calorieapp.pojos.ResponseFactory;
import com.wks.calorieapp.utils.FileSystem;
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

public class GetCaloriesActivity extends Activity
{
	private static final String TAG = GetCaloriesActivity.class.getCanonicalName ();

	private static final int NUM_TRIES_GET_NUTR_INFO = 3;

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

	private String cameraPhotoName;
	private NutritionInfoListAdapter adapter;
	private NutritionInfo selectedFood;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_get_calories );
	
		this.init();
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
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent cameraIntent = new Intent ( this, CameraActivity.class );
			cameraIntent.addFlags ( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
			startActivity ( cameraIntent );
			return true;

		case R.id.get_calories_menu_done:
			Calendar calendar = Calendar.getInstance ();
			
			Intent dateCaloriesIntent = new Intent(this,DateCaloriesActivity.class);
			dateCaloriesIntent.putExtra( DateCaloriesActivity.KEY_DATE, calendar.getTimeInMillis () );
			startActivity(dateCaloriesIntent);
			
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

	private void init()
	{
		Bundle extras = this.getIntent ().getExtras ();
		if ( extras != null )
		{
			this.cameraPhotoName = extras.getString ( "image" );
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
		this.viewSwitcher = (ViewSwitcher) this.findViewById ( R.id.get_calories_view_switcher );
		
		this.viewLoading = (RelativeLayout) this.findViewById ( R.id.get_calories_relativelayout_loading );
		this.viewResults = (RelativeLayout) this.findViewById ( R.id.get_calories_relativelayout_results );
		
		this.progressLoading = ( ProgressBar ) this.findViewById ( R.id.get_calories_spinner_loading );
		this.textLoading = ( TextView ) this.findViewById ( R.id.get_calories_text_loading );
		this.listNutritionInfo = ( ExpandableListView ) this.findViewById ( R.id.get_calories_expandlist_nutrition_info );
		this.buttonAddToJournal = ( ImageButton ) this.findViewById ( R.id.get_calories_button_add_to_journal );
		this.textConfirm = ( TextView ) this.findViewById ( R.id.get_calories_text_confirm );
	
		this.setViewMode(ViewMode.VIEW_LOADING);
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
			this.adapter = new NutritionInfoListAdapter ( this, nutritionInfoDictionary );
			this.listNutritionInfo.setAdapter ( adapter );
		}
	}

	private void setViewMode ( ViewMode view )
	{
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

		DatabaseManager manager = DatabaseManager.getInstance ( this );
		SQLiteDatabase db = manager.open ();

		JournalDAO journalDao = new JournalDAO ( db );
		long journalId = journalDao.create ( journal );

		db.close ();
		return journalId;

	}

	class OnListItemClicked implements ExpandableListView.OnChildClickListener
	{

		@Override
		public boolean onChildClick ( ExpandableListView parent, View v, int groupPosition, int childPosition, long id )
		{
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
		}
	}

	class GetCaloriesTask extends AsyncTask< String, String, Response >
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
		protected void onPostExecute ( Response response )
		{
			if ( response != null && response.isSuccessful () && response.getData () instanceof HashMap )
			{
				// get data
				@SuppressWarnings ( "unchecked" )
				HashMap< String, List< NutritionInfo >> nutritionInfoForFoods = ( HashMap< String, List< NutritionInfo >> ) response.getData ();
				// pass data to Application object
				
				GetCaloriesActivity.this.setListContents ( nutritionInfoForFoods );
				GetCaloriesActivity.this.setViewMode ( ViewMode.VIEW_RESULTS );

			}else
			{
				if ( response != null )
				{
					Log.e ( TAG, response.getMessage () );
				}

				Intent searchFoodIntent = new Intent ( GetCaloriesActivity.this, SearchActivity.class );
				startActivity ( searchFoodIntent );
			}
		}

		/**
		 * I just want to go on record saying that I am definitely not pleased
		 * with the quality of code in this method. but i'm really frustrated so
		 * i jsut want it to work cuz i've got a deadline and a dissertation to
		 * wrtie.
		 */
		@Override
		protected Response doInBackground ( String... params )
		{
			String picturesDir = FileSystem.getPicturesDirectory ( GetCaloriesActivity.this );
			this.fileName = params[0];
			File imageFile = new File ( picturesDir + this.fileName );

			String json = "";
			Response response = null;

			try
			{
				// upload image
				publishProgress ( "Uploading Image..." );
				json = HttpClient.uploadFile ( imageFile, WebServiceUrlFactory.upload () );
				response = ResponseFactory.createResponseForUploadRequest ( json );

				Log.e ( "UPLOAD", json );

				if ( response == null || !response.isSuccessful () ) return response;
				if ( this.isCancelled () ) this.cancel ( true );
				// get matches for image
				publishProgress ( "Identifying Food..." );
				json = HttpClient.get ( WebServiceUrlFactory.identify ( fileName ) );
				response = ResponseFactory.createResponseForIdentifyRequest ( json );

				Log.e ( "IDENTIFY", json );

				// if response not received or matching foods not found, return
				if ( response == null || !response.isSuccessful () ) return response;
				if ( this.isCancelled () ) this.cancel ( true );
				// if matching foods found
				if ( response.getData () != null )
				{
					// assert that data is a list of matching foods before
					// casting.
					if ( response.getData () instanceof List )
					{
						@SuppressWarnings ( "unchecked" )
						List< FoodSimilarity > foodSimilarity = ( List< FoodSimilarity > ) response.getData ();

						publishProgress ( "Getting Nutrition Information..." );
						// create hashmap containing food name => nutrtion info
						HashMap< String, List< NutritionInfo >> nutritionInfoForFoods = new HashMap< String, List< NutritionInfo >> ();

						// get nutrition info for each food in list.
						for ( int i = 0 ; i < foodSimilarity.size () ; i++ )
						{
							String foodName = foodSimilarity.get ( i ).getFoodName ();

							// nutrition info request doesn't always work, so if
							// it fails,
							// make NUM_TRIES_GET_NUTR_INFO tries before giving
							// up.
							for ( int j = 0 ; j < NUM_TRIES_GET_NUTR_INFO ; j++ )
							{
								if ( this.isCancelled () ) this.cancel ( true );
								json = HttpClient.get ( WebServiceUrlFactory.getNutritionInfo ( foodName ) );
								response = ResponseFactory.createResponseForNutritionInfoRequest ( json );

								Log.e ( "GET INFO", json );
								// when response is received with data, move on
								// to next step.

								if ( response != null && response.isSuccessful () ) break;
							}

							if ( this.isCancelled () ) this.cancel ( true );
							publishProgress ( "Proccessing Information" );
							// double check that the response is not null and
							// that data is received.
							if ( response != null && response.isSuccessful () )
							{
								// asseert tht data is list of nutrition info
								if ( response.getData () instanceof List )
								{

									@SuppressWarnings ( "unchecked" )
									List< NutritionInfo > nutritionInfoForFood = ( List< NutritionInfo > ) response.getData ();

									// put nutrition info into map with food
									// name as key.
									nutritionInfoForFoods.put ( foodName, nutritionInfoForFood );
								}
							}

						}
						// replace list of food similarity with hashmap
						// foodname=>nutrinfo
						response.setData ( nutritionInfoForFoods );
					}else
					// you might want to throw an exception over here.
					return null;

				}

			}
			catch ( IOException e )
			{
				Log.e ( TAG, "" + e.getMessage () );
			}
			catch ( ParseException e )
			{
				Log.e ( TAG, "" + e.getMessage () );
			}

			if ( this.isCancelled () ) this.cancel ( true );
			return response;
		}

		@Override
		protected void onProgressUpdate ( String... values )
		{
			GetCaloriesActivity.this.textLoading.setText ( values[0] );
		}

	}

}
