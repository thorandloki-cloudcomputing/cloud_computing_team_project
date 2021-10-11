import java.lang.Math;

public class Main {
    //Returns an array of 1's and 0's that is the logistic map.
    public static int[] logisticMap(){
      int[] anArray = {1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1,
        1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0,
        0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0,
        0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0,
        0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1,
        1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
        1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1,
        1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1,
        0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1,
        1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1,
        0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0,
        1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0,
        0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1,
        1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1,
        1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1,
        1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0,
        0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1,
        0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0,
        0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0,
        1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1,
        0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1,
        1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1,
        0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0,
        1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1,
        1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0,
        0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1,
        1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1,
        1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0,
        0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1,
        1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0,
        1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1,
        1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0,
        0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0,
        0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1,
        0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0,
        0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1,
        0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0,
        0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1,
        1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0,
        0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0,
        0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1,
        0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1,
        0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1,
        1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1,
        1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1,
        1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0,
        0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0,
        0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1,
        0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1,
        1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0,
        1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1,
        0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1,
        1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0,
        0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1,
        0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0};
      return anArray;
    }

    //Returns an array of the zig zag coordinates for 25x25 matrix.
    public static int[] zigzag25(){
        int[] size25array = {24, 24, 24, 23, 23, 24, 23, 23, 22, 24, 22, 23, 21, 24, 21, 23, 20, 24, 20, 23, 19, 24, 19, 23, 18, 24, 18, 23, 17, 24, 17, 23, 16, 24, 16, 23, 15, 24, 15, 23, 14, 24, 14, 23, 13, 24, 13, 23, 12, 24, 12, 23, 11, 24, 11, 23, 10, 24, 10, 23, 9, 24, 9, 23, 8, 24, 8, 23, 8, 22, 8, 21, 9, 22, 9, 21, 10, 22, 10, 21, 11, 22, 11, 21, 12, 22, 12, 21, 13, 22, 13, 21, 14, 22, 14, 21, 15, 22, 15, 21, 16, 22, 16, 21, 17, 22, 17, 21, 18, 22, 18, 21, 19, 22, 19, 21, 20, 22, 20, 21, 21, 22, 21, 21, 22, 22, 22, 21, 23, 22, 23, 21, 24, 22, 24, 21, 24, 20, 24, 19, 23, 20, 23, 19, 22, 20, 22, 19, 21, 20, 21, 19, 15, 20, 15, 19, 14, 20, 14, 19, 13, 20, 13, 19, 12, 20, 12, 19, 11, 20, 11, 19, 10, 20, 10, 19, 9, 20, 9, 19, 8, 20, 8, 19, 8, 18, 8, 17, 9, 18, 9, 17, 10, 18, 10, 17, 11, 18, 11, 17, 12, 18, 12, 17, 13, 18, 13, 17, 14, 18, 14, 17, 15, 18, 15, 17, 21, 18, 21, 17, 22, 18, 22, 17, 23, 18, 23, 17, 24, 18, 24, 17, 24, 16, 24, 15, 23, 16, 23, 15, 22, 16, 22, 15, 21, 16, 21, 15, 15, 16, 15, 15, 14, 16, 14, 15, 13, 16, 13, 15, 12, 16, 12, 15, 11, 16, 11, 15, 10, 16, 10, 15, 9, 16, 9, 15, 8, 16, 8, 15, 7, 16, 7, 15, 5, 16, 5, 15, 4, 16, 4, 15, 3, 16, 3, 15, 2, 16, 2, 15, 1, 16, 1, 15, 0, 16, 0, 15, 0, 14, 0, 13, 1, 14, 1, 13, 2, 14, 2, 13, 3, 14, 3, 13, 4, 14, 4, 13, 5, 14, 5, 13, 7, 14, 7, 13, 8, 14, 8, 13, 9, 14, 9, 13, 10, 14, 10, 13, 11, 14, 11, 13, 12, 14, 12, 13, 13, 14, 13, 13, 14, 14, 14, 13, 15, 14, 15, 13, 16, 14, 16, 13, 17, 14, 17, 13, 18, 14, 18, 13, 19, 14, 19, 13, 20, 14, 20, 13, 21, 14, 21, 13, 22, 14, 22, 13, 23, 14, 23, 13, 24, 14, 24, 13, 24, 12, 24, 11, 23, 12, 23, 11, 22, 12, 22, 11, 21, 12, 21, 11, 20, 12, 20, 11, 19, 12, 19, 11, 18, 12, 18, 11, 17, 12, 17, 11, 16, 12, 16, 11, 15, 12, 15, 11, 14, 12, 14, 11, 13, 12, 13, 11, 12, 12, 12, 11, 11, 12, 11, 11, 10, 12, 10, 11, 9, 12, 9, 11, 8, 12, 8, 11, 7, 12, 7, 11, 5, 12, 5, 11, 4, 12, 4, 11, 3, 12, 3, 11, 2, 12, 2, 11, 1, 12, 1, 11, 0, 12, 0, 11, 0, 10, 0, 9, 1, 10, 1, 9, 2, 10, 2, 9, 3, 10, 3, 9, 4, 10, 4, 9, 5, 10, 5, 9, 7, 10, 7, 9, 8, 10, 8, 9, 9, 10, 9, 9, 10, 10, 10, 9, 11, 10, 11, 9, 12, 10, 12, 9, 13, 10, 13, 9, 14, 10, 14, 9, 15, 10, 15, 9, 16, 10, 16, 9, 17, 10, 17, 9, 18, 10, 18, 9, 19, 10, 19, 9, 20, 10, 20, 9, 21, 10, 21, 9, 22, 10, 22, 9, 23, 10, 23, 9, 24, 10, 24, 9, 16, 8, 16, 7, 15, 8, 15, 7, 14, 8, 14, 7, 13, 8, 13, 7, 12, 8, 12, 7, 11, 8, 11, 7, 10, 8, 10, 7, 9, 8, 9, 7, 8, 8, 8, 7, 8, 5, 8, 4, 9, 5, 9, 4, 10, 5, 10, 4, 11, 5, 11, 4, 12, 5, 12, 4, 13, 5, 13, 4, 14, 5, 14, 4, 15, 5, 15, 4, 16, 5, 16, 4, 16, 3, 16, 2, 15, 3, 15, 2, 14, 3, 14, 2, 13, 3, 13, 2, 12, 3, 12, 2, 11, 3, 11, 2, 10, 3, 10, 2, 9, 3, 9, 2, 8, 3, 8, 2, 8, 1, 8, 0, 9, 1, 9, 0, 10, 1, 10, 0, 11, 1, 11, 0, 12, 1, 12, 0, 13, 1, 13, 0, 14, 1, 14, 0, 15, 1, 15, 0, 16, 1, 16, 0};
        return size25array;
    }
    //Returns an array of the zig zag coordinates for 21x21 matrix.
    public static int[] zigzag21(){
        int[] size21array = {20, 20, 20, 19, 19, 20, 19, 19, 18, 20, 18, 19, 17, 20, 17, 19, 16, 20, 16, 19, 15, 20, 15, 19, 14, 20, 14, 19, 13, 20, 13, 19, 12, 20, 12, 19, 11, 20, 11, 19, 10, 20, 10, 19, 9, 20, 9, 19, 8, 20, 8, 19, 8, 18, 8, 17, 9, 18, 9, 17, 10, 18, 10, 17, 11, 18, 11, 17, 12, 18, 12, 17, 13, 18, 13, 17, 14, 18, 14, 17, 15, 18, 15, 17, 16, 18, 16, 17, 17, 18, 17, 17, 18, 18, 18, 17, 19, 18, 19, 17, 20, 18, 20, 17, 20, 16, 20, 15, 19, 16, 19, 15, 18, 16, 18, 15, 17, 16, 17, 15, 16, 16, 16, 15, 15, 16, 15, 15, 14, 16, 14, 15, 13, 16, 13, 15, 12, 16, 12, 15, 11, 16, 11, 15, 10, 16, 10, 15, 9, 16, 9, 15, 8, 16, 8, 15, 8, 14, 8, 13, 9, 14, 9, 13, 10, 14, 10, 13, 11, 14, 11, 13, 12, 14, 12, 13, 13, 14, 13, 13, 14, 14, 14, 13, 15, 14, 15, 13, 16, 14, 16, 13, 17, 14, 17, 13, 18, 14, 18, 13, 19, 14, 19, 13, 20, 14, 20, 13, 20, 12, 20, 11, 19, 12, 19, 11, 18, 12, 18, 11, 17, 12, 17, 11, 16, 12, 16, 11, 15, 12, 15, 11, 14, 12, 14, 11, 13, 12, 13, 11, 12, 12, 12, 11, 11, 12, 11, 11, 10, 12, 10, 11, 9, 12, 9, 11, 8, 12, 8, 11, 7, 12, 7, 11, 5, 12, 5, 11, 4, 12, 4, 11, 3, 12, 3, 11, 2, 12, 2, 11, 1, 12, 1, 11, 0, 12, 0, 11, 0, 10, 0, 9, 1, 10, 1, 9, 2, 10, 2, 9, 3, 10, 3, 9, 4, 10, 4, 9, 5, 10, 5, 9, 7, 10, 7, 9, 8, 10, 8, 9, 9, 10, 9, 9, 10, 10, 10, 9, 11, 10, 11, 9, 12, 10, 12, 9, 13, 10, 13, 9, 14, 10, 14, 9, 15, 10, 15, 9, 16, 10, 16, 9, 17, 10, 17, 9, 18, 10, 18, 9, 19, 10, 19, 9, 20, 10, 20, 9, 12, 8, 12, 7, 11, 8, 11, 7, 10, 8, 10, 7, 9, 8, 9, 7, 8, 8, 8, 7, 8, 5, 8, 4, 9, 5, 9, 4, 10, 5, 10, 4, 11, 5, 11, 4, 12, 5, 12, 4, 12, 3, 12, 2, 11, 3, 11, 2, 10, 3, 10, 2, 9, 3, 9, 2, 8, 3, 8, 2, 8, 1, 8, 0, 9, 1, 9, 0, 10, 1, 10, 0, 11, 1, 11, 0, 12, 1, 12, 0};
        return size21array;
}

