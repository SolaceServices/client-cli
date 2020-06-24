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

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv2.admin.model.Service;
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
@Command(name = "create",description = "Creates a service.")
public class SolServiceCreateCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceCreateCommand.class);
	
	@Option(names = {"-serviceName"}, required = true, description = "the name of the service")
	private String serviceName;	

	@Option(names = {"-dc"}, required = true, description = "the data center ID")
	private String datacenterId;	

	@Option(names = {"-type"}, required = true, description = "the service type ID")
	private String serviceTypeId;	
	
	@Option(names = {"-class"}, required = true, description = "the service class")
	private String serviceClassId;	
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceCreateCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service details [-serviceName=<name>] [-serviceId=<id>] \n");
	    System.out.println(" -serviceName - the name of the service.");
	    System.out.println(" -type - the service type. Type 'sol service types' to get all available classes.");
	    System.out.println(" -dc - the service data center ID. Type 'sol dc list' to get all available datacenters IDs.");
	    System.out.println(" -class - the service class. Type 'sol service classes' to get all available classes.");
	    System.out.println(" Example command: sol service create -serviceName=testService -type=enterprise -class=enterprise-kilo -dc=aws-eu-west-2a ");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running service create command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Creating a service with VPN named:" + serviceName);
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try to login first.");	
				return;
			}
			
			ServiceFacade sf = new ServiceFacade(token);
			
     		Service service = sf.createServiceAsync(serviceName, serviceTypeId, serviceClassId, datacenterId);
			
			if (service != null)
				System.out.println("Service creation initiated with serviceId: " + service.getServiceId() + "\nType 'sol service list' to check the creation progress.");
			else 
				System.out.println("Failed to create a service. Switch to debug and check logs for more info.");
		}
		catch (ApiException e)
		{
			//System.out.println("Error occurred while running service create command: " + e.getResponseBody());
			logger.error("Error occurred while running service create command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			//System.out.println("Error occurred while running service create command: " + e.getMessage());
			logger.error("Error occurred while running service create command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
