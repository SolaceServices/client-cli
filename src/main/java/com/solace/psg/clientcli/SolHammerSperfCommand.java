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
import com.solace.psg.sempv2.admin.model.ServiceManagementContext;
import com.solace.psg.sempv2.apiclient.ApiException;

import com.solace.psg.sempv2.ServiceManager;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


/**
 * Command class to handle SDKPerf tasks.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "sperf", description = "Returns an SDKPerf connection string for a service.")
public class SolHammerSperfCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolHammerSperfCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@Option(names = {"-s", "-space"}, defaultValue = "false" ,description="Generates space instead of equal signs.")
	private boolean space;

	@Option(names = {"-ss", "-secured"}, defaultValue = "false" ,description="Generates secured SMF connection string.")
	private boolean secured;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParam excl;

    static class ExcParam {
        @Option(names = "-serviceName", required = true) String serviceName;
        @Option(names = "-serviceId", required = true) String serviceId;
    }

	/**
	 * Initialises a new instance of the class.
	 */
	public SolHammerSperfCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol hammer sperf \n");

	    System.out.println(" Example command: sol hammer sperf <serviceId> | <serviceName>");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running sperf command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Generating SDKPerf connection details...");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceManager sf = new ServiceManager(token);
			String ctxServiceId = ConfigurationManager.getInstance().getCurrentServiceId();
			String ctxServiceName = ConfigurationManager.getInstance().getCurrentServiceName();
			
			char delimiter = space ? ' ' : '=' ;
			
			ServiceDetails sd = null;
			if (excl != null && excl.serviceId != null)
			{
				sd = sf.getServiceDetails(excl.serviceId);
			}
			else if (excl != null && excl.serviceName != null)
			{
				sd = sf.getServiceDetailsByName(excl.serviceName);
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
				ServiceManagementContext ctx = new ServiceManagementContext(sd);
				
				String url = null; 
				if (ctx.getSmfUrl() == null || secured)
					url = ServiceManagementContext.SECURE_SMF_PREFIX + ctx.getSecureSmfUrl();
				else
					url = ServiceManagementContext.SMF_PREFIX + ctx.getSmfUrl();
				
				StringBuffer sb = new StringBuffer();
				
				boolean isWindows = System.getProperty("os.name")
						  .toLowerCase().startsWith("windows");
				
				if (isWindows)
					sb.append("sdkperf_java.bat ");
				else
					sb.append("./sdkperf_java.sh ");
				sb.append("-cip");sb.append(delimiter);sb.append(url);sb.append(" ");
				sb.append("-cu");sb.append(delimiter);sb.append(ctx.getUserUsername());sb.append("@");sb.append(ctx.getVpnName());sb.append(" ");
				sb.append("-cp");sb.append(delimiter);sb.append(ctx.getUserPassword());
				sb.append(" <sdkPerfParams>");
				System.out.println(sb.toString());
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running command: " + e.getResponseBody());
			logger.error("Error occured while running command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running  command: " + e.getMessage());
			logger.error("Error occured while running  command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
