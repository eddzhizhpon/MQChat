package ec.edu.ups.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Chat extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField jtfMessage;
	private JTextField jtfDestinationUsername;
	private JTextArea jtaHistorialChat;
	
	private String username;
	
	private InitialContext initialContext = null;
	private TopicConnectionFactory connectionFactory;
	private TopicConnection connection = null;
	private TopicPublisher publisher;
	private TopicSubscriber subscriber;
	private TopicSession session;
	private Topic topic;
	
	
	public Chat() throws JMSException, NamingException {
		this.initComponent();
	}
	
	private void initComponent() throws JMSException, NamingException {
		this.setTitle("MQChat");
		
		this.jtfDestinationUsername = new JTextField(30);
		this.getContentPane().add(this.jtfDestinationUsername, BorderLayout.NORTH);
		
		this.jtaHistorialChat = new JTextArea(15, 30);
		this.jtaHistorialChat.setEditable(false);
		JScrollPane jspForJtaHistorialChat = new JScrollPane(this.jtaHistorialChat);
		this.getContentPane().add(jspForJtaHistorialChat, BorderLayout.CENTER);
		
		this.jtfMessage = new JTextField(30);
		this.getContentPane().add(this.jtfMessage, BorderLayout.SOUTH);
		
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					connection.close();
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		this.jtfMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();	
			}
		});
		
		
		this.inputUsername();
		this.setTitle("MQChat - " + this.username);
		
		this.activeMQ();
		
		
	}
	
	private void inputUsername() {
		String name = "";
		while (name == null || name.isBlank()) {
			name = JOptionPane.showInputDialog("Ingrese su nombre de usuario");
			this.username = name;
		}
	}
	
	private void sendMessage() {
		MapMessage map;
		
		try {
			String destination = this.jtfDestinationUsername.getText();
			String message = this.jtfMessage.getText();
			if (!destination.isBlank() && !message.isBlank()) {
				
				map = session.createMapMessage();
				map.setString("sender", this.username);
				map.setString("destination", destination);
				map.setString("message", message);
				
				publisher.publish(map);
				
				this.jtaHistorialChat.append(" > " + this.username + ": " + message + "\n");
				this.jtfMessage.setText("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void activeMQ() throws JMSException, NamingException {
		
		// Step 1. Create an initial context to perform the JNDI lookup.
		initialContext = new InitialContext();

		// Step 2. Look-up the JMS topic
		topic = (Topic) initialContext.lookup("topic/chat");

		// Step 3. Look-up the JMS Topic connection factory
		connectionFactory = (TopicConnectionFactory) initialContext.lookup("ConnectionFactory");

		// Step 4. Create a JMS Topic connection
		connection = connectionFactory.createTopicConnection();
		
		// Step 5. Set an client id to persist in time
		connection.setClientID(this.username);

		// Step 6. Set the client-id on the connection
		connection.start();

		// step 7. Create Topic session
		session = connection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);

		// step 8. Create publisher
		publisher = session.createPublisher(topic);
		
		// step 9. Create a durable subscriber to receive messages 
		subscriber = session.createDurableSubscriber(topic, "durableSubscriber");
		// publisher.setDeliveryMode(DeliveryMode.PERSISTENT);

		// Step 10. Set a message listener to 
		subscriber.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				onMessageToDestination(message);
			}
		});
	}
	
	private void onMessageToDestination(Message message) {
		if (message instanceof MapMessage) {
			MapMessage map = (MapMessage) message;
			try {
				String sender = map.getString("sender");
				String destination = map.getString("destination");
				String messageText = map.getString("message");
				
				if (destination.equals(this.username))
					this.jtaHistorialChat.append(sender + ": " + messageText + "\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
