/**
 * 
 */
package com.solace.psg.clientcli.utils;

import java.io.IOException;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Simple HTTP interface.
 * @author VictorTsonkov
 *
 */
public class HttpClient
{
	private String url;
	/**
	 * Initialises a new instance of the class.
	 */
	public HttpClient(String url)
	{
		this.url = url;
	}

	/**
	 * Executes response.
	 * @return the body.
	 * @throws IOException
	 */
	public String executeRequest() throws IOException
	{
		OkHttpClient client = new OkHttpClient();

	    Request request = new Request.Builder().url(url).build();
	 
	    Call call = client.newCall(request);
	    Response response = call.execute();		 
	    return response.body().string();
	}
}
