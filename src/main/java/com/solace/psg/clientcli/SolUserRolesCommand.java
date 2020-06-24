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

import java.util.List;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv2.admin.model.Permission;
import com.solace.psg.sempv2.admin.model.Role;

import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.interfaces.ServiceFacade;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle roles.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "roles",description = "Lists user roles.")
public class SolUserRolesCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolUserRolesCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolUserRolesCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol user roles \n");

	    System.out.println(" Example command: sol user roles");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running roles command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Listing roles:");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceFacade sf = new ServiceFacade(token);	
			List<Role> roles = sf.getAllOrganizationRoles();
			
			printResults(roles, "");		
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running command: " + e.getResponseBody());
			logger.error("Error occurred while running command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running command: " + e.getMessage());
			logger.error("Error occurred while running command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private void printResults(List<Role> roles, String message) throws IOException
	{
		System.out.println(message);
		logger.debug("Printing roles");
		
		for (Role role : roles)
		{
			System.out.println();
			System.out.println("Role ID: [" + role.getId() + "], role name: [" + role.getName() + "], Permissions: ");
			List<Permission> perms = role.getPermissions();
			System.out.print("     ");
			for (Permission perm : perms)
			{
				System.out.print("[" + perm.getName() + "] "); 
			}
		}
	}
}
