package com.wks.calorieapp.activities;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.HomeMenuAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends Activity
{
	
	ListView listviewActivities;
	HomeMenuAdapter adapter;
	
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_home );
		
		setupActionBar();
		setupView();
		setupListeners();
	}
	
	private void setupActionBar()
	{
		ActionBar actionBar = this.getActionBar ();
		
		Drawable d = this.getResources ().getDrawable ( R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( d );
	}
	
	private void setupView()
	{
		this.listviewActivities = (ListView) this.findViewById ( R.id.home_listview_activities );
		this.adapter = new HomeMenuAdapter(this);
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
			
			
			switch(HomeActivity.this.adapter.getItem ( position ))
			{
			case CAMERA:
				Intent cameraIntent = new Intent(HomeActivity.this, CameraActivity.class);
				startActivity(cameraIntent);
				return;
			case SEARCH:
				Intent searchIntent = new Intent(HomeActivity.this, SearchActivity.class);
				startActivity(searchIntent);
				return;
			case JOURNAL:
				Intent journalIntent = new Intent(HomeActivity.this, JournalActivity.class);
				startActivity(journalIntent);
				return;
			case PROFILE:
				Intent profileIntent = new Intent(HomeActivity.this,ProfileActivity.class);
				startActivity(profileIntent);
				return;
			
			default:
				Toast.makeText(HomeActivity.this,"Not yet implemented.",Toast.LENGTH_LONG).show();
				return;
			}
		}
	}
}
