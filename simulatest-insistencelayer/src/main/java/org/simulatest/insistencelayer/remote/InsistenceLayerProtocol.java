package org.simulatest.insistencelayer.remote;

/**
 * Wire protocol for remote Insistence Layer communication.
 *
 * <p>Three void/void commands encoded as single-byte requests with single-byte responses.
 * On error, the response byte is {@link #ERROR} followed by a length-prefixed UTF-8
 * error message (2 bytes big-endian length, then message bytes).</p>
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

}