    public static int[] stringToBitList(String hexString){
      String [] brokenHex = hexString.split("0x",0);
      // Debug
      long hexInt = 0;
      String temp;
      String [] tempList;
      int length = 32*(brokenHex.length-1); //Size of needed array...
      int[] bitList = new int[length];
      //Skip empty "" string
      int ctr = 0;
      for (int i = 1; i < brokenHex.length; i++){
          //System.out.println(brokenHex[i]);
          hexInt = Long.parseLong(brokenHex[i], 16);
          //System.out.println(hexInt);
          temp = String.format("%32s", Long.toBinaryString(hexInt)).replace(' ', '0');
          String [] tempArr = temp.split("",0);

          for (int j = 0; j < 32; j++){
            bitList[ctr] = Integer.parseInt(tempArr[j]);
            ctr++;
          }
          //Debug
          //System.out.println(bitList[ctr-1]);
      }
      return bitList;
    }

    public static int[][] reshape(int[] myList , int dimension){
      int [][] outputMatrix = new int[dimension][dimension];
      int offset = 0;
      for (int i = 0; i < dimension; i++){
        for (int j = 0; j < dimension; j++){
          outputMatrix[i][j] = myList[j+offset];
        }
        offset += dimension;
      }
      return outputMatrix;

    }

    public static boolean checkCoords(int [][] matrix, int i, int j){
      boolean row1 = (matrix[i][j] == 1)&&(matrix[i][j+1] == 1)&&(matrix[i][j+2] == 1)&&(matrix[i][j+3] == 1)
        &&(matrix[i][j+4] == 1)&&(matrix[i][j+5] == 1)&&(matrix[i][j+6] == 1);
      boolean row2 = (matrix[i+1][j] == 1)&&(matrix[i+1][j+1] == 0)&&(matrix[i+1][j+2] == 0)&&(matrix[i+1][j+3] == 0)
        &&(matrix[i+1][j+4] == 0)&&(matrix[i+1][j+5] == 0)&&(matrix[i+1][j+6] == 1);
      boolean row3 = (matrix[i+2][j] == 1)&&(matrix[i+2][j+1] == 0)&&(matrix[i+2][j+2] == 1)&&(matrix[i+2][j+3] == 1)
        &&(matrix[i+2][j+4] == 1)&&(matrix[i+2][j+5] == 0)&&(matrix[i+2][j+6] == 1);
      boolean row4 = (matrix[i+3][j] == 1)&&(matrix[i+3][j+1] == 0)&&(matrix[i+3][j+2] == 1)&&(matrix[i+3][j+3] == 1)
        &&(matrix[i+3][j+4] == 1)&&(matrix[i+3][j+5] == 0)&&(matrix[i+3][j+6] == 1);
      boolean row5 = (matrix[i+4][j] == 1)&&(matrix[i+4][j+1] == 0)&&(matrix[i+4][j+2] == 1)&&(matrix[i+4][j+3] == 1)
        &&(matrix[i+4][j+4] == 1)&&(matrix[i+4][j+5] == 0)&&(matrix[i+4][j+6] == 1);
      boolean row6 = (matrix[i+5][j] == 1)&&(matrix[i+5][j+1] == 0)&&(matrix[i+5][j+2] == 0)&&(matrix[i+5][j+3] == 0)
        &&(matrix[i+5][j+4] == 0)&&(matrix[i+5][j+5] == 0)&&(matrix[i+5][j+6] == 1);
      boolean row7 = (matrix[i+6][j] == 1)&&(matrix[i+6][j+1] == 1)&&(matrix[i+6][j+2] == 1)&&(matrix[i+6][j+3] == 1)
        &&(matrix[i+6][j+4] == 1)&&(matrix[i+6][j+5] == 1)&&(matrix[i+6][j+6] == 1);
      boolean match = row1&&row2&&row3&&row4&&row5&&row6&&row7;
      if (match){
        return true;
      }
      return false;

    }
    public static int[] findTargets(int[] targetList, int[][] matrix){
      int oldI = targetList[0];
      int oldJ = targetList[1];
      int i = 0;
      int j = 0;
      int [] outputs = new int[6];
      //Store first pair
      outputs[0] = targetList[0];
      outputs[1] = targetList[1];
      int ctr = 2;
      //Init to false
      boolean match = false;
      //Check for all possibilities...
      //21x21 versions
      //right
      i = oldI;
      j = oldJ+14; //Not out bounds
      if (j<26){
        match = checkCoords(matrix,i,j);
        if (match){
          outputs[ctr] = i;
          outputs[ctr+1] = j;
          ctr +=2;
          match = false; // Re init
        }
      }
      //down to the left
      i = oldI+14;
      j = oldJ-14; //Not out bounds
      if ((i<26)&&(j>-1)){
        match = checkCoords(matrix,i,j);
        if (match){
          outputs[ctr] = i;
          outputs[ctr+1] = j;
          ctr +=2;
          match = false; // Re init
        }
      }
      //down
      i = oldI+14;
      j = oldJ; //Not out bounds
      if (i<26){
        match = checkCoords(matrix,i,j);
        if (match){
          outputs[ctr] = i;
          outputs[ctr+1] = j;
          ctr +=2;
          match = false; // Re init
        }
      }
      //down to the right
      i = oldI+14;
      j = oldJ+14; //Not out bounds
      if ((i<26)&&(j<26)){
        match = checkCoords(matrix,i,j);
        if (match){
          outputs[ctr] = i;
          outputs[ctr+1] = j;
          ctr +=2;
          match = false; // Re init
        }
      }
      //Early Return Condition
      if(ctr == 6){
        return outputs;
      }
      //25x25 versions
      // //left
      // i = oldI;
      // j = oldJ-18; //Not out bounds
      // if (j>-1){
      //   match = checkCoords(matrix,i,j);
      //   if (match){
      //     outputs[ctr] = i;
      //     outputs[ctr+1] = j;
      //     ctr +=2;
      //     match = false; // Re init
      //   }
      // }
      //right
      i = oldI;
      j = oldJ+18; //Not out bounds
      if (j<26){
        match = checkCoords(matrix,i,j);
        if (match){
          outputs[ctr] = i;
          outputs[ctr+1] = j;
          ctr +=2;
          match = false; // Re init
        }
      }
      //down to the left
      i = oldI+18;
      j = oldJ-18; //Not out bounds
      if ((i<26)&&(j>-1)){
        match = checkCoords(matrix,i,j);
        if (match){
          outputs[ctr] = i;
          outputs[ctr+1] = j;
          ctr +=2;
          match = false; // Re init
        }
      }
      //down
      i = oldI+18;
      j = oldJ; //Not out bounds
      if (i<26){
        match = checkCoords(matrix,i,j);
        if (match){
          outputs[ctr] = i;
          outputs[ctr+1] = j;
          ctr +=2;
          match = false; // Re init
        }
      }
      //down to the right
      i = oldI+18;
      j = oldJ+18; //Not out bounds
      if ((i<26)&&(j<26)){
        match = checkCoords(matrix,i,j);
        if (match){
          outputs[ctr] = i;
          outputs[ctr+1] = j;
          ctr +=2;
          match = false; // Re init
        }
      }
      return outputs;
    }

