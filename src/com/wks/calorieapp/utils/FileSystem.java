package com.wks.calorieapp.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class FileSystem
{
	public static String getPicturesDirectory(Context context)
	{
		String appName = context.getString ( com.wks.calorieapp.R.string.app_name );
		File picturesDir = new File( Environment.getExternalStoragePublicDirectory ( Environment.DIRECTORY_PICTURES ), appName);
		if(!picturesDir.exists ())
			if(!picturesDir.mkdirs ())
			{
				return null;
			}
		return picturesDir.getPath () + File.separator;
	}
}
