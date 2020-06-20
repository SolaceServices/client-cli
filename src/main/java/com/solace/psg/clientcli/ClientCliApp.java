package com.solace.psg.clientcli;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import picocli.CommandLine;


/**
 * ClientCli main app. 
 *
 */
public class ClientCliApp 
{
	private static final Logger logger = LogManager.getLogger(ClientCliApp.class);

	public static void main( String[] args )
    {
		/*if (args.length < 2)
		{
			logger.error("Arguments must contain username and password passed: app.jar <uysername> <password>");
			System.exit(-1);
		}*/

        try
		{
        	CommandLine.run(new SolCommand(), args);
		}
		catch (Exception e)
		{
			logger.error("Client CLI App exception occured: {}, {}", e.getMessage(), e.getCause()); 
		}
    }
}
