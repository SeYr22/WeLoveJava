package com.company;

import org.opencv.core.Point3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PhotosInfo {
    public static List<Photo> photos = new ArrayList<>();

    public void receivePhoto(Image image, double height, double longitude, double latitude,
                             double yaw, double roll, double pitch){
        Matrices.buildYawMatrix(MathTransform.degreeToRadian(yaw));
        Matrices.buildRollMatrix(MathTransform.degreeToRadian(roll));
        Matrices.buildPitchMatrix(MathTransform.degreeToRadian(pitch));

        photos.add(new Photo(image, height, MathTransform.getMatrixPointOnGround(new Point3(longitude, latitude, height * 1000), "leftTopCorner"),
                MathTransform.getMatrixPointOnGround(new Point3(longitude, latitude, height * 1000), "rightTopCorner"),
                MathTransform.getMatrixPointOnGround(new Point3(longitude, latitude, height * 1000), "leftBottomCorner"),
                MathTransform.getMatrixPointOnGround(new Point3(longitude, latitude, height * 1000), "rightBottomCorner")));
    }

    public class Photo{
        private Image image;
        private double height;
        private Point3 leftTopCorner, rightTopCorner, leftBottomCorner, rightBottomCorner;

        public Photo(Image image, double height, Point3 leftTopCorner, Point3 rightTopCorner, Point3 leftBottomCorner, Point3 rightBottomCorner){
            this.image = image;
            this.height = height;
            this.leftTopCorner = leftTopCorner;
            this.rightTopCorner = rightTopCorner;
            this.leftBottomCorner = leftBottomCorner;
            this.rightBottomCorner = rightBottomCorner;
        }

        public Image getImage() {
            return image;
        }

        public Point3 getLeftTopCorner() {
            return leftTopCorner;
        }

        public Point3 getRightTopCorner() {
            return rightTopCorner;
        }

        public Point3 getLeftBottomCorner() {
            return leftBottomCorner;
        }

        public Point3 getRightBottomCorner() {
            return rightBottomCorner;
        }

        public double getHeight() { return height; }
    }
}
