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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
	//private RelativeLayout layout;
	private FrameLayout framelayoutCameraPreview;
	private ImageButton buttonTakePicture;

	//private LinearLayout layoutProgress;
	//private TextView textProgress;

	private CameraPreview preview;

	//private View viewResults;
	//private ListView listviewResults;
	//private RelativeLayout.LayoutParams paramsListViewResults;

	// MEMBERS
	/*
	public enum ViewMode
	{
		IDLE, LOADING, RESULTS
	};*/

	
	//private ViewMode viewMode = ViewMode.IDLE;
	//private String photoName = "";
	//private IdentifyResultsModel identifyResultsModel;
	//private IdentifyResultsAdapter adapter;

	@Override
	public void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_camera );

		super.identifyResultsModel = new IdentifyResultsModel ();

		this.setupActionBar ();
		this.setupView ();
		this.setupListeners ();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu ( Menu menu ) { MenuInflater
	 * inflater = this.getMenuInflater (); inflater.inflate (
	 * R.menu.activity_camera, menu ); return true; }
	 * 
	 * //Make OK and Try again menu items visible after the picture has been
	 * taken.
	 * 
	 * @Override public boolean onPrepareOptionsMenu ( Menu menu ) {
	 * menu.findItem ( R.id.camera_menu_ok ).setVisible ( this.viewMode.equals (
	 * ViewMode.POSTVIEW )); menu.findItem ( R.id.camera_menu_cancel
	 * ).setVisible ( this.viewMode.equals ( ViewMode.POSTVIEW ) ); return true;
	 * }
	 */

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
		actionBar.setBackgroundDrawable ( backgroundActionBar );

		actionBar.setDisplayHomeAsUpEnabled ( true );
	}

	/**
	 * - Initialisez UI components - Sets default View mode to preview.
	 */
	private void setupView ()
	{
		this.layout = ( RelativeLayout ) this.findViewById ( R.id.camera_layout );
		this.framelayoutCameraPreview = ( FrameLayout ) this.findViewById ( R.id.camera_framelayout_preview );
		this.buttonTakePicture = ( ImageButton ) this.findViewById ( R.id.camera_button_take_picture );
		
		super.layoutProgress = ( LinearLayout ) this.findViewById ( R.id.camera_layout_progress );
		super.textProgress = ( TextView ) this.findViewById ( R.id.camera_text_progress );

		/*
		LayoutInflater inflater = LayoutInflater.from ( this );
		this.viewResults = inflater.inflate ( R.layout.identify_list_results, null );
		this.listviewResults = ( ListView ) this.viewResults.findViewById ( R.id.identify_listview_results );

		this.identifyResultsAdpater = new IdentifyResultsAdapter ( this, this.identifyResultsModel );
		this.listviewResults.setAdapter ( this.identifyResultsAdpater );

		this.params = new RelativeLayout.LayoutParams ( DisplayUtils.getScreenWidth ( this ) / 3, LayoutParams.WRAP_CONTENT );
		this.params.addRule ( RelativeLayout.CENTER_IN_PARENT );
		*/
		this.preview = new CameraPreview ( this );
		this.framelayoutCameraPreview.addView ( preview );

		this.setViewMode ( ViewMode.IDLE );
	}

	/**
	 * Adds listeners to UI components.
	 */
	private void setupListeners ()
	{
		this.buttonTakePicture.setOnClickListener ( new OnButtonTakePictureClicked () );
		//this.listviewResults.setOnItemClickListener ( new OnListViewResultsItemClicked () );
	}

	/**
	 * Switches between view modes and performs accompanying UI changes.
	 * 
	 * @param mode
	 *            View Mode {@link: ViewMode}
	 */
	protected void setViewMode ( ViewMode mode )
	{
		super.setViewMode ( mode );
		
		if(mode==ViewMode.IDLE)
		{
			if(this.preview != null)
				this.preview.refreshPreview ();
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

	/*
	private void showResults ()
	{
		this.setViewMode ( ViewMode.RESULTS );
		AnimatorSet set = ( AnimatorSet ) AnimatorInflater.loadAnimator ( this, R.animator.fade );
		set.setTarget ( this.viewResults );
		this.layout.addView ( this.viewResults, this.params );
		set.start ();
	}
	 */
	/*
	class OnListViewResultsItemClicked implements AdapterView.OnItemClickListener
	{
		@Override
		public void onItemClick ( AdapterView< ? > parent, View view, int position, long id )
		{
			Toast.makeText ( CameraActivity.this, "to be implemented", Toast.LENGTH_LONG ).show ();
		}
	}
*/
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
				CameraActivity.this.setViewMode ( ViewMode.LOADING );
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

			if ( preview != null ) preview.refreshPreview ();
			CameraActivity.this.savePhoto ( data );
			new IdentifyTask (CameraActivity.this).execute ( CameraActivity.this.photoName );
		}

	}

	// Identify Task Invoker
	
	/*
	 * @Override
	public void onPreExecute ()
	{
		// TODO Auto-generated method stub

	}

	 
	@Override
	public void onPostExecute ( Map< String, List< NutritionInfo >> response )
	{
		// this.progressDialog.dismiss ();
		if ( response != null )
		{
			// a really stupid way of checking that the result is not empty.
			for ( Entry< String, List< NutritionInfo >> entry : response.entrySet () )
			{
				// there should be atleast one list.
				if ( !entry.getValue ().isEmpty () )
				{
					this.identifyResultsModel.setPossibleMatchesList ( new ArrayList< String > ( response.keySet () ) );
					this.showResults ();
					return;
				}
			}

		}

		// If no response, start searchActivity so that the user can manually
		// search for the item.

		Toast.makeText ( this, "No Matches Found", Toast.LENGTH_LONG ).show ();
		Intent searchFoodIntent = new Intent ( this, SearchActivity.class );
		searchFoodIntent.putExtra ( SearchActivity.EXTRAS_PHOTO_NAME, photoName );
		startActivity ( searchFoodIntent );

	}

	@Override
	public void onCancelled ()
	{
		this.setViewMode ( ViewMode.IDLE );
	}

	@Override
	public void onProgressUpdate ( String [] values )
	{
		this.textProgress.setText ( values[0] );
	}
*/
}
