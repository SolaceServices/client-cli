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

import java.io.IOException;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv2.config.model.MsgVpnQueue;
import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.sempv2.VpnManager;


import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle queue details.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "details", description = "Service queue details.")
public class SolServiceQueueDetailsCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceQueueDetailsCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = {"-serviceName", "-sn"}, required = true) String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = true) String serviceId;
    }
    
	@Parameters(index = "0", arity = "1", description="the queue name")
	private String queueName;
    
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceQueueDetailsCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service queue details <queueName> \n");
	    System.out.println(" details - details for a queue");

	    System.out.println(" Example command: sol service cp details <queueName>");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running queue details command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Queues details:");	
			
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
				VpnManager vf = new VpnManager(sd);
				MsgVpnQueue queue = vf.getQueue(queueName);

				printDetails(queue, "");		
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}
		}
		catch (ApiException e)
		{
			if (e.getResponseBody().contains("NOT_FOUND"))
					System.out.println("No queue found with the provided name.");
			else
				System.out.println("Error occured while running queue command: " + e.getResponseBody());
			logger.error("Error occured while running queue command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running queue command: " + e.getMessage());
			logger.error("Error occured while running queue command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private void printDetails(MsgVpnQueue queue, String message) throws IOException
	{
		System.out.println(message);
		logger.debug("Printing queue details.");
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		String yaml = mapper.writeValueAsString(queue);
		System.out.println(yaml);
	}
}
