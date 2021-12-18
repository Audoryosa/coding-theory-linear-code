package com.audriuskumpis;

/**
 *  Iškraipo duotą pranešimą su duota tikimybe.
 */
public class Channel {
    private final double faultProbability;

    public Channel(double probability) {
        this.faultProbability = probability;
    }

    /**
     * Iškraipo duoto pranešimo kiekvieną bitą su duota tikimybe.
     * @param message Pranešimas, kurį norima iškraipyti.
     * @return Gražina iškraipyto pranešimo klaidų vektorių.
     */
    public byte[] distortMessage(byte[] message) {
        byte[] errorVector = new byte[message.length];
        for (int i = 0; i < message.length; i++) {
            if (Math.random() < faultProbability) {
                if (message[i] == 0) {
                    message[i] = 1;
                } else {
                    message[i] = 0;
                }
                errorVector[i] = 1;
            }
        }
        return errorVector;
    }
}
