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

import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.apiclient.ApiException;

import com.solace.psg.sempv2.config.model.MsgVpnQueue;
import com.solace.psg.sempv2.interfaces.ServiceFacade;
import com.solace.psg.sempv2.interfaces.VpnFacade;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle queue create.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "create", description = "Creates queue.")
public class SolServiceQueueCreateCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceQueueCreateCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = "-serviceName", required = true) String serviceName;
        @Option(names = "-serviceId", required = true) String serviceId;
    }

	@Parameters(index = "0", arity = "1", description="the queue name")
	private String queueName;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceQueueCreateCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service queue create \n");
	    System.out.println(" create - Creates a queue for a service.");

	    System.out.println(" Example command: sol service queue create <queueName>");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running queue create command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Creating queue...");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceFacade sf = new ServiceFacade(token);
			String ctxServiceId = ConfigurationManager.getInstance().getCurrentServiceId();
			String ctxServiceName = ConfigurationManager.getInstance().getCurrentServiceName();
			
			ServiceDetails sd = null;
			if (exclusive != null && exclusive.serviceId != null)
			{
				sd = sf.getServiceDetails(exclusive.serviceId);
			}
			else if (exclusive != null && exclusive.serviceName != null)
			{
				sd = sf.getServiceDetailsByName(exclusive.serviceName);
			}
			else if (ctxServiceId != null)
			{
				sd = sf.getServiceDetails(ctxServiceId);
			}
			else if (ctxServiceName != null)
			{
				sd = sf.getServiceDetailsByName(ctxServiceName);
			}
			else
			{
				System.out.println("Service ID or service name was not provided.");
				return;
			}
			
			if (sd != null)
			{
				VpnFacade vf = new VpnFacade(sd);
				MsgVpnQueue request = new MsgVpnQueue();
				request.setQueueName(queueName);
				boolean result = vf.addQueue(request);

				if (result)
					System.out.println("Queue created successfully.");
				else
					System.out.println("Error creating the queue.  Check logs for more details.");	
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
