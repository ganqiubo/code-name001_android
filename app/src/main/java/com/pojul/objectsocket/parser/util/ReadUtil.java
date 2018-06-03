package com.pojul.objectsocket.parser.util;

import java.io.InputStream;

public class ReadUtil {

	public static byte[] recvBytes(InputStream is, int tempReadLength) throws Exception {
		int tmpLength = 512; // 每次读取最大缓冲区大小
		//System.out.println("recvBytes length：" + tempReadLength);
		byte[] ret = new byte[tempReadLength];
		int readed = 0, offset = 0, left = tempReadLength;
		byte[] bs = new byte[tmpLength];
		while (left > 0) {
			try {
				readed = is.read(bs, 0, Math.min(tmpLength, left));
				if (readed == -1)
					break;
				System.arraycopy(bs, 0, ret, offset, readed);
			}catch(Exception e) {
				throw(e);
			} finally {
				offset += readed;
				left -= readed;
			}
		}
		return ret;
	}

}
