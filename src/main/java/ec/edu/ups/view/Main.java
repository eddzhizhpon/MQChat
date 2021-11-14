package ec.edu.ups.view;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

	public static void main(String[] args) {
		
		Main.applyLookAndFeel();
		try {
			new Chat();
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
 	}
	
	private static void applyLookAndFeel() {
		String themeName = UIManager.getSystemLookAndFeelClassName();
		try {
			
			UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
			for (int i = 0; i < lafInfo.length; i++) {
				if (lafInfo[i].getClassName().toString().equals("com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
					
					themeName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
				}
			}
			System.out.println("[INFO] Applaing: " + themeName);
			UIManager.setLookAndFeel(themeName);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

}
