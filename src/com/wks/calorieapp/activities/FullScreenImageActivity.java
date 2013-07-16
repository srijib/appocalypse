package com.wks.calorieapp.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wks.android.utils.BitmapUtils;
import com.wks.android.utils.DisplayUtils;
import com.wks.android.utils.FileSystem;
import com.wks.calorieapp.R;
import com.wks.calorieapp.adapters.IdentifyResultsAdapter;
import com.wks.calorieapp.apis.NutritionInfo;
import com.wks.calorieapp.models.IdentifyResultsModel;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

public class FullScreenImageActivity extends IdentifyTaskActivity
{
	public static final String EXTRAS_PHOTO_NAME = "image";

	/*
	 * private enum ViewMode { IDLE, LOADING, RESULTS };
	 */
	//private RelativeLayout layout;

	private ImageView imageView;
	private ShareActionProvider shareActionProvider;
	// private LinearLayout layoutProgress;;
	// private TextView textProgress;

	// private View viewResults;
	// private ListView listviewResults;
	// private RelativeLayout.LayoutParams params;

	private String photoName;

	// private IdentifyResultsAdapter identifyResultsAdapter;
	// private IdentifyResultsModel identifyResultsModel;
	// private ViewMode viewMode = ViewMode.IDLE;

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onCreate ( savedInstanceState );
		this.setContentView ( R.layout.activity_full_screen_image );

		Bundle extras = this.getIntent ().getExtras ();
		if ( extras != null && extras.getString ( EXTRAS_PHOTO_NAME ) != null )
		{
			this.photoName = extras.getString ( EXTRAS_PHOTO_NAME );
		}else
		{
			Toast.makeText ( this, R.string.full_screen_image_not_found, Toast.LENGTH_LONG ).show ();
			this.finish ();
		}

		this.identifyResultsModel = new IdentifyResultsModel ();

		this.setupActionBar ();
		this.setupView ();
		this.loadImage ();
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

		default:
			return super.onOptionsItemSelected ( item );
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
		this.layout = ( RelativeLayout ) this.findViewById ( R.id.full_screen_layout );

		this.imageView = ( ImageView ) this.findViewById ( R.id.full_screen_imageview );
		this.layoutProgress = ( LinearLayout ) this.findViewById ( R.id.full_screen_layout_progress );
		this.textProgress = ( TextView ) this.findViewById ( R.id.full_screen_text_progress );

		LayoutInflater inflater = LayoutInflater.from ( this );
		this.viewResults = inflater.inflate ( R.layout.identify_list_results, null );
		this.listviewResults = ( ListView ) this.viewResults.findViewById ( R.id.identify_listview_results );

		this.identifyResultsAdpater = new IdentifyResultsAdapter ( this, identifyResultsModel );
		this.listviewResults.setAdapter (identifyResultsAdpater);

		this.params = new RelativeLayout.LayoutParams ( DisplayUtils.getScreenWidth ( this ) / 3, LayoutParams.WRAP_CONTENT );
		this.params.addRule ( RelativeLayout.CENTER_IN_PARENT );
	}

	protected void setViewMode ( ViewMode mode )
	{
		super.setViewMode (mode);
		
		this.viewMode = mode;
		this.invalidateOptionsMenu ();
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
	
	private void loadImage ()
	{
		String [] params =
		{
			this.photoName
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
				result = BitmapUtils.rotate ( result, 90 );
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
		this.setViewMode ( ViewMode.LOADING );
		new IdentifyTask ( this ).execute ( this.photoName );
	}


	@Override
	public void onProgressUpdate ( String [] values )
	{
		this.textProgress.setText ( values[0] );
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
}
