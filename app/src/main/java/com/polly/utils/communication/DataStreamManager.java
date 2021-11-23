package com.polly.utils.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.polly.config.Config;
import com.polly.utils.ListWrapper;
import com.polly.utils.MapWrapper;
import com.polly.utils.Message;
import com.polly.utils.command.CreatePollCommand;
import com.polly.utils.command.ErrorCommand;
import com.polly.utils.command.GetMyPollsCommand;
import com.polly.utils.command.GetParticipatedPollsCommand;
import com.polly.utils.command.LoadPollCommand;
import com.polly.utils.command.LoadPollOptionsCommand;
import com.polly.utils.command.RequestPollUpdatesCommand;
import com.polly.utils.command.VotePollCommand;
import com.polly.utils.poll.Poll;

public class DataStreamManager {
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
		long responseId = readLong();
		String className = readString();

		return readInput(sender, receiver, responseId, Class.forName(className));
	}

	private Message readInput(long sender, long receiver, long responseId, Class<?> dataType) throws IOException, ClassNotFoundException {
		if(dataType == null) {
			throw new NullPointerException();
		}
		Object data;
		List<Class<?>> generics = new ArrayList<>();
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
		else if (dataType.equals(LoadPollOptionsCommand.class))
			data = readLoadPollOptionsCommand();
		else if (dataType.equals(Poll.class))
			data = readPoll();
		else if (isList(dataType)) {
			ListWrapper lw = readList();
			data = lw.getList();
			generics = new ArrayList<>();
			generics.add(lw.getType());
		}
		else if (isMap(dataType)) {
			MapWrapper mw = readMap();
			data = mw.getMap();
			generics = new ArrayList<>();
			generics.add(mw.getKeyType());
			generics.add(mw.getValueType());
		}
		else if (dataType.equals(ErrorCommand.class))
			data = readErrorCommand();
		else if (dataType.equals(GetParticipatedPollsCommand.class))
			data = readGetParticipatedPollsCommand();
		else if (dataType.equals(GetMyPollsCommand.class))
			data = readGetMyPollsCommand();
		else if (dataType.equals(RequestPollUpdatesCommand.class))
			data = readRequestPollUpdatesCommand();
		// default type:
		else
			data = readString();

		return new Message(sender, receiver, responseId, dataType, data, generics);
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

	private LoadPollOptionsCommand readLoadPollOptionsCommand() throws IOException {
		return new LoadPollOptionsCommand(readLong());
	}

	private Poll readPoll() throws IOException {
		long id = readLong();
		String name = readString();
		Map<String, Integer> map = new HashMap<>();
		int size = readInteger();
		for(int i=0;i<size;i++) {
			map.put(readString(), readInteger());
		}
		String description = readString();
		return new Poll(id, name, map, description);
	}

	private ListWrapper readList() throws IOException, ClassNotFoundException {
		Class<?> type = Class.forName(readString());
		int size = readInteger();
		List<Object> list = new ArrayList<>();
		for(int i = 0;i <size;i++) {
			Message message = readInput(0L, 0L, 0L, type);
			list.add(message.getDataType().cast(message.getData()));
		}
		return new ListWrapper(list, type);

	}

	private MapWrapper readMap() throws IOException, ClassNotFoundException {
		Class<?> keyType = Class.forName(readString());
		Class<?> valueType = Class.forName(readString());
		int size = readInteger();
		Map<Object, Object> map = new HashMap<>();
		for(int i = 0;i <size;i++) {
			Message key = readInput(0L, 0L, 0L, keyType);
			Message value = readInput(0L, 0L, 0L, valueType);
			map.put(key.getDataType().cast(key.getData()), value.getDataType().cast(value.getData()));
		}
		return new MapWrapper(map, keyType, valueType);
	}

	private ErrorCommand readErrorCommand() throws IOException {
		return new ErrorCommand(readString());
	}

	private GetParticipatedPollsCommand readGetParticipatedPollsCommand() throws IOException{
		return new GetParticipatedPollsCommand();
	}

	private GetMyPollsCommand readGetMyPollsCommand() throws IOException {
		return new GetMyPollsCommand();
	}

	private RequestPollUpdatesCommand readRequestPollUpdatesCommand() throws IOException{
		long pollId = readLong();
		if(RequestPollUpdatesCommand.RequestType.START.getValue() == readInteger()){
			return new RequestPollUpdatesCommand(pollId, RequestPollUpdatesCommand.RequestType.START);
		}
		return new RequestPollUpdatesCommand(pollId, RequestPollUpdatesCommand.RequestType.STOP);
	}

	public void send(Message message) throws IOException{
		Class<?> dataType = message.getDataType();
		if(dataType == null) {
			throw new NullPointerException();
		}
		Object data = message.getData();
		List<Class<?>> generics = message.getGenerics();
		writeLong(message.getSender());
		writeLong(message.getReceiver());
		writeLong(message.getResponseId());
		writeString(message.getDataType().getName());

		write(dataType, data, generics);
	}

	@SuppressWarnings("unchecked")
	private void write(Class<?> dataType, Object data, List<Class<?>> generics) throws IOException, IllegalArgumentException{
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
		else if (dataType.equals(LoadPollOptionsCommand.class))
			writeLoadPollOptionsCommand((LoadPollOptionsCommand) data);
		else if (dataType.equals(Poll.class))
			writePoll((Poll) data);
		else if (isList(dataType)) {
			if(generics.isEmpty())
				throw new IllegalArgumentException("missing generics");
			writeList((List<Object>) data, generics.get(0));
		}
		else if (isMap(dataType)) {
			if(generics.size() < 2)
				throw new IllegalArgumentException("missing generics");
			writeMap((Map<Object,Object>) data, generics.get(0), generics.get(1));
			// default type:
		}
		else if (dataType.equals(ErrorCommand.class))
			writeErrorCommand((ErrorCommand) data);
		else if (dataType.equals(GetParticipatedPollsCommand.class))
			writeGetParticipatedPollsCommand((GetParticipatedPollsCommand) data);
		else if (dataType.equals(GetMyPollsCommand.class))
			writeGetMyPollsCommand((GetMyPollsCommand) data);
		else if (dataType.equals(RequestPollUpdatesCommand.class))
			writeRequestPollUpdatesCommand((RequestPollUpdatesCommand) data);
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

	private void writeLoadPollOptionsCommand(LoadPollOptionsCommand data) throws IOException {
		writeLong(data.getPollId());
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

	private void writeList(List<Object> data, Class<?> type) throws IOException {
		writeString(type.getName());
		writeInteger(data.size());
		for(Object obj : data) {
			write(type, type.cast(obj), new ArrayList<>());
		}
	}

	private void writeMap(Map<Object,Object> data, Class<?> keyType, Class<?> valueType) throws IOException {
		writeString(keyType.getName());
		writeString(valueType.getName());
		writeInteger(data.size());
		for(Entry<Object, Object> entry : data.entrySet()) {
			write(keyType, keyType.cast(entry.getKey()), new ArrayList<>());
			write(valueType, valueType.cast(entry.getValue()), new ArrayList<>());
		}
	}

	private void writeErrorCommand(ErrorCommand data) throws IOException {
		writeString(data.getMessage());
	}

	private void writeGetParticipatedPollsCommand(GetParticipatedPollsCommand data) throws IOException {
		// empty
	}

	private void writeGetMyPollsCommand(GetMyPollsCommand data) throws IOException {
		//empty
	}

	private void writeRequestPollUpdatesCommand(RequestPollUpdatesCommand data) throws IOException {
		writeLong(data.getPollId());
		writeInteger(data.getStartStop());
	}

	public static boolean isList(Class<?> classType) {
		return classType.equals(ArrayList.class) || classType.equals(LinkedList.class);
	}

	public static boolean isMap(Class<?> classType) {
		return classType.equals(HashMap.class) || classType.equals(TreeMap.class);
	}
}