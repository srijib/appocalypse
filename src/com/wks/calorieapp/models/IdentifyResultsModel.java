package com.wks.calorieapp.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class IdentifyResultsModel extends Observable
{
	List<String> possibleMatchesList;
	
	public IdentifyResultsModel()
	{
		this.possibleMatchesList = new ArrayList<String>();
	}
	
	public void setPossibleMatchesList ( List< String > possibleMatchesList )
	{
		this.possibleMatchesList = possibleMatchesList;
		this.setChanged ();
		this.notifyObservers ();
	}
	
	public List< String > getPossibleMatchesList ()
	{
		return possibleMatchesList;
	}
	
}
