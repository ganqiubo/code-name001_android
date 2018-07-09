package com.pojul.fastIM.message.chat;

import com.pojul.objectsocket.message.StringFile;

public class VideoMessage extends ChatMessage{

    private StringFile video;
    private StringFile firstPic;
    private int videoWidth;
    private int videoHeight;

    public StringFile getVideo() {
        return video;
    }

    public void setVideo(StringFile video) {
        this.video = video;
    }

    public StringFile getFirstPic() {
        return firstPic;
    }

    public void setFirstPic(StringFile firstPic) {
        this.firstPic = firstPic;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

	@Override
	public String toString() {
		return "VideoMessage [video=" + video + ", firstPic=" + firstPic + ", videoWidth=" + videoWidth
				+ ", videoHeight=" + videoHeight + ", chatType=" + chatType + ", isRead=" + isRead + ", from=" + from
				+ ", to=" + to + ", sendTime=" + sendTime + ", receiveTime=" + receiveTime + ", MessageUid="
				+ MessageUid + ", isSend=" + isSend + "]";
	}
    
}
