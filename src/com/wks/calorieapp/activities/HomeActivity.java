package com.wks.calorieapp.activities;

import java.util.ArrayList;
import java.util.List;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.NavigationListAdapter;
import com.wks.calorieapp.adapters.GridItem;

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
	private static final int NUM_ROWS = 3;
	
	ListView listviewActivities;
	List<GridItem> activitiesList;
	
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_home );
		
		
		this.activitiesList = new ArrayList<GridItem>();
		
		Activity[] activities = Activity.values ();
		for(Activity activity: activities)
			activitiesList.add ( new GridItem(activity.getText (),activity.getResourceId ()) );
		
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
		this.listviewActivities.setAdapter ( new NavigationListAdapter(this, this.activitiesList, NUM_ROWS));
	}
	
	private void setupListeners()
	{
		this.listviewActivities.setOnItemClickListener ( new OnGridActivitiesClicked() );
	}
	
	enum Activity
	{
		CAMERA("Calorie Camera",R.drawable.ic_launcher),
		SEARCH("Food Search",R.drawable.search),
		JOURNAL("Calorie Journal",R.drawable.ic_launcher),
		GALLERY("Gallery",R.drawable.gallery),
		PROFILE("Profile",R.drawable.ic_launcher);
		
		private final String text;
		private final int resourceId;
		
		Activity(String text, int resourceId)
		{
			this.text = text;
			this.resourceId = resourceId;
		}
		
		public String getText ()
		{
			return text;
		}
		
		public int getResourceId ()
		{
			return resourceId;
		}
	
	}
	
	class OnGridActivitiesClicked implements AdapterView.OnItemClickListener
	{
		@Override
		public void onItemClick ( AdapterView< ? > parent, View view, int position, long id )
		{
			
			
			switch(Activity.values ()[position])
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
