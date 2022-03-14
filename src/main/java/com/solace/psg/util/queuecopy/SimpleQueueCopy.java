/**
 * 
 */
package com.solace.psg.util.queuecopy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.CapabilityType;
import com.solacesystems.jcsmp.ConsumerFlowProperties;
import com.solacesystems.jcsmp.EndpointProperties;
import com.solacesystems.jcsmp.FlowEventArgs;
import com.solacesystems.jcsmp.FlowEventHandler;
import com.solacesystems.jcsmp.FlowReceiver;
import com.solacesystems.jcsmp.JCSMPChannelProperties;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProducerEventHandler;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.ProducerEventArgs;
import com.solacesystems.jcsmp.ProducerFlowProperties;
import com.solacesystems.jcsmp.Queue;
import com.solacesystems.jcsmp.SessionEventArgs;
import com.solacesystems.jcsmp.SessionEventHandler;
import com.solacesystems.jcsmp.XMLMessageListener;
import com.solacesystems.jcsmp.XMLMessageProducer;

/**
 * A small class to copy messages from one queue to another.
 * @author VictorTsonkov
 *
 */
public class SimpleQueueCopy implements Runnable, SessionEventHandler, XMLMessageListener, FlowEventHandler, JCSMPProducerEventHandler, JCSMPStreamingPublishEventHandler
{
	private static final Logger logger = LogManager.getLogger(SimpleQueueCopy.class);
	
	public static final int STATUS_ERRORED = -1;
	public static final int STATUS_NOT_STARTED = 1;
	public static final int STATUS_COMPLETED = 2;
	public static final int STATUS_INITIALIZING = 3;	
	public static final int STATUS_PROCESSING = 4;
	
	public static final int DEFAULT_BUFFER_SIZE = 65536;
	public static final int DEFAULT_WINDOW_SIZE = 255;
	public static final int DEFAULT_TRANSPORT_WINDOW_SIZE = 0;
	public static final int DEFAULT_BATCH_SIZE = 1; // [1..50]
	
	private String sourceQueueName;
	private String targetQueueName;

	private Queue sourceQueue;
	private Queue targetQueue;
	
	private JCSMPSession sourceSession = null;
	private JCSMPSession targetSession = null;
	
    private ConsumerFlowProperties consFlowProps;
	private ProducerFlowProperties prodFlowProps;
	
	private FlowReceiver sourceReceiver;
	private XMLMessageProducer targetProducer;
		
	private VPN sourceVpn;
	private VPN targetVpn;
	
	private long messageCount = 0;
	private long messagesCopied = 0;
	
	private int batchSize = DEFAULT_BATCH_SIZE; // Currently copy only 1 by 1 message
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    private int sendBuffer;
    private int receiveBuffer;
    private int transportWindowSize = DEFAULT_TRANSPORT_WINDOW_SIZE;
    private int windowSize = DEFAULT_WINDOW_SIZE;
    private int waitTimeout = 60000;
    
    // Indicates when finished or no more queued messages 
    private boolean stopped = false;
    
    // if moved messages are acked on the source.
    private boolean move;

	private int status = STATUS_NOT_STARTED;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SimpleQueueCopy(VPN sourceVpn, VPN targetVpn, String sourceQueueName, String targetQueueName, long messageCount, boolean move)
	{
		this.sourceVpn = sourceVpn;
		this.targetVpn = targetVpn;
		this.sourceQueueName = sourceQueueName;
		this.targetQueueName = targetQueueName;
		this.messageCount = messageCount;
		this.move = move;
			
		this.sendBuffer = batchSize * bufferSize;
		this.receiveBuffer = batchSize * bufferSize;

		// Specific to Windows platform, the receive socket buffer size must be much larger than the send socket buffer size to prevent data loss when sending and receiving messages. 
		// The recommended ratio is 3 parts send buffer to 5 parts receive buffer.
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		if (isWindows)
		{
			receiveBuffer = receiveBuffer + (receiveBuffer * 3 )/ 5;
		}
	}
	