    public static int[] detectPosition(int[][] matrix){
      // Matrix dimension is always 32x32
      // Pattern is 7x7 -> 26, 26 are limits
      //int[] output = new int[6];
      int ctr = 0;
      boolean match = false;
      int i = 0;
      int j = 0;
      for (i=0;  i<26; i++ ){
        for (j=0; j<26; j++ ){
          boolean row1 = (matrix[i][j] == 1)&&(matrix[i][j+1] == 1)&&(matrix[i][j+2] == 1)&&(matrix[i][j+3] == 1)
            &&(matrix[i][j+4] == 1)&&(matrix[i][j+5] == 1)&&(matrix[i][j+6] == 1);
          boolean row2 = (matrix[i+1][j] == 1)&&(matrix[i+1][j+1] == 0)&&(matrix[i+1][j+2] == 0)&&(matrix[i+1][j+3] == 0)
            &&(matrix[i+1][j+4] == 0)&&(matrix[i+1][j+5] == 0)&&(matrix[i+1][j+6] == 1);
          boolean row3 = (matrix[i+2][j] == 1)&&(matrix[i+2][j+1] == 0)&&(matrix[i+2][j+2] == 1)&&(matrix[i+2][j+3] == 1)
            &&(matrix[i+2][j+4] == 1)&&(matrix[i+2][j+5] == 0)&&(matrix[i+2][j+6] == 1);
          boolean row4 = (matrix[i+3][j] == 1)&&(matrix[i+3][j+1] == 0)&&(matrix[i+3][j+2] == 1)&&(matrix[i+3][j+3] == 1)
            &&(matrix[i+3][j+4] == 1)&&(matrix[i+3][j+5] == 0)&&(matrix[i+3][j+6] == 1);
          boolean row5 = (matrix[i+4][j] == 1)&&(matrix[i+4][j+1] == 0)&&(matrix[i+4][j+2] == 1)&&(matrix[i+4][j+3] == 1)
            &&(matrix[i+4][j+4] == 1)&&(matrix[i+4][j+5] == 0)&&(matrix[i+4][j+6] == 1);
          boolean row6 = (matrix[i+5][j] == 1)&&(matrix[i+5][j+1] == 0)&&(matrix[i+5][j+2] == 0)&&(matrix[i+5][j+3] == 0)
            &&(matrix[i+5][j+4] == 0)&&(matrix[i+5][j+5] == 0)&&(matrix[i+5][j+6] == 1);
          boolean row7 = (matrix[i+6][j] == 1)&&(matrix[i+6][j+1] == 1)&&(matrix[i+6][j+2] == 1)&&(matrix[i+6][j+3] == 1)
            &&(matrix[i+6][j+4] == 1)&&(matrix[i+6][j+5] == 1)&&(matrix[i+6][j+6] == 1);
          match = row1&&row2&&row3&&row4&&row5&&row6&&row7;
          if (match){
            //System.out.println("MATCH"); //Debug
            // output[ctr] = i;
            // output[ctr+1] = j;
            // ctr+=2;
            break;
          }
        }
        if (match){
          break;
        }
      }
      int [] input = {i,j};
      int [] output = findTargets(input,matrix);
      return output;
    }

