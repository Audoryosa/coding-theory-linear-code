package com.audriuskumpis;

/**
 * Pagalbine klase, skirta veiksmams su matricomis.
 */
public class MatrixCalculationUtils {

    /**
     * Sugeneruoja dvejetaine matrica, su pseudo atsitiktinem reiksmem
     * @param k
     * @param n
     * @return
     */
    public static byte[][] generateRandomKxNMatrix(int k, int n) {
        byte[][] randomMatrix = new byte[k][n];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < n; j++) {
                if (Math.random() >= 0.5) {
                    randomMatrix[i][j] = 1;
                }
            }
        }

        return randomMatrix;
    }

    /**
     * Prilipdo viena matrica prie kitos sono. T.y. jei turim matrica A, ir matrica B, sio metodo rezultatas yra matrica (A | B)
     * @param baseMatrix
     * @param matrixToAppend
     * @return
     */
    public static byte[][] appendMatrixTo(byte[][] baseMatrix, byte[][] matrixToAppend) {
        int baseMatrixRowCount = baseMatrix.length;
        int baseMatrixColumnCount = baseMatrix[0].length;

        int matrixToAppendRowCount = matrixToAppend.length;
        int matrixToAppendColumnCount = matrixToAppend[0].length;

        if (baseMatrixRowCount != matrixToAppendRowCount) {
            throw new IllegalArgumentException("Matrix to append must have same row count as base matrix.");
        }

        int newMatrixColumnCount = baseMatrixColumnCount + matrixToAppendColumnCount;
        byte[][] appendedMatrix = new byte[baseMatrixRowCount][newMatrixColumnCount];
        int matrixToAppendCounter = 0;
        for (int i = 0; i < baseMatrixRowCount; i++) {
            for (int j = 0; j < newMatrixColumnCount; j++) {
                if (j >= baseMatrixColumnCount) {
                    appendedMatrix[i][j] = matrixToAppend[i][matrixToAppendCounter];
                    matrixToAppendCounter++;
                } else {
                    appendedMatrix[i][j] = baseMatrix[i][j];
                }
            }
            matrixToAppendCounter = 0;
        }
        return appendedMatrix;
    }

    /**
     * Transponuoja matrica A. A -> At
     * @param matrix
     * @return
     */
    public static byte[][] transposeMatrix(byte[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;

        byte[][] transposedMatrix = new byte[columns][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                transposedMatrix[j][i] = matrix[i][j];
            }
        }

        return transposedMatrix;
    }

    /**
     * Transponuoja 1d matrica.
     * @param matrix
     * @return
     */
    public static byte[][] transpose1DMatrix(byte[] matrix) {
        int rows = matrix.length;

        byte[][] transposedMatrix = new byte[rows][1];

        for (int i = 0; i < rows; i++) {
            transposedMatrix[i][0] = matrix[i];
        }

        return transposedMatrix;
    }

    /**
     * Transponuoja 2d matrica i 1d matrica.
     * @param matrix
     * @return
     */
    public static byte[] transpose2dTo1dMatrix(byte[][] matrix) {
        int rows = matrix.length;
        byte[] transposed = new byte[rows];
        for (int i = 0; i < rows; i++) {
            transposed[i] = matrix[i][0];
        }
        return transposed;
    }

    /**
     * SUgeneruoja n x n dydzio vienetine matrica.
     * @param size
     * @return
     */
    public static byte[][] generateSizeNIdentityMatrix(int size) {
        byte[][] identityMatrix = new byte[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (j == i) {
                    identityMatrix[i][j] = 1;
                }
            }
        }
        return identityMatrix;
    }

    /**
     * Atspausdina 2d matricos turini
     * @param matrix
     */
    public static void print2dMatrix(byte[][] matrix) {
        for (byte[] row : matrix) {
            for (int column : row) {
                System.out.print(column + " ");
            }
            System.out.println();
        }
    }

    /**
     * Sudaugina 2 matricas.
     * @param matrixA
     * @param matrixB
     * @return
     */
    public static byte[][] multiplyMatrices(byte[][] matrixA, byte[][] matrixB) {
        int aRow = matrixA.length;
        int aColumn = matrixA[0].length;

        int bRow = matrixB.length;
        int bColumn = matrixB[0].length;

        if (aColumn != bRow) {
            throw new IllegalArgumentException("Matrices are not compatible");
        }

        byte[][] result = new byte[aRow][bColumn];
        for (int i = 0; i < aRow; i++) {
            for (int j = 0; j < bColumn; j++) {
                for (int k = 0; k < bRow; k++)
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                    result[i][j] = (byte) (result[i][j] % 2);
            }
        }

        return result;
    }

    /**
     * Sudaugina vektoriu ir matrica.
     * @param vector
     * @param gMatrix
     * @return
     */
    public static byte[] multiplyVectorByMatrixG(byte[] vector, byte[][] gMatrix) {
        if (vector.length != gMatrix.length) {
            throw new IllegalArgumentException("Matrices are not compatible");
        }
        byte[] result = new byte[gMatrix[0].length];
        for (int i=0; i < gMatrix.length; i++)
        {
            for (int j=0; j < gMatrix[0].length; j++){
                result[j] += gMatrix[i][j] * vector[i];
            }
        }
        return result;
    }

    /**
     * Atvaizduoja 1d matricos turini.
     * @param array
     */
    public static void print1dMatrix(byte[] array) {
        for (int i : array) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    /**
     * Istraukiama "bet kokia" matrica is generuojancios matricos.
     * Generuojanti matrica yra pavidalo (I | A). Metodas grazina matrica A.
     * @param gMatrix
     * @return
     */
    public static byte[][] generateAnotherMatrixFromGMatrix(byte[][] gMatrix) {
        int rows = gMatrix.length;
        int columns = gMatrix[0].length - gMatrix.length;
        byte[][] anotherMatrix = new byte[rows][columns];

        for (int i = 0; i < rows; i++) {
            int anotherMatrixColumnIndex = 0;
            for (int j = rows; j < gMatrix[0].length; j++) {
                anotherMatrix[i][anotherMatrixColumnIndex] = gMatrix[i][j];
                anotherMatrixColumnIndex++;
            }
        }
        return anotherMatrix;
    }
}
