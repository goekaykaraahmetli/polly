package com.polly.interfaces.communication;

import com.polly.testclasses.DoppelInteger;
import com.polly.testclasses.Poll;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

class DataStreamManager {
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

	private Class<?> convertToPrimitiveDataTypeIfPossible(Class<?> theClass) {
		if (theClass.equals(Integer.class)) {
			return int.class;
		}
		if (theClass.equals(Boolean.class)) {
			return boolean.class;
		}
		if (theClass.equals(Byte.class)) {
			return byte.class;
		}
		if (theClass.equals(Character.class)) {
			return char.class;
		}
		if (theClass.equals(Double.class)) {
			return double.class;
		}
		if (theClass.equals(Float.class)) {
			return float.class;
		}
		if (theClass.equals(Long.class)) {
			return long.class;
		}
		if (theClass.equals(Short.class)) {
			return short.class;
		}
		return theClass;
	}

	public WrappedData readNextInput() throws IOException, ClassNotFoundException {
		while(input.available() <= 0){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		String data = readString();
		return readInputAsSoonAsAvailable(Class.forName(data));
	}

	public WrappedData readInput() throws IOException, ClassNotFoundException {
		if (input.available() > 0) {
			String data = readString();
			return readInputAsSoonAsAvailable(Class.forName(data));
		}
		return null;
	}

	private WrappedData readInputAsSoonAsAvailable(Class<?> dataType) throws IOException {
		while (input.available() <= 0) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return readInput(dataType);
	}

	private WrappedData readInput(Class<?> dataType) throws IOException {
		if (dataType == null) {
			throw new NullPointerException();
		}
		
		// Primitive data types:
		if (dataType.equals(Integer.class)) {
			return new WrappedData(Integer.class, readInteger());
		}
		if (dataType.equals(Boolean.class)) {
			return new WrappedData(Boolean.class, readBoolean());
		}
		if (dataType.equals(Byte.class)) {
			return new WrappedData(Byte.class, readByte());
		}
		if (dataType.equals(Character.class)) {
			return new WrappedData(Character.class, readChar());
		}
		if (dataType.equals(Double.class)) {
			return new WrappedData(Double.class, readDouble());
		}
		if (dataType.equals(Float.class)) {
			return new WrappedData(Float.class, readFloat());
		}
		if (dataType.equals(Long.class)) {
			return new WrappedData(Long.class, readLong());
		}
		if (dataType.equals(Short.class)) {
			return new WrappedData(Short.class, readShort());
		}
		
		//complex data types:
		if(dataType.equals(DoppelInteger.class)) {
			return new WrappedData(DoppelInteger.class, readDoppelInteger());
		}
		if(dataType.equals(Poll.class)){
			return new WrappedData(Poll.class, readPoll());
		}
		
		
		// if(dataType.equals(String.class)){
		// return new InputData(String.class, input.readUTF());
		// }
		return new WrappedData(String.class, readString());
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

	private DoppelInteger readDoppelInteger() throws IOException {
		return new DoppelInteger(readInteger(), readInteger());
	}

	private Poll readPoll() throws IOException {
		Map<String, Integer> map = new HashMap<>();
		int size = readInteger();
		for(int i = 0;i<size;i++){
			map.put(readString(), readInteger());
		}
		return new Poll(map);
	}

	public void writeOutput(WrappedData wrappedData) throws IOException {
		Class<?> dataType = wrappedData.getDataType();
		Object data = wrappedData.getData();
		if (dataType == null) {
			throw new NullPointerException();
		}
		writeString(dataType.getName());

		if (dataType.equals(Integer.class)) {
			writeInteger((int) data);
		} else if (dataType.equals(Boolean.class)) {
			writeBoolean((boolean) data);
		} else if (dataType.equals(Byte.class)) {
			writeByte((byte) data);
		} else if (dataType.equals(char.class)) {
			writeChar((char) data);
		} else if (dataType.equals(Double.class)) {
			writeDouble((double) data);
		} else if (dataType.equals(Float.class)) {
			writeFloat((float) data);
		} else if (dataType.equals(Long.class)) {
			writeLong((long) data);
		} else if (dataType.equals(Short.class)) {
			writeShort((short) data);
		}
		// complex data types:
		else if(dataType.equals(DoppelInteger.class)) {
			writeDoppelInteger((DoppelInteger) data);
		} else if(dataType.equals(Poll.class)){
			writePoll((Poll) data);
		}
		// default data type (String):
		//else if (dataType.equals(String.class)){
		else {
			writeString((String) data);
		}
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

	private void writeDoppelInteger(DoppelInteger data) throws IOException {
		writeInteger(data.getIntEins());
		writeInteger(data.getIntZwei());
	}

	private void writePoll(Poll data) throws IOException {
		writeInteger(data.getPoll().size());
		for(String pollOption : data.getPoll().keySet()){
			writeString(pollOption);
			writeInteger(data.getPoll().get(pollOption));
		}
	}
}
