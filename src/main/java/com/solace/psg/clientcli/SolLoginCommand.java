/**
 * 
 */
package com.solace.psg.clientcli;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.interfaces.ServiceFacade;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to display version.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "login",description = "Login to a solace cloud account.")
public class SolLoginCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolLoginCommand.class);

	@Option(names = {"-u", "-username"})
	private String username;
	
	@Option(names = {"-p", "-password"})	
	private String password;
	
	@Option(names = {"-n"})
	private boolean show;
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolLoginCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol login [-u, -username=<username>] [-p, -password=<password>] [-n] \n");
	    System.out.println(" -username - the email or username to login to Solace Cloud Console Account");
	    System.out.println(" -password - the password to login to Solace Cloud Console Account");
	    System.out.println(" -n        - does not print in command line the token generated for the Solace Cloud Console Account");
	    System.out.println(" Example command: sol login -u=John.Smith@example.com -p=hotshot -n");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running login command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			ConfigurationManager config = ConfigurationManager.getInstance();
			ServiceFacade sf = new ServiceFacade(username, password);
			
			String token = sf.getCurrentAccessToken();
			
			if (!show)
				System.out.println("The following login token was generated: \n " + token);
			
			if (token != null)
			{
				config.setCloudAccountUsername(username);
				config.setCloudAccountPassword(password);
				config.setCloudAccountToken(token);
				
				// store the input data into the configuration file.
				config.store();
				
				System.out.println("Login to Solace Cloud Console successful.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running login command: " + e.getResponseBody());
			logger.error("Error occured while running login command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running login command: " + e.getMessage());
			logger.error("Error occured while running login command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
