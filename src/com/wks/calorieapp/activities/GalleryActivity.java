package com.wks.calorieapp.activities;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.GalleryAdapter;
import com.wks.calorieapp.utils.FileSystem;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class GalleryActivity extends Activity
{
	private static final String TAG = GalleryActivity.class.getCanonicalName ();
	
	private GridView gridviewGallery;
	private List< File > pictureFilesList;
	private GalleryAdapter adapter;

	protected void onCreate ( Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_gallery );

		this.init ();
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
		//this.loadGallery();
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent homeIntent = new Intent ( this, HomeActivity.class );
			homeIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			this.startActivity ( homeIntent );
			return true;

		default:
			return super.onOptionsItemSelected ( item );
		}
	}

	private void init ()
	{
		this.pictureFilesList = this.getPictureFiles ();
		if(this.pictureFilesList == null)
		{
			Log.e ( TAG, "Images Directory does not exist." );
			Toast.makeText ( this, this.getResources ().getString ( R.string.gallery_toast_pictures_dir_not_found ), Toast.LENGTH_LONG ).show();
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
		this.adapter = new GalleryAdapter(this,this.pictureFilesList);
		this.gridviewGallery = ( GridView ) this.findViewById ( R.id.gallery_grid_gallery );
		this.gridviewGallery.setAdapter ( adapter );
	}

	private void setupListeners ()
	{
		this.gridviewGallery.setOnItemClickListener ( new OnPictureClicked() );
	}

	private List< File > getPictureFiles ()
	{
		File picturesDir = new File ( FileSystem.getPicturesDirectory ( this ) );
		if ( picturesDir.exists () && picturesDir.isDirectory () )
		{

			File [] pictureFiles = picturesDir.getAbsoluteFile ().listFiles ();

			if ( pictureFiles != null ) 
				return Arrays.asList ( pictureFiles );
		}
		
		return null;
	}
	
	/*private void loadGallery()
	{
		new LoadGalleryTask().execute ( );
	}*/
	
	class OnPictureClicked implements AdapterView.OnItemClickListener
	{

		@Override
		public void onItemClick ( AdapterView< ? > parent, View view, int position, long id )
		{
			File selectedFile = GalleryActivity.this.adapter.getItem ( position );
			Intent fullScreenImageIntent = new Intent(GalleryActivity.this, FullScreenImageActivity.class);
			fullScreenImageIntent.putExtra ( FullScreenImageActivity.KEY_IMAGE, selectedFile.getName () );
			GalleryActivity.this.startActivity ( fullScreenImageIntent );
			
		}
		
	}
	
	/*class LoadGalleryTask extends AsyncTask<Void,Void,Void>
	{
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute ()
		{
			progressDialog = new ProgressDialog(GalleryActivity.this);
			progressDialog.setCancelable ( false );
			progressDialog.setIndeterminate ( true );
			progressDialog.setTitle ( GalleryActivity.this.getResources ( ).getString ( R.string.gallery_progress_dialog_title ));
			progressDialog.setMessage ( GalleryActivity.this.getResources ().getString ( R.string.gallery_progress_dialog_default ) );
			progressDialog.show ();
		}

		@Override
		protected Void doInBackground ( Void... arg0 )
		{
			progressDialog.setMessage ( GalleryActivity.this.getResources ().getString ( R.string.gallery_progress_dialog_loading ) );
			//adapter is slow to load because it creates bitmap for each image
			GalleryActivity.this.adapter = new GalleryAdapter(GalleryActivity.this, GalleryActivity.this.pictureFilesList);
			GalleryActivity.this.gridviewGallery.setAdapter ( adapter );
			return null;
		}
		
		@Override
		protected void onPostExecute ( Void result )
		{
			this.progressDialog.dismiss ();
		}
	}*/
}
