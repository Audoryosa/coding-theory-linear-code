package com.audriuskumpis;

import java.util.*;
import java.util.stream.Collectors;

import static com.audriuskumpis.CodingUtils.generateAllPossibleLengthNBinaries;

public class Decoder {

    private final byte[][] hMatrix;
    private Map<String, String> cosetLeaderMap;
    private Map<String, Integer> cosetWeightMap;
    private Map<String, Integer> syndromeWeightMap;

    public Decoder(byte[][] hMatrix) {
        this.hMatrix = hMatrix;
        buildCosetLeaderMap(hMatrix);
    }

    public byte[] decodeMessage(byte[] message, int expectedMessageLength) {
        byte[] fixedMessage = fixErrors(message);
        return decode(fixedMessage, expectedMessageLength);
    }

    private byte[] fixErrors(byte[] message) {
        int weight = getWeight(message);
        if (weight == 0) {
            return message;
        }

        int previousWeight;
        int position = 0;
        while (weight != 0) {
            previousWeight = weight;
            message[position] = (byte) ((message[position] + 1) % 2);
            weight = getWeight(message);
            if (weight >= previousWeight) {
                message[position] = (byte) ((message[position] + 1) % 2);
                weight = previousWeight;
            }
            position++;
            if (position == message.length) {
                position--;
            }
        }
        return message;
    }

    private byte[] decode(byte[] fixedMessage, int length) {
        byte[] decoded = new byte[length];
        for (int i = 0; i < decoded.length; i++) {
            decoded[i] = fixedMessage[i];
        }
        return decoded;
    }

    private int getWeight(byte[] message) {
        byte[][] transposedMessage = MatrixCalculationUtils.transpose1DMatrix(message);
        byte[][] messageSyndrome = MatrixCalculationUtils.multiplyMatrices(hMatrix, transposedMessage);
        byte[] messageSyndrome1d = MatrixCalculationUtils.transpose2dTo1dMatrix(messageSyndrome);
        String syndrome = CodingUtils.get1DMatrixAsString(messageSyndrome1d);
        return syndromeWeightMap.get(syndrome);
    }

    private void buildCosetLeaderMap(byte[][] hMatrix) {
        cosetLeaderMap = new TreeMap<>();
        cosetWeightMap = new HashMap<>();
        syndromeWeightMap = new HashMap<>();
        List<byte[]> cosets = generateAllPossibleLengthNBinaries(hMatrix[0].length);

        Map<String, String> syndromeMap = new LinkedHashMap<>();

        for (byte[] array : cosets) {
            byte[][] transposedArray = MatrixCalculationUtils.transpose1DMatrix(array);
            byte[][] syndrome = MatrixCalculationUtils.multiplyMatrices(hMatrix, transposedArray);
            byte[] syndrome1d = MatrixCalculationUtils.transpose2dTo1dMatrix(syndrome);
            syndromeMap.put(CodingUtils.get1DMatrixAsString(array), CodingUtils.get1DMatrixAsString(syndrome1d));
        }

        Map<String, List<String>> grouped = syndromeMap.keySet().stream()
                .collect(Collectors.groupingBy(syndromeMap::get));

        for (List<String> cosetsList : grouped.values()) {
            // get coset with minimum weight
            int weight = Integer.MAX_VALUE;
            String cosetLeader = "";
            // find coset with the smallest weight
            for (String coset : cosetsList) {
                byte[] cosetArr = CodingUtils.stringToArray(coset);
                int currentWeight = CodingUtils.getMatrixWeight(cosetArr);
                if (currentWeight < weight) {
                    weight = currentWeight;
                    cosetLeader = coset;
                }
            }
            cosetWeightMap.put(cosetLeader, weight);
            cosetLeaderMap.put(cosetLeader, syndromeMap.get(cosetLeader));
            syndromeWeightMap.put(syndromeMap.get(cosetLeader), cosetWeightMap.get(cosetLeader));
        }
    }
}