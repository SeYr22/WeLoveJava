package com.company;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class Task4 {
    public static List<Image> solveTask4(){
        List<Area> areaList = new ArrayList<>();
        Image[] arrayOfImages = new Image[22];
        List<Image> listOfImages = new ArrayList<>();

        for(int i = 0; i < PhotosInfo.photos.size(); i++){
            Path2D.Double path = new Path2D.Double();
            path.moveTo(PhotosInfo.photos.get(i).getLeftTopCorner().x, PhotosInfo.photos.get(i).getLeftTopCorner().y);
            path.lineTo(PhotosInfo.photos.get(i).getRightTopCorner().x, PhotosInfo.photos.get(i).getRightTopCorner().y);
            path.lineTo(PhotosInfo.photos.get(i).getRightBottomCorner().x, PhotosInfo.photos.get(i).getRightBottomCorner().y);
            path.lineTo(PhotosInfo.photos.get(i).getLeftBottomCorner().x, PhotosInfo.photos.get(i).getLeftBottomCorner().y);
            path.closePath();

            Shape temp = path;
            areaList.add(new Area(temp));
        }

        boolean[][] matrix = new boolean[PhotosInfo.photos.size()][PhotosInfo.photos.size()];
        for(int i = 0; i < PhotosInfo.photos.size(); ++i) {
            for(int j = 0; j < PhotosInfo.photos.size(); ++j) {
                if(check(areaList.get(i), areaList.get(j))) {
                    matrix[i][j] = true;
                    matrix[j][i] = true;
                }
                else {
                    matrix[i][j] = false;
                    matrix[j][i] = false;
                }
            }
        }
        int[] a = new int[PhotosInfo.photos.size()];
        for(int i = 0; i < PhotosInfo.photos.size(); ++i) {
            a[i] = i;
        }
        boolean ok;
        do {
            ok = true;
            for(int i = 1; i < PhotosInfo.photos.size(); ++i) {
                if(!matrix[a[i - 1]][a[i]]) {
                    ok = false;
                    break;
                }
            }
            if(ok) break;
        }
        while(next_permutation(a));
        if(ok) {
          //  System.out.println("Result:");
            for(int i = 0; i < PhotosInfo.photos.size(); ++i) {
            //    System.out.print(a[i] + " ");
                arrayOfImages[a[i]] = PhotosInfo.photos.get(i).getImage();
             /*   System.out.println(PhotosInfo.photos.get(i).getLeftBottomCorner().toString() + " " +
                        PhotosInfo.photos.get(i).getRightBottomCorner().toString() + " " +
                        PhotosInfo.photos.get(i).getRightTopCorner().toString() + " " +
                        PhotosInfo.photos.get(i).getLeftTopCorner().toString());*/
            }
            for(int i = 0; i < PhotosInfo.photos.size(); i++)
                listOfImages.add(arrayOfImages[i]);
        }
        else {
          //  for(int i = 0; i < PhotosInfo.photos.size(); ++i) {
                //    System.out.print(a[i] + " ");
           /*     System.out.println(PhotosInfo.photos.get(i).getLeftBottomCorner().toString() + " " +
                        PhotosInfo.photos.get(i).getRightBottomCorner().toString() + " " +
                        PhotosInfo.photos.get(i).getRightTopCorner().toString() + " " +
                        PhotosInfo.photos.get(i).getLeftTopCorner().toString());*/
           // }
            System.out.println("Incorrect data");
        }
        return listOfImages;
    }
    public static boolean next_permutation(int[] p) {
        for (int a = p.length - 2; a >= 0; --a)
            if (p[a] < p[a + 1])
                for (int b = p.length - 1;; --b)
                    if (p[b] > p[a]) {
                        int t = p[a];
                        p[a] = p[b];
                        p[b] = t;
                        for (++a, b = p.length - 1; a < b; ++a, --b) {
                            t = p[a];
                            p[a] = p[b];
                            p[b] = t;
                        }
                        return true;
                    }
        return false;
    }
    public static boolean check(Area a, Area b) {
        Area c = (Area)a.clone();
        Area d = (Area)b.clone();
        c.intersect(d);
        if(!c.getPathIterator(null).isDone()) {
            return true;
        }
        else return false;
    }
}
