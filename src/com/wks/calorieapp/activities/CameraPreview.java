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
import android.widget.Toast;

/**
 * handles all preview aspects of the camera stream.
 * 
 * @author Waqqas
 * 
 */
public class CameraPreview extends SurfaceView implements Callback
{
	private static final String TAG = CameraPreview.class.getCanonicalName ();

	private static final int JPEG_QUALITY = 90;

	private static final int PICTURE_WIDTH = 300;

	private Context context;// useless
	private Camera camera = null;
	private SurfaceHolder holder;

	public CameraPreview ( Context context )
	{
		super ( context );
		this.context = context;
		this.holder = this.getHolder ();
		this.holder.addCallback ( this );

	}

	@Override
	public void surfaceCreated ( SurfaceHolder holder )
	{
		try
		{
			this.camera = Camera
					.open ( getCameraId ( CameraInfo.CAMERA_FACING_BACK ) );
			this.camera.setPreviewDisplay ( holder );
			if ( camera == null )
				Toast.makeText ( context, "cam null", Toast.LENGTH_LONG )
						.show ();
		}
		catch ( IOException e )
		{
			// TODO
			Log.e ( TAG, "" + e );
		}
	}

	@Override
	public void surfaceChanged ( SurfaceHolder holder, int format, int width,
			int height )
	{
		if ( holder.getSurface () == null )
		{
			Log.e ( TAG, "Camera surface holder is null" );
			return;
		}

		try
		{
			if ( camera == null ) return;

			Camera.Parameters parameters = camera.getParameters ();
			// List< Size > sizes = parameters.getSupportedPreviewSizes ();
			// Size previewSize = sizes.get ( sizes.size () - 1 );
			parameters.setPreviewSize ( width, height );

			List<Size> sizes = parameters.getSupportedPictureSizes ();
			Collections.sort ( sizes, new Comparator<Size>(){

				@Override
				public int compare ( Size lhs, Size rhs )
				{
					return lhs.width - rhs.width;
				}
				
			} );
			Size pictureSize = sizes.get ( 0 );

			parameters.setPictureSize ( pictureSize.width, pictureSize.height );
			parameters.setJpegQuality ( JPEG_QUALITY );
			camera.setParameters ( parameters );
			camera.setDisplayOrientation ( 90 );
			camera.setPreviewDisplay ( holder );
			camera.startPreview ();
		}
		catch ( IOException e )
		{
			// TODO
			Log.e ( TAG, "" + e );
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

	public Camera getCamera ()
	{
		return this.camera;
	}

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
