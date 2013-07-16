package com.wks.calorieapp.models;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import com.wks.calorieapp.entities.CalendarEvent;

import android.content.Context;

public class JournalModel extends Observable
{
	private Map< Calendar, CalendarEvent > calorieCalendar;

	public JournalModel ( )
	{
		this.calorieCalendar = new HashMap< Calendar, CalendarEvent > ();
	}

	public Map<Calendar,CalendarEvent> getCalorieCalendar ()
	{
		return calorieCalendar;
	}

	public void setCalorieCalendar (  Map<Calendar,CalendarEvent> calorieCalendar )
	{
		this.calorieCalendar = calorieCalendar;
		this.setChanged ();
		this.notifyObservers ( this );
	}

	
	
}
