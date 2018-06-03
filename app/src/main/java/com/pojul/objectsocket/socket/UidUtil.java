package com.pojul.objectsocket.socket;

public class UidUtil {

	public static String getMessageUid(String from) {
		return System.currentTimeMillis() + "_" + from + "_" + (int)(Math.random()*20);
	}
	
}
