package com.wks.calorieapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Time
{

	public static final String DATE_FORMAT_DASH = "yyyy-MM-dd";
	public static final String DATE_FORMAT_SLASH = "yyyy/MM/dd";
	
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
	
	public static String toDate(long timeInMillis,String format)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format ( timeInMillis );
	}
	
	public static String toDate(Calendar calendar, String format)
	{
		return toDate(calendar.getTimeInMillis (),format);
	}
	
	
}
