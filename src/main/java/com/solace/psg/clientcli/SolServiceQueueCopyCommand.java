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
import com.solace.psg.sempv2.admin.model.ServiceManagementContext;
import com.solace.psg.sempv2.apiclient.ApiException;


import com.solace.psg.util.queuecopy.SimpleQueueCopy;
import com.solace.psg.util.queuecopy.VPN;
import com.solace.psg.sempv2.ServiceManager;


import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


/**
 * Command class to handle queue copy.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "copy", description = "Copy queue.")
public class SolServiceQueueCopyCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceQueueCopyCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParamLocal local;

	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParamRemote remote;

    static class ExcParamLocal {
        @Option(names = {"-localServiceName", "-ln"}, required = true) String localServiceName;
        @Option(names = {"-localServiceId", "-lid"}, required = true) String localServiceId;
    }

    static class ExcParamRemote {
        @Option(names = {"-remoteServiceName", "-rn"}, required = true) String remoteServiceName;
        @Option(names = {"-remoteServiceId", "-rid"}, required = true) String remoteServiceId;
    }

    static class ParamSubscription {
        @Option(names = {"-remoteServiceName", "-rn"}, required = true) String remoteServiceName;
        @Option(names = {"-remoteServiceId", "-rid"}, required = true) String remoteServiceId;
    }

	@Option(names = {"-lq", "-localQueue"}, arity = "1", description = "local queue name")
	private String localQueueName;	

	@Option(names = {"-lu", "-localUsername"}, arity = "0..1", description = "local queue username")
	private String localQueueUsername;	
	
	@Option(names = {"-lp", "-localPassword"}, arity = "0..1", description = "local queue password")
	private String localQueuePassword;	
	
	@Option(names = {"-rq", "-remoteQueue"}, arity = "1", description = "remote queue name")
	private String remoteQueueName;	

	@Option(names = {"-ru", "-remoteUsername"}, arity = "0..1", description = "remote queue username")
	private String remoteQueueUsername;	
	
	@Option(names = {"-rp", "-remotePassword"}, arity = "0..1", description = "remote queue password")
	private String remoteQueuePassword;	

	@Option(names = {"-mn", "-messageNumber"}, arity = "1", description = "message number to copy")
	private long messageNumber;	

    @Option(names = {"-s", "-secure"}, description = "Connect securely", defaultValue = "true", arity = "0..1") 
    private boolean secure;

    @Option(names = {"-r", "-remove"}, description = "Remove messages from the source queue", defaultValue = "false", arity = "0..1") 
    private boolean remove;

    @Option(names = {"-dmq"}, description = "Sets DMQ eligible flag to messages. This can be useful when messages are moved back from a DMQ to another queue.", defaultValue = "false", arity = "0..1") 
    private boolean dmq;

	@Option(names = {"-ttl"}, description = "Sets a TTL value for all messages t be processed.")
	private long ttl;	

    /**
	 * Initialises a new instance of the class.
	 */
	public SolServiceQueueCopyCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" copy - Copies / moves messages from one queue to another.");

	    System.out.println(" Example command: sol service queue copy [-ln=<localServiceName>] -lq=<localQueueName> -lu=<local username> -lp=<local password> -rn=<remoteServiceName> -rq=<remoteQueueName> -ru=<remote username> -rp=<remote password> -mn=<message number to copy> [-r]");
	    System.out.println(" Example command: sol service queue copy -lq=<source queue name> -rq=<target queue name> -mn=<message number>\n");
	    System.out.println(" Example command: sol service queue copy -lq=<source queue name> -rq=<target queue name> -mn=<message number> -dmq -ttl=<ttl value> \n");
	    System.out.println(" When -r | -remove is used, messages are removed from the source queue, which makes this a move operation.");
	    System.out.println(" When -dmq is used, messages' DMQ eligible flag will be set to true.");
	    System.out.println(" When remote service name or Id is not specified, the copy operation works only on one broker.");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running queue copy command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			if (remove)
				System.out.println("Moving messages:");	
			else
				System.out.println("Copying messages:");	
			
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
			if (local != null && local.localServiceId != null)
			{
				sd = sm.getServiceDetails(local.localServiceId);
			}
			else if (local != null && local.localServiceName != null)
			{
				sd = sm.getServiceDetailsByName(local.localServiceName);
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
					
			ServiceDetails rsd = null;
			if (remote == null)
				rsd = sd;
			else if (remote.remoteServiceId != null)
			{
				rsd = sm.getServiceDetails(remote.remoteServiceId);
			}
			else if (remote.remoteServiceName != null)
			{
				rsd = sm.getServiceDetailsByName(remote.remoteServiceName);
			}
			
			if (sd != null && rsd != null)
			{
				ServiceManagementContext lsc = new ServiceManagementContext(sd);
				ServiceManagementContext rsc = new ServiceManagementContext(rsd);
				
				String lUrl = lsc.getSecureSmfUrlWithPrefix();
				String rUrl = rsc.getSecureSmfUrlWithPrefix();
				if (!secure)
				{
					lUrl = lsc.getSmfUrlWithPrefix();
					rUrl = rsc.getSmfUrlWithPrefix();
				}
					
				VPN localVpn = new VPN(lUrl, lsc.getVpnName(), lsc.getUserUsername(), lsc.getUserPassword());
				VPN remoteVpn = new VPN(rUrl, rsc.getVpnName(), rsc.getUserUsername(), rsc.getUserPassword());
				
				if (localQueueUsername != null)
				{
					localVpn.setUsername(localQueueUsername);
					localVpn.setPassword(localQueuePassword);
				}
	
				if (remoteQueueUsername != null)
				{
					remoteVpn.setUsername(remoteQueueUsername);
					remoteVpn.setPassword(remoteQueuePassword);
				}
				
				SimpleQueueCopy sqc = new SimpleQueueCopy(localVpn, remoteVpn, localQueueName, remoteQueueName, messageNumber, remove);
				sqc.setDmqEligible(dmq);
				sqc.setTtl(ttl);
				
				Thread thread = new Thread(sqc);
				thread.start();			
				
				while (sqc.getStatus() != SimpleQueueCopy.STATUS_COMPLETED && sqc.getStatus() != SimpleQueueCopy.STATUS_ERRORED)
				{
					Thread.sleep(1000);
					System.out.print(".");
				}
				
				System.out.println("");
				if ((sqc.getStatus() == SimpleQueueCopy.STATUS_COMPLETED))
					if (remove)
						System.out.println(sqc.getMessagesCopied() + " messages moved successfully.");
					else
						System.out.println(sqc.getMessagesCopied() + " messages copied successfully.");
				else
					System.out.println("Error copying messages. Check logs for more details.");	
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
