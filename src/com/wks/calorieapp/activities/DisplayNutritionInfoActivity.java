package com.wks.calorieapp.activities;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.NutritionInfoListAdapter;
import com.wks.calorieapp.daos.DatabaseManager;
import com.wks.calorieapp.daos.JournalDAO;
import com.wks.calorieapp.pojos.ImageEntry;
import com.wks.calorieapp.pojos.JournalEntry;
import com.wks.calorieapp.pojos.NutritionInfo;

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

public class DisplayNutritionInfoActivity extends Activity
{
	private ExpandableListView listNutritionInfo;
	private ImageButton buttonAddToJournal;
	private TextView textConfirm;

	private String fileName;
	private NutritionInfoListAdapter adapter;
	private Map< String, List< NutritionInfo >> nutritionInfoMap;
	private NutritionInfo selectedFood;

	private enum TextConfirmGist
	{
		DEFAULT,CONFIRM_ADD,ADDED,NOT_ADDED;
	}
	
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.activity_display_nutrition_info );
		
		this.init();
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
		this.setListContents ();
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
			Calendar calendar = Calendar.getInstance ();
			
			Intent dateCaloriesIntent = new Intent(this,DateCaloriesActivity.class);
			dateCaloriesIntent.putExtra( DateCaloriesActivity.KEY_DATE, calendar.getTimeInMillis () );
			startActivity(dateCaloriesIntent);
			
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

	private void init()
	{
		this.nutritionInfoMap = CalorieApplication.getNutritionInfoMap ();
		if ( nutritionInfoMap == null )
		{
			Toast.makeText ( this, R.string.diplay_nutrition_info_error_display, Toast.LENGTH_LONG ).show ();
			this.finish ();
		}

		Bundle extras = this.getIntent ().getExtras ();
		if ( extras != null )
		{
			this.fileName = extras.getString ( "image" );
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

	private void setListContents ()
	{
		if ( this.nutritionInfoMap != null )
		{
			this.adapter = new NutritionInfoListAdapter ( this, nutritionInfoMap );
			this.listNutritionInfo.setAdapter ( adapter );
		}
	}

	private void setupListeners ()
	{
		this.listNutritionInfo.setOnChildClickListener ( new OnListItemClicked () );
		this.buttonAddToJournal.setOnClickListener ( new OnAddToJournalClicked () );
	}

	private void setTextConfirm (TextConfirmGist gist)
	{
		String confirmMessage = "";
		switch(gist)
		{
		case ADDED:
			if ( this.selectedFood != null )
			{
				String confirmTemplate = this.getString ( R.string.display_nutrition_info_layout_text_confirm_template_added );
				confirmMessage = String.format ( confirmTemplate, this.selectedFood.getName () );
			}	
			break;
		case NOT_ADDED:
			if ( this.selectedFood != null )
			{
				String confirmTemplate = this.getString ( R.string.display_nutrition_info_layout_text_confirm_template_not_added );
				confirmMessage = String.format ( confirmTemplate, this.selectedFood.getName () );
			}
			break;
		case CONFIRM_ADD:
			if ( this.selectedFood != null )
			{
				String confirmTemplate = this.getString ( R.string.display_nutrition_info_layout_text_confirm_template_confirm_add );
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
			if ( this.fileName != null && !this.fileName.isEmpty () )
			{
				image = new ImageEntry ();
				image.setFileName ( this.fileName );
			}
			
			Calendar cal = Calendar.getInstance ();
			
			JournalEntry journal = new JournalEntry ();
			journal.setTimestamp ( cal.getTimeInMillis () );
			journal.setNutritionInfo ( this.selectedFood );
			journal.setImageEntry ( image );

			DatabaseManager manager = DatabaseManager.getInstance ( this );
			SQLiteDatabase db = manager.open ();
			
			JournalDAO journalDao = new JournalDAO ( db );
			long journalId = journalDao.create ( journal);
			
			db.close ();
			return journalId;
		
	}

	class OnListItemClicked implements ExpandableListView.OnChildClickListener
	{

		@Override
		public boolean onChildClick ( ExpandableListView parent, View v, int groupPosition, int childPosition, long id )
		{
			NutritionInfo info = DisplayNutritionInfoActivity.this.adapter.getChild ( groupPosition, childPosition );

			DisplayNutritionInfoActivity.this.selectedFood = info;
			DisplayNutritionInfoActivity.this.setTextConfirm (TextConfirmGist.CONFIRM_ADD);

			return true;
		}

	}

	class OnAddToJournalClicked implements View.OnClickListener
	{

		@Override
		public void onClick ( View v )
		{
			boolean success = ( DisplayNutritionInfoActivity.this.addToJournal () > 0 );
			DisplayNutritionInfoActivity.this.setTextConfirm(success?TextConfirmGist.ADDED: TextConfirmGist.NOT_ADDED);
		}
	}
}
