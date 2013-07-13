package com.wks.calorieapp.apis;

import org.json.simple.parser.ParseException;

public abstract class CAAbstractResponseFactory
{
	public abstract CAAbstractResponse createResponseFromJSON(String json) throws ParseException;
}
