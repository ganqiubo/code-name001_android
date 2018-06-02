package com.pojul.objectsocket.message;

public class MessageHeader {

	protected String className;
	protected int entityLength;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getEntityLength() {
		return entityLength;
	}
	public void setEntityLength(int entityLength) {
		this.entityLength = entityLength;
	}
	@Override
	public String toString() {
		return "MessageHeader [className=" + className + ", entityLength=" + entityLength + "]";
	}
	
}
