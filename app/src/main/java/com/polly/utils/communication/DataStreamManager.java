package com.polly.utils.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.polly.config.Config;
import com.polly.utils.Message;

class DataStreamManager {
	private static final int REFRESH_DELAY = Config.DATA_STREAM_MANAGER_REFRESH_DELAY;
	private final DataInputStream input;
	private final DataOutputStream output;
	
	public DataStreamManager(DataInputStream input, DataOutputStream output) {
		this.input = input;
		this.output = output;
	}

	public DataStreamManager(InputStream input, OutputStream output) {
		this.input = new DataInputStream(input);
		this.output = new DataOutputStream(output);
	}
	
	public Message receive() throws IOException, ClassNotFoundException{
		while(input.available() <= 0) {
			try {
				Thread.sleep(REFRESH_DELAY);
			} catch(InterruptedException e) {
				//TODO replace with logger
				e.printStackTrace();
			}
		}
		long sender = readLong();
		long receiver = readLong();
		String className = readString();
		
		return readInput(sender, receiver, Class.forName(className));
	}
	
	private Message readInput(long sender, long receiver, Class<?> dataType) throws IOException {
		if(dataType == null) {
			throw new NullPointerException();
		}
		Object data;
		// primitive types:
		if(dataType.equals(Integer.class))
			data = readInteger();
		else if (dataType.equals(Boolean.class))
			data = readBoolean();
		else if (dataType.equals(Byte.class))
			data = readByte();
		else if (dataType.equals(Character.class))
			data = readChar();
		else if (dataType.equals(Double.class))
			data = readDouble();
		else if (dataType.equals(Float.class))
			data = readFloat();
		else if (dataType.equals(Long.class))
			data = readLong();
		else if (dataType.equals(Short.class))
			data = readShort();
		// complex types:

		
		// default type:
		else
			data = readString();
		
		return new Message(sender, receiver, dataType, data);
	}
	
	private int readInteger() throws IOException {
		return input.readInt();
	}

	private boolean readBoolean() throws IOException {
		return input.readBoolean();
	}

	private byte readByte() throws IOException {
		return input.readByte();
	}

	private char readChar() throws IOException {
		return input.readChar();
	}

	private double readDouble() throws IOException {
		return input.readDouble();
	}

	private float readFloat() throws IOException {
		return input.readFloat();
	}

	private long readLong() throws IOException {
		return input.readLong();
	}

	private short readShort() throws IOException {
		return input.readShort();
	}

	private String readString() throws IOException {
		return input.readUTF();
	}

	
	public void send(Message message) throws IOException{
		Class<?> dataType = message.getDataType();
		if(dataType == null) {
			throw new NullPointerException();
		}
		Object data = message.getData();
		writeLong(message.getSender());
		writeLong(message.getReceiver());
		writeString(message.getDataType().getName());
		
		if(dataType.equals(Integer.class))
			writeInteger((Integer) data);
		else if (dataType.equals(Boolean.class))
			writeBoolean((boolean) data);
		else if (dataType.equals(Byte.class))
			writeByte((byte) data);
		else if (dataType.equals(Character.class))
			writeChar((char) data);
		else if (dataType.equals(Double.class))
			writeDouble((double) data);
		else if (dataType.equals(Float.class))
			writeFloat((float) data);
		else if (dataType.equals(Long.class))
			writeLong((long) data);
		else if (dataType.equals(Short.class))
			writeShort((short) data);
		// complex types:

		
		// default type:
		else
			writeString((String) data);
	}
	
	private void writeInteger(Integer data) throws IOException {
		output.writeInt(data);
	}

	private void writeBoolean(Boolean data) throws IOException {
		output.writeBoolean(data);
	}

	private void writeByte(Byte data) throws IOException {
		output.writeByte(data);
	}

	private void writeChar(Character data) throws IOException {
		output.writeChar(data);
	}

	private void writeDouble(Double data) throws IOException {
		output.writeDouble(data);
	}

	private void writeFloat(Float data) throws IOException {
		output.writeFloat(data);
	}

	private void writeLong(Long data) throws IOException {
		output.writeLong(data);
	}

	private void writeShort(Short data) throws IOException {
		output.writeShort(data);
	}

	private void writeString(String data) throws IOException {
		output.writeUTF(data);
	}


}