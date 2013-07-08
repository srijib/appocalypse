package com.wks.calorieapp.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.wks.calorieapp.R;
import com.wks.calorieapp.utils.FileSystem;

import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class CameraActivity extends Activity
{
	private static final String TAG = CameraActivity.class.getCanonicalName ();

	private static final String IMAGE_EXTENSION = ".jpg";
	private static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
	private static final SimpleDateFormat timestampFormatter = new SimpleDateFormat ( TIMESTAMP_FORMAT );

	private ImageButton buttonTakePicture;
	private CameraPreview preview;

	private enum ViewMode{PREVIEW,POSTVIEW};
	
	private ViewMode viewMode = ViewMode.PREVIEW;
	private String fileName = "";
	boolean pictureBeingPreviewed = false;

	private FrameLayout framelayoutCameraPreview;

	@Override
	public void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_camera );
		
		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
	}

	
	
	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{
		MenuInflater inflater = this.getMenuInflater ();
		inflater.inflate ( R.menu.activity_camera, menu );
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu ( Menu menu )
	{
		menu.findItem ( R.id.camera_menu_ok ).setVisible ( this.viewMode.equals ( ViewMode.POSTVIEW ));
		menu.findItem ( R.id.camera_menu_cancel ).setVisible ( this.viewMode.equals ( ViewMode.POSTVIEW ) );
		return true;
	}
	
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent intent = new Intent ( CameraActivity.this, HomeActivity.class );
			intent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			this.startActivity ( intent );
			return true;

		case R.id.camera_menu_ok:
			this.setViewMode(ViewMode.PREVIEW);
			Intent getCaloriesIntent = new Intent ( this, GetCaloriesActivity.class );
			getCaloriesIntent.putExtra ( GetCaloriesActivity.KEY_IMAGE, this.fileName );
			this.startActivity ( getCaloriesIntent );
			return true;
		
		case R.id.camera_menu_cancel:
			if(this.preview != null)
			{
				this.setViewMode(ViewMode.PREVIEW);
				this.preview.refreshPreview ();
			}
			return true;
			
		default:
			return super.onOptionsItemSelected ( item );

		}
	}
	

	private void setupActionBar ()
	{
		ActionBar actionBar = getActionBar ();

		Drawable d = getResources ().getDrawable ( R.drawable.bg_actionbar );
		actionBar.setBackgroundDrawable ( d );

		actionBar.setDisplayHomeAsUpEnabled ( true );
	}

	private void setupView ()
	{

		buttonTakePicture = ( ImageButton ) findViewById ( R.id.camera_button_take_picture );

		framelayoutCameraPreview = ( FrameLayout ) findViewById ( R.id.camera_framelayout_preview );

		preview = new CameraPreview ( this );
		framelayoutCameraPreview.addView ( preview );

		this.setViewMode(ViewMode.PREVIEW);
	}

	private void setupListeners ()
	{
		buttonTakePicture.setOnClickListener ( new OnButtonTakePictureClicked () );
	}

	private void setViewMode(ViewMode mode)
	{
		this.viewMode = mode;
		this.invalidateOptionsMenu ();
		this.buttonTakePicture.setVisibility ( mode==ViewMode.PREVIEW? View.VISIBLE : View.GONE );
		
	}
	
	private String generateFileName ()
	{
		// get IMEI number
		TelephonyManager telephonyManager = ( TelephonyManager ) this.getSystemService ( Context.TELEPHONY_SERVICE );
		String imei = telephonyManager.getDeviceId ();

		if ( imei == null )
		{
			int random = ( int ) ( Math.random () * 1000000 );
			imei = String.valueOf ( random );
		}

		// get timestamp
		Calendar cal = Calendar.getInstance ();
		String timestamp = timestampFormatter.format ( cal.getTime () );

		return imei + timestamp + IMAGE_EXTENSION;
	}


	class OnButtonTakePictureClicked implements View.OnClickListener
	{

		@Override
		public void onClick ( View v )
		{
			Camera camera = preview.getCamera ();

			if ( camera != null )
			{
				CameraActivity.this.setViewMode(ViewMode.POSTVIEW);
				camera.takePicture ( null, null, new OnPictureTaken () );

			}

		}

	}


	class OnPictureTaken implements PictureCallback
	{

		public void onPictureTaken ( byte [] data, Camera camera )
		{
			// check that SD card exists
			if ( !Environment.getExternalStorageState ().equals ( Environment.MEDIA_MOUNTED ) )
			{
				Toast.makeText ( CameraActivity.this, R.string.camera_error_media_not_ready, Toast.LENGTH_LONG ).show ();
				return;
			}

			CameraActivity.this.fileName =  generateFileName ();
			/*
			Uri imagesDirUri = Uri.parse ( "file:///mnt/sdcard/Pictures/CalorieApp/"+getFileName());
			
			ContentValues values = new ContentValues();
			values.put ( Images.Media.TITLE, getFileName() );
			values.put ( Images.Media.MIME_TYPE, "image/jpeg" );
			Uri imageUri = CameraActivity.this.getContentResolver ().insert ( imagesDirUri, values );
			
			OutputStream out= null;
			try{
				
				out = CameraActivity.this.getContentResolver ().openOutputStream ( imageUri );
				Bitmap bitmap = BitmapFactory.decodeByteArray ( data, 0, data.length );
				bitmap.compress ( Bitmap.CompressFormat.JPEG, 70, out );
			}catch(IOException e)
			{
				e.printStackTrace ();
			}finally{
				try
				{
					if(out != null)
					out.close ();
				}
				catch ( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			*/
			
			String picturesDir = FileSystem.getPicturesDirectory ( CameraActivity.this );


			File imageFile = new File ( picturesDir + CameraActivity.this.fileName );
			FileOutputStream fos = null;

			try
			{
				fos = new FileOutputStream ( imageFile );
				fos.write ( data );
				Toast.makeText ( CameraActivity.this, R.string.camera_image_saved + ": " + imageFile.getPath (), Toast.LENGTH_LONG ).show ();
			}
			catch ( FileNotFoundException e )
			{
				Log.e ( TAG, "File not found." + e );
				e.printStackTrace ();
			}
			catch ( IOException e )
			{
				Toast.makeText ( CameraActivity.this, R.string.camera_error_file_not_saved, Toast.LENGTH_LONG ).show ();
				Log.e ( TAG, "Failure to save image." + e );
				e.printStackTrace ();
			}
			finally
			{
				if ( fos != null )
				{
					try
					{
						fos.close ();
					}
					catch ( IOException e )
					{
						Log.e ( TAG, "Failure closing FileOutputStream: " + e );
						e.printStackTrace ();
					}
				}
			}
			
			
		}

	}

}
