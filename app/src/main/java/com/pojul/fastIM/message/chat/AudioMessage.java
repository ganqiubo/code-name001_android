package com.pojul.fastIM.message.chat;

import com.pojul.objectsocket.message.StringFile;

public class AudioMessage extends ChatMessage{

    private StringFile audio;

    public StringFile getAudio() {
        return audio;
    }

    public void setAudio(StringFile audio) {
        this.audio = audio;
    }

	@Override
	public String toString() {
		return "AudioMessage [audio=" + audio + ", chatType=" + chatType + ", isRead=" + isRead + ", from=" + from
				+ ", to=" + to + ", sendTime=" + sendTime + ", receiveTime=" + receiveTime + ", MessageUid="
				+ MessageUid + ", isSend=" + isSend + "]";
	}

}
