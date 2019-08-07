package util;

import java.lang.reflect.Array;

public class Matrix {
    /**
     * Transposes the given matrix, swapping its dimensions and elements.
     *
     * @param matrix    matrix to be transposed
     * @param <T>       any object type
     * @return          new matrix
     */
    public static <T> T[][] transpose(T[][] matrix) {
        T[][] newMatrix = (T[][]) Array.newInstance(matrix[0][0].getClass(), matrix[0].length, matrix.length);

        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                newMatrix[y][x] = matrix[x][y];
            }
        }

        return newMatrix;
    }
}
