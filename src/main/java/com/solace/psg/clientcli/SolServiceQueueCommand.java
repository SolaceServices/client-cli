/**
 * Copyright 2021 Solace Systems, Inc. All rights reserved.
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

import com.solace.psg.clientcli.sempv1.SolServiceQueuePurgeCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle queue operations.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "queue", description = "Handles queue operations.", subcommands = {

	    SolServiceQueueCreateCommand.class,
	    SolServiceQueueDeleteCommand.class,
	    SolServiceQueueDetailsCommand.class,
	    SolServiceQueueListCommand.class,
	    SolServiceQueuePurgeCommand.class
})
public class SolServiceQueueCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceQueueCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceQueueCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service queue: ");
	    System.out.println(" create  - Creates a queue.");
	    System.out.println(" delete  - Deteles a queue.");
	    System.out.println(" details - Details of a queue.");
	    System.out.println(" list    - Lists all queues.");
	    System.out.println(" purge   - Purges messages from a queue.");

	    System.out.println(" Example command: sol service queue create <queueName> -exclusive");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running queue command.");
		
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
			System.out.println("Error occurred while running queue command: " + e.getMessage());
			logger.error("Error occurred while running queue command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
