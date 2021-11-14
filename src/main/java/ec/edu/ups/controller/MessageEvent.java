package ec.edu.ups.controller;

import javax.jms.Message;

public abstract class MessageEvent {
	private MessageListenerEvent messageListener;
	
	public MessageEvent(MessageListenerEvent messageListener) {
		this.messageListener = messageListener;
	}
	
	public void messageToListener(Message message) {
		this.messageListener.onMeesage(message);
	}
	
	public void sendMessageToListener(String message) {
		this.messageListener.onSend(message);
	}
}
