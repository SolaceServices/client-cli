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
import java.nio.file.Path;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.apiclient.ApiException;

import com.solace.psg.util.FileUtils;
import com.solace.tools.solconfig.Commander;
import com.solace.tools.solconfig.RestCommandList;
import com.solace.tools.solconfig.SempClient;
import com.solace.tools.solconfig.Utils;
import com.solace.tools.solconfig.model.AttributeType;
import com.solace.tools.solconfig.model.ConfigBroker;
import com.solace.tools.solconfig.model.ConfigObject;

import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.sempv2.VpnManager;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle configuration restore.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "restore", description = "restores a service config from a backup config file.")
public class SolServiceConfigRestoreCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceConfigRestoreCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParam excl;

    static class ExcParam {
        @Option(names = {"-serviceName", "-sn"}, required = true) String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = true) String serviceId;
    }

	@Parameters(index = "0", arity = "1", description="the config file path")
	private Path filePath;

	//@Option(names = {"-f", "-filename"} , defaultValue = Option.NULL_VALUE,  description="Sets a custom opaque password for the password encryption.")
	//private String opaquePassword;	

    @Option(names = {"-k", "--insecure"}, description = "Allow insecure server connections when using SSL")
    private boolean insecure = false;

    @Option(names = "--cacert", description = "CA certificate file to verify peer against when using SSL")
    private Path cacert;

	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceConfigRestoreCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service config restore <params> \n");
	    System.out.println(" restore - restores a service from a config file");

	    System.out.println(" Example command: sol service config restore <filename>");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running config backup command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Restoring service configuration ...");	
			
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
				VpnManager vf = new VpnManager(sd);

				String host = vf.getDefaultVpnContext().getSempV1Url();
				host = host.substring(0, host.indexOf("/SEMP"));
				
				String username = vf.getDefaultVpnContext().getSempUsername();
				String password = vf.getDefaultVpnContext().getSempPassword();
	
				SempClient sempClient = new SempClient(host, username, password, insecure, cacert);								
				Commander commander = Commander.ofSempClient(sempClient);
		        commander.setCurlOnly(false);
				
		        commander.update(filePath);
		        
		        System.out.println("Config backup created successfully.");
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
	
	/**
	 * Print content.
	 * @param configBroker
	 * @param filename
	 * @throws IOException
	 */
	private void printFile(ConfigBroker configBroker, String filename) throws IOException
	{
		//Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		//String outgoingRequest = gson.toJson(configBroker);
		FileUtils.writeReponseToFile(filename, configBroker.toString().getBytes());
		
		//FileUtils.writeReponseToFile(filename, outgoingRequest.getBytes());
	}
}
