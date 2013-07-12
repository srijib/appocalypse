package com.wks.calorieapp.apis;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CAResponseFactory extends CAAbstractResponseFactory
{
	private static final String KEY_STATUS_CODE = "code";
	private static final String KEY_MESSAGE = "message";
	

	@Override
	public CAResponse createResponseFromJSON ( String json ) throws ParseException
	{
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse ( json );
		long statusCode = ((Number) jsonObject.get ( KEY_STATUS_CODE )).longValue ();
		String message = (String) jsonObject.get ( KEY_MESSAGE );
		return new CAResponse(statusCode,message);
	}
}
