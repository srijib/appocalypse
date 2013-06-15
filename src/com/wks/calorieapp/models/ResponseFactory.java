package com.wks.calorieapp.models;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ResponseFactory
{
	private static final String KEY_SUCCESS = "success";
	private static final String KEY_MESSAGE = "message";
	private static final JSONParser parser = new JSONParser ();

	public static Response createResponseForUploadRequest ( String json ) throws ParseException
	{
		if(json == null || json.isEmpty ())
			return null;
		
		JSONObject jsonObject = jsonify(json);
		boolean success = isSuccessful ( jsonObject );
		String message = getMessage ( jsonObject );
		
		Response response = new Response();
		response.setSuccessful ( success );
		response.setMessage ( message );
		response.setData ( null );

		return response;
	}

	public static Response createResponseForIdentifyRequest ( String json )
	{
		return null;
	}

	public static Response createResponseForNutritionInfoRequest ( String json )
	{
		return null;
	}

	private static JSONObject jsonify ( String json ) throws ParseException
	{
		Object object =  parser.parse ( json );
		JSONObject jsonObject = (JSONObject) object;
		return jsonObject;
	}

	private static String getMessage ( JSONObject jsonObject )
	{
		return (String) jsonObject.get ( KEY_MESSAGE );
	}

	private static boolean isSuccessful ( JSONObject jsonObject )
	{
		return (Boolean) jsonObject.get ( KEY_SUCCESS );
	}
}
