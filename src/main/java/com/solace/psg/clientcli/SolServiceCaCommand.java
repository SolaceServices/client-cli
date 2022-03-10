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
 * Command class to handle certificate authority operations.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "ca", description = "Handles certificate authority operations.", subcommands = {
		SolServiceCaAddCommand.class,
		SolServiceCaDeleteCommand.class,
	    SolServiceCaListCommand.class
})
public class SolServiceCaCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceCaCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceCaCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol ca: ");
	    System.out.println(" add    - Adds a certificate authority.");
	    System.out.println(" delete - Deteles a certificate authority.");
	    System.out.println(" list   - Lists all certificate authorities. ");

	    System.out.println(" Example command: sol ca list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running certificate authority command.");
		
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
			System.out.println("Error occurred while running certificate authority command: " + e.getMessage());
			logger.error("Error occurred while running certificate authority command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
