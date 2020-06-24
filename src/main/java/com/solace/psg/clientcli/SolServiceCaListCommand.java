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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.config.model.CertAuthority;
import com.solace.psg.sempv2.config.model.MsgVpnClientProfile;
import com.solace.psg.sempv2.interfaces.ServiceFacade;
import com.solace.psg.sempv2.interfaces.VpnFacade;
import com.solace.psg.tablereporter.Block;
import com.solace.psg.tablereporter.Board;
import com.solace.psg.tablereporter.Table;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle certificate authority lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "list", description = "Lists service certificate authority details.")
public class SolServiceCaListCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceCaListCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = "-serviceName", required = true) String serviceName;
        @Option(names = "-serviceId", required = true) String serviceId;
    }
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceCaListCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service ca list \n");
	    System.out.println(" list - lists all certificate authorities.");

	    System.out.println(" Example command: sol service ca list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running certificate authority list command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Listing certificate authoritys:");	
			
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
				List<CertAuthority> cas = vf.listCAs(sd.getServiceId()); 

				printResults(cas, "");		
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running certificate authority command: " + e.getResponseBody());
			logger.error("Error occured while running certificate authority command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running certificate authority command: " + e.getMessage());
			logger.error("Error occured while running certificate authority command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private void printResults(List<CertAuthority> cas, String message) throws IOException
	{
		System.out.println(message);
		logger.debug("Printing certificate authority list.");
		
		List<String> headersList = Arrays.asList("Profile name", "Guar. Send", "Guar. Reci.", "Use Tx", "Bridge conn", "Allow create", "Max Ingress", "Max Egress", "Max subscr.", "Max Tx", "Max Sess Tx", "Max Conn Usr");

		List<List<String>> rowsList = new ArrayList<List<String>>(cas.size());

		for (CertAuthority ca : cas)
		{
			//rowsList.add(Arrays.asList(cp.getClientProfileName(), "" + cp.isAllowGuaranteedMsgSendEnabled(), "" + cp.isAllowGuaranteedMsgReceiveEnabled(), "" + cp.isTlsAllowDowngradeToPlainTextEnabled(), "" + cp.isAllowBridgeConnectionsEnabled(), "" + cp.isAllowGuaranteedEndpointCreateEnabled(), "" + cp.getMaxIngressFlowCount(), "" + cp.getMaxEgressFlowCount(), "" + cp.getMaxSubscriptionCount(), "" + cp.getMaxTransactedSessionCount(), "" + cp.getMaxTransactionCount(), ""  + cp.getServiceSmfMaxConnectionCountPerClientUsername()));
		}
		
		List<Integer> colAlignList = Arrays.asList(Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT);
		List<Integer> colWidthsListEdited = Arrays.asList(25, 11, 11, 10, 11, 12, 11, 11, 11, 9, 11, 12);
		int width = Board.getRecommendedWidth(colWidthsListEdited, true);
				
		Board board = new Board(width);	
		Table table = new Table(board, width, headersList, rowsList);		
		table.setColWidthsList(colWidthsListEdited);
		table.setColAlignsList(colAlignList);
		
		Block block = table.tableToBlocks();
		//block.setBelowBlock(summaryBlock);
		board.setInitialBlock(block);
		
		String tableString = board.build().getPreview();
		System.out.println(tableString);
	}

}
