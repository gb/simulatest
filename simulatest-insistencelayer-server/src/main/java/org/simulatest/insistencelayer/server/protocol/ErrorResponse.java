package org.simulatest.insistencelayer.server.protocol;

public record ErrorResponse(String message, String sqlState, int errorCode) {
}
