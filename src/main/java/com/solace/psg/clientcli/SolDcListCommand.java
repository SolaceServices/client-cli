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
import com.solace.psg.sempv2.admin.model.DataCenter;

import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.tablereporter.Block;
import com.solace.psg.tablereporter.Board;
import com.solace.psg.tablereporter.Table;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle DC lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "list",description = "Lists dc details.")
public class SolDcListCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolDcListCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolDcListCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol dc list \n");
	    System.out.println(" list - lists all data centers for Solace Cloud Console Account");

	    System.out.println(" Example command: sol dc list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running DC list command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Listing Data centers:");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceManager sm = new ServiceManager(token);	
			List<DataCenter> dcs = sm.getDataCenters();
			
			printResults(dcs, "");		
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running list command: " + e.getResponseBody());
			logger.error("Error occurred while running list command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running list command: " + e.getMessage());
			logger.error("Error occurred while running list command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private void printResults(List<DataCenter> dcs, String message) throws IOException
	{
		System.out.println(message);
		logger.debug("Printing DC list");
		
		List<String> headersList = Arrays.asList("Datacenter ID", "Provider", "Display name", "Certificate ID", "Continent", "Access type", "Cloud type", "Private", "Available", "Admin state", "Latitude ", "Longtitude");

		List<List<String>> rowsList = new ArrayList<List<String>>(dcs.size());

		for (DataCenter dc : dcs)
		{
			rowsList.add(Arrays.asList(StringUtils.abbreviate(dc.getId(), 28), dc.getProvider(), StringUtils.abbreviate(dc.getDisplayName().replace("\t", " "), 30), dc.getServerCertificateId(), dc.getContinent(), dc.getAccessType(), dc.getCloudType(), ""+ dc.getIsPrivate(), ""+ dc.getAvailable(), dc.getAdminState(), dc.getLat(), dc.getLng() ));
		}
		
		List<Integer> colAlignList = Arrays.asList(Block.DATA_MIDDLE_LEFT,Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_CENTER, Block.DATA_CENTER, Block.DATA_MIDDLE_RIGHT, Block.DATA_MIDDLE_RIGHT);
		List<Integer> colWidthsListEdited = Arrays.asList(29, 12, 35, 14, 14, 11, 11, 9, 10, 14, 10, 11);
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
