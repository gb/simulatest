package org.simulatest.insistencelayer.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Wire protocol for remote Insistence Layer communication.
 *
 * <p>Three void/void commands encoded as single-byte requests with single-byte responses.
 * On error, the response byte is {@link #ERROR} followed by a length-prefixed UTF-8
 * error message (2 bytes big-endian length, then message bytes).</p>
 *
 * <p>All wire-format read/write helpers live here so that a format change
 * touches exactly one file.</p>
 */
public final class InsistenceLayerProtocol {

	/** Command byte: increase the savepoint level. */
	public static final byte INCREASE = 0x01;

	/** Command byte: decrease the savepoint level. */
	public static final byte DECREASE = 0x02;

	/** Command byte: reset (rollback) the current savepoint level. */
	public static final byte RESET = 0x03;

	/** Response byte: command executed successfully. */
	public static final byte OK = 0x00;

	/** Response byte: command failed. Followed by 2-byte length + UTF-8 error message. */
	public static final byte ERROR = (byte) 0xFF;

	/** Default TCP port for the Insistence Layer server. */
	public static final int DEFAULT_PORT = 4242;

	private InsistenceLayerProtocol() {
	}

	/** Writes {@link #OK} as the response byte. */
	public static void writeOk(DataOutputStream out) throws IOException {
		out.writeByte(OK);
		out.flush();
	}

	/** Writes {@link #ERROR} followed by the length-prefixed UTF-8 message. */
	public static void writeError(DataOutputStream out, String message) throws IOException {
		out.writeByte(ERROR);
		String safe = message != null ? message : "Unknown error";
		byte[] messageBytes = safe.getBytes(StandardCharsets.UTF_8);
		out.writeShort(messageBytes.length);
		out.write(messageBytes);
		out.flush();
	}

	/** Reads a length-prefixed UTF-8 error message (the byte after an {@link #ERROR} response). */
	public static String readErrorMessage(DataInputStream in) throws IOException {
		int length = in.readUnsignedShort();
		byte[] bytes = new byte[length];
		in.readFully(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

}
