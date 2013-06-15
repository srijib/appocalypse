package com.wks.calorieapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpClient {
    
	public static boolean isConnectedToNetwork(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService ( Context.CONNECTIVITY_SERVICE );
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo ();
		return networkInfo.isConnected ();
	}
	
    public static String get(String url) throws IOException
    {
	URL _url = new URL(url);
	HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
	
	connection.setRequestMethod(HttpMethod.GET.value());
	
	//TODO check if this code works
	
	//if(connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED)
	  //throw new ConnectException(connection.getResponseCode() + " - "+connection.getResponseMessage());
	
	BufferedReader buffy = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	
	String content = "";
	String line = "";
	while((line = buffy.readLine())!= null)
	    content += line;
	
	connection.disconnect();
	return content;
    }
    
    public enum HttpMethod{
	GET("GET"),
	POST("POST");
	
	private final String method;
	
	private HttpMethod(String method)
	{
	    this.method = method;
	}
	
	public String value(){
	    return this.method;
	}
    }
    
}