    public static int[][] sliceMatrix(int[][] matrix, int[] corner, int size){
      int [][] output = new int[size][size];
      for (int i = 0; i < size; i++){
        for (int j = 0; j < size; j++){
          output[i][j] = matrix[i+corner[0]][j+corner[1]];
        }
      }
      return output;
    }

    //ONLY WORKS FOR SQUARE MATRIX!!!
    public static int[][] rotate90(int[][] matrix){
      int [][] transposed = new int[matrix.length][matrix.length];
      int [][] output = new int[matrix.length][matrix.length];
      //Transpose
      for (int i = 0; i < matrix.length; i++){
        for (int j = 0; j < matrix.length; j++){
          output[i][j] = matrix[j][31-i];
        }
      }
      //Reverse columns
      // for (int i = 0; i < matrix.length; i++){
      //   for (int j = 0; j < matrix.length; j++){
      //     output[i][j] = transposed[matrix.length-i-1][j];
      //   }
      // }
      return output;
    }

    public static int[][] fixRotation(int[][] matrix, int[] targets){
      //Grab starting corners
      int i1 = targets[0];
      int j1 = targets[1];
      int i2 = targets[2];
      int j2 = targets[3];
      int i3 = targets[4];
      int j3 = targets[5];
      System.out.println("Initial Coords are");
      System.out.println(i1);
      System.out.println(j1);
      System.out.println(i2);
      System.out.println(j2);
      System.out.println(i3);
      System.out.println(j3);

      // case 1: 0 degrees, no rotation
      if ((j1 == j3)&&(i1 == i2)) {
        System.out.println("Case 0");
        int size = j2-j1+7;
        int[] corner = {i1,j1};
        System.out.println("Corner Coords are ");
        System.out.println(corner[0]);
        System.out.println(corner[1]);
        int[][] slicedMatrix = sliceMatrix(matrix,corner,size);
        return slicedMatrix;
      }
      // case 2: 270 degrees ccw turn
      if ((j1 == j2)){
        //System.out.println("270 Degree Case");
        // rotate ccw three times...
        System.out.println("Case 1");
        int[][] temp = matrix;
        for (int i = 0; i < 3; i++){
          temp = rotate90(temp);
        }
        // pass through detection
        int [] newTargs = detectPosition(temp);
        int [] corner = {newTargs[0],newTargs[1]};
        System.out.println("Rotated Coords are ");
        System.out.println(corner[0]);
        System.out.println(corner[1]);
        System.out.println(newTargs[2]);
        System.out.println(newTargs[3]);
        System.out.println(newTargs[4]);
        System.out.println(newTargs[5]);
        int size = newTargs[4]-newTargs[2]+7;
        int [][] slicedMatrix = sliceMatrix(temp,corner,size);
        return slicedMatrix;
      }
      // case 3: 90 degree ccw turn
      if (j2 == j3){
        System.out.println("Case 2");
        int[][] temp = rotate90(matrix);
        int [] newTargs = detectPosition(temp);
        int [] corner = {newTargs[0],newTargs[1]};
        System.out.println("Corner Coords are ");
        System.out.println(corner[0]);
        System.out.println(corner[1]);
        int size = newTargs[4]-newTargs[2]+7;
        int [][] slicedMatrix = sliceMatrix(temp,corner,size);
        return slicedMatrix;
      }
      // 180 degree turn
      if ((j1 == j3)&&(i2==i3)){
        System.out.println("Case 3");
        int[][] temp = matrix;
        for (int i = 0; i < 2; i++){
          temp = rotate90(temp);
        }
        // pass through detection
        int [] newTargs = detectPosition(temp);
        int [] corner = {newTargs[0],newTargs[1]};
        System.out.println("Corner Coords are ");
        System.out.println(corner[0]);
        System.out.println(corner[1]);
        int size = newTargs[4]-newTargs[2]+7;
        int [][] slicedMatrix = sliceMatrix(temp,corner,size);
        return slicedMatrix;
      }

      int [][] temp = {{1,2,3},{4,5,6}};
      return temp;
    }

