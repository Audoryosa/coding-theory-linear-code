package com.audriuskumpis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int columns;
        int rows;

        Scanner scanner = new Scanner(System.in);

        System.out.print("Iveskite dimensija n: ");
        columns = scanner.nextInt();

        System.out.print("Iveskite kodo ilgi k: ");
        rows = scanner.nextInt();

        if (rows >= columns) {
            throw new IllegalArgumentException("k turi buti daugiau uz n");
        }

        System.out.println("Ar patys ivesite generuojancia matrica? (t/n)");
        String answerGeneratingMatrix = scanner.next();

        // sugeneruojame G matrica pagal vartotojo ivestus parametrus.
        byte[][] gMatrix = new byte[rows][columns];
        byte[][] anotherMatrix = new byte[rows][columns - rows];
        if (answerGeneratingMatrix.equalsIgnoreCase("n")) {
            anotherMatrix = MatrixCalculationUtils.generateRandomKxNMatrix(rows, columns - rows);
            gMatrix = CodingUtils.buildGMatrix(anotherMatrix);
            System.out.println("G matrica:");
            MatrixCalculationUtils.print2dMatrix(gMatrix);

        } else if (answerGeneratingMatrix.equalsIgnoreCase("t")) {
            System.out.println("Suveskite savo generuojancia matrica. ");
            for (int i = 0; i < rows; i++) {
                System.out.println(i + 1 + " eilute, " + columns + " skaiciai:");
                String enteredRow = scanner.next();
                for (int j = 0; j < columns; j++) {
                    gMatrix[i][j] = (byte) Character.getNumericValue(enteredRow.charAt(j));
                }
            }
            System.out.println("G matrica:");
            MatrixCalculationUtils.print2dMatrix(gMatrix);
            anotherMatrix = MatrixCalculationUtils.generateAnotherMatrixFromGMatrix(gMatrix);
        }


        System.out.print("Iveskite klaidos tikimybe: ");
        double faultProbability = scanner.nextDouble();

        System.out.println("Pasirinkimai: ");
        System.out.println("\t1. Dvejetainis kodas");
        System.out.println("\t2. Tekstas");
        System.out.println("\t3. Paveiksliukas");
        System.out.print("Iveskite pasirinkima: ");

        int option = scanner.nextInt();

        switch (option) {
            case 1:
                initBinaryCodeOption(rows, columns, faultProbability, gMatrix, anotherMatrix);
                break;

            case 2:
                initTextOption(rows, faultProbability, gMatrix, anotherMatrix);
                break;

            case 3:
                initImageOption(rows, faultProbability, gMatrix, anotherMatrix);
                break;

            default:
                break;
        }

    }

    private static boolean arrayToBMP(byte[] pixelData, int width, int height, File outputFile) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(pixelData, pixelData.length), null));
        return javax.imageio.ImageIO.write(img, "bmp", outputFile);
    }

    // Neveikia su paveiksleliu.
    private static void initImageOption(int rows, double faultProbability, byte[][] gMatrix, byte[][] anotherMatrix) {
        Channel channel = new Channel(faultProbability);
        Encoder encoder = new Encoder(gMatrix);
        Decoder decoder = new Decoder(CodingUtils.buildHMatrix(anotherMatrix));
        Scanner scanner = new Scanner(System.in);
        System.out.println("Iveskite pilna kelia iki paveikslelio .bpm formatu, pvz (C:/Images/pic.bpm):");
        String enteredText = scanner.nextLine();
        BufferedImage image = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try
        {
            image = ImageIO.read(new File(enteredText));
            ImageIO.write(image, "bmp", byteArrayOutputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        StringBuilder imageBinaryString = null;
        try {
            byte[] jpgByteArray = Files.readAllBytes(new File(enteredText).toPath());
            imageBinaryString = new StringBuilder();
            for (byte by : jpgByteArray) {
                imageBinaryString.append(Integer.toBinaryString(by & 0xFF));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // paveikslelio visi bitai
        String imageBinary = imageBinaryString.toString();

        String untouchableBytes = imageBinary.substring(0, 1000);
        imageBinary = imageBinary.substring(1000);
        byte[] imageBinArray = CodingUtils.stringToArray(imageBinary);

        List<byte[]> splitted = new ArrayList<>();

        for (int i = 0; i < imageBinArray.length; i += rows) {
            splitted.add(Arrays.copyOfRange(imageBinArray, i, Math.min(imageBinArray.length, i + rows)));
        }

        for (int i = 0; i < splitted.size(); i++) {
            if (splitted.get(i).length != rows) {
                byte[] arr = splitted.get(i);
                String arrString = CodingUtils.arrayToString(arr);
                String fixedArr = CodingUtils.fillInMissingZeros(arrString, rows);
                splitted.set(i, CodingUtils.stringToArray(fixedArr));
            }
        }

        StringBuilder distortedImage = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        for (byte[] codeChunk : splitted) {
            byte[] encoded = encoder.encode(codeChunk);
            channel.distortMessage(encoded);
            distortedImage.append(CodingUtils.arrayToString(encoded), 0, rows);
            byte[] decoded = decoder.decodeMessage(encoded, rows);
            stringBuilder.append(CodingUtils.get1DMatrixAsString(decoded));
        }

        String distortedResult = untouchableBytes + distortedImage.toString();
        String result = untouchableBytes + stringBuilder.toString();

        try {
            arrayToBMP(CodingUtils.stringToArray(result), image.getWidth(), image.getHeight(), new File("sukurtas.bmp"));
            arrayToBMP(CodingUtils.stringToArray(distortedResult), image.getWidth(), image.getHeight(), new File("iskreiptas.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int l = imageBinary.length();
        int g =      result.length();

        int errors = CodingUtils.compareTwoBinaryResults(imageBinary, result);
        System.out.println("Nesutampantys bitai po dekodavimo: " + errors + ", is viso bitu: " + imageBinary.length());
        System.out.println("Klaidu procentas: " + (errors * 100) / imageBinary.length() + "%");
    }

    // uzkoduojams ivestas tekstas, prasiunciamas pro kanala su klaidos tikimybe 0-1, ir dekoduojamas.
    private static void initTextOption(int rows,double faultProbability, byte[][] gMatrix, byte[][] anotherMatrix) {
        Channel channel = new Channel(faultProbability);
        Encoder encoder = new Encoder(gMatrix);
        Decoder decoder = new Decoder(CodingUtils.buildHMatrix(anotherMatrix));
        Scanner scanner = new Scanner(System.in);

        System.out.println("Iveskite teksta:");
        String enteredText = scanner.nextLine();
        byte[] binaryText = CodingUtils.convertStringToBinary(enteredText);

        List<byte[]> splitted = new ArrayList<>();

        // kadangi bitu yra daug, susiskaidome ji patogesniais gabalais.
        for (int i = 0; i < binaryText.length; i += rows) {
            splitted.add(Arrays.copyOfRange(binaryText, i, Math.min(binaryText.length, i + rows)));
        }

        for (int i = 0; i < splitted.size(); i++) {
            if (splitted.get(i).length != rows) {
                byte[] arr = splitted.get(i);
                String arrString = CodingUtils.arrayToString(arr);
                String fixedArr = CodingUtils.fillInMissingZeros(arrString, rows);
                splitted.set(i, CodingUtils.stringToArray(fixedArr));
            }
        }

        // su kiekvienu gabalu atliekame visa procedura
        StringBuilder distortedBuilder = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        for (byte[] codeChunk : splitted) {
            byte[] encoded = encoder.encode(codeChunk);
            channel.distortMessage(encoded);
            distortedBuilder.append(CodingUtils.arrayToString(encoded), 0, rows);
            byte[] decoded = decoder.decodeMessage(encoded, codeChunk.length);
            stringBuilder.append(CodingUtils.get1DMatrixAsString(decoded));
        }

        StringBuilder distortedResult = new StringBuilder();
        Arrays.stream(
                distortedBuilder.toString().split("(?<=\\G.{8})"))
                .forEach(s -> distortedResult.append((char) Integer.parseInt(s, 2)));

        System.out.println("Iskraipytas tekstas:");
        System.out.println(distortedResult);

        StringBuilder result = new StringBuilder();
        Arrays.stream(
                stringBuilder.toString().split("(?<=\\G.{8})"))
                .forEach(s -> result.append((char) Integer.parseInt(s, 2)));

        String output = result.toString();

        System.out.println("Dekoduotas tekstas: ");
        System.out.println(output);

        int errors = CodingUtils.compareTwoTextResults(enteredText, output);
        System.out.println("Nesutampantys bitai po dekodavimo: " + errors + ", is viso bitu: " + enteredText.length());
        System.out.println("Klaidu procentas: " + (errors * 100) / enteredText.length() + "%");
    }


    // uzkoduoja, prasiuncia pro kanal ir dekoduoja ivesta dvejetaini pranesima, pvz "1101"
    private static void initBinaryCodeOption(int rows, int columns, double faultProbability, byte[][] gMatrix, byte[][] anotherMatrix) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Iveskite " + rows + " ilgio koda: ");
        byte[] message = new byte[rows];
        String enteredMessage = scanner.next();
        for (int i = 0; i < rows; i++) {
            message[i] = (byte) Character.getNumericValue(enteredMessage.charAt(i));
        }

        System.out.println("Ivestas pranesimas: ");
        MatrixCalculationUtils.print1dMatrix(message);
        String originalMessage = CodingUtils.arrayToString(message);

        Encoder encoder = new Encoder(gMatrix);
        Channel channel = new Channel(faultProbability);

        byte[] encodedMessage = encoder.encode(message);
        System.out.println("Uzkoduotas pranesimas: ");
        MatrixCalculationUtils.print1dMatrix(encodedMessage);

        System.out.println("Iskraipytas pranesimas:");
        channel.distortMessage(encodedMessage);
        MatrixCalculationUtils.print1dMatrix(encodedMessage);

        byte[][] hMatrix = CodingUtils.buildHMatrix(anotherMatrix);

        System.out.println("Ar norite pakeisti iskraipyta pranesima? (t/n)");
        String changeAnswer =  scanner.next();
        if (changeAnswer.equalsIgnoreCase("t")) {
            System.out.println("Iveskite nauja iskraipyta pranesima " + columns + " ilgio: ");
            String newDistortedMessage = scanner.next();
            for (int i = 0; i < message.length; i++) {
                encodedMessage[i] = (byte) Character.getNumericValue(newDistortedMessage.charAt(i));
            }
            System.out.println("Naujas iskraipytas pranesimas: ");
            MatrixCalculationUtils.print1dMatrix(encodedMessage);
        }

        System.out.println("H Matrica:");
        MatrixCalculationUtils.print2dMatrix(hMatrix);

        Decoder decoder = new Decoder(hMatrix);
        byte[] decoded = decoder.decodeMessage(encodedMessage, rows);
        System.out.println("Dekoduotas pranesimas: ");
        MatrixCalculationUtils.print1dMatrix(decoded);

        int errors = CodingUtils.compareTwoTextResults(originalMessage, CodingUtils.arrayToString(decoded));
        System.out.println("Nesutampantys bitai po dekodavimo: " + errors + ", is viso bitu: " + originalMessage.length());
        System.out.println("Klaidu procentas: " + (errors * 100) / originalMessage.length() + "%");
    }

}
