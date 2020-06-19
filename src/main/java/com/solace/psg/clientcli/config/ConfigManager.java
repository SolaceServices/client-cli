/**
 * 
 */
package com.solace.psg.clientcli.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class to handle configuration of the CLI. 
 * @author VictorTsonkov
 *
 */
public class ConfigManager
{

	private String configPath;
	
	private Properties props;
	
	/**
	 * Initialises a new instanc of the class.
	 * @throws IOException 
	 */
	public ConfigManager(String configPath) throws IOException
	{
		this.configPath = configPath;
		props = new Properties();
		FileInputStream in = new FileInputStream(configPath);
		props.load(in);
		in.close();
	}
	
	/**
	 * Saves properties.
	 * @throws IOException 
	 */
	public void saveProperties(String comment) throws IOException
	{
		FileOutputStream out = new FileOutputStream(configPath);
		props.store(out, comment);
		out.close();	
	}
	
	/**
	 * Gets the whole collection of properties.
	 * @return
	 */
	public Properties getProperties()
	{
		return props;
	}
}
