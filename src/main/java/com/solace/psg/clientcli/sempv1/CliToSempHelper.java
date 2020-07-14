/**
 * 
 */
package com.solace.psg.clientcli.sempv1;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.solace.psg.sempv1.HttpSempSession;
import com.solace.psg.sempv1.SempSession;

/**
 * Class to be used for Cli-To-Semp tool. 
 * @author VictorTsonkov
 *
 */
public class CliToSempHelper
{
	String cliToSempCommand = "cli-to-semp";
	String perlPath;
	String cliToSempPath;
	
	/**
	 * 
	 */
	public CliToSempHelper(String perlPath, String cliToSempPath)
	{
		this.perlPath = perlPath;
		this.cliToSempPath = cliToSempPath;
	}
	
	public String executeCommand(String url, String username, String password, String command) throws AuthenticationException, IOException
	{
		SempSession session = new HttpSempSession();
		session.setSolaceSEMPURI(url);
		session.setSolaceSEMPUsername(username);
		session.setSolaceSEMPPassword(password);
		session.open();
		CloseableHttpResponse response = session.executeRequest(command);
		
		HttpEntity httpEntity = response.getEntity();
		String apiOutput = EntityUtils.toString(httpEntity);
		
		session.close();

		return apiOutput;
	}

	/**
	 * Generates a process call command.
	 * @param cliCommand
	 * @return
	 */
	public String generateCommand(String cliCommand)
	{
		StringBuffer sb = new StringBuffer();
		
		boolean isWindows = System.getProperty("os.name")
				  .toLowerCase().startsWith("windows");
		
		//"<path to perl>\\perl.exe <path to cli-to-semp>\\cli-to-semp <input command>"
		sb.append(perlPath);
		if (!perlPath.endsWith(File.separator))
				sb.append(File.separator);
		if (isWindows)
			sb.append("perl.exe ");
		else
			sb.append("perl ");
		
		sb.append(cliToSempPath);
		if (!cliToSempPath.endsWith(File.separator))
				sb.append(File.separator);
		
		sb.append(cliToSempCommand);sb.append(" ");
		
		sb.append(cliCommand);
		
		return sb.toString();		
	}
}
