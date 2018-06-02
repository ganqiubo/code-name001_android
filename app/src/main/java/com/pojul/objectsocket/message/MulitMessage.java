package com.pojul.objectsocket.message;

import java.util.ArrayList;
import java.util.Arrays;

public class MulitMessage extends BaseMessage {

	protected String text;
	protected StringFile[] mStringFile1s;
	protected ArrayList<StringFile> mStringFile2s;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public StringFile[] getmStringFile1s() {
		return mStringFile1s;
	}
	public void setmStringFile1s(StringFile[] mStringFile1s) {
		this.mStringFile1s = mStringFile1s;
	}
	public ArrayList<StringFile> getmStringFile2s() {
		return mStringFile2s;
	}
	public void setmStringFile2s(ArrayList<StringFile> mStringFile2s) {
		this.mStringFile2s = mStringFile2s;
	}
	@Override
	public String toString() {
		return "MulitMessage [text=" + text + ", mStringFile1s=" + Arrays.toString(mStringFile1s) + ", mStringFile2s="
				+ mStringFile2s + ", from=" + from + ", to=" + to + ", sendTime=" + sendTime + ", receiveTime="
				+ receiveTime + "]";
	}
	
}
