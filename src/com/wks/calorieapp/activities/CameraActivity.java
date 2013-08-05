package com.wks.calorieapp.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.wks.android.utils.BitmapUtils;
import com.wks.android.utils.FileSystem;
import com.wks.calorieapp.R;
import com.wks.calorieapp.models.IdentifyResultsModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CameraActivity extends IdentifyTaskActivity
{
	// DEBUGGING TAG
	private static final String TAG = CameraActivity.class.getCanonicalName ();

	// CONSTANTS
	private static final String IMAGE_EXTENSION = ".jpg";
	private static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
	private static final SimpleDateFormat timestampFormatter = new SimpleDateFormat ( TIMESTAMP_FORMAT );

	// UI ELEMENTS
	private FrameLayout framelayoutCameraPreview;
	private ImageButton buttonTakePicture;

	private CameraPreview preview;

	@Override
	public void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_camera );

		super.identifyResultsModel = new IdentifyResultsModel ();

		this.setupActionBar ();
		this.setupView ();

		/*
		 * The super setView method creates an overlay of list results. In order
		 * to create the overlay, the child layout must be created first. Hence,
		 * the setupView() of the child activity is called before the seupView()
		 * of the parent activity.
		 */

		super.setupView ();
		this.setupListeners ();

		this.setViewMode ( ViewMode.IDLE );
	}

	public boolean onOptionsItemSelected ( MenuItem item )
	{
		switch ( item.getItemId () )
		{
		case android.R.id.home:
			Intent intent = new Intent ( CameraActivity.this, MainMenuActivity.class );
			intent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			this.startActivity ( intent );
			return true;

		case R.id.camera_menu_ok:
			this.setViewMode ( ViewMode.IDLE );
			Intent getCaloriesIntent = new Intent ( this, ResultsActivity.class );
			getCaloriesIntent.putExtra ( ResultsActivity.EXTRAS_PHOTO_NAME, this.photoName );
			this.startActivity ( getCaloriesIntent );
			return true;

		case R.id.camera_menu_cancel:
			this.setViewMode ( ViewMode.IDLE );
			return true;

		default:
			return super.onOptionsItemSelected ( item );

		}
	}

	/**
	 * - Applies background to action bar. - Enables Home Menu Item
	 */
	private void setupActionBar ()
	{
		ActionBar actionBar = getActionBar ();

		Drawable backgroundActionBar = getResources ().getDrawable ( R.drawable.bg_actionbar );
		Drawable iconActionBar = getResources ().getDrawable ( R.drawable.ic_actionbar );

		actionBar.setBackgroundDrawable ( backgroundActionBar );
		actionBar.setIcon ( iconActionBar );

		actionBar.setDisplayHomeAsUpEnabled ( true );
	}

	/**
	 * - Initialisez UI components - Sets default View mode to preview.
	 */
	protected void setupView ()
	{
		super.layout = ( RelativeLayout ) this.findViewById ( R.id.camera_layout );
		this.framelayoutCameraPreview = ( FrameLayout ) this.findViewById ( R.id.camera_framelayout_preview );
		this.buttonTakePicture = ( ImageButton ) this.findViewById ( R.id.camera_button_take_picture );

		this.preview = new CameraPreview ( this );
		this.framelayoutCameraPreview.addView ( preview );
	}

	/**
	 * Adds listeners to UI components.
	 */
	private void setupListeners ()
	{
		this.buttonTakePicture.setOnClickListener ( new OnButtonTakePictureClicked () );
	}

	/**
	 * Switches between view modes and performs accompanying UI changes.
	 * 
	 * @param mode
	 *            viewMode
	 * 
	 */
	protected void setViewMode ( ViewMode mode )
	{
		super.setViewMode ( mode );

		if ( mode == ViewMode.IDLE )
		{
			if ( this.preview != null ) this.preview.refreshPreview ();
		}

		this.invalidateOptionsMenu ();
		this.buttonTakePicture.setVisibility ( mode == ViewMode.IDLE ? View.VISIBLE : View.GONE );

	}

	/**
	 * Creates unique filename for photo: IMEI number + timestamp.
	 * 
	 * @return photo name
	 */
	private String generateFileName ()
	{
		// get IMEI number
		TelephonyManager telephonyManager = ( TelephonyManager ) this.getSystemService ( Context.TELEPHONY_SERVICE );
		String imei = telephonyManager.getDeviceId ();

		// If IMEI number not found, create random 6 digit number.
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

	private void savePhoto ( byte [] data )
	{
		super.photoName = generateFileName ();

		// Save photo in application's pictures directory.
		String picturesDir = FileSystem.getPicturesDirectory ( CameraActivity.this );

		File imageFile = new File ( picturesDir + CameraActivity.this.photoName );
		FileOutputStream fos = null;

		try
		{
			fos = new FileOutputStream ( imageFile );
			Bitmap bitmap = BitmapFactory.decodeByteArray ( data, 0, data.length );

			/**
			 * Version 1.0: The application takes photos in portrait mode while
			 * the Android platform saves the photos in landscape mode. The
			 * bitmap must be rotated from portrait to landscape so that it
			 * complies with Android default photo orientation.
			 */
			bitmap = BitmapUtils.rotate ( bitmap, 90 );

			// convert to JPEG
			bitmap.compress ( Bitmap.CompressFormat.JPEG, 90, fos );
			Toast.makeText ( CameraActivity.this, R.string.camera_image_saved, Toast.LENGTH_LONG ).show ();
		}
		catch ( FileNotFoundException e )
		{
			Log.e ( TAG, "File not found." + e );
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

	/**
	 * OnClickListener for when the take picture button is clicked.
	 * 
	 * @author Waqqas
	 * 
	 */
	class OnButtonTakePictureClicked implements View.OnClickListener
	{

		@Override
		public void onClick ( View v )
		{
			Camera camera = preview.getCamera ();

			if ( camera != null ) 
			{
				/**
				 * Version 1.0:
				 * Photo is saved, 
				 * Identify Task is executed.
				 */
				camera.takePicture ( null, null, new OnPictureTaken () );
			}
			

		}

	}

	/**
	 * Callback when picture is taken.
	 * 
	 * @author Waqqas
	 * 
	 */
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

			CameraActivity.this.savePhoto ( data );

			CameraActivity.this.setViewMode ( ViewMode.LOADING );
			identifyTask = new IdentifyTask ( CameraActivity.this );
			identifyTask.execute ( CameraActivity.this.photoName );
		}

	}

}
