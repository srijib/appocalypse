package com.wks.calorieapp.utils;

//http://developer.android.com/training/displaying-bitmaps/load-bitmap.html

import java.io.File;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class AndroidBitmap
{
	public static Bitmap getThumbnailFromFile(Context context, File file, int thumbnailWidth, int thumbnailHeight)
	{
		final BitmapFactory.Options options = new BitmapFactory.Options ();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile ( file.getAbsolutePath (), options );
		
		options.inSampleSize = calculateInSampleSize(options,thumbnailWidth,thumbnailHeight);
		
		options.inJustDecodeBounds= false;
		return BitmapFactory.decodeFile ( file.getAbsolutePath (), options );
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options,int thumbnailWidth, int thumbnailHeight)
	{
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		int inSampleSize = 8;
		
		if(imageWidth > thumbnailWidth || imageHeight > thumbnailHeight )
		{
			int heightRatio = Math.round((float) imageHeight/(float)thumbnailHeight);
			int widthRatio = Math.round ( (float) imageWidth/(float)thumbnailWidth );
			
			inSampleSize = Math.max ( widthRatio, heightRatio );
		}
		
		return inSampleSize;
	}
	
	/*
	public static Bitmap getThumbnailFromFile(Context context,File file, int thumbnailWidth, int thumbnailHeight) throws FileNotFoundException
	{
		try
		{
			FileInputStream fis = new FileInputStream( file);
			Bitmap bitmap = BitmapFactory.decodeStream(fis);

			 bitmap = Bitmap.createScaledBitmap(bitmap,
			        thumbnailWidth, thumbnailHeight, false);

			ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream(); 
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytearroutstream);

			return bitmap;
		}
		catch ( FileNotFoundException e )
		{
			throw e;
		}
	}
	*/
	public static Bitmap rotate(Bitmap bitmap, float degrees)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate ( degrees );
		return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth (),bitmap.getHeight (),matrix,true);
	}

}
