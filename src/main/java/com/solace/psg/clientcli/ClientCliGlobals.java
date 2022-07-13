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
package com.solace.psg.clientcli;

/**
 * Class to hold global constants.
 * 
 *
 */
public class ClientCliGlobals
{
	/**
	 * Current application version.
	 */
	public final static String VERSION = "0.4.3";

	/**
	 * Default queue allocation quota in Mb.
	 */
	public static final String DEFAULT_QUEUE_QUOTA = "5000";
	
	/**
	 * Default queue TTL.
	 */
	public static final String DEFAULT_QUEUE_TTL = "0";
	
	/**
	 * This class should not be instantiated
	 */
	private ClientCliGlobals()
	{
	}

}
