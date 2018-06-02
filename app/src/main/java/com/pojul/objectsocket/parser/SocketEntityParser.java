package com.pojul.objectsocket.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import com.google.gson.Gson;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.MessageHeader;
import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.parser.interfacer.ISocketEntityParser;
import com.pojul.objectsocket.utils.BytesUtil;
import com.pojul.objectsocket.utils.LogUtil;
import com.sun.prism.paint.Stop;

public class SocketEntityParser {
	
	protected ISocketEntityParser mISocketEntityParser;
	protected BaseMessage mBaseMessage;
	
	protected MessageHeader mMessageHeader;
	
	protected  int stringLength;

	protected int entieyJsonLength;
	protected String entityJson;
	
	protected int headerLength;
	protected String headerJson;
	
	protected ArrayList<String> files;
	protected long filesLength;
	
	protected int writeLength = 1024;
	
	private static final String TAG = "SocketEntityParser";
	
	public boolean stopSend = false;
	
	public SocketEntityParser(ISocketEntityParser mISocketEntityParser) {
		super();
		this.mISocketEntityParser = mISocketEntityParser;
	}
	
	public void startParse(BaseMessage mBaseMessage) throws IllegalArgumentException, IllegalAccessException, IOException {
		this.mBaseMessage = mBaseMessage;
		files = new ArrayList<>();
		if(mISocketEntityParser == null) {
			LogUtil.i(TAG, "error: parser callback is null");
			return;
		}
		if(mBaseMessage == null) {
			LogUtil.i(TAG, "error: parser mesage is null");
			mISocketEntityParser.onParserError();
			return;
		}
		
		ready();
		
		parse();
	}
	
	protected void ready() throws IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException {
		//将entity转json字符及并设置其长度
		setEntityJson();
		//设置Header及其长度
		setHeaderJson();
		//获取message string区总长度
		getStringLength();
	}
	
	protected void setEntityJson() throws UnsupportedEncodingException {
		Gson gson = new Gson();
		entityJson = gson.toJson(mBaseMessage);
		getFilesPath();
		LogUtil.d(TAG, "BaseMessage to jsonstring = " + entityJson);
		entieyJsonLength = entityJson.getBytes("utf-8").length;
	}
	
	protected void getFilesPath() {
		while(!stopSend) {
			Pattern pattern = Pattern.compile(StringFile.regexStr);
			Matcher matcher = pattern.matcher(entityJson);
			if (matcher.find()) {
				//System.out.println("getFilesPath: find");
				files.add(entityJson.substring((matcher.start() + StringFile.pathBorderStart.length()), 
						(matcher.end() - StringFile.pathBorderEnd.length()) ));
				LogUtil.d(TAG, "find file local path = " + files.get( (files.size() - 1) ));
				StringBuilder sb = new StringBuilder(entityJson);
				sb.replace((matcher.start()), 
						(matcher.end()), (StringFile.pathSendingBorderStart + StringFile.pathSendingBorderEnd));
				entityJson = sb.toString();
				matcher = pattern.matcher(entityJson);
			}else {
				break;
			}
		}
	}
	
	protected void setHeaderJson() throws UnsupportedEncodingException {
		Gson gson = new Gson();
		mMessageHeader = new MessageHeader();
		mMessageHeader.setClassName(mBaseMessage.getClass().getName());
		mMessageHeader.setEntityLength(entieyJsonLength);
		headerJson = gson.toJson(mMessageHeader) + "\n";
		headerLength = headerJson.getBytes("utf-8").length;
	}
	
	protected void getStringLength() {
		stringLength = headerLength + entieyJsonLength;
	}
	
	protected void parse() throws IOException {
		parseStringLength();
		
		parseHead();
		
		parseEntityJson();
		
		parseFiles();
	}
	
	protected void parseStringLength() throws IOException {
		byte[] tempBytes = BytesUtil.intToByteArray(stringLength);
		mISocketEntityParser.onParser(tempBytes);
	}
	
	protected void parseHead() throws IOException {
		parseString(headerJson);
	}
	
	protected void parseEntityJson() throws IOException {
		parseString(entityJson);
	}

	protected void parseFiles() throws IOException {
		for(int i = 0; i< files.size(); i++) {
			mISocketEntityParser.onParser(new byte[] {0});
			parseFile(files.get(i));
		}
		mISocketEntityParser.onParser(new byte[] {1});
		mISocketEntityParser.onParserFinish(mBaseMessage);
	}
	
	protected void parseString(String str) throws IOException {
		byte[] tempBytes = str.getBytes("utf-8");
		mISocketEntityParser.onParser(tempBytes);
	}
	
	protected void parseFile(String path) throws IOException {
		File f = new File(path);
		Long length = 0L;
		if(f.exists()) {
			length = f.length();
			LogUtil.d(TAG, "write file exit");
		}
		mISocketEntityParser.onParser(BytesUtil.longToBytes(length));
		if(length <= 0) {
			return;
		}
		FileInputStream fis = new FileInputStream(f);
		byte[] bytes = new byte[writeLength];
		int len = 0;
		long total = 0;
		while(!stopSend && (len = fis.read(bytes, 0, bytes.length)) != -1) {
			byte[] readBytes = new byte[len];
			System.arraycopy(bytes, 0, readBytes, 0, len);
			mISocketEntityParser.onParser(readBytes);
			total = total + len;
		}
		fis.close();
	}
	
	public void stop() {
		stopSend = true;
	}
	
}
