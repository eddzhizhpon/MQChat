package ec.edu.ups.controller;

import javax.jms.Message;

public interface MessageListenerEvent {
	public void onMeesage(Message message);
	public void onSend(String message);
}