    public static String bitUnravelDecode(int[] indices,int [][] matrix){
      String [] bitList = new String[indices.length/2];
      int temp = 0;
      for (int i = 0; i < bitList.length; i++){
        temp = matrix[indices[2*i]][indices[2*i+1]];
        bitList[i] = Integer.toString(temp);
      }
      // Now all bits are stored as strings in a bitList
      // Calculate length of message...
      String [] msgSize = new String[8];
      for (int i = 0; i < 8; i++){
        msgSize[i] = bitList[i];
      }
      String mySize = String.join("",msgSize);
      int sizeInt = Integer.parseInt(mySize,2);
      int numBits = (2*sizeInt+1)*8;
      int ctr = 0;
      String [] message = new String[sizeInt];
      for (int i = 8; i <  numBits; i+=16){
        for (int j = 0; j < 8; j++){
          //Grab all the bits
          msgSize[j] = bitList[i+j];
        }
        // Convert to an integer
        mySize = String.join("",msgSize);
        sizeInt = Integer.parseInt(mySize,2);
        //Convert Integer to ASCII and store
        message[ctr] = String.valueOf(Character.toChars((int)sizeInt));
        ctr +=1;
      }
      return String.join("",message);
      //return "Message";
    }

    public static String decode(String hexString) {
      // Grab Logistic Map Stuff
      int [] myMapInvFlat = logisticMap();
      int [] myBitList = stringToBitList(hexString);
      // Do XOR Step...
      int [] myXORList = new int[myBitList.length];
      for (int i = 0; i < myBitList.length; i++){
        myXORList[i] = myBitList[i]^myMapInvFlat[i];
      }
      int arrayDim = (int) Math.sqrt(myBitList.length);
      int [][] reshaped = reshape(myXORList,arrayDim);
      int [] targets = detectPosition(reshaped);
      int [][] adjusted = fixRotation(reshaped,targets);
      int[] unraveledIndices;
      if (adjusted.length == 25){
        unraveledIndices = zigzag25();
      } else {
        unraveledIndices = zigzag21();
      }
      String message = bitUnravelDecode(unraveledIndices,adjusted);





      return message;
    }



    public static void main(String[] args) {
        // Prints "Hello, World" to the terminal window.
        String testString = "0xa820c3180x6d3bb73b0x60bbc9250x372dbae30x548116770x6a5ef9f0xc411d31b0xb061ba660x132a81860xe40c73a50x19896a420x3f9c76e70x90869dff0xdc6e3fb40x619d85720xeafcd77f0xe64a913c0x4f4f54280xc1a060a60xb4a5fe300x8e19145e0xd49578860xa336c4e70xa882ded80x812b992a0xf855759a0x19fff1cc0xc552cce30x866f584a0xf56ed50b0x4bb8d3180x74b5878b";
        String mystring = decode(testString);
        System.out.println(mystring);
    }

}
