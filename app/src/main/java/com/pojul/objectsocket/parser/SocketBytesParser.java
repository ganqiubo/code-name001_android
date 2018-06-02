package com.pojul.objectsocket.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.MessageHeader;
import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.parser.interfacer.ISocketBytesParser;
import com.pojul.objectsocket.parser.util.ReadUtil;
import com.pojul.objectsocket.utils.BytesUtil;
import com.pojul.objectsocket.utils.Constant;
import com.pojul.objectsocket.utils.LogUtil;


public class SocketBytesParser{
	
	protected Socket mSocket;
	protected ISocketBytesParser mISocketBytesParser;
	protected boolean recOnce;
	protected InputStream is;
	protected int readLength = 1024;
	protected MessageHeader mMessageHeader;
	protected BaseMessage mMessageEntity;
	public boolean stopRec = false;
	private static final String TAG = "SocketBytesParser"; 
	
	public SocketBytesParser(Socket mSocket, ISocketBytesParser mISocketBytesParser, boolean recOnce) {
		super();
		this.mSocket = mSocket;
		this.mISocketBytesParser = mISocketBytesParser;
		this.recOnce = recOnce;
		try {
			is=mSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			mISocketBytesParser.onError(e);
		}
		parse();
	}
	
	protected void parse() {
		while(!stopRec) {
			try {
				parseHead();
			} catch (IOException | JsonSyntaxException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				stopRec = true;
				mISocketBytesParser.onError(e);
			}
			if(recOnce) {
				break;
			}
		}
	}
	
	protected void parseHead() throws IOException, JsonSyntaxException, ClassNotFoundException {
		byte[] b;
		b = ReadUtil.recvBytes(is, 4);
		int entityLength = BytesUtil.byteArrayToInt(b);
		if(entityLength <= 0) {
			stopRec = true;
			mISocketBytesParser.onError(new Exception("失去连接"));
			return;
		}
		LogUtil.d(TAG, "rec entity length = " + entityLength);
		parseEntity(entityLength);
	}
	
	protected void parseEntity(int entityLength) throws IOException, JsonSyntaxException, ClassNotFoundException {
		byte[] b;
		b = ReadUtil.recvBytes(is, entityLength);
		String str = new String(b,"UTF-8");
		Gson gs = new Gson();
		int index = str.indexOf("\n");
		String entityString = "";
		if(index != -1) {
			String headerString = str.substring(0, index);
			LogUtil.d(TAG, "parseHeader raw MessageHeader = " + headerString);
			mMessageHeader = gs.fromJson(headerString, MessageHeader.class);
			mISocketBytesParser.onReadHead(mMessageHeader);
			if(str.length() > (index + 1)) {
				entityString = str.substring((index + 1), str.length());
			}
			LogUtil.d(TAG, "parseHeader MessageHeader = " + mMessageHeader.toString());
		}
		parseFile(entityString);
	}
	
	protected void parseFile(String entityString) throws IOException, JsonSyntaxException, ClassNotFoundException {
		
		while(!stopRec) {
			byte[] hasFile = ReadUtil.recvBytes(is, 1);
			if(hasFile[0] != 0) {
				break;
			}
			byte[] fileLengthBytes = ReadUtil.recvBytes(is, 8);
			long fileLength = BytesUtil.bytesToLong(fileLengthBytes);
			LogUtil.d(TAG, mSocket.isClosed() + "::" + mSocket.isConnected() + ":hasFile, file byte length = " + fileLength);
			Pattern pattern = Pattern.compile(StringFile.regexServerStr2);
			Matcher matcher = pattern.matcher(entityString);
			StringFileIndex stringFileIndex = null;
			if(matcher.find()) {
				stringFileIndex = getStringFile(entityString, matcher.start(), matcher.end());
				LogUtil.d(TAG, "matched StringFile = " + stringFileIndex.stringFile.toString());
			}
			String currentTimeMillis = System.currentTimeMillis() + "_";
			String fileName = "file_" + currentTimeMillis;
			if(stringFileIndex.stringFile != null) {
				fileName = stringFileIndex.stringFile.getFileName();
				//设置文件存储类型： 0:本地存储; 1:网络存储
				stringFileIndex.stringFile.setStorageType(1);
				if(Constant.STORAGE_TYPE == 0) {
					stringFileIndex.stringFile.setStorageType(0);
				}
				stringFileIndex.stringFile.setFilePath(Constant.BASE_URL + currentTimeMillis + fileName);
				entityString = new StringBuilder(entityString).replace(
						stringFileIndex.start ,stringFileIndex.end,
						(new Gson().toJson(stringFileIndex.stringFile)) ).toString();
				
				LogUtil.d(TAG, "matched StringFile URL = " + stringFileIndex.stringFile.getFilePath());
			}
			if(fileLength > 0) {
				long tempReadLength = 0;
				FileOutputStream fos = new FileOutputStream(new File(Constant.SERVICE_LOCAL_FILE_PATH + currentTimeMillis + fileName));
				byte[] writeBytes;
				long total = 0;
				while(!stopRec) {
					if(fileLength <= 0) {
						break;
					}
					fileLength = fileLength - readLength;
					tempReadLength = (fileLength > 0) ? readLength:(fileLength + readLength);
					writeBytes = ReadUtil.recvBytes(is, (int)tempReadLength);
					total = total + writeBytes.length;
					fos.write(writeBytes);
					fos.flush();
				}
				fos.close();
			}
			
		}
		LogUtil.d(TAG, "receive file entityString: " + entityString);
		mMessageEntity =  (BaseMessage) new Gson().fromJson(entityString, Class.forName(mMessageHeader.getClassName()));
		mISocketBytesParser.onReadEntity(mMessageEntity);
		mISocketBytesParser.onReadFinish();
	}
	
	protected StringFileIndex getStringFile(String entityString, int start, int end) {
		int tempStart = -1;
		for(int i = start; i > 0; i-- ) {
			char ch = entityString.charAt(i);
			if(ch == '{') {
				tempStart = i;
				break;
			}
		}
		if(tempStart == -1) {
			return null;
		}
		int tempEnd = -1;
		for(int i = end; i < entityString.length(); i++) {
			char ch = entityString.charAt(i);
			if(ch == '}') {
				tempEnd = i;
				break;
			}
		}
		if(tempEnd == -1) {
			return null;
		}
		String tempStringFile = entityString.substring(tempStart, (tempEnd + 1));
		try {
			StringFile stringFile = new Gson().fromJson(tempStringFile, StringFile.class);
			StringFileIndex stringFileIndex = new StringFileIndex(tempStart, (tempEnd + 1), stringFile);
			return stringFileIndex;
		}catch(Exception e) {
			return null;
		}
	}
	
	class StringFileIndex {
		private StringFileIndex(int start, int end, StringFile stringFile) {
			super();
			this.start = start;
			this.end = end;
			this.stringFile = stringFile;
		}
		int start;
		int end;
		StringFile stringFile;
	}
	
	public void stop() {
		stopRec = true;
	}
	
}
