package ch.ethz.ssh2.packets;

import java.io.UnsupportedEncodingException;

/**
 * PacketSessionExecCommand.
 * 
 * @author Christian Plattner
 * @version 2.50, 03/15/10
 */
public class PacketSessionExecCommand {
	byte[] payload;

	public int recipientChannelID;
	public boolean wantReply;
	public String command;

	public PacketSessionExecCommand(int recipientChannelID, boolean wantReply,
			String command) {
		this.recipientChannelID = recipientChannelID;
		this.wantReply = wantReply;
		this.command = command;
	}

	public byte[] getPayload() {
		if (payload == null) {
			TypesWriter tw = new TypesWriter();
			tw.writeByte(Packets.SSH_MSG_CHANNEL_REQUEST);
			tw.writeUINT32(recipientChannelID);
			tw.writeString("exec");
			tw.writeBoolean(wantReply);
			tw.writeString(command);
			payload = tw.getBytes();
		}
		return payload;
	}

	public byte[] getPayload(String encoding)
			throws UnsupportedEncodingException {
		if (payload == null) {
			TypesWriter tw = new TypesWriter();
			tw.writeByte(Packets.SSH_MSG_CHANNEL_REQUEST);
			tw.writeUINT32(recipientChannelID);
			tw.writeString("exec");
			tw.writeBoolean(wantReply);
			tw.writeString(command, encoding);
			payload = tw.getBytes();
		}
		return payload;
	}
}
