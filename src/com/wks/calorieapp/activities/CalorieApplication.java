package com.wks.calorieapp.activities;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

import com.wks.android.utils.CryptoException;
import com.wks.android.utils.DecryptUtils;
import com.wks.android.utils.EncryptUtils;
import com.wks.android.utils.FileUtils;
import com.wks.calorieapp.apis.NutritionInfo;
import com.wks.calorieapp.entities.Profile;
import com.wks.calorieapp.entities.ProfileException;
import com.wks.calorieapp.entities.ProfileFactory;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class CalorieApplication extends Application
{
	public static final String TAG = CalorieApplication.class.getCanonicalName ();
	public static final String KEY = "AED6EF4DFB491FAD";
	public static final String FILENAME_PROFILE_JSON = "profile.json";
	
	private Profile profile;
	private static Map<String,List<NutritionInfo>> identifyResults;
	
	@Override
	public void onCreate ()
	{
		super.onCreate ();
	}
	
	public Profile getProfile()
	{
		if(profile == null)
			profile = loadProfile();
		return profile; 
	}
	
	private Profile loadProfile()
	{
		Profile profile = null;
		
		try
		{
			byte[] encrypted = FileUtils.readFromFile ( this, FILENAME_PROFILE_JSON );
			String decrypted = DecryptUtils.AES ( encrypted, KEY );
			Log.i(TAG,new String(decrypted));
			profile = ProfileFactory.createProfileFromJson ( decrypted );
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( CryptoException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( ParseException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( ProfileException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return profile;
	}
	
	/**Encrypts and saves profile to disc.
	 * 
	 * @param profile
	 */
	public void saveProfile(Profile profile)
	{
		if(profile == null)
			throw new IllegalStateException("profile must not be null");
		
		try
		{
			String json = profile.toJSON ();
			byte[] encrypted = EncryptUtils.AES ( json, KEY );
			Log.i(TAG,new String(encrypted));
			FileUtils.writeToFile ( this, FILENAME_PROFILE_JSON, encrypted, Context.MODE_PRIVATE );
			this.profile = profile;
		}
		catch ( CryptoException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	//This is bad, I know ;(
	public Map<String,List<NutritionInfo>> getIdentifyResults()
	{
		return identifyResults;
	}
	
	public void setIdentifyResults(Map<String,List<NutritionInfo>> identifyResults)
	{
		CalorieApplication.identifyResults = identifyResults;
	}
	
}
