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
package com.solace.psg.clientcli.sempv1;

import java.io.IOException;


import javax.xml.bind.JAXBException;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv1.AdminCommands;
import com.solace.psg.sempv1.HttpSempSession;
import com.solace.psg.sempv1.SempSession;
import com.solace.psg.sempv2.admin.model.ServiceDetails;
import com.solace.psg.sempv2.admin.model.ServiceManagementContext;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.ServiceManager;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle service lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "purge",description = "Purge service queue.")
public class SolServiceQueuePurgeCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceQueuePurgeCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@Parameters(index = "0", arity="1", description = "The queue name.")
	private String queueName;
	
	
	@Option(names = {"-serviceName"})
	private String serviceName;	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceQueuePurgeCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service queue purge <queueName> [-serviceName=<name>] \n");
	    System.out.println(" -serviceName - the name of the service.");
	    System.out.println(" <queueName> - the name of the queue to be purged.");
	    System.out.println(" Example command: sol service queue purge testQueue");
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
			
			ServiceManager sf = new ServiceManager(token);
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
		SempSession session = new HttpSempSession(ctx.getSempUsername(), ctx.getSempPassword(), ctx.getSempV1Url());
		AdminCommands com = new AdminCommands(session);
		boolean result = com.purgeQueueMessages(ctx.getVpnName(), queueName);
		
		if (result)
			System.out.println("Messages purged succesfully.");
		else
			System.out.println("Purging failed for queue: " + queueName);
	}
}
