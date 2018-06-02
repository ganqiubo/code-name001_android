package com.pojul.objectsocket.parser.interfacer;

import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.MessageHeader;
import com.pojul.objectsocket.message.StringFile;

public interface ISocketBytesParser {
	public void onReadHead(MessageHeader header);
	
	public String onReadFile(StringFile mStringFile);
	
	public void onReadEntity(BaseMessage message);
	
	public void onReadFinish();
	
	public void onError(Exception e);
}