	/**
	 * Connect.
	 * @throws JCSMPException 
	 */
	public void connect() throws JCSMPException
	{
		status = STATUS_INITIALIZING;
		connectSource();
		connectTarget();
	}
	
	/**
	 * Disconnect.
	 * @throws JCSMPException 
	 */
	public void disconnect() throws JCSMPException
	{
		disconnectSource();
		disconnectTarget();
	}
	
	/**
	 * Start a copy process.
	 * @throws InterruptedException 
	 * @throws JCSMPException 
	 */
	public void copy() throws InterruptedException, JCSMPException
	{
		initFlows();
		status = STATUS_PROCESSING;

		logger.info("Copy process started for source queue {} and target queue {}", sourceQueueName, targetQueueName);
		sourceReceiver.start();
	
		while ((messagesCopied < messageCount) && !stopped)
		{
			logger.debug("Messages copied for queue {}, count : {}", sourceQueueName, messagesCopied);
			copyMessage();
		}	
		
		status = STATUS_COMPLETED;
		sourceReceiver.stop();
		logger.info("Copy process finished for source queue {} and target queue {}. total messages copied: {}", sourceQueueName, targetQueueName, messagesCopied);
	}
	
	/**
	 * Copying a single message.
	 * @throws JCSMPException 
	 */
	private void copyMessage() throws JCSMPException
	{
		BytesXMLMessage msg = sourceReceiver.receive(waitTimeout);
		if (msg == null)
		{
			stopped = true;
			System.out.println("Received no message for source queue within default receive timeout. Check messageCount.");
			logger.error("Received no message for source queue {}. Stopping the copy process.", sourceQueueName);
			//messagesCopied -= 1;
			return;
		}
		
		BytesXMLMessage tMsg = JCSMPFactory.onlyInstance().createMessage(msg);
		targetProducer.send(tMsg, targetQueue);
		if (move)
			msg.ackMessage();
		messagesCopied += 1;
	}

	/**
	 * Init flows.
	 * @throws JCSMPException
	 */
	private void initFlows() throws JCSMPException
	{
		logger.info("Initializing flows for source queue {} and target queue {}", sourceQueueName, targetQueueName);

		consFlowProps = new ConsumerFlowProperties();
	    prodFlowProps = new ProducerFlowProperties();
        EndpointProperties consEndpointProps = new EndpointProperties();
        consEndpointProps.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
       
        sourceQueue = JCSMPFactory.onlyInstance().createQueue(sourceQueueName);
        targetQueue = JCSMPFactory.onlyInstance().createQueue(targetQueueName);
        
        consFlowProps.setEndpoint(sourceQueue);
        consFlowProps.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);
        consFlowProps.setTransportWindowSize(transportWindowSize);
        
        ConsumerFlowProperties targetConsFlowProps = new ConsumerFlowProperties();
        targetConsFlowProps.setEndpoint(targetQueue);
        targetConsFlowProps.setNoLocal(true);
        targetConsFlowProps.setTransportWindowSize(transportWindowSize);
        
        // Set producer properties.
        prodFlowProps.setWindowSize(windowSize);
        prodFlowProps.setAckEventMode(JCSMPProperties.SUPPORTED_ACK_EVENT_MODE_WINDOWED);
        prodFlowProps.setRtrWindowedAck(true);
        
