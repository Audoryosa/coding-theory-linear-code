package com.audriuskumpis;

import java.util.ArrayList;
import java.util.List;

/**
 * Pagalbine klase
 */
public class CodingUtils {

    /**
     * Sukuria generuojancia matrica is duotos kitos matricos. Rezultatas - matrica, pavidalu (I | A)
     * @param someMatrix A matrica
     * @return grazina generuojancia matrica
     */
    public static byte[][] buildGMatrix(byte[][] someMatrix) {
        byte[][] identity = MatrixCalculationUtils.generateSizeNIdentityMatrix(someMatrix.length);
        return MatrixCalculationUtils.appendMatrixTo(identity, someMatrix);
    }

    /**
     * Sukuria kontroline matrica, skirta dekodavimui, kurios pavidalas yra (At | I)
     * @param someMatrix
     * @return grazina kontroline H matrica
     */
    public static byte[][] buildHMatrix(byte[][] someMatrix) {
        byte[][] randomTransposed = MatrixCalculationUtils.transposeMatrix(someMatrix);
        byte[][] id = MatrixCalculationUtils.generateSizeNIdentityMatrix(randomTransposed.length);
        return MatrixCalculationUtils.appendMatrixTo(randomTransposed, id);
    }

    /**
     * Sukuria visas imanomas n ilgio bitu sekas.
     * @param n norimo ilgio sekos iligs.
     * @return grazina visu imanomu n ilgio baitu sarasa
     */
    public static List<byte[]> generateAllPossibleLengthNBinaries(int n) {
        List<byte[]> result = new ArrayList<>();
        int totalRows = (int) Math.pow(2, n);
        for (int i = 0; i < totalRows; i++) {
            String variation = Integer.toBinaryString(i);
            variation = fillInMissingZeros(variation, n, true);
            byte[] variationArray = new byte[n];
            for (int j = 0; j < variation.length(); j++)
            {
                variationArray[j] = (byte) Character.getNumericValue(variation.charAt(j));
            }
            result.add(variationArray);
        }
        return result;
    }

    /**
     * Uzpildo trukstamas bito reiksmes nuliais 11 --> 000011
     * @param variation seka, kuriai truksta nuliu
     * @param n koks turi buti sekos ilgis
     * @return grazina sutvarkyta seka
     */
    public static String fillInMissingZeros(String variation, int n, boolean appendToStart) {
        int variationLength = variation.length();
        StringBuilder missingZeros = new StringBuilder();
        if (variationLength < n) {
            int zerosToAppendCount = n - variationLength;
            for (int i = 0; i < zerosToAppendCount; i++) {
                missingZeros.append("0");
            }
        }
        if (appendToStart) {
            return missingZeros + variation;
        } else {
            return variation + missingZeros;
        }
    }

    /**
     * 1d matrica --> String pavidalu
     * @param array
     * @return
     */
    public static String get1DMatrixAsString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (int num : array) {
            sb.append(num);
        }
        return sb.toString();
    }

    /**
     * Randa, kiek duotame vektoriuje yra vienetu.
     * @param matrix
     * @return
     */
    public static int getMatrixWeight(byte[] matrix) {
        int weight = 0;
        for (int num : matrix) {
            if (num == 1) {
                weight++;
            }
        }
        return weight;
    }

    /**
     * String --> vektorius
     * @param array
     * @return
     */
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

    /**
     * Pavercia ivesta teksta i dvejetaini pavidala
     * @param input masyvas String pavidalu
     * @return grazina masyva is String
     */
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

    /**
     * Palygina, kiek yra neatitikimu tarp dvieju seku
     * @param arrayOne pirmas masyvas
     * @param arrayTwo antras masyvas
     * @return grazina neatitikimu skaiciu
     */
    public static int compareTwoBinaryResults(String arrayOne, String arrayTwo) {
        int errors = 0;
        for (int i = 0; i < arrayOne.length(); i++) {
            int original = Character.getNumericValue(arrayOne.charAt(i));
            int decoded = Character.getNumericValue(arrayTwo.charAt(i));
            if (original != decoded) {
                errors++;
            }
        }
        return errors;
    }

    /**
     * Palygina rezultata tarp dvieju String'u
     * @param stringOne pirma seka
     * @param stringTwo antra seka
     * @return grazina neatitikimu tarp dvieju seku skaiciu
     */
    public static int compareTwoTextResults(String stringOne, String stringTwo) {
        int errors = 0;
        for (int i = 0; i < stringOne.length(); i++) {
            if (stringOne.charAt(i) != stringTwo.charAt(i)) {
                errors++;
            }
        }
        return errors;
    }
}
