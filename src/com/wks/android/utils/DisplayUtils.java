package com.wks.android.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils
{
	private static  int screenWidth = -1;
	private static int screenHeight = -1;
	
	private static void getScreenDimensions(Context context)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager manager = (WindowManager) context.getSystemService ( Context.WINDOW_SERVICE );
		manager.getDefaultDisplay ().getMetrics ( metrics );
		DisplayUtils.screenWidth = metrics.heightPixels;
		DisplayUtils.screenHeight = metrics.widthPixels;
	}
	
	public static int getScreenWidth(Context context)
	{
		if(screenWidth == -1)
		{
			getScreenDimensions(context);
			
		}
		
		return screenWidth;
	}
	
	public static int getScreenHeight(Context context)
	{
		if(screenHeight == -1)
		{
			getScreenDimensions(context);
		}
		
		return screenHeight;
	}
}
