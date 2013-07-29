package com.wks.calorieapp.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.wks.calorieapp.apis.NutritionInfo;

public class SearchResultsModel extends Observable
{
	private String searchTerm;
	private Map<String,List<NutritionInfo>> searchResults;
	
	public SearchResultsModel()
	{
		this.searchResults = new HashMap<String,List<NutritionInfo>>();
	}
	
	public String getSearchTerm ()
	{
		return searchTerm;
	}
	
	public void setSearchTerm ( String searchTerm )
	{
		this.searchTerm = searchTerm;
	}
	
	public Map< String, List< NutritionInfo >> getSearchResults()
	{
		return this.searchResults;
	}
	
	public void setSearchResults(Map< String, List< NutritionInfo >> results)
	{
		this.searchResults = results;
		this.setChanged ();
		this.notifyObservers (this);
	}
}
