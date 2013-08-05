package com.wks.android.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class FileSystem
{
	public static String getPicturesDirectory(Context context)
	{
		String appName =  "CalorieApp";
		File picturesDir = new File( Environment.getExternalStoragePublicDirectory ( Environment.DIRECTORY_PICTURES ), appName);
		if(!picturesDir.exists ())
			if(!picturesDir.mkdirs ())
			{
				return null;
			}
		return picturesDir.getPath () + File.separator;
	}
	
}
