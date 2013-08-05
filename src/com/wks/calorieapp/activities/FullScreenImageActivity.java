package com.wks.calorieapp.activities;

import java.io.File;
import com.wks.android.utils.FileSystem;
import com.wks.calorieapp.R;
import com.wks.calorieapp.models.IdentifyResultsModel;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class FullScreenImageActivity extends IdentifyTaskActivity
{
	public static final String EXTRAS_PHOTO_NAME = "image";

	private ImageView imageView;
	private ShareActionProvider shareActionProvider;


	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_full_screen_image );

		Bundle extras = this.getIntent ().getExtras ();
		if ( extras != null && extras.getString ( EXTRAS_PHOTO_NAME ) != null )
		{
			super.photoName = extras.getString ( EXTRAS_PHOTO_NAME );
		}else
		{
			Toast.makeText ( this, R.string.full_screen_image_not_found, Toast.LENGTH_LONG ).show ();
			this.finish ();
		}

		super.identifyResultsModel = new IdentifyResultsModel ();

		this.setupActionBar ();
		this.setupView ();
		super.setupView ();
		this.loadPhoto ();
	}
	
	@Override
	protected void onPause ()
	{
		super.onPause ();
		this.finish ();
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{
		MenuInflater menuInflater = this.getMenuInflater ();
		menuInflater.inflate ( R.menu.activity_full_screen_image, menu );

		this.shareActionProvider = ( ShareActionProvider ) menu.findItem ( R.id.full_screen_image_menu_share ).getActionProvider ();
		
		if ( this.shareActionProvider != null )
		{
			Intent shareIntent = new Intent ();
			shareIntent.setAction ( Intent.ACTION_SEND );
			shareIntent.putExtra ( Intent.EXTRA_STREAM, Uri.fromFile ( new File ( FileSystem.getPicturesDirectory ( this ) + this.photoName ) ) );
			shareIntent.setType ( "image/jpeg" );

			this.shareActionProvider.setShareIntent ( shareIntent );
		}

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu ( Menu menu )
	{
		menu.findItem ( R.id.full_screen_image_menu_get_calories ).setVisible ( this.viewMode == ViewMode.IDLE );
		menu.findItem ( R.id.full_screen_image_menu_share ).setVisible ( this.viewMode == ViewMode.IDLE );
		menu.findItem ( R.id.full_screen_image_menu_delete ).setVisible ( this.viewMode == ViewMode.IDLE );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent galleryIntent = new Intent ( this, GalleryActivity.class );
			galleryIntent.addFlags ( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
			this.startActivity ( galleryIntent );
			return true;

		case R.id.full_screen_image_menu_get_calories:
			this.doIdentifyTask ();
			return true;

		case R.id.full_screen_image_menu_delete:
			boolean deleted = deletePhoto();
			String toast = this.getResources ().getString ( deleted? R.string.full_screen_image_deleted_success: R.string.full_screen_image_deleted_fail );
			Toast.makeText ( this, toast, Toast.LENGTH_LONG ).show ();
			this.finish ();
			return true;
			
		default:
			return super.onOptionsItemSelected ( item );
		}
	}

	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		Drawable backgroundActionBar = getResources ().getDrawable ( R.drawable.bg_actionbar );
		Drawable iconActionBar = getResources().getDrawable ( R.drawable.ic_actionbar );
		
		actionBar.setBackgroundDrawable ( backgroundActionBar );
		actionBar.setIcon ( iconActionBar );

		actionBar.setDisplayHomeAsUpEnabled ( true );

	}

	protected void setupView ()
	{
		super.layout = ( RelativeLayout ) this.findViewById ( R.id.full_screen_layout );
		this.imageView = ( ImageView ) this.findViewById ( R.id.full_screen_imageview );
	}

	protected void setViewMode ( ViewMode mode )
	{
		super.setViewMode (mode);
		this.invalidateOptionsMenu ();
	}
	
	private boolean deletePhoto()
	{
		boolean deleted = false;
		if(this.photoName != null && !this.photoName.isEmpty ())
		{
			File photo = new File(FileSystem.getPicturesDirectory ( this )+this.photoName);
			if(photo.exists ())
			{
				deleted =  photo.delete ();
			}
		}
		return deleted;
	}
	
	private void loadPhoto ()
	{
		String [] params =
		{
			super.photoName
		};
		new LoadPhotoTask ().execute ( params );
	}

	class LoadPhotoTask extends AsyncTask< String, String, Bitmap >
	{
		String imageUri;
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute ()
		{
			this.progressDialog = new ProgressDialog ( FullScreenImageActivity.this );
			this.progressDialog.setCancelable ( false );
			this.progressDialog.setIndeterminate ( true );
			this.progressDialog
					.setTitle ( FullScreenImageActivity.this.getResources ().getString ( R.string.full_screen_image_progress_dialog_title ) );
			this.progressDialog.show ();
		}

		@Override
		protected Bitmap doInBackground ( String... params )
		{
			this.imageUri = FileSystem.getPicturesDirectory ( FullScreenImageActivity.this ) + params[0];

			this.progressDialog.setMessage ( FullScreenImageActivity.this.getResources ().getString (
					R.string.full_screen_image_progress_dialog_loading ) );

			return BitmapFactory.decodeFile ( imageUri );

		}

		@Override
		protected void onPostExecute ( Bitmap result )
		{
			this.progressDialog.dismiss ();
			if ( result != null )
			{
				//result = BitmapUtils.rotate ( result, 90 );
				FullScreenImageActivity.this.imageView.setImageBitmap ( result );
			}else
			{
				Toast.makeText ( FullScreenImageActivity.this,
						FullScreenImageActivity.this.getResources ().getString ( R.string.full_screen_image_could_not_load ), Toast.LENGTH_LONG )
						.show ();
			}
		}

		@Override
		protected void onCancelled ()
		{
			this.progressDialog.dismiss ();
		}
	}

	// IdentifyTaskInvoker
	
	private void doIdentifyTask ()
	{
		super.setViewMode ( ViewMode.LOADING );
		identifyTask = new IdentifyTask ( this );
		identifyTask.execute ( this.photoName );
	}
}
