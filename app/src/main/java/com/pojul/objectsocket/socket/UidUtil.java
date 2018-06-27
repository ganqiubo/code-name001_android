package com.pojul.objectsocket.socket;

import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.utils.ServerConstant;

public class UidUtil {

	public static String getMessageUid(String from) {
		return System.currentTimeMillis() + "_" + from + "_" + (int)(Math.random()*20);
	}
	
	public static String getChatRoomUid(ChatMessage message) {
		// TODO Auto-generated method stub
		String from = message.getFrom();
		String to = message.getTo();
		int chatType = message.getChatType();
		if(from == null || from.equals(to)) {
			return null;
		}
		if(to== null || "".equals(to)) {
			return null;
		}
		if(chatType == ServerConstant.CHAT_TYPE_SINGLE) {
			return from.compareTo(to) >= 0 ? (from + "_" + to) : (to + "_" + from);
		}else {
			return to;
		}
	}

	public static String getSingleChatRoomUid(String from, String to) {
		if(from == null || from.equals(to)) {
			return "";
		}
		if(to== null || "".equals(to)) {
			return "";
		}
		return from.compareTo(to) >= 0 ? (from + "_" + to) : (to + "_" + from);
	}
	
}
