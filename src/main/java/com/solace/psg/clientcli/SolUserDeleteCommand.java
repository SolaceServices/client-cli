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


import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.apiclient.ApiException;


import com.solace.psg.sempv2.ServiceManager;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle user create.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "delete", description = "Deletes a user and sends an invitation to his email address to join.")
public class SolUserDeleteCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolUserDeleteCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;

	@Parameters(index = "0", arity = "1",  description="The user ID")
	private String userId;

	/**
	 * Initialises a new instance of the class.
	 */
	public SolUserDeleteCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol user delete \n");
	    System.out.println(" delete - Deletes a user.");

	    System.out.println(" Example command: sol user delete <userId>");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running user delete command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Deleting user...");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceManager sm = new ServiceManager(token);
			
			Boolean result = sm.deleteUser(userId);

			if (result)
				System.out.println("User deleted successfully");
			else
				System.out.println("Error deleting the user.  Check logs for more details.");	
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running command: " + e.getResponseBody());
			logger.error("Error occured while running command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running command: " + e.getMessage());
			logger.error("Error occured while running command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
