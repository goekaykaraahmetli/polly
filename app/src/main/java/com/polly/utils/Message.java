package com.polly.utils;

import java.util.ArrayList;
import java.util.List;

public class Message {
	private final long sender;
	private final long receiver;
	private final Class<?> dataType;
	private final Object data;
	private final List<Class<?>> generics;
	
	public Message(long sender, long receiver, Class<?> dataType, Object data) {
		this(sender, receiver, dataType, data, new ArrayList<>());
	}
	
	public Message(long sender, long receiver, Class<?> dataType, Object data, List<Class<?>> generics) {
		this.sender = sender;
		this.receiver = receiver;
		this.dataType = dataType;
		this.data = data;
		this.generics = generics;
	}

	public long getSender() {
		return sender;
	}

	public long getReceiver() {
		return receiver;
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
