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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.apiclient.ApiException;

import com.solace.psg.sempv2.config.model.MsgVpnQueue;
import com.solace.psg.sempv2.config.model.MsgVpnQueue.AccessTypeEnum;
import com.solace.psg.sempv2.config.model.MsgVpnQueue.PermissionEnum;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.sempv2.VpnManager;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle queue create.
 * 
 * 
 *
 */
@Command(name = "create", description = "Creates queue.")
public class SolServiceQueueCreateCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceQueueCreateCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParam excl;

    static class ExcParam {
        @Option(names = {"-serviceName", "-sn"}, required = true) String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = true) String serviceId;
    }

	@Parameters(index = "0", arity = "1", description="the queue name")
	private String queueName;

	@Option(names = {"-e", "-exclusive"} , defaultValue = "false",  description="Indicates the queue should be created as exclusive. Default is non-exlusive.")
	private boolean exclusive;	

	@Option(names = {"-rt", "-respectTtl"} , defaultValue = "false",  description="Indicates if respect TTL should be enabled. Default is false.")
	private boolean respectTtl;	

	@Option(names = {"-mt", "-maxTtl"} , defaultValue = ClientCliGlobals.DEFAULT_QUEUE_TTL,  description="Sets the queue maximum TTL. Default is 0.")
	private long maxTtl;	

	@Option(names = {"-q", "-quota"} , defaultValue = ClientCliGlobals.DEFAULT_QUEUE_QUOTA,  description="Sets the maximum queue quota in Mb. Default is 5000 Mb.")
	private int quota;	

	@Option(names = {"-dmq"} , defaultValue = Option.NULL_VALUE,  description="Sets a specific dead message queue name. Otherwise, the DMQ will be set to the default one. ")
	private String dmQueueName;	

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

	    System.out.println(" Example command: sol service queue create <queueName> -exclusive");
	    System.out.println(" Example command: sol service queue create <queueName> -q=<quota size in Mb> -dmq=<specific DMQ> -rt");
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
				MsgVpnQueue request = new MsgVpnQueue();
				request.setQueueName(queueName);
				request.setMaxMsgSpoolUsage(Long.valueOf(quota));
				request.setPermission(PermissionEnum.CONSUME);
				request.setMaxTtl(maxTtl);
				request.respectTtlEnabled(respectTtl);
				request.setEgressEnabled(true);
				request.setIngressEnabled(true);
				if (exclusive)
					request.accessType(AccessTypeEnum.EXCLUSIVE);
				else
					request.accessType(AccessTypeEnum.NON_EXCLUSIVE);
	
				if (dmQueueName != null && !dmQueueName.isBlank())
					request.setDeadMsgQueue(dmQueueName);
				
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
