package com.company;

import org.opencv.core.Point;
import org.opencv.core.Point3;

public class MathTransform {
    public static double matrixWidth = 9;
    public static double matrixHeight = 6;
    public static double focalLength = 2;

    public static double degreeToRadian(double degree){
        return degree * Math.PI / 180;
    }

    public static double convertMmToM(double mm){
        return mm / 1000;
    }

    public static double distance(Point p1, Point p2){
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    public static double[][] multiply(double[][] m1, double[][] m2) {
        int m1ColLength = m1[0].length;
        int m2RowLength = m2.length;

        if(m1ColLength != m2RowLength)
            return null;

        int mRRowLength = m1.length;
        int mRColLength = m2[0].length;

        double[][] mResult = new double[mRRowLength][mRColLength];
        for(int i = 0; i < mRRowLength; i++) {
            for(int j = 0; j < mRColLength; j++) {
                for(int k = 0; k < m1ColLength; k++) {
                    mResult[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }

    public static Point3 getDirectingVector(double[][] pointMatrix, double[][] focus){
        Point3 vector = new Point3();

        vector.x = pointMatrix[0][0] - focus[0][0];
        vector.y = pointMatrix[1][0]- focus[1][0];
        vector.z = pointMatrix[2][0] - focus[2][0];

        return vector;
    }

    public static Point3 getMatrixPointOnGround(Point3 centerMatrix, String point){
        Point3 groundMatrix = new Point3();
        groundMatrix.z = 0;

        double[][] resultMatrix = multiply(Matrices.yawMatrix, multiply(Matrices.rollMatrix, Matrices.pitchMatrix));
        double[][] focusMatrix = multiply(resultMatrix, getMatrixPointOnAir(centerMatrix, "focus"));
        double[][] airMatrix = new double[4][1];

        airMatrix = multiply(resultMatrix, getMatrixPointOnAir(centerMatrix, point));
        Point3 directVector = getDirectingVector(airMatrix, focusMatrix);

        double param = -airMatrix[2][0] / directVector.z;
        groundMatrix.x = airMatrix[0][0] + directVector.x * param;
        groundMatrix.y = airMatrix[1][0] + directVector.y * param;

        return groundMatrix;
    }

    public static double[][] getMatrixPointOnAir(Point3 center, String point){
        double[][] cornerMatrix = new double[4][1];

        if(point == "rightTopCorner"){
            cornerMatrix[0][0] = center.x + convertMmToM(matrixWidth);
            cornerMatrix[1][0] = center.y + convertMmToM(matrixHeight);
            cornerMatrix[2][0] = center.z;
            cornerMatrix[3][0] = 1;
        }

        if(point == "leftTopCorner"){
            cornerMatrix[0][0] = center.x - convertMmToM(matrixWidth);
            cornerMatrix[1][0] = center.y + convertMmToM(matrixHeight);
            cornerMatrix[2][0] = center.z;
            cornerMatrix[3][0] = 1;
        }

        if(point == "rightBottomCorner"){
            cornerMatrix[0][0] = center.x + convertMmToM(matrixWidth);
            cornerMatrix[1][0] = center.y - convertMmToM(matrixHeight);
            cornerMatrix[2][0] = center.z;
            cornerMatrix[3][0] = 1;
        }

        if(point == "leftBottomCorner"){
            cornerMatrix[0][0] = center.x - convertMmToM(matrixWidth);
            cornerMatrix[1][0] = center.y - convertMmToM(matrixHeight);
            cornerMatrix[2][0] = center.z;
            cornerMatrix[3][0] = 1;
        }

        if(point == "focus"){
            cornerMatrix[0][0] = center.x;
            cornerMatrix[1][0] = center.y;
            cornerMatrix[2][0] = center.z - convertMmToM(focalLength);
            cornerMatrix[3][0] = 1;
        }

        return  cornerMatrix;
    }


}
