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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.clientcli.sempv1.CliToSempHelper;
import com.solace.psg.sempv2.admin.model.ServiceDetails;
import com.solace.psg.sempv2.admin.model.ServiceManagementContext;
import com.solace.psg.sempv2.interfaces.ServiceFacade;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


/**
 * Command class to handle SDKPerf tasks.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "callCli", description = "Executes a cli command on a service via Semp v1.")
public class SolHammerCallCliCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolHammerCallCliCommand.class);
		
	@Option(names = {"-h", "-help"})
	private boolean help;

	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParam excl;

    static class ExcParam {
        @Option(names = "-serviceName", required = true) String serviceName;
        @Option(names = "-serviceId", required = true) String serviceId;
    }

	@Parameters(index = "0", arity = "1", description="the cli command to be executed")
	private String input;

	/**
	 * Initialises a new instance of the class.
	 */
	public SolHammerCallCliCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol hammer callCli \n");

	    System.out.println(" Example command: sol hammer callCli \"show version\" ");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running cliToSemp command.");
		
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
			
			ServiceFacade sf = new ServiceFacade(token);
			String ctxServiceId = ConfigurationManager.getInstance().getCurrentServiceId();
			String ctxServiceName = ConfigurationManager.getInstance().getCurrentServiceName();
						
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
				
				String cliToSempPath = ConfigurationManager.getInstance().getCliToSempPath();
				String perlPath = ConfigurationManager.getInstance().getPerlPath();
			
				if (cliToSempPath == null || perlPath == null)
				{
					System.out.println("Set config properties for cliToSempPath and perlPath before calling this command.");
					return;
			    }
				
				// Generate CLI command
				CliToSempHelper helper = new CliToSempHelper(perlPath, cliToSempPath);
				String cliToSempCommand = helper.generateCommand(input);
				
				Process process = Runtime.getRuntime()
				        .exec(cliToSempCommand, null, new File(cliToSempPath));
				
				String sempCommand = getCommand(process);
				System.out.println("Executing command. Service SEMP v1 response: ");
				String response = helper.executeCommand(ctx.getSempV1Url(), ctx.getSempUsername(), ctx.getSempPassword(), sempCommand);
				System.out.println(response);
				
				System.exit(0);
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}			
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running  command: " + e.getMessage());
			logger.error("Error occured while running  command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	public static String getCommand(Process process) throws IOException {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    StringBuffer sb = new StringBuffer();
	    String line = "";
	    while ((line = reader.readLine()) != null) {
	        sb.append(line);
	    }
	    
	    int start = sb.indexOf("<rpc");
	    return sb.substring(start);
	}
}
