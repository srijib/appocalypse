package com.wks.calorieapp.activities;


import java.io.File;

import com.wks.calorieapp.R;
import com.wks.calorieapp.utils.AndroidBitmap;
import com.wks.calorieapp.utils.FileSystem;

import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class FullScreenImageActivity extends Activity
{
	public static final String KEY_IMAGE = "image";
	
	ShareActionProvider shareActionProvider;
	String imageName;
	ImageView imageView;
	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_full_screen_image );

		this.init();
		this.setupActionBar ();
		this.setupView ();
		this.loadImage();
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{
		MenuInflater menuInflater = this.getMenuInflater ();
		menuInflater.inflate ( R.menu.activity_full_screen_image, menu );
		
		this.shareActionProvider = (ShareActionProvider) menu.findItem ( R.id.full_screen_image_menu_share ).getActionProvider ();
		
		if(this.shareActionProvider != null)
		{
			Intent shareIntent = new Intent();
			shareIntent.setAction ( Intent.ACTION_SEND );
			shareIntent.putExtra ( Intent.EXTRA_STREAM, Uri.fromFile ( new File(FileSystem.getPicturesDirectory ( this )+this.imageName) ) );
			shareIntent.setType ( "image/jpeg" );
			
			this.shareActionProvider.setShareIntent ( shareIntent );
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent galleryIntent = new Intent(this,GalleryActivity.class);
			galleryIntent.addFlags ( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
			this.startActivity ( galleryIntent );
			return true;
			
		case R.id.full_screen_image_menu_get_calories:
			Intent getCaloriesIntent = new Intent ( this, GetCaloriesActivity.class );
			getCaloriesIntent.putExtra ( GetCaloriesActivity.KEY_IMAGE, this.imageName);
			this.startActivity ( getCaloriesIntent );
			return true;
			
		default:
			return super.onOptionsItemSelected ( item );
		}
	}

	
	
	private void init()
	{
		Bundle extras = this.getIntent ().getExtras ();
		if(extras != null && extras.getString ( KEY_IMAGE ) != null)
		{
			this.imageName = extras.getString ( KEY_IMAGE);
		}else
		{
			Toast.makeText(this,R.string.full_screen_image_not_found,Toast.LENGTH_LONG).show();
			this.finish ();	
		}
	}
	
	private void setupActionBar ()
	{
		ActionBar actionBar = this.getActionBar ();

		Drawable d = this.getResources ().getDrawable ( R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( d );

		actionBar.setDisplayHomeAsUpEnabled ( true );
		
		//String fileName = imageUri.substring (imageUri.lastIndexOf ( File.pathSeparator ));
		//actionBar.setTitle ( fileName );
	}
	
	private void setupView()
	{
		this.imageView = (ImageView) this.findViewById ( R.id.full_screen_imageview );
	}
	
	private void loadImage()
	{
		String[] params = {this.imageName};
		new LoadPhotoTask().execute(params);
	}
	
	class LoadPhotoTask extends AsyncTask<String,String,Bitmap>
	{
		String imageUri;
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute ()
		{
			this.progressDialog = new ProgressDialog(FullScreenImageActivity.this);
			this.progressDialog.setCancelable ( false );
			this.progressDialog.setIndeterminate ( true );
			this.progressDialog.setTitle ( FullScreenImageActivity.this.getResources ().getString ( R.string.full_screen_image_progress_dialog_title ) );
			this.progressDialog.show ();
		}
		
		@Override
		protected Bitmap doInBackground ( String... params )
		{
			this.imageUri = FileSystem.getPicturesDirectory ( FullScreenImageActivity.this )+params[0];
			
			this.progressDialog.setMessage ( FullScreenImageActivity.this.getResources ().getString ( R.string.full_screen_image_progress_dialog_loading ) );
			
			return BitmapFactory.decodeFile ( imageUri );
			
		}
		
		@Override
		protected void onPostExecute ( Bitmap result )
		{
			this.progressDialog.dismiss ();
			if(result != null)
			{
				result = AndroidBitmap.rotate ( result, 90 );
				FullScreenImageActivity.this.imageView.setImageBitmap ( result );
			}else
			{
				Toast.makeText ( FullScreenImageActivity.this, FullScreenImageActivity.this.getResources ().getString ( R.string.full_screen_image_could_not_load ), Toast.LENGTH_LONG ).show();
			}
		}
	
		@Override
		protected void onCancelled ()
		{
			this.progressDialog.dismiss ();
		}
	}
}
