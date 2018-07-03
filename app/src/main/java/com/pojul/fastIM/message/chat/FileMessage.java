package com.pojul.fastIM.message.chat;

import com.pojul.objectsocket.message.StringFile;

public class FileMessage extends ChatMessage{

    private StringFile file;

    public StringFile getFile() {
        return file;
    }

    public void setFile(StringFile file) {
        this.file = file;
    }

	@Override
	public String toString() {
		return "FileMessage [file=" + file + ", chatType=" + chatType + ", isRead=" + isRead + ", from=" + from
				+ ", to=" + to + ", sendTime=" + sendTime + ", receiveTime=" + receiveTime + ", MessageUid="
				+ MessageUid + ", isSend=" + isSend + "]";
	}
    
}