        targetProducer = targetSession.getMessageProducer(this);
        sourceReceiver = sourceSession.createFlow(null, consFlowProps, consEndpointProps, this);   
	}

	/**
	 * Gets processing status.
	 * @return
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * Gets message number copied.
	 * @return
	 */
	public long getMessagesCopied()
	{
		return messagesCopied;
	}
	
	@Override
	public void run()
	{
		try
		{
			connect();
			copy();
		}
		catch (InterruptedException e)
		{
			status = STATUS_ERRORED;
			System.out.println("Thread error while copying messages: " + e.getMessage());
			logger.error("Thread error while copying messages: {}", e.getMessage());
		}
		catch (JCSMPException e)
		{
			status = STATUS_ERRORED;
			System.out.println("Error while connecting to service: " + e.getMessage());
			logger.error("Error while connecting to service: {}", e.getMessage());
		}
		catch (Exception e)
		{
			status = STATUS_ERRORED;
			System.out.println("Unhandled error while running the copy process: " + e.getMessage());
			logger.error("Unhandled error while running the copy process: {}", e.getMessage());
		}
		finally
		{
			try
			{
				disconnect();
			}
			catch (JCSMPException e)
			{
				status = STATUS_ERRORED;
				System.out.println("Error while disconnecting from service: " + e.getMessage());
				logger.error("Error while disconnecting from service: {}", e.getMessage());
			}
		}
	}
	
	/**
	 * Connect to source VPN.
	 * @throws JCSMPException 
	 */
	private void connectSource() throws JCSMPException
	{
		logger.info( "Connecting to source VPN: {}",  sourceVpn.getName());	

		JCSMPProperties properties = new JCSMPProperties();
		
		String host = sourceVpn.getUrl();
		if (host.toLowerCase().startsWith("tcps:"))	
		{
			// If true then use the Trusted store properties.
			properties.setProperty(JCSMPProperties.SSL_VALIDATE_CERTIFICATE, false);
		}
			
		properties.setProperty(JCSMPProperties.HOST, host); 
		properties.setProperty(JCSMPProperties.VPN_NAME, sourceVpn.getName());
		properties.setProperty(JCSMPProperties.USERNAME, sourceVpn.getUsername());
		properties.setProperty(JCSMPProperties.PASSWORD, sourceVpn.getPassword());
		properties.setBooleanProperty(JCSMPProperties.REAPPLY_SUBSCRIPTIONS, true);
		properties.setProperty(JCSMPProperties.CLIENT_NAME, "CL_CLI_" + sourceVpn.getUsername() + "_" + properties.hashCode());		
		
        JCSMPChannelProperties cp = (JCSMPChannelProperties) properties.getProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES);
        
        cp.setConnectRetries(sourceVpn.getConnectRetries());
        cp.setReconnectRetries(sourceVpn.getReconnectRetries());
        cp.setConnectTimeoutInMillis(sourceVpn.getConnectTimeoutInMillis());
        cp.setReconnectRetryWaitInMillis(sourceVpn.getReconnectRetryWaitInMillis());
        cp.setConnectRetriesPerHost(sourceVpn.getConnectRetriesPerHost());
        cp.setKeepAliveIntervalInMillis(sourceVpn.getKeepAliveIntervalInMillis());
        
        cp.setSendBuffer(sendBuffer);
        cp.setReceiveBuffer(receiveBuffer);
        
        sourceSession = JCSMPFactory.onlyInstance().createSession(properties, null, this);
        sourceSession.connect();

        logger.info( "Connected to source VPN: {}",  sourceVpn.getName());	
        
       // Check capability to browse queues
       if (!sourceSession.isCapable(CapabilityType.BROWSER))
    	   throw new JCSMPException("Session is not capable of browsing messages");
	}
	
	/**
	 * Connect to target VPN.
	 * @throws JCSMPException 
	 */
	private void connectTarget() throws JCSMPException
	{
		logger.info("Connecting to target VPN: {}",  targetVpn.getName());	

		JCSMPProperties properties = new JCSMPProperties();
			
		properties.setProperty(JCSMPProperties.HOST, targetVpn.getUrl()); 
		properties.setProperty(JCSMPProperties.VPN_NAME, targetVpn.getName());
		properties.setProperty(JCSMPProperties.USERNAME, targetVpn.getUsername());
		properties.setProperty(JCSMPProperties.PASSWORD, targetVpn.getPassword());
		properties.setBooleanProperty(JCSMPProperties.REAPPLY_SUBSCRIPTIONS, true);
		properties.setProperty(JCSMPProperties.CLIENT_NAME, "CL_CLI_" + targetVpn.getUsername() + "_" + properties.hashCode());
				
		String host = targetVpn.getUrl();
		if (host.toLowerCase().startsWith("tcps:"))	
		{
			// If true then use the Trusted store properties.
			properties.setProperty(JCSMPProperties.SSL_VALIDATE_CERTIFICATE, false);
		}

		JCSMPChannelProperties cp = (JCSMPChannelProperties) properties.getProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES);
        
        cp.setConnectRetries(targetVpn.getConnectRetries());
        cp.setReconnectRetries(targetVpn.getReconnectRetries());
        cp.setConnectTimeoutInMillis(targetVpn.getConnectTimeoutInMillis());
        cp.setReconnectRetryWaitInMillis(targetVpn.getReconnectRetryWaitInMillis());
        cp.setConnectRetriesPerHost(targetVpn.getConnectRetriesPerHost());
        cp.setKeepAliveIntervalInMillis(targetVpn.getKeepAliveIntervalInMillis());

        cp.setSendBuffer(sendBuffer);
        cp.setReceiveBuffer(receiveBuffer);

        targetSession = JCSMPFactory.onlyInstance().createSession(properties, null, this);
        targetSession.connect();
        
		logger.info( "Connected to target VPN: {}",  targetVpn.getName());	
	}
	
	/**
	 * Disconnects source.
	 */
	public void disconnectSource()
	{
		logger.info( "Disconnecting from VPN source: {}",  sourceVpn.getName());				

		if (sourceSession != null)
			sourceSession.closeSession();
		
		logger.info( "Disconnected from VPN source: {}",  sourceVpn.getName());				
	}
	
	/**
	 * Disconnects target.
	 */
	public void disconnectTarget()
	{
		logger.info( "Disconnecting from VPN target: {}",  targetVpn.getName());				

		if (targetSession != null)
			targetSession.closeSession();
		
		logger.info( "Disconnected from VPN target: {}",  targetVpn.getName());				
	}	

	@Override
	public void handleEvent(SessionEventArgs event)
	{
		logger.info(event.getInfo());
	}

	/**
	 * Implements JCSMPStreamingPublishEventHandler handleError.
	 */
	@Override
	public void handleError(String var1, JCSMPException var2, long var3)
	{
		logger.error("Stream publish event error occured: {}, exception: {}, value {}", var1, var2.getMessage(), var3);		
	}

	/**
	 * Implements JCSMPStreamingPublishEventHandler responseReceived.
	 */
	@Override
	public void responseReceived(String var1)
	{
		logger.info("Stream publish event response received: {}", var1);		
	}

	/**
	 * JCSMPProducerEventHandler handleEvent implementation.
	 */
	@Override
	public void handleEvent(Object event, FlowEventArgs args)
	{
		logger.info("FlowEventHandler received event from queue {}, event: {}, args: {}", sourceQueue, event.toString(), args.getInfo());
	}

	/**
	 * JCSMPProducerEventHandler handleEvent implementation.
	 */
	@Override
	public void handleEvent(ProducerEventArgs args)
	{
		logger.info("ProducerEventHandler received event from queue {}, args: {}", sourceQueue, args.getInfo());
	}

	/**
	 * XMLMessageListener onException implementation.
	 * @param ex The exception
	 */
	@Override
	public void onException(JCSMPException ex)
	{
		logger.error("XMLListener error occured while processing queue {}, exception: {}", sourceQueue, ex.getMessage());
	}

	/**
	 * XMLMessageListener onException implementation.
	 * @param msg The message received.
	 */
	@Override
	public void onReceive(BytesXMLMessage msg)
	{
		logger.trace("XMLListener received message from queue {}, message: {}", sourceQueue, msg.getAckMessageId());		
	}
}
