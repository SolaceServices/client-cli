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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle dc operations.
 * 
 * 
 *
 */
@Command(name = "dc", description = "Handles data center operations.", subcommands = {
	      SolDcListCommand.class
})
public class SolDcCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolDcCommand.class);
	
	@Option(names = {"h", "help"})
	private boolean help;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolDcCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol dc: \n");
	    System.out.println(" list - lists all data centers for Solace Cloud Console Account");
	    System.out.println(" Example command: sol dc list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running list command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{		
				System.out.println("Missing parameters for command. Try invokig command with -h for list of parameters.");

		}
		/*catch (ApiException e)
		{
			System.out.println("Error occurred while running login command: " + e.getResponseBody());
			logger.error("Error occurred while running login command: {}", e.getResponseBody());
		}*/
		catch (Exception e)
		{
			System.out.println("Error occurred while running list command: " + e.getMessage());
			logger.error("Error occurred while running list command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
