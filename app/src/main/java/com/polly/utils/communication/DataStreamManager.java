package com.polly.utils.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polly.config.Config;
import com.polly.utils.Message;
import com.polly.utils.command.CreatePollCommand;
import com.polly.utils.command.LoadPollCommand;
import com.polly.utils.command.VotePollCommand;
import com.polly.utils.poll.Poll;

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
		else if (dataType.equals(CreatePollCommand.class))
			data = readCreatePollCommand();
		else if (dataType.equals(LoadPollCommand.class))
			data = readLoadPollCommand();
		else if (dataType.equals(VotePollCommand.class))
			data = readVotePollCommand();
		else if (dataType.equals(Poll.class))
			data = readPoll();
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

	private CreatePollCommand readCreatePollCommand() throws IOException {
		String pollName = readString();
		List<String> pollOptions = new ArrayList<>();
		int listSize = readInteger();
		for(int i = 0;i<listSize;i++){
			pollOptions.add(i, readString());
		}
		return new CreatePollCommand(pollName, pollOptions);
	}

	private LoadPollCommand readLoadPollCommand() throws IOException {
		return new LoadPollCommand(readLong());
	}

	private VotePollCommand readVotePollCommand() throws IOException {
		return new VotePollCommand(readLong(), readString());
	}

	private Poll readPoll() throws IOException {
		//TODO id wird nicht verwendet!
		long id = readLong();
		String name = readString();
		Map<String, Integer> map = new HashMap<>();
		int size = readInteger();
		for(int i=0;i<size;i++) {
			map.put(readString(), readInteger());
		}
		String description = readString();
		return new Poll(name, map, description);
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
		else if (dataType.equals(CreatePollCommand.class))
			writeCreatePollCommand((CreatePollCommand) data);
		else if (dataType.equals(LoadPollCommand.class))
			writeLoadPollCommand((LoadPollCommand) data);
		else if (dataType.equals(VotePollCommand.class))
			writeVotePollCommand((VotePollCommand) data);
		else if (dataType.equals(Poll.class))
			writePoll((Poll) data);
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

	private void writeCreatePollCommand(CreatePollCommand data) throws IOException {
		String pollName = data.getPollName();
		List<String> pollOptions = data.getPollOptions();

		writeString(pollName);
		writeInteger(pollOptions.size());
		for(int i = 0;i<pollOptions.size();i++){
			writeString(pollOptions.get(i));
		}
	}

	private void writeLoadPollCommand(LoadPollCommand data) throws IOException {
		writeLong(data.getPollId());
	}

	private void writeVotePollCommand(VotePollCommand data) throws IOException {
		writeLong(data.getPollId());
		writeString(data.getPollOption());
	}

	private void writePoll(Poll data) throws IOException {
		writeLong(data.getId());
		writeString(data.getName());
		writeInteger(data.getData().size());
		for(String s : data.getData().keySet()) {
			writeString(s);
			writeInteger(data.getData().get(s));
		}
		writeString(data.getDescription());
	}
}