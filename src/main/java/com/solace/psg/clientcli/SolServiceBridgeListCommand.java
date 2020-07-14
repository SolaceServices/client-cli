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
import org.apache.maven.shared.utils.StringUtils;

import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.config.model.MsgVpnBridge;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.sempv2.VpnManager;
import com.solace.psg.tablereporter.Block;
import com.solace.psg.tablereporter.Board;
import com.solace.psg.tablereporter.Table;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle bridge list.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "list", description = "Lists service bridge details.")
public class SolServiceBridgeListCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceBridgeListCommand.class);
	
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
	public SolServiceBridgeListCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service bridge list \n");
	    System.out.println(" list - lists all bridges.");

	    System.out.println(" Example command: sol service bridge list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running bridge list command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Listing bridges:");	
			
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
				List<MsgVpnBridge> bridges = vf.getBridges();

				printResults(bridges, "");		
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running bridge command: " + e.getResponseBody());
			logger.error("Error occured while running bridge command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running bridge command: " + e.getMessage());
			logger.error("Error occured while running bridge command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private void printResults(List<MsgVpnBridge> bridges, String message) throws IOException
	{
		System.out.println(message);
		logger.debug("Printing bridge list.");
		
		List<String> headersList = Arrays.asList("Bridge name", "Enabled", "Virtual router", "Max TTL");

		List<List<String>> rowsList = new ArrayList<List<String>>(bridges.size());

		for (MsgVpnBridge b : bridges)
		{
			rowsList.add(Arrays.asList(StringUtils.abbreviate(b.getBridgeName(), 23), "" + b.isEnabled(), b.getBridgeVirtualRouter().getValue(), "" + b.getMaxTtl()));
		}
		
		List<Integer> colAlignList = Arrays.asList(Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT);
		List<Integer> colWidthsListEdited = Arrays.asList(25, 10, 20, 10);
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
