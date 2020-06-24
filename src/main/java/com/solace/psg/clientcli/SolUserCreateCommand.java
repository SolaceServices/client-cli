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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.admin.model.User;
import com.solace.psg.sempv2.admin.model.UserRequest;
import com.solace.psg.sempv2.apiclient.ApiException;


import com.solace.psg.sempv2.interfaces.ServiceFacade;



import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle user create.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "create", description = "Creates a user and sends an invitation to his email address to join.")
public class SolUserCreateCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolUserCreateCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;

	@Parameters(index = "0", arity = "1",  description="the first name")
	private String firstName;

	@Parameters(index = "1", arity = "1", description="the last name")
	private String lastName;

	@Parameters(index = "2", arity = "1", description="the email")
	private String email;

	@Parameters(index = "3", arity = "0..6", description="the roles")
	private List<String> roles;

	/**
	 * Initialises a new instance of the class.
	 */
	public SolUserCreateCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol user create \n");
	    System.out.println(" create - Creates a user.");

	    System.out.println(" Example command: sol user create John Smith jsmith@example.com messaging-service-viewer messaging-service-editor ");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running user create command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Creating user...");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceFacade sf = new ServiceFacade(token);
			
			UserRequest request = new UserRequest(email, roles);
			request.setFirstName(firstName);
			request.setLastName(lastName);
			
			User user = sf.addUser(request);

			if (user != null)
				System.out.println("User created successfully with user ID: " + user.getUserId());
			else
				System.out.println("Error creating the user.  Check logs for more details.");	
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running user command: " + e.getResponseBody());
			logger.error("Error occured while running user command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running user command: " + e.getMessage());
			logger.error("Error occured while running user command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
