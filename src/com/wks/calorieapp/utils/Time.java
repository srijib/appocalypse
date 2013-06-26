package com.wks.calorieapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Time
{
	public static String getTimeAsString(long time, String format)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format ( time );
	}
	
	public static String getTimeAsString(Calendar cal,String format)
	{
		Calendar calendar = (Calendar) cal.clone ();
		return Time.getTimeAsString ( calendar.getTimeInMillis (), format );
	}
}
