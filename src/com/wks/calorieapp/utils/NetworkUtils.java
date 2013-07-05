package com.wks.calorieapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils
{
	public static boolean isConnectedToNetwork(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService ( Context.CONNECTIVITY_SERVICE );
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo ();
		return networkInfo != null;
	}
}
