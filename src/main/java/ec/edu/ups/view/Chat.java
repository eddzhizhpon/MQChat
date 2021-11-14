package ec.edu.ups.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ec.edu.ups.controller.ActiveMQController;
import ec.edu.ups.controller.MessageListenerEvent;

public class Chat extends JFrame implements ActionListener, MessageListenerEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField jtfMessage;
	private JTextField jtfDestinationUsername;
	private JTextArea jtaHistorialChat;
	private JTextField jtfUsername;
	private JButton jbLogin;
	
	private String username;
	
	private ActiveMQController activeMQController;
	
	public Chat() throws JMSException, NamingException {
		this.initComponent();
	}
	
	private void initComponent() throws JMSException, NamingException {
		
		this.setMinimumSize(new Dimension(500, 500));
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		
		JLabel jlUsername = new JLabel("Usuario:", SwingConstants.RIGHT);
		
		this.jtfUsername = new JTextField(30);
		this.getContentPane().add(this.jtfUsername);
		
		this.jbLogin = new JButton("Iniciar Sesión");
		this.jbLogin.setActionCommand("login");
		this.jbLogin.addActionListener(this);
		
		c.gridy = 0;
		c.gridx = 0;
		c.weightx = 0.3;
		mainPanel.add(jlUsername, c);
		
		c.gridx = 1;
		c.weightx = 0.6;
		mainPanel.add(this.jtfUsername, c);
		
		c.gridx = 2;
		c.weightx = 0.1;
		mainPanel.add(this.jbLogin, c);
		
		JLabel jlDestinationUsername = new JLabel("Destino:", SwingConstants.RIGHT);
		
		this.jtfDestinationUsername = new JTextField(30);
		this.getContentPane().add(this.jtfDestinationUsername);
		
		c.gridy = 1;
		c.gridx = 0;
		c.weightx = 0.3;
		mainPanel.add(jlDestinationUsername, c);
		
		c.gridx = 1;
		c.weightx = 0.7;
		c.gridwidth = 2;
		mainPanel.add(this.jtfDestinationUsername, c);
		
		JLabel jlChat = new JLabel("Chat:", SwingConstants.RIGHT);
		
		this.jtaHistorialChat = new JTextArea(15, 30);
		this.jtaHistorialChat.setEditable(false);
		JScrollPane jspForJtaHistorialChat = new JScrollPane(this.jtaHistorialChat);

		c.gridy = 2;
		c.gridx = 0;
		c.weightx = 0.3;
		c.gridwidth = 1;
		mainPanel.add(jlChat, c);
		
		c.gridx = 1;
		c.weightx = 0.7;
		c.gridwidth = 2;
		mainPanel.add(jspForJtaHistorialChat, c);
		
		JLabel jlMessage= new JLabel("Mensaje:", SwingConstants.RIGHT);
		this.jtfMessage = new JTextField(30);
		this.jtfMessage.setEnabled(false);
		
		c.gridy = 3;
		c.gridx = 0;
		c.weightx = 0.3;
		c.gridwidth = 1;
		mainPanel.add(jlMessage, c);
		
		c.gridx = 1;
		c.weightx = 0.7;
		c.gridwidth = 2;
		mainPanel.add(this.jtfMessage, c);
		
		
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				logout();
			}
		});
		
		this.jtfMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();	
			}
		});
		
		this.setTitle("MQChat");
		this.setVisible(true);
		
	}
	
	private boolean login() {
		String username = this.jtfUsername.getText();
		if (username.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Ingresar nombre de usuario");
			return false;
		}
		this.username = username;
		this.setTitle("MQChat - " + this.username);
		try {
			this.activeMQ();
			this.jbLogin.setActionCommand("logout");
			this.jbLogin.setText("Cerrar Sesión");
			this.jtfMessage.setEnabled(true);
			this.jtfUsername.setEnabled(false);
		} catch (JMSException | NamingException e) {
			JOptionPane.showMessageDialog(null, 
					"No se pudo iniciar sesión", 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean logout() {
		try {
			if (this.activeMQController == null) return true;
			this.activeMQController.logout();;
			this.jbLogin.setActionCommand("login");
			this.jbLogin.setText("Iniciar Sesión");
			this.jtfMessage.setEnabled(false);
			this.jtaHistorialChat.setText("");
			this.jtfUsername.setText("");
			this.jtfUsername.setEnabled(true);
			this.setTitle("MQChat");
		} catch (JMSException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void sendMessage() {
		try {
			String destinationUsername = this.jtfDestinationUsername.getText();
			String message = this.jtfMessage.getText();
			
			if (destinationUsername == null || message == null) return;
			
			if (!destinationUsername.isEmpty() && !message.isEmpty()) {
				
				this.activeMQController.sendMeessageToDestination(destinationUsername, message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void activeMQ() throws JMSException, NamingException {
		activeMQController = new ActiveMQController(username, this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String id = e.getActionCommand();
		switch (id) {
		case "login":
			this.login();
			break;
		case "logout":
			this.logout();
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onMeesage(Message message) {
		if (message instanceof MapMessage) {
			MapMessage map = (MapMessage) message;
			try {
				String sender = map.getString("sender");
				String messageText = map.getString("message");
				this.jtaHistorialChat.append(" > " +sender + ": " + messageText + "\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onSend(String message) {
		this.jtaHistorialChat.append(this.username + ": " + message + "\n");
		this.jtfMessage.setText("");
	}
}
