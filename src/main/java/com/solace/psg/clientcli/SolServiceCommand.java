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

import com.solace.psg.clientcli.sempv1.SolServiceLogCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle service operations.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "service", aliases = "s", description = "Handles service operations.", subcommands = {
		SolServiceBridgeCommand.class,
		SolServiceCaCommand.class,
		SolServiceCreateCommand.class,
		SolServiceClassesCommand.class,
		SolServiceClientProfileCommand.class,
		SolServiceConfigCommand.class,
		SolServiceDeleteCommand.class,
		SolServiceDetailsCommand.class,
	    SolServiceListCommand.class,    
	    SolServiceLogCommand.class,    
	    SolServiceQueueCommand.class,
	    SolServiceSetCommand.class,	      
		SolServiceTypesCommand.class,
		SolServiceUsernameCommand.class
})
public class SolServiceCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service: ");
	    System.out.println(" bridge   - Handles service bridges.");
	    System.out.println(" ca       - Handles service certificate authorities.");
	    System.out.println(" classes  - Displays available service classes.");
	    System.out.println(" config   - Handles service config backups.");
	    System.out.println(" cp       - Handles service client profiles.");
	    System.out.println(" create   - Creates a service.");
	    System.out.println(" delete   - Deteles a service.");
	    System.out.println(" details  - Lists all service details for a service.");
	    System.out.println(" list     - Lists all services for a Solace Cloud Console Account.");
	    System.out.println(" log      - Shows service logs.");
	    System.out.println(" queue    - Handles service queues.");
	    System.out.println(" set      - Sets a service as the default service context by service name or service ID.");
	    System.out.println(" types    - Displays available service types.");
	    System.out.println(" username - Handles service usernames.");

	    System.out.println(" Example command: sol service list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running service command.");
		
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
			System.out.println("Error occurred while running service command: " + e.getMessage());
			logger.error("Error occurred while running service command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
