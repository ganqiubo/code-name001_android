package com.pojul.fastIM.message.chat;

import com.pojul.objectsocket.message.StringFile;

public class PicMessage extends ChatMessage{

    private StringFile pic;

    public StringFile getPic() {
        return pic;
    }

    public void setPic(StringFile pic) {
        this.pic = pic;
    }

	@Override
	public String toString() {
		return "PicMessage [pic=" + pic + ", chatType=" + chatType + ", isRead=" + isRead + ", from=" + from + ", to="
				+ to + ", sendTime=" + sendTime + ", receiveTime=" + receiveTime + ", MessageUid=" + MessageUid
				+ ", isSend=" + isSend + "]";
	}

}
