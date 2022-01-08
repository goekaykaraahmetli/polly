package com.polly.utils.wrapper;

import java.util.ArrayList;
import java.util.List;

public class Message {
	private static long nextResponseId = 0L;

	private final long sender;
	private final long receiver;

	private final long responseId;

	private final Class<?> dataType;
	private final Object data;
	private final List<Class<?>> generics;

	public Message(long sender, long receiver, long responseId, Class<?> dataType, Object data) {
		this(sender, receiver, responseId, dataType, data, new ArrayList<>());
	}
	public Message(long sender, long receiver, long responseId, Class<?> dataType, Object data, List<Class<?>> generics) {
		this.sender = sender;
		this.receiver = receiver;
		this.responseId = responseId;
		this.dataType = dataType;
		this.data = data;
		this.generics = generics;
	}

	public static long getNextResponseId(){
		return nextResponseId++;
	}

	public long getSender() {
		return sender;
	}

	public long getReceiver() {
		return receiver;
	}

	public long getResponseId() {
		return responseId;
	}
	
	public Class<?> getDataType() {
		return dataType;
	}
	
	public Object getData() {
		return data;
	}

	public List<Class<?>> getGenerics() {
		return generics;
	}
}
