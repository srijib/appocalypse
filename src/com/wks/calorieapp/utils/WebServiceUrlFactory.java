package com.wks.calorieapp.utils;

public class WebServiceUrlFactory
{
	private static final String WEBAPP_URL = "http://uploadte-wksheikh.rhcloud.com/calorieapp/";
	private static final String SERVLET_UPLOAD = "upload";
	private static final String SERVLET_IDENTIFY = "identify";
	private static final String SERVLET_NUTRITION_INFO = "nutrition_info";
	private static final String SERVLET_UPDATE = "update";
	
	public static String upload()
	{
		return WEBAPP_URL + SERVLET_UPLOAD;
	}
	
	public static String identify(String imageName)
	{
		return WEBAPP_URL + SERVLET_IDENTIFY + "/"+imageName;
	}
	
	public static String identify(String imageName, int maximumHits)
	{
		return WEBAPP_URL + SERVLET_IDENTIFY + "/"+imageName + "/"+maximumHits;
	}
	
	public static String getNutritionInfo(String foodName)
	{
		return WEBAPP_URL + SERVLET_NUTRITION_INFO + "/"+foodName;
	}
	
	public static String getNutritionInfo(String foodName, int numResults)
	{
		return WEBAPP_URL + SERVLET_NUTRITION_INFO + "/"+foodName+"/"+numResults;
	}
	
	public static String update(String imageName, String foodName)
	{
		return WEBAPP_URL + SERVLET_UPDATE + "/"+imageName+"/"+foodName;
	}
}
