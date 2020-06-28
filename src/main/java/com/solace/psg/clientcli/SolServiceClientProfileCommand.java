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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle client profile operations.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "cp", description = "Handles client profile operations.", subcommands = {
		SolServiceClientProfileCreateCommand.class,
		SolServiceClientProfileDeleteCommand.class,
		SolServiceClientProfileDetailsCommand.class,
	    SolServiceClientProfileListCommand.class
})
public class SolServiceClientProfileCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceClientProfileCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceClientProfileCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol cp: ");
	    System.out.println(" create  - Creates a client profile.");
	    System.out.println(" delete  - Deteles a client profile.");
	    System.out.println(" details - Details for a client profile.");	    
	    System.out.println(" list    - Lists all client profiles for a Solace Cloud Console Account.");

	    System.out.println(" Example command: sol cp list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running client profile command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{		
			System.out.println("Missing parameters for command. Try invokig command with -h for list of parameters.");
			
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running client profile command: " + e.getMessage());
			logger.error("Error occurred while running client profile command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
