package com.wks.calorieapp.activities;

import java.util.List;
import java.util.Map;

import com.wks.calorieapp.apis.NutritionInfo;

public interface IdentifyTaskInvoker
{
	public void onPreExecute();
	public void onPostExecute(Map<String,List<NutritionInfo>> identifyResult);
	public void onProgressUpdate(String [] values);
	public void onCancelled();
}
