/**
 * 
 */
package com.solace.psg.clientcli.utils;

import com.solace.psg.sempv2.admin.model.MqttSubscription;
import com.solace.psg.sempv2.admin.model.SmfSubscription;
import com.solace.psg.sempv2.admin.model.Subscription;
import com.solace.psg.sempv2.admin.model.SubscriptionDirection;
import com.solace.psg.sempv2.admin.model.SubscriptionType;

import picocli.CommandLine.ITypeConverter;

/**
 * Class to convert subscription inputs.
 * 
 *
 */
public class SubscriptionConverter implements ITypeConverter<Subscription>
{

	/**
	 * Initialises a new instance of the class.
	 */
	public SubscriptionConverter()
	{
	}

	/**
	 * Converts a string format <topicName> <IN>|<OUT> <D>|<G>|<DA>
	 */
	@Override
	public Subscription convert(String value) throws Exception
	{
		if (value == null || value.isEmpty())
			return null;

		Subscription sub = null;
		
		String[] params = value.split(" ");

		String subscriptionName = params[0];
		SubscriptionDirection direction = SubscriptionDirection.Ingoing;
		SubscriptionType type = SubscriptionType.Direct;
		boolean smf = true;
		
		if (params.length > 1)
		{
			if (params[1].equalsIgnoreCase("in"))
				direction = SubscriptionDirection.Ingoing;
			else if (params[1].equalsIgnoreCase("out"))
				direction = SubscriptionDirection.Outgoing;
			else
				throw new Exception("The subscription direction value should be <IN> or <OUT>, instead is: " + params[1]);
		}

		if (params.length > 2)
		{
			if (params[2].equalsIgnoreCase("d"))
				type = SubscriptionType.Direct;
			else if (params[2].equalsIgnoreCase("da"))
				type = SubscriptionType.DirectDeliverAlways;
			else if (params[2].equalsIgnoreCase("g"))
				type = SubscriptionType.Guaranteed;
			else
				throw new Exception("The subscription type value should be <D>, <DA> or <G>, instead is: " + params[2]);
		}
		if (params.length > 3)
		{
			if (params[3].equalsIgnoreCase("smf"))
				smf = true;
			else if (params[3].equalsIgnoreCase("mqtt"))
				smf = false;
			else
				throw new Exception("The subscription type value should be <SMF> or <MQTT>, instead is: " + params[3]);
		}

		if (smf)
			sub = new SmfSubscription(subscriptionName, direction, type);
		else
			sub = new MqttSubscription(subscriptionName);

		return sub;
	}
}
