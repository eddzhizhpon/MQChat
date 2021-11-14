package ec.edu.ups.view;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class Main {

	public static void main(String[] args) {
		Chat chat;
		try {
			chat = new Chat();
			chat.setVisible(true);
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
 	}

}
