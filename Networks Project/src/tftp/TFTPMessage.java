package tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Base64;

public class TFTPMessage {
	InetAddress hostAddress;
	TFTPMessageType messageType;
	String fileName;
	int sequenceNumber;
	short length;
	byte[] body;
	public int port;

	public TFTPMessage(InetAddress hostAddress, TFTPMessageType messageType, String fileName, int sequenceNumber, byte[] body, short length) {
		this.hostAddress = hostAddress;
		this.messageType = messageType;
		this.body = body;
		this.length = length;
		this.fileName = fileName;
		this.sequenceNumber = sequenceNumber;
	}

	public TFTPMessage(DatagramPacket packet) {
		this(new String(packet.getData()).trim());
		port = packet.getPort();
	}

	public TFTPMessage(String message) {
		String[] messageParts = message.split("\r\n");
		try {
			hostAddress = InetAddress.getByName(messageParts[0]);
			messageType = TFTPMessageType.valueOf(messageParts[1]);
			fileName = messageParts[2];
			sequenceNumber = Integer.parseInt(messageParts[3]);
			length = Short.parseShort(messageParts[4]);
			if (messageParts.length > 6)
				body = Base64.getDecoder().decode(messageParts[6]);
		} catch (Exception e) {
			System.out.println("Error parsing message: " + e.getMessage());
		}
	}

	public InetAddress getHostAddress() {
		return hostAddress;
	}

	public TFTPMessageType getMessageType() {
		return messageType;
	}

	public byte[] getBody() {
		return body;
	}

	public String getFileName() {
		return fileName;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public short getLength() {
		return length;
	}

	public String getBodyAsString() {
		if (body != null)
			return new String(body);
		return "";
	}

	public String pretty() {
		if (body != null && new String(body).trim().equals("ACK")) return null;
		return "{Address: '" + hostAddress.getHostAddress() + "', Type: " + messageType.toString() + ", File: '"
						+ fileName + "', Sequence Number: " + sequenceNumber + ", Body Length: "
						+ length + "}";
	}

	public String toString() {
		String bodyEncoded = (body == null) ? "" : Base64.getEncoder().encodeToString(body);
		return hostAddress.getHostAddress() + "\r\n" + messageType.toString() + "\r\n"
						+ fileName + "\r\n" + sequenceNumber + "\r\n"
						+ length + "\r\n\r\n"
						+ bodyEncoded;
	} // Address|Type|Filename|SequenceNumber|Length||Body
}
