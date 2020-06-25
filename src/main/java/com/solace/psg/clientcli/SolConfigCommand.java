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

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle login.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "config", description = "Config settings.")
public class SolConfigCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolConfigCommand.class);

	@Option(names = {"-e", "-encrypted"}, fallbackValue = "true", description = "Sets whether the credentials should be encrypted." )
	private Boolean encrypt;
	
	@Option(names = {"-p", "-prompt"}, fallbackValue = "true", description = "Sets Prompt to confirm flag to true or false."  )
	private Boolean prompt;

	@Option(names = {"-r", "-reset"}, fallbackValue = "false", description = "Resets the configuration."  )
	private Boolean reset;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParam excl;

	@Parameters(index = "0", arity = "0..1", description="the profile name")
	private String profileName;
	
    static class ExcParam {
	@Option(names = {"-l", "-load"}, defaultValue = "false", description = "Loads a configuration with a provided profile name."  )
	private Boolean load;
	@Option(names = {"-d", "-delete"}, defaultValue = "false", description = "Deletes a configuration with a provided profile name."  )
	private Boolean delete;
	@Option(names = {"-s", "-save"}, defaultValue = "false", description = "Saves a configuration with a provided profile name."  )
	private Boolean save;
    }	
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	private boolean changed = false;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolConfigCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol config [-e, -encrypted=true|false] default: true \n");
	    System.out.println(" sol config [-p, -prompt=true|false] default: true \n");
	    System.out.println(" sol config [-r, -reset=true|false] default: false\n");

	    System.out.println(" Example config command: sol config -e -p");
	    System.out.println(" Example reset command: sol config -r");
	    System.out.println(" Example save command: sol config -s seProfile");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running config command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			ConfigurationManager config = ConfigurationManager.getInstance();
						
			if (encrypt != null)
			{
				config.setEncryptDetails(encrypt);
				changed = true;
			}
			if (prompt != null)
			{
				config.setPromptToConfirm(prompt);
				changed = true;
			}

			if (reset != null)
			{
				config.reset();
				changed = true;
			}
			
			if (changed)
			{
				// store the input data into the configuration file.
				config.store();
			
				System.out.println("Config values saved.");
			}
			
			if (excl != null)
			{
				if (profileName == null || profileName.isEmpty())
				{	
					System.out.println("Specify a profile name.");
					return;
				}
				if (excl.delete)
				{
					ConfigurationManager.getInstance().deleteConfig(profileName);
					System.out.println("Profile deleted successfully.");
				}
				else if (excl.save)
				{
					ConfigurationManager.getInstance().saveConfig(profileName);
					System.out.println("Profile Saved successfully.");
				}
				else if (excl.load)
				{
					ConfigurationManager.getInstance().loadConfig(profileName);
					System.out.println("Profile loaded successfully.");
				}
			}

		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running config command: " + e.getMessage());
			logger.error("Error occurred while running config command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
