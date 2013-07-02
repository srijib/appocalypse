package com.wks.calorieapp.pojos;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProfileFactory
{
	public static Profile createProfileFromJson(String json) throws ParseException, ProfileException
	{
		JSONParser parser = new JSONParser();
		Object o = parser.parse ( json );
		JSONObject profileJson = (JSONObject) o;
		
		Profile profile = new Profile();
		profile.setAge ( (Integer) profileJson.get ( Profile.KEY_AGE ) );
		profile.setHeight ( ((Double) profileJson.get ( Profile.KEY_HEIGHT )).floatValue () );
		profile.setWeight ( ((Double) profileJson.get ( Profile.KEY_WEIGHT )).floatValue () );
		profile.setActivityFactor ( ((Double) profileJson.get ( Profile.KEY_ACTIVITY_FACTOR )).floatValue () );
		profile.setWeightLossGoal ( (Integer) profileJson.get ( Profile.KEY_WEIGHT_LOSS_GOAL ) );
		
		String sSex = (String) profileJson.get ( Profile.KEY_SEX );
		profile.setSex ( Profile.Sex.valueOf ( sSex ) );
		
		return profile;
	}
}
