package com.audriuskumpis;

/**
 * Klasė, skirta pranešimo užkodavimui
 */
public class Encoder {

    private final byte[][] gMatrix;

    public Encoder(byte[][] gMatrix) {
        this.gMatrix = gMatrix;
    }

    public byte[] encode(byte[] message) {
        byte[] encodedMessage = MatrixCalculationUtils.multiplyVectorByMatrixG(message, gMatrix);

        for (int i = 0; i < encodedMessage.length; i++) {
            encodedMessage[i] = (byte) (encodedMessage[i] % 2); // rezultatas turi būti dvejetainis
        }

        return encodedMessage;
    }
}
