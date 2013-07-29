package com.wks.android.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClient
{

	public static String get ( String url ) throws IOException
	{
		URL _url = new URL ( url );
		HttpURLConnection connection = ( HttpURLConnection ) _url.openConnection ();

		connection.setRequestMethod ( HttpMethod.GET.value () );

		// TODO check if this code works

		// if(connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED)
		// throw new ConnectException(connection.getResponseCode() +
		// " - "+connection.getResponseMessage());

		BufferedReader buffy = new BufferedReader ( new InputStreamReader ( connection.getInputStream () ) );

		String content = "";
		String line = "";
		while ( ( line = buffy.readLine () ) != null )
			content += line;

		connection.disconnect ();
		return content;
	}

	public enum HttpMethod
	{
		GET ( "GET" ), POST ( "POST" );

		private final String method;

		private HttpMethod ( String method )
		{
			this.method = method;
		}

		public String value ()
		{
			return this.method;
		}
	}

	@SuppressWarnings ( "deprecation" )
	public static String uploadFile ( File file, String _url ) throws IOException
	{
		String httpMethod = "POST";
		String boundary = "*****";
		String twoHyphens = "--";
		String carriageReturn = "\r\n";

		HttpURLConnection connection = null;
		DataOutputStream requestStream = null;
		DataInputStream responseStream = null;

		FileInputStream fileIn = null;

		fileIn = new FileInputStream ( file );
		URL url = new URL ( _url );
		connection = ( HttpURLConnection ) url.openConnection ();

		// set connection parameters

		// request will be sent (input)
		// response expected (output)

		connection.setDoInput ( true );
		connection.setDoOutput ( true );

		connection.setRequestMethod ( httpMethod );
		connection.setRequestProperty ( "Connection", "Keep-Alive" );
		connection.setRequestProperty ( "Content-Type", "multipart/form-data" );
		connection.setRequestProperty ( "Content-Type", "multipart/form-data;boundary=" + boundary );

		// establish connection
		requestStream = new DataOutputStream ( connection.getOutputStream () );
		requestStream.writeBytes ( twoHyphens + boundary + carriageReturn );
		requestStream.writeBytes ( "Content-Disposition: form-data; name='uploadedfile';filename=" + file.getName () + carriageReturn );
		requestStream.writeBytes ( "\r\n" );

		// begin writing image
		int bytesAvailable = fileIn.available ();
		int maxBufferSize = 1 * 1024 * 1024; // !1MB
		int bufferSize = Math.min ( bytesAvailable, maxBufferSize );
		byte [] buffer = new byte [bufferSize];

		int bytesRead = fileIn.read ( buffer, 0, bufferSize );
		while ( bytesRead > 0 )
		{
			requestStream.write ( buffer );
			bytesAvailable = fileIn.available ();
			bufferSize = Math.min ( bytesAvailable, maxBufferSize );
			bytesRead = fileIn.read ( buffer, 0, bufferSize );
		}
		requestStream.writeBytes ( carriageReturn );
		requestStream.writeBytes ( twoHyphens + boundary + twoHyphens + carriageReturn );
		// close streams

		fileIn.close ();
		requestStream.flush ();
		requestStream.close ();

		responseStream = new DataInputStream ( connection.getInputStream () );
		String response = "";
		String line = "";

		while ( ( line = responseStream.readLine () ) != null )
		{
			response += line;
		}
		responseStream.close ();

		return response;

	}

	public static String appendGetParameters ( String url, Map< String, String > params )
	{
		String urlWithParameters = url;
		if(params.size ()!=0)
		{
			urlWithParameters += "?";
			Iterator< Entry< String, String >> paramIterator = params.entrySet ().iterator ();
			while(paramIterator.hasNext ())
			{
				Entry<String,String> parameter = paramIterator.next ();
				urlWithParameters += parameter.getKey ()+"="+parameter.getValue ();
				if(paramIterator.hasNext ()) urlWithParameters += "&";
			}
		}
		return urlWithParameters;
	}
}
