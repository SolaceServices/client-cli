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
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.solace.psg.sempv1.sempinterface.AdminCommands;
import com.solace.psg.sempv1.sempinterface.HttpSempSession;
import com.solace.psg.sempv1.sempinterface.SempSession;
import com.solace.psg.sempv2.admin.model.ServiceDetails;
import com.solace.psg.sempv2.admin.model.ServiceManagementContext;
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
@Command(name = "purge",description = "Purge service queue.")
public class SolServicePurgeCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServicePurgeCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@Option(names = {"-queue"}, required = true)
	private String queueName;	

	@Option(names = {"-serviceName"})
	private String serviceName;	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServicePurgeCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service purge [-queueName=<name>] [-serviceName=<name>] \n");
	    System.out.println(" -serviceName - the name of the service.");
	    System.out.println(" -queueName - the name of the queue to be purged.");
	    System.out.println(" Example command: sol service purge -queueName=testQueue");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running service purge command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			String serviceId = ConfigurationManager.getInstance().getCurrentServiceId();			

			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try to login first.");	
				return;
			}

			System.out.println("Purging queue: " + queueName);
			
			ServiceFacade sf = new ServiceFacade(token);
			ServiceDetails sd = null;
			
			if (serviceName != null)
			{
				sd = sf.getServiceDetailsByName(serviceName);				
			}
			else if (serviceId != null)
			{
				sd = sf.getServiceDetails(serviceId);				
			}
			else
			{
				System.out.println("No service name provided or default service set.");
			}
			
			if (sd != null)
			{
				purgeQueue(sd);
			}
			else 
			{
				System.out.println("No service details available for the provided service.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running service details command: " + e.getResponseBody());
			logger.error("Error occurred while running service details command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running service details command: " + e.getMessage());
			logger.error("Error occurred while running service details command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private void purgeQueue(ServiceDetails sd) throws AuthenticationException, ClientProtocolException, IOException, JAXBException 
	{
		ServiceManagementContext ctx = new ServiceManagementContext(sd);
		SempSession session = new HttpSempSession(ctx.getSempUsername(), ctx.getSempPassword(), ctx.getSempUrl());
		AdminCommands com = new AdminCommands(session);
		boolean result = com.purgeQueueMessages(ctx.getVpnName(), queueName);
		
		if (result)
			System.out.println("Messages purged succesfully.");
		else
			System.out.println("Purging failed for queue: " + queueName);
	}
}
