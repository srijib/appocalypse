package com.wks.calorieapp.utils;

//http://developer.android.com/training/displaying-bitmaps/load-bitmap.html

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class AndroidBitmap
{
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
	
	public static Bitmap rotate(Bitmap bitmap, float degrees)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate ( degrees );
		return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth (),bitmap.getHeight (),matrix,true);
	}

}
