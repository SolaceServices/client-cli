/**
 * Copyright 2022 Solace Systems, Inc. All rights reserved.
 *
 * http://www.solace.com
 *
 * This source is distributed under the terms and conditions
 * of any contract or contracts between Solace Systems, Inc.
 * ("Solace") and you or your company.
 * If there are no contracts in place use of this source
 * is not authorized.
 * No support is provided and no distribution, sharing with
 * others or re-use of this source is authorized unless
 * specifically stated in the contracts referred to above.
 *
 * This product is provided as is and is not supported
 * by Solace unless such support is provided for under 
 * an agreement signed between you and Solace.
 * 
 */
package com.solace.psg.util.queuecopy;

/**
 * Class to encapsulate properties of SubSub VPN.
 * 
 *
 */
public class VPN
{
	private String url;
	private String name;
	private String username;
	private String password;
		
	private int connectRetries = -1;
	private int reconnectRetries = -1;
	private int connectTimeoutInMillis = 60000;
	private int reconnectRetryWaitInMillis = 6000;
	private int connectRetriesPerHost = 5;
	private int keepAliveIntervalInMillis = 6000;	

	/**
	 * Gets the host.
	 * @return the host
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * Sets the host.
	 * @param host the host to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	/**
	 * Gets VPN name.
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets VPN name.
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets username.
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Sets the username.
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * Gets password.
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets password
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 * Initialises a new instance of the class.
	 */
	public VPN(String url, String name, String username, String password)
	{
		if (url == null)
			throw new IllegalArgumentException("Parameter host cannot be null.");
		if (username == null)
			throw new IllegalArgumentException("Parameter username cannot be null.");
		if (password == null)
			throw new IllegalArgumentException("Parameter password cannot be null.");		
		
		this.url = url;
		this.name = name;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Gets the connection retries.
	 * @return the connectRetries count.
	 */
	public int getConnectRetries()
	{
		return connectRetries;
	}

	/**
	 * Sets the connection retries.
	 * @param connectRetries the connectRetries to set
	 */
	public void setConnectRetries(int connectRetries)
	{
		this.connectRetries = connectRetries;
	}

	/**
	 * Gets the reconnection retries.
	 * @return the reconnectRetries
	 */
	public int getReconnectRetries()
	{
		return reconnectRetries;
	}

	/**
	 * Sets the reconnection retries.
	 * @param reconnectRetries the reconnectRetries to set
	 */
	public void setReconnectRetries(int reconnectRetries)
	{
		this.reconnectRetries = reconnectRetries;
	}

	/**
	 * Gets the connect timeout in millis.
	 * @return the connectTimeoutInMillis
	 */
	public int getConnectTimeoutInMillis()
	{
		return connectTimeoutInMillis;
	}

	/**
	 * Sets the connect timeout in millis.
	 * @param connectTimeoutInMillis the connectTimeoutInMillis to set
	 */
	public void setConnectTimeoutInMillis(int connectTimeoutInMillis)
	{
		this.connectTimeoutInMillis = connectTimeoutInMillis;
	}

	/**
	 * Gets the reconnect timeout in millis.
	 * @return the reconnectRetryWaitInMillis
	 */
	public int getReconnectRetryWaitInMillis()
	{
		return reconnectRetryWaitInMillis;
	}

	/**
	 * Sets the reconnect timeout in millis.
	 * @param reconnectRetryWaitInMillis the reconnectRetryWaitInMillis to set
	 */
	public void setReconnectRetryWaitInMillis(int reconnectRetryWaitInMillis)
	{
		this.reconnectRetryWaitInMillis = reconnectRetryWaitInMillis;
	}

	/**
	 * Gets the connect retries per url.
	 * @return the connectRetriesPerHost
	 */
	public int getConnectRetriesPerHost()
	{
		return connectRetriesPerHost;
	}

	/**
	 * Sets the connect retries per url.
	 * @param connectRetriesPerHost the connectRetriesPerHost to set
	 */
	public void setConnectRetriesPerHost(int connectRetriesPerHost)
	{
		this.connectRetriesPerHost = connectRetriesPerHost;
	}

	/**
	 * Gets the keep alive interval in millis.
	 * @return the keepAliveIntervalInMillis
	 */
	public int getKeepAliveIntervalInMillis()
	{
		return keepAliveIntervalInMillis;
	}

	/**
	 * Sets the keep alive interval in millis.
	 * @param keepAliveIntervalInMillis the keepAliveIntervalInMillis to set
	 */
	public void setKeepAliveIntervalInMillis(int keepAliveIntervalInMillis)
	{
		this.keepAliveIntervalInMillis = keepAliveIntervalInMillis;
	}
}
