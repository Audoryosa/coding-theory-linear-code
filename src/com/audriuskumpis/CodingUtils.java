package com.audriuskumpis;

import java.util.ArrayList;
import java.util.List;

public class CodingUtils {

    public static byte[][] buildGMatrix(byte[][] someMatrix) {
        byte[][] identity = MatrixCalculationUtils.generateSizeNIdentityMatrix(someMatrix.length);
        return MatrixCalculationUtils.appendMatrixTo(identity, someMatrix);
    }

    public static byte[][] buildHMatrix(byte[][] someMatrix) {
        byte[][] randomTransposed = MatrixCalculationUtils.transposeMatrix(someMatrix);
        byte[][] id = MatrixCalculationUtils.generateSizeNIdentityMatrix(randomTransposed.length);
        return MatrixCalculationUtils.appendMatrixTo(randomTransposed, id);
    }

    public static List<byte[]> generateAllPossibleLengthNBinaries(int n) {
        List<byte[]> result = new ArrayList<>();
        int totalRows = (int) Math.pow(2, n);
        for (int i = 0; i < totalRows; i++) {
            String variation = Integer.toBinaryString(i);
            variation = fillInMissingZeros(variation, n);
            byte[] variationArray = new byte[n];
            for (int j = 0; j < variation.length(); j++)
            {
                variationArray[j] = (byte) Character.getNumericValue(variation.charAt(j));
            }
            result.add(variationArray);
        }
        return result;
    }

    public static String fillInMissingZeros(String variation, int n) {
        int variationLength = variation.length();
        StringBuilder missingZeros = new StringBuilder();
        if (variationLength < n) {
            int zerosToAppendCount = n - variationLength;
            for (int i = 0; i < zerosToAppendCount; i++) {
                missingZeros.append("0");
            }
        }
        return missingZeros + variation;
    }

    public static String get1DMatrixAsString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (int num : array) {
            sb.append(num);
        }
        return sb.toString();
    }

    public static int getMatrixWeight(byte[] matrix) {
        int weight = 0;
        for (int num : matrix) {
            if (num == 1) {
                weight++;
            }
        }
        return weight;
    }

    public static byte[] stringToArray(String array) {
        byte[] result = new byte[array.length()];
        String[] splitted = array.split("");
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(splitted[i]);
        }
        return result;
    }

    public static String arrayToString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte number : array) {
            sb.append(number);
        }
        return sb.toString();
    }

    public static byte[] convertStringToBinary(String input) {
        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))
                            .replaceAll(" ", "0")
            );
        }
        byte[] binaryResult = new byte[result.toString().length()];
        for (int i = 0; i < binaryResult.length; i++) {
            binaryResult[i] = (byte) Character.getNumericValue(result.toString().charAt(i));
        }

        return binaryResult;

    }
}
