package com.wks.calorieapp.activities;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.NutritionInfoListAdapter;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.pojos.FoodEntry;
import com.wks.calorieapp.pojos.ImageEntry;
import com.wks.calorieapp.pojos.JournalEntry;
import com.wks.calorieapp.pojos.NutritionInfo;
import com.wks.calorieapp.utils.Time;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayNutritionInfoActivity extends Activity
{
	private ExpandableListView listNutritionInfo;
	private ImageButton buttonAddToJournal;
	private TextView textConfirm;

	private String fileName;
	private NutritionInfoListAdapter adapter;
	private Map< String, List< NutritionInfo >> nutritionInfoDictionary;
	private NutritionInfo selectedFood;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.activity_display_nutrition_info );

		this.nutritionInfoDictionary = CalorieApplication.getNutritionInfoDictionary ();
		if ( nutritionInfoDictionary == null )
		{
			Toast.makeText ( this, R.string.diplay_nutrition_info_error_display, Toast.LENGTH_LONG ).show ();
			this.finish ();
		}

		Log.e ( "shit", nutritionInfoDictionary.toString () );

		Bundle extras = this.getIntent ().getExtras ();
		if ( extras != null )
		{
			this.fileName = extras.getString ( "image" );
		}

		setupActionBar ();
		setupView ();
		setupListeners ();
		setupList ();
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{
		MenuInflater inflater = this.getMenuInflater ();
		inflater.inflate ( R.menu.activity_display_nutrition_info, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent cameraIntent = new Intent ( DisplayNutritionInfoActivity.this, CameraActivity.class );
			cameraIntent.addFlags ( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
			startActivity ( cameraIntent );
			return true;

		case R.id.display_nutrition_info_menu_done:
			Toast.makeText ( this, "goes to days calories", Toast.LENGTH_SHORT ).show ();
			return true;

		case R.id.display_nutrition_info_menu_no_match:
			Intent searchIntent = new Intent ( this, SearchActivity.class );
			searchIntent.putExtra ( "image", this.fileName );
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
		this.listNutritionInfo = ( ExpandableListView ) this.findViewById ( R.id.display_nutrition_info_expandlist_nutrition_info );
		this.buttonAddToJournal = ( ImageButton ) this.findViewById ( R.id.display_nutrition_info_button_add_to_journal );
		this.textConfirm = ( TextView ) this.findViewById ( R.id.display_nutrition_info_text_confirm );
	}

	private void setupList ()
	{
		if ( this.nutritionInfoDictionary != null )
		{
			this.adapter = new NutritionInfoListAdapter ( this, nutritionInfoDictionary );
			this.listNutritionInfo.setAdapter ( adapter );
		}
	}

	private void setupListeners ()
	{
		this.listNutritionInfo.setOnChildClickListener ( new OnListItemClicked () );
		this.buttonAddToJournal.setOnClickListener ( new OnAddToJournalClicked () );
	}

	private void showConfirmMessage ()
	{
		String confirmMessage = "";

		if ( this.selectedFood != null )
		{
			String confirmTemplate = this.getString ( R.string.display_nutrition_info_confirm );
			confirmMessage = String.format ( confirmTemplate, this.selectedFood.getName () );

		}else
		{
			confirmMessage = this.getString ( R.string.display_nutrition_info_layout_text_default_confirm );
		}

		this.textConfirm.setText ( confirmMessage );
	}

	private long addToJournal ()
	{
		try
		{
			if ( this.selectedFood == null ) return -1;

			JournalEntry journal = new JournalEntry ();
			FoodEntry food = new FoodEntry ();
			ImageEntry image = null;

			food.setId ( this.selectedFood.getId () );
			food.setName ( this.selectedFood.getName () );
			food.setCalories ( this.selectedFood.getCaloriesPer100g () );

			String time = Time.getTimeAsString ( Calendar.getInstance (), JournalEntry.DATE_FORMAT + "~"
					+ JournalEntry.TIME_FORMAT );
			String [] timeTokens = time.split ( "~" );

			if ( timeTokens.length < 2 ) return -1;

			journal.setDate ( timeTokens[0] );
			journal.setTime ( timeTokens[1] );

			if ( this.fileName != null && !this.fileName.isEmpty () )
			{
				image = new ImageEntry ();
				image.setFileName ( this.fileName );
			}

			DatabaseManager helper = DatabaseManager.getInstance ( this );
			SQLiteDatabase db = helper.open ();
			JournalDAO journalDao = new JournalDAO ( db );
			long journalId = journalDao.create ( journal, food, image );
			db.close ();
			return journalId;
		}
		catch ( java.text.ParseException e )
		{

			e.printStackTrace ();
			return -1;
		}
	}

	class OnListItemClicked implements ExpandableListView.OnChildClickListener
	{

		@Override
		public boolean onChildClick ( ExpandableListView parent, View v, int groupPosition, int childPosition, long id )
		{
			NutritionInfo info = DisplayNutritionInfoActivity.this.adapter.getChild ( groupPosition, childPosition );

			DisplayNutritionInfoActivity.this.selectedFood = info;
			DisplayNutritionInfoActivity.this.showConfirmMessage ();

			return true;
		}

	}

	class OnAddToJournalClicked implements View.OnClickListener
	{

		@Override
		public void onClick ( View v )
		{
			boolean success = ( DisplayNutritionInfoActivity.this.addToJournal () > 0 );
			String message = String.format (
					DisplayNutritionInfoActivity.this.getString ( 
							success ? 
									R.string.display_nutrition_info_toast_entry_added
									: R.string.display_nutrition_info_toast_entry_not_added ), 
							DisplayNutritionInfoActivity.this.selectedFood.getName () );

			Toast.makeText ( DisplayNutritionInfoActivity.this, message, Toast.LENGTH_LONG ).show ();
		}
	}
}
