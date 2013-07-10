package com.wks.calorieapp.models;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import android.content.Context;

public class JournalModel extends Observable
{
	private Map< Calendar, Float > calorieCalendar;

	public JournalModel ( Context context)
	{
		this.calorieCalendar = new HashMap< Calendar, Float > ();
	}

	public Map<Calendar,Float> getCalorieCalendar ()
	{
		return calorieCalendar;
	}

	public void setCalorieCalendar (  Map<Calendar,Float> calorieCalendar )
	{
		this.calorieCalendar = calorieCalendar;
		this.setChanged ();
		this.notifyObservers ( this );
	}

	
	
}
