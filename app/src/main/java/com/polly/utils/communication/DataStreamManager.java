package com.polly.utils.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.polly.config.Config;
import com.polly.utils.Area;
import com.polly.utils.Location;
import com.polly.utils.command.GetMyPollsCommand;
import com.polly.utils.command.GetParticipatedPollsCommand;
import com.polly.utils.command.poll.DeletePollCommand;
import com.polly.utils.command.poll.EditPollDescriptionCommand;
import com.polly.utils.command.poll.EditPollNameCommand;
import com.polly.utils.command.poll.FindPollCommand;
import com.polly.utils.command.poll.GetGeofencePollAreaCommand;
import com.polly.utils.command.poll.GetPollOptionsCommand;
import com.polly.utils.command.poll.GetPollResultsCommand;
import com.polly.utils.command.poll.IsMyPollCommand;
import com.polly.utils.command.poll.RegisterPollChangeListenerCommand;
import com.polly.utils.command.poll.RemovePollChangeListenerCommand;
import com.polly.utils.command.poll.VoteCommand;
import com.polly.utils.command.poll.create.CreateCustomPollCommand;
import com.polly.utils.command.poll.create.CreateGeofencePollCommand;
import com.polly.utils.command.poll.create.CreatePublicPollCommand;
import com.polly.utils.command.user.FindUsersCommand;
import com.polly.utils.command.user.GetUsernameCommand;
import com.polly.utils.command.user.IsUsernameAvailableCommand;
import com.polly.utils.command.user.LoginCommand;
import com.polly.utils.command.user.LogoutCommand;
import com.polly.utils.command.user.RegisterCommand;
import com.polly.utils.encryption.ciphers.AESCipher;
import com.polly.utils.encryption.ciphers.RSACipher;
import com.polly.utils.encryption.exceptions.FailedDecryptionException;
import com.polly.utils.encryption.exceptions.FailedEncryptionException;
import com.polly.utils.encryption.exceptions.FailedKeyGenerationException;
import com.polly.utils.encryption.utils.CipherKeyGenerator;
import com.polly.utils.geofencing.GeofenceEntry;
import com.polly.utils.poll.BasicPollInformation;
import com.polly.utils.poll.PollDescription;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.GeofenceEntryListWrapper;
import com.polly.utils.wrapper.ListWrapper;
import com.polly.utils.wrapper.LoginAnswerWrapper;
import com.polly.utils.wrapper.LogoutAnswerWrapper;
import com.polly.utils.wrapper.MapWrapper;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.PollListWrapper;
import com.polly.utils.wrapper.PollOptionListWrapper;
import com.polly.utils.wrapper.PollOptionsWrapper;
import com.polly.utils.wrapper.PollResultsWrapper;
import com.polly.utils.wrapper.UserListWrapper;
import com.polly.utils.wrapper.UserWrapper;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DataStreamManager {
	private static final int REFRESH_DELAY = Config.DATA_STREAM_MANAGER_REFRESH_DELAY;
	private final DataInputStream input;
	private final DataOutputStream output;
	private PublicKey publicKeyComPartner;
	private SecretKey secretKeyComPartner;
	private SecretKey secretKey;
	private KeyPair keyPair;
	private long partnersDefaultCommunicationId;
	public static final long PARTNERS_DEFAULT_COMMUNICATION_ID = -1;
	
	private static final String CHARSET = "UTF-16";
	private static final int SECRET_KEY_LENGTH = 256;
	private static final int RSA_KEY_LENGTH = 2048;

	public DataStreamManager(InputStream input, OutputStream output, long defaultCommunicationId) throws FailedKeyGenerationException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, FailedDecryptionException, FailedEncryptionException {
		this.input = new DataInputStream(input);
		this.output = new DataOutputStream(output);
		this.secretKey = generateSecretKey();
		this.keyPair = generateKeyPair();
		this.publicKeyComPartner = exchangePublicKeys();
		this.secretKeyComPartner = exchangeSecretKeys();
		this.partnersDefaultCommunicationId = exchangeDefaultCommunicationIds(defaultCommunicationId);
	}

	public Message receive() throws IOException, ClassNotFoundException{
		while(input.available() <= 0) {
			try {
				Thread.sleep(REFRESH_DELAY);
			} catch(InterruptedException e) {
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
			// own types:
		else if(dataType.equals(VoteCommand.class))
			data = readVoteCommand();
		else if(dataType.equals(RegisterPollChangeListenerCommand.class))
			data = readRegisterPollChangeListenerCommand();
		else if(dataType.equals(RemovePollChangeListenerCommand.class))
			data = readRemovePollChangeListenerCommand();
		else if(dataType.equals(IsUsernameAvailableCommand.class))
			data = readIsUsernameAvailableCommand();
		else if(dataType.equals(LoginCommand.class))
			data = readLoginCommand();
		else if(dataType.equals(RegisterCommand.class))
			data = readRegisterCommand();
		else if(dataType.equals(GetMyPollsCommand.class))
			data = readGetMyPollsCommand();
		else if(dataType.equals(GetParticipatedPollsCommand.class))
			data = readGetParticipatedPollsCommand();
		else if(dataType.equals(PollDescription.class))
			data = readPollDescription();
		else if(dataType.equals(ErrorWrapper.class))
			data = readErrorWrapper();
		else if(dataType.equals(LoginAnswerWrapper.class))
			data = readLoginAnswerWrapper();
		else if(dataType.equals(PollOptionsWrapper.class))
			data = readPollOptionsWrapper();
		else if(dataType.equals(BasicPollInformation.class))
			data = readBasicPollInformation();
		else if(dataType.equals(LocalDateTime.class))
			data = readLocalDateTime();
		else if(dataType.equals(FindUsersCommand.class))
			data = readFindUsersCommand();
		else if(dataType.equals(GetPollResultsCommand.class))
			data = readGetPollResultsCommand();
		else if(dataType.equals(GetPollOptionsCommand.class))
			data = readGetPollOptionsCommand();
		else if(dataType.equals(Area.class))
			data = readArea();
		else if(dataType.equals(UserWrapper.class))
			data = readUserWrapper();
		else if(dataType.equals(UserListWrapper.class))
			data = readUserListWrapper();
		else if(dataType.equals(PollResultsWrapper.class))
			data = readPollResultsWrapper();
		else if(dataType.equals(PollListWrapper.class))
			data = readPollListWrapper();
		else if(dataType.equals(CreatePublicPollCommand.class))
			data = readCreatePublicPollCommand();
		else if(dataType.equals(CreateGeofencePollCommand.class))
			data = readCreateGeofencePollCommand();
		else if(dataType.equals(CreateCustomPollCommand.class))
			data = readCreateCustomPollCommand();
		else if(dataType.equals(GetUsernameCommand.class))
			data = readGetUsernameCommand();
		else if(dataType.equals(Location.class))
			data = readLocation();
		else if(dataType.equals(GetGeofencePollAreaCommand.class))
			data = readGetGeofencePollArea();
		else if(dataType.equals(EditPollDescriptionCommand.class))
			data = readEditPollDescriptionCommand();
		else if(dataType.equals(EditPollNameCommand.class))
			data = readEditPollNameCommand();
		else if(dataType.equals(IsMyPollCommand.class))
			data = readIsMyPollCommand();
		else if(dataType.equals(LogoutCommand.class))
			data = readLogoutCommand();
		else if(dataType.equals(LogoutAnswerWrapper.class))
			data = readLogoutAnswerWrapper();
		else if(dataType.equals(PollOptionListWrapper.class))
			data = readPollOptionListWrapper();
		else if(dataType.equals(FindPollCommand.class))
			data = readFindPollCommand();
		else if(dataType.equals(GeofenceEntry.class))
			data = readGeofenceEntry();
		else if(dataType.equals(GeofenceEntryListWrapper.class))
			data = readGeofenceEntryListWrapper();
		else if(dataType.equals(DeletePollCommand.class))
			data = readDeletePollCommand();
			
			// default type:
		else
			data = readString();

		return new Message(sender, receiver, responseId, dataType, data, generics);
	}

	private int readInteger() throws IOException {
		return Integer.valueOf(readString());
	}

	private boolean readBoolean() throws IOException {
		return Boolean.valueOf(readString());
	}

	private byte readByte() throws IOException {
		return input.readByte();
	}

	private char readChar() throws IOException {
		return Character.valueOf(readString().toCharArray()[0]);
	}

	private double readDouble() throws IOException {
		return Double.valueOf(readString());
	}

	private float readFloat() throws IOException {
		return Float.valueOf(readString());
	}

	private long readLong() throws IOException {
		return Long.valueOf(readString());
	}

	private short readShort() throws IOException {
		return Short.valueOf(readString());
	}

	private String readString() throws IOException {
		return new String(readDecryptedByteArray(), CHARSET);
	}

	private int readClearInteger() throws IOException {
		return input.readInt();
	}

	private String readClearString() throws IOException {
		return input.readUTF();
	}

	private byte[] readDecryptedByteArray() throws IOException {
		int length = readClearInteger();
		byte[] bytes = new byte[length];
		for(int i = 0;i<length;i++){
			bytes[i] = readByte();
		}

		try {
			return AESCipher.decrypt(bytes, secretKeyComPartner);
		} catch (FailedDecryptionException e) {
			throw new IOException(e);
		}
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

	public void send(Message message) throws IOException{
		Class<?> dataType = message.getDataType();
		if(dataType == null) {
			throw new NullPointerException();
		}
		Object data = message.getData();
		List<Class<?>> generics = message.getGenerics();
		writeLong(message.getSender());
		
		long receiver = message.getReceiver();
		if(receiver == PARTNERS_DEFAULT_COMMUNICATION_ID)
			receiver = partnersDefaultCommunicationId;
		writeLong(receiver);
		
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
		else if (isList(dataType)) {
			if(generics.isEmpty())
				throw new IllegalArgumentException("missing generics");
			writeList((List<Object>) data, generics.get(0));
		}
		else if (isMap(dataType)) {
			if(generics.size() < 2)
				throw new IllegalArgumentException("missing generics");
			writeMap((Map<Object,Object>) data, generics.get(0), generics.get(1));
		}
			// own types:
		else if(dataType.equals(VoteCommand.class))
			writeVoteCommand((VoteCommand) data);
		else if(dataType.equals(RegisterPollChangeListenerCommand.class))
			writeRegisterPollChangeListenerCommand((RegisterPollChangeListenerCommand) data);
		else if(dataType.equals(RemovePollChangeListenerCommand.class))
			writeRemovePollChangeListenerCommand((RemovePollChangeListenerCommand) data);
		else if(dataType.equals(IsUsernameAvailableCommand.class))
			writeIsUsernameAvailableCommand((IsUsernameAvailableCommand) data);
		else if(dataType.equals(LoginCommand.class))
			writeLoginCommand((LoginCommand) data);
		else if(dataType.equals(RegisterCommand.class))
			writeRegisterCommand((RegisterCommand) data);
		else if(dataType.equals(GetMyPollsCommand.class))
			writeGetMyPollsCommand((GetMyPollsCommand) data);
		else if(dataType.equals(GetParticipatedPollsCommand.class))
			writeGetParticipatedPollsCommand((GetParticipatedPollsCommand) data);
		else if(dataType.equals(PollDescription.class))
			writePollDescription((PollDescription) data);
		else if(dataType.equals(ErrorWrapper.class))
			writeErrorWrapper((ErrorWrapper) data);
		else if(dataType.equals(LoginAnswerWrapper.class))
			writeLoginAnswerWrapper((LoginAnswerWrapper) data);
		else if(dataType.equals(PollOptionsWrapper.class))
			writePollOptionsWrapper((PollOptionsWrapper) data);
		else if(dataType.equals(BasicPollInformation.class))
			writeBasicPollInformation((BasicPollInformation) data);
		else if(dataType.equals(LocalDateTime.class))
			writeLocalDateTime((LocalDateTime) data);
		else if(dataType.equals(FindUsersCommand.class))
			writeFindUsersCommand((FindUsersCommand) data);
		else if(dataType.equals(GetPollResultsCommand.class))
			writeGetPollResultsCommand((GetPollResultsCommand) data);
		else if(dataType.equals(GetPollOptionsCommand.class))
			writeGetPollOptionsCommand((GetPollOptionsCommand) data);
		else if(dataType.equals(Area.class))
			writeArea((Area) data);
		else if(dataType.equals(UserWrapper.class))
			writeUserWrapper((UserWrapper) data);
		else if(dataType.equals(UserListWrapper.class))
			writeUserListWrapper((UserListWrapper) data);
		else if(dataType.equals(PollResultsWrapper.class))
			writePollResultsWrapper((PollResultsWrapper) data);
		else if(dataType.equals(PollListWrapper.class))
			writePollListWrapper((PollListWrapper) data);
		else if(dataType.equals(CreatePublicPollCommand.class))
			writeCreatePublicPollCommand((CreatePublicPollCommand) data);
		else if(dataType.equals(CreateGeofencePollCommand.class))
			writeCreateGeofencePollCommand((CreateGeofencePollCommand) data);
		else if(dataType.equals(CreateCustomPollCommand.class))
			writeCreateCustomPollCommand((CreateCustomPollCommand) data);
		else if(dataType.equals(GetUsernameCommand.class))
			writeGetUsernameCommand((GetUsernameCommand) data);
		else if(dataType.equals(Location.class))
			writeLocation((Location) data);
		else if(dataType.equals(GetGeofencePollAreaCommand.class))
			writeGetGeofencePollArea((GetGeofencePollAreaCommand) data);
		else if(dataType.equals(EditPollDescriptionCommand.class))
			writeEditPollDescriptionCommand((EditPollDescriptionCommand) data);
		else if(dataType.equals(EditPollNameCommand.class))
			writeEditPollNameCommand((EditPollNameCommand) data);
		else if(dataType.equals(IsMyPollCommand.class))
			writeIsMyPollCommand((IsMyPollCommand) data);
		else if(dataType.equals(LogoutCommand.class))
			writeLogoutCommand((LogoutCommand) data);
		else if(dataType.equals(LogoutAnswerWrapper.class))
			writeLogoutAnswerWrapper((LogoutAnswerWrapper) data);
		else if(dataType.equals(PollOptionListWrapper.class))
			writePollOptionListWrapper((PollOptionListWrapper) data);
		else if(dataType.equals(FindPollCommand.class))
			writeFindPollCommand((FindPollCommand) data);
		else if(dataType.equals(GeofenceEntry.class))
				writeGeofenceEntry((GeofenceEntry) data);
		else if(dataType.equals(GeofenceEntryListWrapper.class))
			writeGeofenceEntryListWrapper((GeofenceEntryListWrapper) data);
		else if(dataType.equals(DeletePollCommand.class))
			writeDeletePollCommand((DeletePollCommand) data);
		
		
			// default type:
		else
			writeString((String) data);
	}

	private void writeInteger(Integer data) throws IOException {
		writeString(String.valueOf(data));
	}

	private void writeBoolean(Boolean data) throws IOException {
		writeString(String.valueOf(data));
	}

	private void writeByte(Byte data) throws IOException {
		output.writeByte(data);
	}

	private void writeChar(Character data) throws IOException {
		writeString(String.valueOf(data));
	}

	private void writeDouble(Double data) throws IOException {
		writeString(String.valueOf(data));
	}

	private void writeFloat(Float data) throws IOException {
		writeString(String.valueOf(data));
	}

	private void writeLong(Long data) throws IOException {
		writeString(String.valueOf(data));
	}

	private void writeShort(Short data) throws IOException {
		writeString(String.valueOf(data));
	}

	private void writeString(String data) throws IOException {
		writeEncryptedByteArray(data.getBytes(CHARSET));
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
	
	private void writeClearString(String string) throws IOException {
		output.writeUTF(string);
	}

	private void writeClearInteger(Integer intVal) throws IOException {
		output.writeInt(intVal);
	}

	private void writeEncryptedByteArray(byte[] bytes) throws IOException {
		byte[] encrypted;
		try {
			encrypted = AESCipher.encrypt(bytes, secretKey);
		} catch (FailedEncryptionException e) {
			throw new IOException(e);
		}

		int length = encrypted.length;
		writeClearInteger(length);
		for(int i=0;i<length;i++){
			writeByte(encrypted[i]);
		}
	}

	public static boolean isList(Class<?> classType) {
		return classType.equals(ArrayList.class) || classType.equals(LinkedList.class);
	}

	public static boolean isMap(Class<?> classType) {
		return classType.equals(HashMap.class) || classType.equals(TreeMap.class);
	}

	private SecretKey generateSecretKey() throws FailedKeyGenerationException{
		return CipherKeyGenerator.generateAESSecretKey();
	}

	private KeyPair generateKeyPair() throws FailedKeyGenerationException{
		return CipherKeyGenerator.generateRSAKeyPair(RSA_KEY_LENGTH);
	}




	private PublicKey exchangePublicKeys() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		sendPublicKey();

		while(input.available() <= 0) {
			try {
				Thread.sleep(REFRESH_DELAY);
			} catch(InterruptedException e) {
				//TODO replace with logger
				e.printStackTrace();
			}
		}

		return readPublicKeyComPartner();
	}

	private void sendPublicKey() throws IOException {
		byte[] pKey = keyPair.getPublic().getEncoded();
		writeClearInteger(pKey.length);
		for(int i = 0;i<pKey.length;i++){
			writeByte(pKey[i]);
		}
	}

	private PublicKey readPublicKeyComPartner() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		int length = readClearInteger();
		byte[] bytes = new byte[length];
		for(int i = 0;i<length;i++){
			bytes[i] = readByte();
		}

		return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
	}

	private SecretKey exchangeSecretKeys() throws IOException, FailedEncryptionException, FailedDecryptionException {
		sendSecretKey();

		while(input.available() <= 0) {
			try {
				Thread.sleep(REFRESH_DELAY);
			} catch(InterruptedException e) {
				//TODO replace with logger
				e.printStackTrace();
			}
		}

		return readSecretKeyComPartner();
	}
	
	private long exchangeDefaultCommunicationIds(long defaultCommunicationId) throws IOException {
		writeLong(defaultCommunicationId);
		return readLong();
	}

	private SecretKey readSecretKeyComPartner() throws IOException, FailedDecryptionException {
		byte[] encryptedSecretKeyBytes = new byte[SECRET_KEY_LENGTH];
		for(int i = 0;i<SECRET_KEY_LENGTH;i++){
			encryptedSecretKeyBytes[i] = readByte();
		}

		byte[] decryptedSecretKeyBytes = RSACipher.decrypt(encryptedSecretKeyBytes, keyPair.getPrivate());
		return new SecretKeySpec(decryptedSecretKeyBytes, "AES");
	}

	private void sendSecretKey() throws FailedEncryptionException, IOException {
		byte[] encryptedSecretKey = RSACipher.encrypt(secretKey.getEncoded(), publicKeyComPartner);
		for (int i = 0; i < SECRET_KEY_LENGTH; i++) {
			writeByte(encryptedSecretKey[i]);
		}
	}

	public static byte[] intToByteArray(final int x)
	{
		final byte[] array = new byte[4];
		array[0] = (byte)((x & 0xFF000000) >> 24);
		array[1] = (byte)((x & 0xFF0000) >> 16);
		array[2] = (byte)((x & 0xFF00) >> 8);
		array[3] = (byte)(x & 0xFF);
		return array;
	}

	public static int byteArrayToInt(final byte[] pByteArray)
	{
		return (((int)(pByteArray[0]) << 24) & 0xFF000000) |
				(((int)(pByteArray[1]) << 16) & 0xFF0000) |
				(((int)(pByteArray[2]) << 8) & 0xFF00) |
				((int)(pByteArray[3]) & 0xFF);
	}
	
	
	// own read/write methods
	private void writeVoteCommand(VoteCommand data) throws IOException {
		boolean locationExists = data.getLocation() != null;
		writeBoolean(locationExists);
		
		writeLong(data.getId());
		writeString(data.getOption());
		if(locationExists)
			writeLocation(data.getLocation());
	}
	
	private VoteCommand readVoteCommand() throws IOException{
		if(readBoolean())
			return new VoteCommand(readLong(), readString(), readLocation());
		return new VoteCommand(readLong(), readString());
	}
	
	private void writeRegisterPollChangeListenerCommand(RegisterPollChangeListenerCommand data) throws IOException{
		writeLong(data.getId());
		writeBoolean(data.hasVoted());
	}
	
	private RegisterPollChangeListenerCommand readRegisterPollChangeListenerCommand() throws IOException {
		return new RegisterPollChangeListenerCommand(readLong(), readBoolean());
	}
	
	private void writeRemovePollChangeListenerCommand(RemovePollChangeListenerCommand data) throws IOException {
		writeLong(data.getId());
	}
	
	private RemovePollChangeListenerCommand readRemovePollChangeListenerCommand() throws IOException {
		return new RemovePollChangeListenerCommand(readLong());
	}
	
	private void writeIsUsernameAvailableCommand(IsUsernameAvailableCommand data) throws IOException {
		writeString(data.getUsername());
	}
	
	private IsUsernameAvailableCommand readIsUsernameAvailableCommand() throws IOException {
		return new IsUsernameAvailableCommand(readString());
	}
	
	private void writeLoginCommand(LoginCommand data) throws IOException {
		writeString(data.getIdToken());
	}
	
	private LoginCommand readLoginCommand() throws IOException {
		return new LoginCommand(readString());
	}
	
	private void writeRegisterCommand(RegisterCommand data) throws IOException {
		writeString(data.getIdToken());
		writeString(data.getName());
	}
	
	private RegisterCommand readRegisterCommand() throws IOException {
		return new RegisterCommand(readString(), readString());
	}
	
	private void writeGetMyPollsCommand(GetMyPollsCommand data) throws IOException {
		// nothing to do
	}
	
	private GetMyPollsCommand readGetMyPollsCommand() throws IOException {
		return new GetMyPollsCommand();
	}
	
	private void writeGetParticipatedPollsCommand(GetParticipatedPollsCommand data) throws IOException {
		// nothing to do
	}
	
	private GetParticipatedPollsCommand readGetParticipatedPollsCommand() throws IOException {
		return new GetParticipatedPollsCommand();
	}
	
	private void writePollDescription(PollDescription data) throws IOException {
		writeString(data.getDescription());
	}
	
	private PollDescription readPollDescription() throws IOException {
		return new PollDescription(readString());
	}
	
	private void writeErrorWrapper(ErrorWrapper data) throws IOException {
		writeString(data.getMessage());
	}
	
	private ErrorWrapper readErrorWrapper() throws IOException {
		return new ErrorWrapper(readString());
	}
	
	private void writeLoginAnswerWrapper(LoginAnswerWrapper data) throws IOException {
		writeBoolean(data.isSuccessful());
		writeString(data.getMessage());
	}
	
	private LoginAnswerWrapper readLoginAnswerWrapper() throws IOException {
		return new LoginAnswerWrapper(readBoolean(), readString());
	}
	
	private void writePollOptionsWrapper(PollOptionsWrapper data) throws IOException {
		writeInteger(data.getPollOptions().size());
		for(int i = 0;i<data.getPollOptions().size();i++) {
			writeString(data.getPollOptions().get(i));
		}
		writeBasicPollInformation(data.getBasicPollInformation());
	}
	
	private PollOptionsWrapper readPollOptionsWrapper() throws IOException {
		List<String> list = new ArrayList<>();
		
		int length = readInteger();
		for(int i = 0;i<length;i++) {
			list.add(readString());
		}
		return new PollOptionsWrapper(list, readBasicPollInformation());
	}
	
	private void writeBasicPollInformation(BasicPollInformation data) throws IOException {
		writeLong(data.getId());
		writeString(data.getName());
		writeString(data.getCreator());
		writePollDescription(data.getDescription());
		writeLocalDateTime(data.getExpirationTime());
	}
	
	private BasicPollInformation readBasicPollInformation() throws IOException {
		return new BasicPollInformation(readLong(), readString(), readString(), readPollDescription(), readLocalDateTime());
	}
	
	private void writeLocalDateTime(LocalDateTime data) throws IOException {
		writeInteger(data.getYear());
		writeInteger(data.getMonthValue());
		writeInteger(data.getDayOfMonth());
		writeInteger(data.getHour());
		writeInteger(data.getMinute());
	}
	
	private LocalDateTime readLocalDateTime() throws IOException {
		return LocalDateTime.of(readInteger(), readInteger(), readInteger(), readInteger(), readInteger());
	}
	
	private void writeFindUsersCommand(FindUsersCommand data) throws IOException {
		FindUsersCommand command = (FindUsersCommand) data;
		writeString(command.getPrefix());
	}
	
	private FindUsersCommand readFindUsersCommand() throws IOException {
		return new FindUsersCommand(readString());
	}
	
	private void writeGetPollResultsCommand(GetPollResultsCommand data) throws IOException {
		writeLong(data.getId());
	}
	
	private GetPollResultsCommand readGetPollResultsCommand() throws IOException {
		return new GetPollResultsCommand(readLong());
	}
	
	private void writeGetPollOptionsCommand(GetPollOptionsCommand data) throws IOException {
		writeLong(data.getId());
	}
	
	private GetPollOptionsCommand readGetPollOptionsCommand() throws IOException {
		return new GetPollOptionsCommand(readLong());
	}
	
	private void writeArea(Area data) throws IOException {
		writeDouble(data.getLatitude());
		writeDouble(data.getLongitude());
		writeDouble(data.getRadius());
	}
	
	private Area readArea() throws IOException {
		return new Area(readDouble(), readDouble(), readDouble());
	}
	
	private void writeUserWrapper(UserWrapper data) throws IOException {
		writeString(data.getName());
	}
	
	private UserWrapper readUserWrapper() throws IOException {
		return new UserWrapper(readString());
	}
	
	private void writeUserListWrapper(UserListWrapper data) throws IOException {
		writeInteger(data.getUserList().size());
		
		for(int i = 0;i<data.getUserList().size();i++) {
			writeUserWrapper(data.getUserList().get(i));
		}
	}
	
	private UserListWrapper readUserListWrapper() throws IOException {
		List<UserWrapper> list = new ArrayList<>();
		int length = readInteger();
		
		for(int i = 0;i<length;i++) {
			list.add(readUserWrapper());
		}
		return new UserListWrapper(list);
	}
	
	private void writePollResultsWrapper(PollResultsWrapper data) throws IOException {
		writeInteger(data.getPollResults().size());
		
		for(String key : data.getPollResults().keySet()) {
			writeString(key);
			writeInteger(data.getPollResults().get(key));
		}
		
		writeBasicPollInformation(data.getBasicPollInformation());
	}
	
	private PollResultsWrapper readPollResultsWrapper() throws IOException {
		Map<String, Integer> map = new HashMap<>();
		
		int length = readInteger();
		for(int i = 0;i<length;i++) {
			map.put(readString(), readInteger());
		}
		
		return new PollResultsWrapper(map, readBasicPollInformation());
	}
	
	private void writePollOptionListWrapper(PollOptionListWrapper data) throws IOException {
		writeInteger(data.getList().size());
		
		for(PollOptionsWrapper wrapper : data.getList()) {
			writePollOptionsWrapper(wrapper);
		}
	}
	
	private PollOptionListWrapper readPollOptionListWrapper() throws IOException {
		List<PollOptionsWrapper> list = new LinkedList<>();
		int length = readInteger();
		
		for(int i = 0;i<length;i++) {
			list.add(readPollOptionsWrapper());
		}
		return new PollOptionListWrapper(list);
	}
	
	private void writePollListWrapper(PollListWrapper data) throws IOException {
		writeInteger(data.getList().size());
		
		for(PollResultsWrapper wrapper : data.getList()) {
			writePollResultsWrapper(wrapper);
		}
	}
	
	private PollListWrapper readPollListWrapper() throws IOException {
		List<PollResultsWrapper> list = new LinkedList<>();
		int length = readInteger();
		
		for(int i = 0;i<length;i++) {
			list.add(readPollResultsWrapper());
		}
		return new PollListWrapper(list);
	}
	
	private void writeCreatePublicPollCommand(CreatePublicPollCommand data) throws IOException {
		writeString(data.getName());
		writePollDescription(data.getDescription());
		writeLocalDateTime(data.getExpirationTime());
		// write options
		writeInteger(data.getOptions().size());
		for(String option : data.getOptions()) {
			writeString(option);
		}
	}
	
	private CreatePublicPollCommand readCreatePublicPollCommand() throws IOException {
		String name = readString();
		PollDescription description = readPollDescription();
		LocalDateTime expirationTime = readLocalDateTime();
		List<String> options = new ArrayList<>();
		
		int length = readInteger();
		for(int i = 0;i<length;i++) {
			options.add(readString());
		}
		return new CreatePublicPollCommand(name, description, expirationTime, options);
	}
	
	private void writeCreateGeofencePollCommand(CreateGeofencePollCommand data) throws IOException {
		writeString(data.getName());
		writePollDescription(data.getDescription());
		writeLocalDateTime(data.getExpirationTime());
		// write options
		writeInteger(data.getOptions().size());
		for(String option : data.getOptions()) {
			writeString(option);
		}
		
		writeArea(data.getArea());
	}
	
	private CreateGeofencePollCommand readCreateGeofencePollCommand() throws IOException {
		String name = readString();
		PollDescription description = readPollDescription();
		LocalDateTime expirationTime = readLocalDateTime();
		List<String> options = new ArrayList<>();
		
		int length = readInteger();
		for(int i = 0;i<length;i++) {
			options.add(readString());
		}
		return new CreateGeofencePollCommand(name, description, expirationTime, options, readArea());
	}
	
	private void writeCreateCustomPollCommand(CreateCustomPollCommand data) throws IOException {
		writeString(data.getName());
		writePollDescription(data.getDescription());
		writeLocalDateTime(data.getExpirationTime());
		// write options
		writeInteger(data.getOptions().size());
		for(String option : data.getOptions()) {
			writeString(option);
		}
		// write canSee
		writeInteger(data.getCanSee().size());
		for(String name : data.getCanSee()) {
			writeString(name);
		}
		// write canSeeResults
		writeInteger(data.getCanSeeResults().size());
		for(String name : data.getCanSeeResults()) {
			writeString(name);
		}
	}
	
	private CreateCustomPollCommand readCreateCustomPollCommand() throws IOException {
		String name = readString();
		PollDescription description = readPollDescription();
		LocalDateTime expirationTime = readLocalDateTime();
		List<String> options = new ArrayList<>();
		List<String> canSee = new ArrayList<>();
		List<String> canSeeResults = new ArrayList<>();
		
		int optionsLength = readInteger();
		for(int i = 0;i<optionsLength;i++) {
			options.add(readString());
		}
		
		int canSeeLength = readInteger();
		for(int i = 0;i<canSeeLength;i++) {
			canSee.add(readString());
		}
		
		int canSeeResultsLength = readInteger();
		for(int i = 0;i<canSeeResultsLength;i++) {
			canSeeResults.add(readString());
		}
		return new CreateCustomPollCommand(name, description, expirationTime, options, canSee, canSeeResults);
	}
	
	private void writeGetUsernameCommand(GetUsernameCommand data) throws IOException {
		// nothing to do
	}
	
	private GetUsernameCommand readGetUsernameCommand() throws IOException {
		return new GetUsernameCommand();
	}
	
	private void writeLocation(Location data) throws IOException {
		writeDouble(data.getLatitude());
		writeDouble(data.getLongitude());
	}
	
	private Location readLocation() throws IOException {
		return new Location(readDouble(), readDouble());
	}
	
	private void writeGetGeofencePollArea(GetGeofencePollAreaCommand data) throws IOException {
		writeLong(data.getId());
	}
	
	private GetGeofencePollAreaCommand readGetGeofencePollArea() throws IOException {
		return new GetGeofencePollAreaCommand(readLong());
	}
	
	private void writeEditPollDescriptionCommand(EditPollDescriptionCommand data) throws IOException {
		writeLong(data.getId());
		writePollDescription(data.getDescription());
	}
	
	private EditPollDescriptionCommand readEditPollDescriptionCommand() throws IOException {
		return new EditPollDescriptionCommand(readLong(), readPollDescription());
	}
	
	private void writeEditPollNameCommand(EditPollNameCommand data) throws IOException {
		writeLong(data.getId());
		writeString(data.getName());
	}
	
	private EditPollNameCommand readEditPollNameCommand() throws IOException {
		return new EditPollNameCommand(readLong(), readString());
	}
	
	private void writeIsMyPollCommand(IsMyPollCommand data) throws IOException {
		writeLong(data.getId());
	}
	
	private IsMyPollCommand readIsMyPollCommand() throws IOException {
		return new IsMyPollCommand(readLong());
	}
	
	private void writeLogoutCommand(LogoutCommand data) throws IOException {
		// nothing to do
	}
	
	private LogoutCommand readLogoutCommand() throws IOException {
		return new LogoutCommand();
	}
	
	private void writeLogoutAnswerWrapper(LogoutAnswerWrapper data) throws IOException {
		writeBoolean(data.isSuccessful());
		writeString(data.getMessage());
	}
	
	private LogoutAnswerWrapper readLogoutAnswerWrapper() throws IOException {
		return new LogoutAnswerWrapper(readBoolean(), readString());
	}
	
	private void writeFindPollCommand(FindPollCommand data) throws IOException {
		writeString(data.getName());
		writeString(data.getCreator());
		writeBoolean(data.getActive());
		writeBoolean(data.getExpired());
	}
	
	private FindPollCommand readFindPollCommand() throws IOException {
		return new FindPollCommand(readString(), readString(), readBoolean(), readBoolean());
	}
	
	private void writeGeofenceEntry(GeofenceEntry data) throws IOException {
		writeArea(data.getArea());
		writeLong(data.getId());
	}
	
	private GeofenceEntry readGeofenceEntry() throws IOException {
		return new GeofenceEntry(readArea(), readLong());
	}
	
	private void writeGeofenceEntryListWrapper(GeofenceEntryListWrapper data) throws IOException {
		writeInteger(data.getGeofenceEntries().size());
		
		for(GeofenceEntry e : data.getGeofenceEntries()) {
			writeGeofenceEntry(e);
		}
	}
	
	private GeofenceEntryListWrapper readGeofenceEntryListWrapper() throws IOException {
		int length = readInteger();
		List<GeofenceEntry> entries = new LinkedList<>();
		for(int i = 0;i<length;i++) {
			entries.add(readGeofenceEntry());
		}
		return new GeofenceEntryListWrapper(entries);
	}
	
	private void writeDeletePollCommand(DeletePollCommand data) throws IOException {
		writeLong(data.getId());
	}
	
	private DeletePollCommand readDeletePollCommand() throws IOException {
		return new DeletePollCommand(readLong());
	}
}