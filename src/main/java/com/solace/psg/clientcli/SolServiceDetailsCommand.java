/**
 * Copyright 2020 Solace Systems, Inc. All rights reserved.
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
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.interfaces.ServiceFacade;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle service lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "details",description = "Lists service details.")
public class SolServiceDetailsCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceDetailsCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceDetailsCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol login [-u, -username=<username>] [-p, -password=<password>] [-n] \n");
	    System.out.println(" -username - the email or username to login to Solace Cloud Console Account");
	    System.out.println(" -password - the password to login to Solace Cloud Console Account");
	    System.out.println(" -n        - does not print in command line the token generated for the Solace Cloud Console Account");
	    System.out.println(" Example command: sol login -u=John.Smith@example.com -p=hotshot -n");
	}
	
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running login command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Listing services:");		
				
			
		}
		/*catch (ApiException e)
		{
			System.out.println("Error occured while running login command: " + e.getResponseBody());
			logger.error("Error occured while running login command: {}", e.getResponseBody());
		}*/
		catch (Exception e)
		{
			System.out.println("Error occured while running login command: " + e.getMessage());
			logger.error("Error occured while running login command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
