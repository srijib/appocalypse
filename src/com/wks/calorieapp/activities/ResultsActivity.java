package com.wks.calorieapp.activities;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.SearchResultsAdapter;
import com.wks.calorieapp.apis.NutritionInfo;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.entities.ImageEntry;
import com.wks.calorieapp.entities.JournalEntry;
import com.wks.calorieapp.models.SearchResultsModel;

public class ResultsActivity extends Activity
{
	//DEBUGGING TAG
	@SuppressWarnings ( "unused" )
	private static final String TAG = ResultsActivity.class.getCanonicalName ();

	//CONSTANTS
	public static final String EXTRAS_PHOTO_NAME = "image";
	public static final String EXTRAS_SELECTED_FOOD_INFO = "selected_food";
	
	//UI COMPONENTS
	private ExpandableListView listNutritionInfo;
	private ImageButton buttonAddToJournal;
	private TextView textConfirm;

	private enum TextConfirmState
	{
		DEFAULT, CONFIRM_ADD, ADDED, NOT_ADDED;
	}

	//MEMBERS
	private String photoName;
	private SearchResultsModel model;
	private SearchResultsAdapter adapter;

	public String genericFoodName;
	private NutritionInfo selectedFood;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_results );

		Bundle extras = this.getIntent ().getExtras ();
		if ( extras != null )
		{
			this.photoName = extras.getString ( EXTRAS_PHOTO_NAME );
		}else
		{
			Toast.makeText ( this, R.string.results_error_image_not_found, Toast.LENGTH_LONG ).show ();
			this.finish ();
			return;
		}
		
		this.model = new SearchResultsModel();
		
		Map<String,List<NutritionInfo>> identifyResults = ((CalorieApplication) this.getApplication ()).getIdentifyResults ();
		if(identifyResults != null)
			this.model.setSearchResults ( identifyResults );
		
		
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
	}

	@Override
	protected void onPause ()
	{
		super.onPause ();
		//getCaloriesTask.cancel ( true );
		this.finish ();
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{
		MenuInflater inflater = this.getMenuInflater ();
		inflater.inflate ( R.menu.activity_results, menu );
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

		//If user clicks on done, redirect to journal entry activity
		//to see all meals consumed today.

		case R.id.results_menu_done:
			//get todays daye
			Calendar calendar = Calendar.getInstance ();
			
			Intent journalEntryIntent = new Intent ( this, JournalEntryActivity.class );
			journalEntryIntent.putExtra ( JournalEntryActivity.EXTRAS_DATE, calendar.getTimeInMillis () );
			startActivity ( journalEntryIntent );

			return true;

		//If results provided by REST WebService are incorrect
		// redirect user to search activity so that he can search for food item.
		case R.id.results_menu_no_match:
			Intent searchIntent = new Intent ( this, SearchActivity.class );
			searchIntent.putExtra ( SearchActivity.EXTRAS_PHOTO_NAME, this.photoName );
			startActivity ( searchIntent );
			return true;

		default:
			return super.onOptionsItemSelected ( item );
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

		this.listNutritionInfo = ( ExpandableListView ) this.findViewById ( R.id.results_expandlist_nutrition_info );
		this.buttonAddToJournal = ( ImageButton ) this.findViewById ( R.id.results_button_add_to_journal );
		this.textConfirm = ( TextView ) this.findViewById ( R.id.results_text_confirm );

		this.adapter = new SearchResultsAdapter(this,this.model);
		this.listNutritionInfo.setAdapter ( this.adapter );
	}

	private void setupListeners ()
	{
		this.listNutritionInfo.setOnChildClickListener ( new OnListItemClicked () );
		this.buttonAddToJournal.setOnClickListener ( new OnAddToJournalClicked () );
	}


	/**Sets the text in the textConfirm TextView depending on selectedFood state: ADDED. NOT ADDED, DEFAULT
	 * 
	 * @param state
	 */
	private void setTextConfirm ( TextConfirmState state )
	{
		String confirmMessage = "";
		switch ( state )
		{
		case ADDED:
			if ( this.selectedFood != null )
			{
				String confirmTemplate = this.getString ( R.string.results_template_added );
				confirmMessage = String.format ( confirmTemplate, this.selectedFood.getName () );
			}
			break;
		case NOT_ADDED:
			if ( this.selectedFood != null )
			{
				String confirmTemplate = this.getString ( R.string.results_template_not_added );
				confirmMessage = String.format ( confirmTemplate, this.selectedFood.getName () );
			}
			break;
		case CONFIRM_ADD:
			if ( this.selectedFood != null )
			{
				String confirmTemplate = this.getString ( R.string.results_template_confirm_add );
				confirmMessage = String.format ( confirmTemplate, this.selectedFood.getName () );
			}
			break;
		default:
			confirmMessage = this.getString ( R.string.search_layout_text_confirm_default );
			break;
		}

		this.textConfirm.setText ( confirmMessage );
	}

	/**Adds journal Entry to db
	 * 
	 * @return id of journal entry
	 */
	private long addToJournal ()
	{
		
		if ( this.selectedFood == null ) return -1;

		//if image attached, create image entry for image
		ImageEntry image = null;
		if ( this.photoName != null && !this.photoName.isEmpty () )
		{
			image = new ImageEntry ();
			image.setFileName ( this.photoName );
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

	/*
	 * - REST Call so that server adds selected food item name as meta data for photo.
	 */
	private void linkPhotoWithGenericFoodName ()
	{
		if ( this.photoName != null && this.genericFoodName != null && !this.genericFoodName.isEmpty ())
		{
			String [] params =
			{
					this.photoName, this.genericFoodName
			};
			new LinkTask ( this ).execute ( params );
		}
	}

	/**Callback when a list item is clicked.
	 * 
	 */
	class OnListItemClicked implements ExpandableListView.OnChildClickListener
	{

		@Override
		public boolean onChildClick ( ExpandableListView parent, View v, int groupPosition, int childPosition, long id )
		{
			NutritionInfo selectedItem = adapter.getChild ( groupPosition, childPosition );

			genericFoodName = adapter.getGroup ( groupPosition ).getGenericFoodName ();
			selectedFood = selectedItem;
			setTextConfirm ( TextConfirmState.CONFIRM_ADD );

			return true;
		}

	}

	/**
	 * Callback when user clicks on 'Add To Journal' button
	 */
	class OnAddToJournalClicked implements View.OnClickListener
	{

		@Override
		public void onClick ( View v )
		{
			boolean success = ( ResultsActivity.this.addToJournal () > 0 );
			ResultsActivity.this.setTextConfirm ( success ? TextConfirmState.ADDED : TextConfirmState.NOT_ADDED );

			ResultsActivity.this.linkPhotoWithGenericFoodName ();

		}
	}


}
