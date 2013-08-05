package com.wks.calorieapp.activities;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.MainMenuAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainMenuActivity extends Activity
{
	
	ListView listviewActivities;
	MainMenuAdapter adapter;
	
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_main_menu );
		
		setupActionBar();
		setupView();
		setupListeners();
	}
	
	private void setupActionBar()
	{
		ActionBar actionBar = this.getActionBar ();
		
		Drawable backgroundActionBar = getResources ().getDrawable ( R.drawable.bg_actionbar );
		Drawable iconActionBar = getResources().getDrawable ( R.drawable.ic_actionbar );
		
		actionBar.setBackgroundDrawable ( backgroundActionBar );
		actionBar.setIcon ( iconActionBar );
	}
	
	private void setupView()
	{
		this.listviewActivities = (ListView) this.findViewById ( R.id.home_listview_activities );
		this.adapter = new MainMenuAdapter(this);
		this.listviewActivities.setAdapter ( this.adapter);
	}
	
	private void setupListeners()
	{
		this.listviewActivities.setOnItemClickListener ( new OnGridActivitiesClicked() );
	}
	
	class OnGridActivitiesClicked implements AdapterView.OnItemClickListener
	{
		@Override
		public void onItemClick ( AdapterView< ? > parent, View view, int position, long id )
		{
			
			
			switch(MainMenuActivity.this.adapter.getItem ( position ))
			{
			case CAMERA:
				Intent cameraIntent = new Intent(MainMenuActivity.this, CameraActivity.class);
				startActivity(cameraIntent);
				return;
			case SEARCH:
				Intent searchIntent = new Intent(MainMenuActivity.this, SearchActivity.class);
				startActivity(searchIntent);
				return;
			case JOURNAL:
				Intent journalIntent = new Intent(MainMenuActivity.this, JournalActivity.class);
				startActivity(journalIntent);
				return;
			case PROFILE:
				Intent profileIntent = new Intent(MainMenuActivity.this,ProfileActivity.class);
				startActivity(profileIntent);
				return;
			case GALLERY: 
				Intent galleryIntent = new Intent(MainMenuActivity.this, GalleryActivity.class);
				startActivity ( galleryIntent );
				return;
				
			default:
				//do nothing.
				return;
			}
		}
	}
}
