package com.wks.calorieapp.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wks.android.utils.FileSystem;
import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.GalleryAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

/**Activity that displays pictures taken by the user in a gallery
 * 
 * @author Waqqas
 *
 */
public class GalleryActivity extends Activity
{
	//DEBUGGING 
	//private static final String TAG = GalleryActivity.class.getCanonicalName ();
	
	//UI Components
	private GridView gridviewGallery;
	private List< File > pictureFilesList;
	private GalleryAdapter adapter;

	
	protected void onCreate ( Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_gallery );
		
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
	}

	/**
	 * - Pressing Up Item on ActionBar returns to home Activity
	 */
	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent homeIntent = new Intent ( this, MainMenuActivity.class );
			homeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			this.startActivity ( homeIntent );
			return true;

		default:
			return super.onOptionsItemSelected ( item );
		}
	}


	/**
	 * - Applies background to ActionBar
	 */
	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		Drawable backgroundActionBar = getResources ().getDrawable ( R.drawable.bg_actionbar );
		Drawable iconActionBar = getResources().getDrawable ( R.drawable.ic_actionbar );
		
		actionBar.setBackgroundDrawable ( backgroundActionBar );
		actionBar.setIcon ( iconActionBar );

		actionBar.setDisplayHomeAsUpEnabled ( true );
	}

	/**
	 * Instantiates UI elements
	 */
	private void setupView ()
	{
		this.pictureFilesList = this.getPictureFiles ();
		
		this.adapter = new GalleryAdapter(this,this.pictureFilesList);
		this.gridviewGallery = ( GridView ) this.findViewById ( R.id.gallery_grid_gallery );
		this.gridviewGallery.setAdapter ( adapter );
	}

	/**
	 * Adds Action Listeners
	 */
	private void setupListeners ()
	{
		this.gridviewGallery.setOnItemClickListener ( new OnPictureClicked() );
	}
	
	/**
	 * @return List of image files in applications' picture directory.
	 */
	private List< File > getPictureFiles ()
	{
		List<File> pictureFilesList = new ArrayList<File>();
		File picturesDir = new File ( FileSystem.getPicturesDirectory ( this ) );
		if ( picturesDir.exists () && picturesDir.isDirectory () )
		{

			File [] pictureFiles = picturesDir.getAbsoluteFile ().listFiles ();

			if ( pictureFiles != null ) 
				pictureFilesList =  Arrays.asList ( pictureFiles );
		}
		
		return pictureFilesList;
	}
	
	/**
	 * - Callback when a picture is clicked in the listview.
	 * - Photo name is sent to full screen activity, which shows the full image.
	 * 
	 * @author Waqqas
	 *
	 */
	class OnPictureClicked implements AdapterView.OnItemClickListener
	{

		@Override
		public void onItemClick ( AdapterView< ? > parent, View view, int position, long id )
		{
			File selectedFile = GalleryActivity.this.adapter.getItem ( position );
			Intent fullScreenImageIntent = new Intent(GalleryActivity.this, FullScreenImageActivity.class);
			fullScreenImageIntent.putExtra ( FullScreenImageActivity.EXTRAS_PHOTO_NAME, selectedFile.getName () );
			GalleryActivity.this.startActivity ( fullScreenImageIntent );
			
		}
		
	}
	
	
}
