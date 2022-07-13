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
package com.solace.psg.util;

/**
 * Class to hold various String utilities.
 * 
 *
 */
public class StringHelper
{
	/**
	 * Initialises a new instance of the class. 
	 */
	public StringHelper()
	{
	}
	
	/**
	 * Shortens String by putting "..." in the middle of it. 
	 * @param value
	 * @param length
	 * @return the short string
	 */
	public static String shortenString(String value, int length)
	{
		String result = null;
		if (length < 3)
			throw new IllegalArgumentException("Variable length cannot be less than 3");
		
		if (value.length() <= length)
		{
			result = value;
		}
		else
		{
			String start = value.substring(0, length / 2 - 1);
			String end = value.substring(value.length()- length/2+2 , value.length());
			result = start + "..." + end;
		}
		
		return result;
	}
	
	/**
	 * Returns empty String when null. 
	 * @param value
	 * @return string or ""
	 */
	public static String nonNull(Object value)
	{
		return (value != null) ? value.toString() : "";
	}

}
