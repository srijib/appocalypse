package com.wks.android.utils;

import android.graphics.Typeface;
import android.widget.TextView;

public class TypefaceUtils
{
	public static void setFont(TextView tv,String path)
	{
		Typeface typeface = Typeface.createFromAsset ( tv.getContext ().getAssets (), path );
		tv.setTypeface ( typeface );
	}
}
