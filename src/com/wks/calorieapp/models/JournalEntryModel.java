package com.wks.calorieapp.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.wks.calorieapp.entities.JournalEntry;

public class JournalEntryModel extends Observable
{

	private List< JournalEntry > mealEntries;

	public JournalEntryModel ()
	{
		this.mealEntries = new ArrayList< JournalEntry > ();
	}

	public void setMealEntries ( List< JournalEntry > mealEntries )
	{
		this.mealEntries = mealEntries;
		this.setChanged ();
		this.notifyObservers ( this );
	}

	public List< JournalEntry > getJournalEntries ()
	{
		return mealEntries;
	}

}
