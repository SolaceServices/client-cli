/**
 * 
 */
package com.solace.psg.clientcli;

import picocli.CommandLine.Command;

/**
 * Sol Command base class.
 * @author VictorTsonkov
 *
 */
@Command(subcommands = {
			      SolLoginCommand.class,
			      SolVersionCommand.class
})
public class SolCommand implements Runnable 
{

	/**
	 * Initialises a new instance of the class.
	 */
	public SolCommand()
	{
	}
	
	@Command(name = "help")
	public void helpCommand() {
	    System.out.println("Usage sol: sol command [parameters] \n");  
	    System.out.println("To login to Solace Cloud Console Account:");
	    System.out.println(" sol login [-u, -username=<username>] [-p, -password=<password>] [-n] \n");
	    System.out.println("To display the current Client CLI version:");
	    System.out.println(" sol version \n");
	    System.out.println(" Type sol <command> -h or -help for detailed help on the command usage. \n");
	}

	public void run()
	{
		System.out.println("For detailed usage run with 'help' parameter.");
	}

}
