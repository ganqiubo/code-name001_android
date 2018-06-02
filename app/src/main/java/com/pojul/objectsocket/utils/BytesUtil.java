package com.pojul.objectsocket.utils;

import java.util.Random;

public class BytesUtil {

	public static int getRandomPort() {
		return new Random().nextInt(65535 - 10000 + 1) + 10000;
	}
	
	public static byte[] longToBytes(long num) {  
	    byte[] byteNum = new byte[8];  
	    for (int ix = 0; ix < 8; ++ix) {  
	        int offset = 64 - (ix + 1) * 8;  
	        byteNum[ix] = (byte) ((num >> offset) & 0xff);  
	    }  
	    return byteNum;  
	}  
	
	public static long bytesToLong(byte[] byteNum) {  
	    long num = 0;
	    for(int ix = 0; ix < 8; ++ix) {
	    	num <<= 8;
	    	num |= (byteNum[ix] & 0xff);
	    }
	    return num;
	}  
	
	public static byte[] intToByteArray(int a) {   
		return new byte[] {   
		        (byte) ((a >> 24) & 0xFF),   
		        (byte) ((a >> 16) & 0xFF),      
		        (byte) ((a >> 8) & 0xFF),      
		        (byte) (a & 0xFF)   
		    };   
	} 
	
	public static int byteArrayToInt(byte[] b) {   
		return   b[3] & 0xFF |   
		            (b[2] & 0xFF) << 8 |   
		            (b[1] & 0xFF) << 16 |   
		            (b[0] & 0xFF) << 24;   
	}   
	
}
