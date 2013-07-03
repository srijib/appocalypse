package com.wks.calorieapp.pojos;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProfileFactory
{
	//DOESNT WORK!
	public static Profile createProfileFromJson(String json) throws ParseException, ProfileException
	{
		JSONParser parser = new JSONParser();
		Object o = parser.parse ( json );
		JSONObject profileJson = (JSONObject) o;
		
		Profile profile = new Profile();
		
		int age = Integer.parseInt ( (String) profileJson.get ( Profile.KEY_AGE ) );
		float weight = Float.parseFloat ( (String) profileJson.get ( Profile.KEY_WEIGHT ) );
		float height = Float.parseFloat ( (String) profileJson.get ( Profile.KEY_HEIGHT ));
		float activityFactor = Float.parseFloat ( (String) profileJson.get ( Profile.KEY_ACTIVITY_FACTOR ) );
		int weightLossGoal = Integer.parseInt ( (String) profileJson.get ( Profile.KEY_WEIGHT_LOSS_GOAL ));
		
		String sSex = (String) profileJson.get ( Profile.KEY_SEX );
		profile.setSex ( Profile.Sex.valueOf ( sSex ) );
		
		profile.setAge ( age );
		profile.setWeight ( weight );
		profile.setHeight ( height );
		profile.setWeightLossGoal ( weightLossGoal );
		profile.setActivityFactor ( activityFactor );
		
		
		return profile;
	}
	
	public static Profile createProfileFromCSV(String profileCSV) throws NumberFormatException, ProfileException
	{
		Profile profile = null;
		String[] tokens = profileCSV.split ( "," );
		if(tokens.length == 6)
		{
			profile = new Profile();
			profile.setSex ( Profile.Sex.valueOf ( tokens[0] ) );
			profile.setAge ( Integer.parseInt ( tokens[1] ) );
			profile.setHeight ( Float.parseFloat ( tokens[2] ) );
			profile.setWeight ( Float.parseFloat ( tokens[3] ) );
			profile.setActivityFactor ( Float.parseFloat ( tokens[4] ) );
			profile.setWeightLossGoal ( Integer.parseInt ( tokens[5] ) );
		}
		
		return profile;
	}
}
