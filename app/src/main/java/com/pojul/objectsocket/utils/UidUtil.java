package com.pojul.objectsocket.utils;

import java.util.Random;

import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.utils.ServerConstant;

public class UidUtil {
	
	
	//size: 45
	private static final String[] letters = {"a", "b", "c", "d", "e" ,"f" ,"g" , "h" , "i", "j", "k" , "l"
			, "m", "n", "o", "p" ,"q" ,"r" , "s" , "t", "u", "v" , "w"
			, "x", "y", "z", "1" ,"2" ,"3", "4" , "5", "6", "7" , "8"
			, "9", "0", "+", "-" ,"=" ,"<" , ">" , "?", "[", "]" , "~"};
	
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
		// TODO Auto-generated method stub
		if(from == null || from.equals(to)) {
			return null;
		}
		if(to== null || "".equals(to)) {
			return null;
		}
		return from.compareTo(to) >= 0 ? (from + "_" + to) : (to + "_" + from);
	}

	public static String getTokenid(String userName) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i< userName.length(); i++) {
			sb.append(userName.charAt(i));
			int random = new Random().nextInt(letters.length);
			sb.append(letters[random]);
		}
		String rawTokenId = sb.toString() + System.currentTimeMillis();
		String tokenId = EncryptionUtil.md5Encryption(rawTokenId);
		return tokenId;
	}
	
}
