package ec.edu.ups.controller;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQController extends MessageEvent {
	
	private MessageProducer producer;
	private MessageConsumer consumer;
	private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private Session session;
	private Connection connection;
	private static String subject = "CHAT.";
	
	private String username;
	   
	public ActiveMQController(String username, MessageListenerEvent messageListenerEvent) throws JMSException {
		super(messageListenerEvent);
		this.username = username;
		connect();
	}
	
	public void sendMeessageToDestination(String destinationUsername, String message) throws JMSException {
		MapMessage map;
		Destination destination = session.createQueue(subject + destinationUsername);
		this.producer = session.createProducer(destination);
	      
		map = session.createMapMessage();
		map.setString("sender", this.username);
		map.setString("destination", destinationUsername);
		map.setString("message", message);
		
		this.producer.send(destination, map);
		this.sendMessageToListener(message);
	}
	
	private void connect() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
	    connection = connectionFactory.createConnection();
	    connection.start();

	    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    
	    Destination destination = session.createQueue(subject + this.username);

	    consumer = session.createConsumer(destination);
	    consumer.setMessageListener(new MessageListener() {
			 @Override
			 public void onMessage(Message message) {
				 messageToListener(message);
			 }
	    });
	}
	
	public void logout() throws JMSException {
		if (connection != null) {
			connection.close();
		}
	}
}
