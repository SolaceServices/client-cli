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

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.clientcli.utils.HttpClient;

import picocli.CommandLine.Command;

/**
 * Random jolt of joy.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "jolt",description = "Random jolt of joy.")
public class SolJoltCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolJoltCommand.class);

	/**
	 * Initialises a new instance of the class.
	 */
	public SolJoltCommand()
	{
	}

	public void run()
	{
		System.out.println("Wait for it ... ");
		String url = "https://geek-jokes.sameerkumar.website/api";
		HttpClient client = new HttpClient(url);
	    String response;
		try
		{
			response = client.executeRequest();
			System.out.print(response);
		}
		catch (IOException e)
		{
			System.out.print("Ups! The vending machine seems broken!");
			logger.error("Error occurred: {}, {}", e.getMessage(), e.getCause());
		}
		
	}
}
