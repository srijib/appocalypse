package com.wks.calorieapp.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.wks.calorieapp.entities.JournalEntry;

import android.content.Context;

public class DateCaloriesModel extends Observable
{

	private List< JournalEntry > mealEntries;

	public DateCaloriesModel ( Context context )
	{

		this.mealEntries = new ArrayList< JournalEntry > ();

	}

	public void setMealEntries ( List< JournalEntry > mealEntries )
	{
		this.mealEntries = mealEntries;
		this.setChanged ();
		this.notifyObservers ( this );
	}
	
	public List< JournalEntry > getMealEntries ()
	{
		return mealEntries;
	}

	
	
}
