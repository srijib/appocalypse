package com.wks.calorieapp.activities;

import java.util.LinkedHashMap;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.HomeMenuGridAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class HomeActivity extends Activity
{
	GridView gridviewActivities;
	LinkedHashMap<String,Integer> activitiesDictionary;
	
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_home );
		
		this.activitiesDictionary = new LinkedHashMap<String,Integer>();
		
		Activity[] activities = Activity.values ();
		for(Activity activity: activities)
			activitiesDictionary.put ( activity.getText (), activity.getResourceId () );
		
		setupView();
		setupListeners();
	}
	
	private void setupView()
	{
		this.gridviewActivities = (GridView) this.findViewById ( R.id.home_gridview_activities );
		this.gridviewActivities.setAdapter ( new HomeMenuGridAdapter(this, this.activitiesDictionary) );
	}
	
	private void setupListeners()
	{
		this.gridviewActivities.setOnItemClickListener ( new OnGridActivitiesClicked() );
	}
	
	enum Activity
	{
		CAMERA("Photo for calories",R.drawable.ic_launcher,CameraActivity.class),
		SEARCH("Search",R.drawable.ic_launcher,SearchActivity.class);
		
		private final String text;
		private final int resourceId;
		private final Class< ? > intent;
		
		Activity(String text, int resourceId, Class< ? > intent)
		{
			this.text = text;
			this.resourceId = resourceId;
			this.intent = intent;
		}
		
		public String getText ()
		{
			return text;
		}
		
		public int getResourceId ()
		{
			return resourceId;
		}
		
		public Class< ? > getIntent ()
		{
			return intent;
		}
	}
	
	class OnGridActivitiesClicked implements AdapterView.OnItemClickListener
	{
		@Override
		public void onItemClick ( AdapterView< ? > parent, View view, int position, long id )
		{
			Activity[] activities = HomeActivity.Activity.values ();
			Class<?> intentClass = activities[position].getClass ();
			
			//begin activity
			Intent activityIntent = new Intent(HomeActivity.this,intentClass);
			HomeActivity.this.startActivity ( activityIntent );
		}
	}
}
