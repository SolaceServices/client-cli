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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.clientcli.sempv1.CliToSempHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


/**
 * Command class to handle SDKPerf tasks.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "cliToSemp", description = "Generates a cli command using Cli-to-semp tool.")
public class SolHammerCliToSempCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolHammerCliToSempCommand.class);
		
	@Option(names = {"-h", "-help"})
	private boolean help;

	@Parameters(index = "0", arity = "1", description="the input parameter for the command")
	private String input;

	/**
	 * Initialises a new instance of the class.
	 */
	public SolHammerCliToSempCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol hammer cliToSemp \n");

	    System.out.println(" Example command: sol hammer cliToSemp \"show version\" ");
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
			System.out.println("Generating SEMP v1 command ...");	
			
			String cliToSempPath = ConfigurationManager.getInstance().getCliToSempPath();
			String perlPath = ConfigurationManager.getInstance().getPerlPath();
			
			CliToSempHelper helper = new CliToSempHelper(perlPath, cliToSempPath);
			String command = helper.generateCommand(input);
			
			Process process = Runtime.getRuntime()
			        .exec(command, null, new File(cliToSempPath));
			printResults(process);		
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running  command: " + e.getMessage());
			logger.error("Error occured while running  command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	public static void printResults(Process process) throws IOException {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    String line = "";
	    while ((line = reader.readLine()) != null) {
	        System.out.println(line);
	    }
	}
}
