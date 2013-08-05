package com.wks.calorieapp.activities;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * Displays Camera Stream
 * 
 * @author Waqqas
 * 
 */
public class CameraPreview extends SurfaceView implements Callback
{
	//DEBUGGING TAG
	private static final String TAG = CameraPreview.class.getCanonicalName ();

	//CONSTANTS
	private static final int JPEG_QUALITY = 100;
	//The camera feed will be displayed in portrait mode:
	private static final int DISPLAY_ORIENTATION = 90;

	//MEMBERS
	private Camera camera = null;
	private SurfaceHolder holder;

	public CameraPreview ( Context context )
	{
		super ( context );
		
		this.holder = this.getHolder ();
		this.holder.addCallback ( this );

	}

	@Override
	public void surfaceCreated ( SurfaceHolder holder )
	{
		try
		{
			this.camera = Camera.open ( getCameraId ( CameraInfo.CAMERA_FACING_BACK ) );
			this.camera.setPreviewDisplay ( holder );
		}
		catch ( IOException e )
		{
			Log.e ( TAG, "" + e.getMessage () );
		}
	}

	@Override
	public void surfaceChanged ( SurfaceHolder holder, int format, int width,
			int height )
	{
		try
		{
			if ( camera == null ) return;

			Camera.Parameters parameters = camera.getParameters ();
			parameters.setPreviewSize ( width, height );

			//get possible picture sizes
			List<Size> sizes = parameters.getSupportedPictureSizes ();
			
			//sort sizes in ascending order.
			Collections.sort ( sizes, new Comparator<Size>(){

				@Override
				public int compare ( Size lhs, Size rhs )
				{
					return lhs.width - rhs.width;
				}
				
			} );
			
			//choose smallest picture size.
			Size pictureSize = sizes.get ( 0 );

			parameters.setPictureSize ( pictureSize.width, pictureSize.height );
			parameters.setJpegQuality ( JPEG_QUALITY );
			camera.setParameters ( parameters );
			camera.setDisplayOrientation ( DISPLAY_ORIENTATION );
			camera.setPreviewDisplay ( holder );
			camera.startPreview ();
		}
		catch ( IOException e )
		{
			Log.e ( TAG, "" + e.getMessage () );
		}

	}

	@Override
	public void surfaceDestroyed ( SurfaceHolder holder )
	{
		if ( camera != null )
		{
			this.camera.setPreviewCallback ( null );
			this.camera.stopPreview ();
			this.camera.release ();
			this.camera = null;
		}

	}

	/**
	 * 
	 * @return device camera object
	 */
	public Camera getCamera ()
	{
		return this.camera;
	}

	/**Returns the id of the specified camera direction (FORWARD FACING, BACKFACING)
	 * 
	 * @param direction
	 * @return
	 */
	private static int getCameraId ( int direction )
	{
		int numCameras = Camera.getNumberOfCameras ();
		for ( int i = 0 ; i < numCameras ; i++ )
		{
			CameraInfo info = new CameraInfo ();
			Camera.getCameraInfo ( i, info );
			if ( info.facing == direction ) { return i; }
		}
		return 0;
	}

	public void refreshPreview ()
	{
		if ( camera != null )
		{
			camera.startPreview ();
		}
	}

}
