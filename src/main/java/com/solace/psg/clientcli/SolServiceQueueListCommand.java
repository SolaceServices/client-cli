/**
 * Copyright 2021 Solace Systems, Inc. All rights reserved.
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

import com.solace.psg.sempv2.config.model.MsgVpnQueue;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.sempv2.VpnManager;
import com.solace.psg.tablereporter.Block;
import com.solace.psg.tablereporter.Board;
import com.solace.psg.tablereporter.Table;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle queue list.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "list", description = "Lists service queue details.")
public class SolServiceQueueListCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceQueueListCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = {"-serviceName", "-sn"}, required = true) String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = true) String serviceId;
    }
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceQueueListCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service queue list \n");
	    System.out.println(" list - lists all queues.");

	    System.out.println(" Example command: sol service queue list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running queue list command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Listing queues:");	
			
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
				List<MsgVpnQueue> queues = vf.listQueues();

				printResults(queues, "");		
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running queue command: " + e.getResponseBody());
			logger.error("Error occured while running queue command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running queue command: " + e.getMessage());
			logger.error("Error occured while running queue command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private void printResults(List<MsgVpnQueue> queues, String message) throws IOException
	{
		System.out.println(message);
		logger.debug("Printing queue list.");
		
		List<String> headersList = Arrays.asList("Queue name", "Access type", "Ingress on", "Egress on", "Cons. Ack on", "Max spool", "DMQ");

		List<List<String>> rowsList = new ArrayList<List<String>>(queues.size());

		for (MsgVpnQueue q : queues)
		{
			rowsList.add(Arrays.asList(q.getQueueName(), q.getAccessType().toString(), "" + q.isIngressEnabled(), "" + q.isEgressEnabled(), "" +  q.isConsumerAckPropagationEnabled(), "" + q.getMaxMsgSpoolUsage() , StringUtils.abbreviate(q.getDeadMsgQueue(), 19) ));
		}
		
		List<Integer> colAlignList = Arrays.asList(Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT);
		List<Integer> colWidthsListEdited = Arrays.asList(25, 14, 11, 11, 13, 12, 20);
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
