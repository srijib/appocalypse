package com.wks.calorieapp.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.wks.calorieapp.R;
import com.wks.calorieapp.utils.ApplicationEnvironment;
import com.wks.calorieapp.views.CameraPreview;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity
{
	private static final String TAG = CameraActivity.class.getCanonicalName ();

	private static final String IMAGE_EXTENSION = ".jpg";
	private static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
	private static final SimpleDateFormat timestampFormatter = new SimpleDateFormat (
			TIMESTAMP_FORMAT );

	private Button buttonOk;
	private Button buttonCancel;
	private Button buttonTakePicture;
	private CameraPreview preview;
	
	private String fileName = "";

	private FrameLayout framelayoutCameraPreview;

	@Override
	public void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.activity_camera );
		setupView ();
		setupListeners ();
	}

	private void setupView ()
	{
		buttonTakePicture = ( Button ) findViewById ( R.id.camera_button_take_picture );
		buttonOk = ( Button ) findViewById ( R.id.camera_button_ok );
		buttonCancel = ( Button ) findViewById ( R.id.camera_button_cancel );

		framelayoutCameraPreview = ( FrameLayout ) findViewById ( R.id.camera_framelayout_preview );

		preview = new CameraPreview ( this );
		framelayoutCameraPreview.addView ( preview );

	}

	private void setupListeners ()
	{
		buttonOk.setOnClickListener ( new OnButtonOkClicked() );
		buttonCancel.setOnClickListener ( new OnButtonCancelClicked() );
		buttonTakePicture.setOnClickListener ( new OnButtonTakePictureClicked() );
	}

	@Override
	protected void onPause ()
	{
		super.onPause ();
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{
		getMenuInflater ().inflate ( R.menu.activity_camera, menu );
		return true;
	}
	
	public void setFileName ( String fileName )
	{
		this.fileName = fileName;
	}
	
	public String getFileName ()
	{
		return fileName;
	}

	private String generateFileName ()
	{
		// get IMEI number
		TelephonyManager telephonyManager = ( TelephonyManager ) this
				.getSystemService ( Context.TELEPHONY_SERVICE );
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

	private void setControlButtonsVisible ( boolean visible )
	{
		buttonOk.setVisibility ( visible ? View.VISIBLE : View.GONE );
		buttonCancel.setVisibility ( visible ? View.VISIBLE : View.GONE );
		buttonTakePicture.setVisibility ( visible ? View.GONE : View.VISIBLE );
	}

	class OnButtonOkClicked implements View.OnClickListener
	{

		@Override
		public void onClick ( View v )
		{
			
			Intent getCaloriesIntent = new Intent(CameraActivity.this,GetCaloriesActivity.class);
			getCaloriesIntent.putExtra ( "image", getFileName() );
			CameraActivity.this.startActivity ( getCaloriesIntent );
		}

	}

	class OnButtonTakePictureClicked implements View.OnClickListener
	{

		@Override
		public void onClick ( View v )
		{
			Camera camera = preview.getCamera ();

			if ( camera != null )
			{
				camera.takePicture ( null, null, new OnPictureTaken () );
				setControlButtonsVisible ( true );
			}

		}

	}

	class OnButtonCancelClicked implements View.OnClickListener
	{

		@Override
		public void onClick ( View v )
		{
			setControlButtonsVisible(false);
			preview.refreshPreview ();
		}

	}

	class OnPictureTaken implements PictureCallback
	{

		public void onPictureTaken ( byte [] data, Camera camera )
		{
			// check that SD card exists
			if ( !Environment.getExternalStorageState ().equals (
					Environment.MEDIA_MOUNTED ) )
			{
				Toast.makeText ( CameraActivity.this,
						R.string.camera_error_media_not_ready,
						Toast.LENGTH_LONG ).show ();
				return;
			}

			setFileName( generateFileName () );

			String picturesDir = ApplicationEnvironment
					.getPicturesDirectory ( CameraActivity.this );

			File imageFile = new File ( picturesDir + getFileName() );
			FileOutputStream fos = null;

			try
			{
				fos = new FileOutputStream ( imageFile );
				fos.write ( data );
				Toast.makeText (
						CameraActivity.this,
						R.string.camera_image_saved + ": "
								+ imageFile.getPath (), Toast.LENGTH_LONG )
						.show ();
			}
			catch ( FileNotFoundException e )
			{
				Log.e ( TAG, "File not found." + e );
				e.printStackTrace ();
			}
			catch ( IOException e )
			{
				Toast.makeText ( CameraActivity.this,
						R.string.camera_error_file_not_saved, Toast.LENGTH_LONG )
						.show ();
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