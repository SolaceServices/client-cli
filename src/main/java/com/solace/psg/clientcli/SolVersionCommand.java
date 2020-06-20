/**
 * 
 */
package com.solace.psg.clientcli;

import picocli.CommandLine.Command;

/**
 * Command class to display version.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "version",description = "Displays the library version.")
public class SolVersionCommand implements Runnable 
{

	/**
	 * 
	 */
	public SolVersionCommand()
	{
		// TODO Auto-generated constructor stub
	}

	public void run()
	{
		System.out.println("Solace Client CLI version: 0.0.1");
	}
	

}
