package com.company;

public class Matrices {
    public static double[][] yawMatrix = new double[4][4];
    public static double[][] rollMatrix = new double[4][4];
    public static double[][] pitchMatrix = new double[4][4];

    public static void buildYawMatrix(double yaw){
        yawMatrix[0][0] = Math.cos(yaw);
        yawMatrix[0][1] = Math.sin(yaw);
        yawMatrix[0][2] = 0;
        yawMatrix[0][3] = 0;

        yawMatrix[1][0] = -Math.sin(yaw);
        yawMatrix[1][1] = Math.cos(yaw);
        yawMatrix[1][2] = 0;
        yawMatrix[1][3] = 0;

        yawMatrix[2][0] = 0;
        yawMatrix[2][1] = 0;
        yawMatrix[2][2] = 1;
        yawMatrix[2][3] = 0;

        yawMatrix[3][0] = 0;
        yawMatrix[3][1] = 0;
        yawMatrix[3][2] = 0;
        yawMatrix[3][3] = 1;
    }

    public static void buildRollMatrix(double roll){
        rollMatrix[0][0] = Math.cos(roll);
        rollMatrix[0][1] = 0;
        rollMatrix[0][2] = -Math.sin(roll);
        rollMatrix[0][3] = 0;

        rollMatrix[1][0] = 0;
        rollMatrix[1][1] = 1;
        rollMatrix[1][2] = 0;
        rollMatrix[1][3] = 0;

        rollMatrix[2][0] = Math.sin(roll);
        rollMatrix[2][1] = 0;
        rollMatrix[2][2] = Math.cos(roll);
        rollMatrix[2][3] = 0;

        rollMatrix[3][0] = 0;
        rollMatrix[3][1] = 0;
        rollMatrix[3][2] = 0;
        rollMatrix[3][3] = 1;
    }

    public static void buildPitchMatrix(double pitch){
        pitchMatrix[0][0] = 1;
        pitchMatrix[0][1] = 0;
        pitchMatrix[0][2] = 0;
        pitchMatrix[0][3] = 0;

        pitchMatrix[1][0] = 0;
        pitchMatrix[1][1] = Math.cos(pitch);
        pitchMatrix[1][2] = Math.sin(pitch);
        pitchMatrix[1][3] = 0;

        pitchMatrix[2][0] = 0;
        pitchMatrix[2][1] = -Math.sin(pitch);
        pitchMatrix[2][2] = Math.cos(pitch);
        pitchMatrix[2][3] = 0;

        pitchMatrix[3][0] = 0;
        pitchMatrix[3][1] = 0;
        pitchMatrix[3][2] = 0;
        pitchMatrix[3][3] = 1;
    }
}
