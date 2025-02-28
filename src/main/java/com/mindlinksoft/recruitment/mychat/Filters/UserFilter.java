package com.mindlinksoft.recruitment.mychat.Filters;

//import java.util.ArrayList;
//import java.util.List;
import java.util.Stack;

import com.mindlinksoft.recruitment.mychat.Conversation;
import com.mindlinksoft.recruitment.mychat.Message;

/**
 * Filters the list of messages of a {@link Conversation} object based on chosen
 * user. Extends abstract class {@link Filter}.
 */
public class UserFilter extends Filter {

	/**
	 * The {@code senderId} whose messages will not be filtered.
	 */
	private String senderId;

	/**
	 * Initialises a new instance of the {@link UserFilter} class.
	 * 
	 * @param senderId The String containing the sender id to filter.
	 */
	public UserFilter(String senderId) {
		this.senderId = senderId;
	}

	/**
	 * Filters the messages in a given {@link Conversation} object to remove
	 * messages not sent by the {@code senderId}.
	 * 
	 * @param convo {@link Conversation} object to be filtered.
	 * @return New {@link Conversation} object with filtered messages.
	 */
	@Override
	public Conversation filterMessages(Conversation convo) {
//		List<Message> filteredMessages = new ArrayList<Message>();
		Stack<Message> filteredMSGS = new Stack<Message>();
		String conversationName = convo.name;

		for (Message m : convo.messages) {
			if (m.senderId.toLowerCase().contains(senderId.toLowerCase())) {
				filteredMSGS.push(m);
			}
		}
		return new Conversation(conversationName, filteredMSGS);
	}

	/**
	 * Get function for the {@code senderId} to filter.
	 * 
	 * @return {@link String} {@code senderId} object.
	 */
	public String getSenderId() {
		return this.senderId;
	}
}
