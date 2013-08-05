package com.wks.calorieapp.adapters;

import java.io.File;
import java.util.List;

import com.wks.android.utils.BitmapUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter
{
	private Context context;
	private int imageSize;
	private int numColumns = 2;

	private List< File > pictureFilesList;

	public GalleryAdapter ( Context context, List< File > pictureFilesList )
	{
		this.context = context;

		this.pictureFilesList = pictureFilesList;

		DisplayMetrics metrics = new DisplayMetrics ();
		WindowManager manager = ( WindowManager ) context.getSystemService ( Context.WINDOW_SERVICE );
		manager.getDefaultDisplay ().getMetrics ( metrics );

		this.imageSize = metrics.widthPixels / numColumns;

	}

	@Override
	public int getCount ()
	{
		return this.pictureFilesList.size ();
	}

	@Override
	public File getItem ( int position )
	{
		return this.pictureFilesList.get ( position );
	}

	@Override
	public long getItemId ( int position )
	{
		return position;
	}

	@Override
	public View getView ( int position, View convertView, ViewGroup parent )
	{
		ImageView resultView;
		if ( convertView == null )
		{
			resultView = new ImageView ( this.context );
			resultView.setScaleType ( ImageView.ScaleType.FIT_CENTER );
		}else
		{
			resultView = ( ImageView ) convertView;
		}

		File file = this.getItem ( position );

		Bitmap bitmap = BitmapUtils.getThumbnailFromFile ( context, file, imageSize, imageSize );
		
		if ( bitmap != null )
		{
			resultView.setImageBitmap ( bitmap );
		}else
		{
			
		}

		return resultView;
	}

}
