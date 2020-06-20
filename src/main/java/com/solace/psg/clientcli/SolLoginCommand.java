/**
 * 
 */
package com.solace.psg.clientcli;

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
	@Option(names = {"-u", "-username"})
	private String username;
	
	@Option(names = {"-p", "-password"})	
	private String password;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolLoginCommand()
	{
	}

	public void run()
	{
		System.out.println("Login to Solace Cloud Console successful.");
	}
	

}
