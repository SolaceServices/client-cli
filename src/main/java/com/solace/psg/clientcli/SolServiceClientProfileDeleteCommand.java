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


import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.apiclient.ApiException;


import com.solace.psg.sempv2.ServiceManager;


import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle client profile delete.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "delete", description = "deletes client profile.")
public class SolServiceClientProfileDeleteCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceClientProfileDeleteCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = "-serviceName", required = true) String serviceName;
        @Option(names = "-serviceId", required = true) String serviceId;
    }

	@Parameters(index = "0", arity = "1", description="the client profile name")
	private String profileName;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceClientProfileDeleteCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service cp delete \n");
	    System.out.println(" delete - deletes a client profile for a service.");

	    System.out.println(" Example command: sol service cp delete <profileName>");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running client profile delete command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Deleting client profile...");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceManager sm = new ServiceManager(token);
			String ctxServiceId = ConfigurationManager.getInstance().getCurrentServiceId();
			String ctxServiceName = ConfigurationManager.getInstance().getCurrentServiceName();
			
			ServiceDetails sd = null;
			if (exclusive != null && exclusive.serviceId != null)
			{
				sd = sm.getServiceDetails(exclusive.serviceId);
			}
			else if (exclusive != null && exclusive.serviceName != null)
			{
				sd = sm.getServiceDetailsByName(exclusive.serviceName);
			}
			else if (ctxServiceId != null)
			{
				sd = sm.getServiceDetails(ctxServiceId);
			}
			else if (ctxServiceName != null)
			{
				sd = sm.getServiceDetailsByName(ctxServiceName);
			}
			else
			{
				System.out.println("Service ID or service name was not provided.");
				return;
			}
			
			if (sd != null)
			{
				boolean result = sm.deleteClientProfile(sd.getServiceId(), profileName);

				if (result)
					System.out.println("Client profile deleted successfully.");
				else
					System.out.println("Error deleting the client profile.  Check logs for more details.");	
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running client profile command: " + e.getResponseBody());
			logger.error("Error occured while running client profile command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running client profile command: " + e.getMessage());
			logger.error("Error occured while running client profile command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
