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
package com.solace.psg.clientcli.sempv1;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv1.HttpSempSession;
import com.solace.psg.sempv1.LogType;
import com.solace.psg.sempv1.SempSession;
import com.solace.psg.sempv1.ShowCommands;
import com.solace.psg.sempv2.admin.model.ServiceDetails;
import com.solace.psg.sempv2.admin.model.ServiceManagementContext;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.ServiceManager;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle service logs.
 * 
 * 
 *
 */
@Command(name = "log", description = "Shows service logs.")
public class SolServiceLogCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceLogCommand.class);

	@Parameters(index = "0", arity = "1", description="The log type")
	private LogType logType;

	@Parameters(index = "1", arity = "0..1", defaultValue="20" ,description="The number of lines to fetch from the end of the log. Default is 20.")
	private int lineCount;

	@Option(names = {"-h", "-help"}, usageHelp = true)
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParam excl;
	
    static class ExcParam {
        @Option(names = {"-serviceName", "-sn"}, required = true) String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = true) String serviceId;
    }
    
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceLogCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service log log_type [line numbers] [-serviceName=<name>] [-serviceId=<id>] \n");
	    System.out.println(" -serviceName - the name of the service.");
	    System.out.println(" -serviceId - the id of the service.");
	    System.out.println(" Example command: sol service log event 30");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Retrieving " + logType.toString() + " log...");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
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
			if (excl != null && excl.serviceId != null)
			{
				sd = sm.getServiceDetails(excl.serviceId);
			}
			else if (excl != null && excl.serviceName != null)
			{
				sd = sm.getServiceDetailsByName(excl.serviceName);
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
				 displayLog(sd);
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
	
	private void displayLog(ServiceDetails sd) throws ClientProtocolException, IOException, JAXBException, SAXException, HttpException 
	{
		System.out.println("Retrieving " + logType.toString() + " log...");
		
		ServiceManagementContext ctx = new ServiceManagementContext(sd);
		SempSession session = new HttpSempSession(ctx.getSempUsername(), ctx.getSempPassword(), ctx.getSempV1Url());
		ShowCommands com = new ShowCommands(session);
		List<String> result = com.getLogTail(logType, lineCount);
		
		if (result != null && result.size() > 0)
		{
			if (logType == LogType.REST)
			{
				System.out.println("      Timestamp           |  Message VPN  |              RDP              |      REST Consumer     |  Local Address   |  Remote Address   | Error Response");
				System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------");					
				for (String line : result)				
					System.out.print(line);
			}
			else // Other log types don't contain new lines 
			{
				for (String line : result)				
					System.out.println(line);
			}
		}
		else
			System.out.println("No log entries were retrieved from the service.");
	}
}
