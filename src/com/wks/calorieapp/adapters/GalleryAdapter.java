package com.wks.calorieapp.adapters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wks.calorieapp.utils.AndroidBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter
{
	private static final int IMAGE_HEIGHT = 100;
	private static final int IMAGE_WIDTH = 100;

	private Context context;
	
	
	private List< File > pictureFilesList;
	
	/*
	private Cursor cursor;
	private int columnIndex;
	*/ 

	public GalleryAdapter ( Context context, List< File > pictureFilesList )
	{
		this.context = context;
		
		this.pictureFilesList = pictureFilesList;
	
	}

	@Override
	public int getCount ()
	{
		return this.pictureFilesList.size ();
		//return this.cursor.getCount ();
	}

	@Override
	public File getItem ( int position )
	{
		return this.pictureFilesList.get ( position );
		//return null;
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
			resultView.setLayoutParams ( new GridView.LayoutParams ( IMAGE_WIDTH, IMAGE_HEIGHT ) );
			resultView.setScaleType ( ImageView.ScaleType.CENTER_CROP );
		}else
		{
			resultView = ( ImageView ) convertView;
		}
		
		try
		{
			File file = this.getItem ( position );

			Bitmap bitmap = AndroidBitmap.getThumbnailFromFile ( context, file, IMAGE_WIDTH, IMAGE_HEIGHT );
			bitmap = AndroidBitmap.rotate ( bitmap, 90 );
			if ( bitmap != null )
			{
				resultView.setImageBitmap ( bitmap );
			}else
			{
				// TODO
				
			}
		}
		catch ( FileNotFoundException e )
		{
			//TODO
		}
		
		/*
		cursor.moveToPosition ( position );
		int id = cursor.getInt ( this.columnIndex );
		Uri imageUri = Uri.withAppendedPath ( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, ""+id );
		
		resultView.setImageURI ( imageUri );
		*/
		return resultView;
	}

}
