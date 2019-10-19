package com.company;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.sqrt;

public class Task23 {
    static public Mat img = null, img2 = null, transform;
    static public double tr[][];
    static  public byte[] arr, res, res2, arr2;
    static  public int[][] value, value2;
    static   public boolean isImageSet, diffFound = true;
    public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }
    static public void readImages() throws IOException {
        isImageSet = true;
        diffFound = true;
        JFileChooser fileopen = new JFileChooser();
        int ret;
        double S, k;
        Mat tra;
        String path1, path2;

        ret = fileopen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            path1 = file.getPath();
            BufferedImage BuffIMG = null;
            try {
                BuffIMG = ImageIO.read(new File(path1));
            } catch (IOException a) {

            }
            if(BuffIMG == null)isImageSet = false;
            else{
            img =BufferedImage2Mat(BuffIMG);
            double normalS = 600 * 600;
            double Sc = img.width() * img.height();
            double k2 = sqrt(Sc / normalS);
            if (k2 < 1) k2 = 1;
            Mat tra2 = new Mat(2, 3, CvType.CV_32FC1);
            tra2.put(0, 0,
                    1 / k2, 0, 0,
                    0, 1 / k2, 0
            );
            Imgproc.warpAffine(img, img, tra2, new Size(img.width() / k2, img.height() / k2));
           // img = Imgcodecs.imread(path1);
        }
        }
        else {
            isImageSet = false;
        }
        if(isImageSet){
        ret = fileopen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            path2 = file.getPath();
            BufferedImage BuffIMG2 = null;
            try {
                BuffIMG2 = ImageIO.read(new File(path2));
            } catch (IOException a) {

            }
            if(BuffIMG2 == null)isImageSet = false;
            else{
            img2 =BufferedImage2Mat(BuffIMG2);
            double normalS = 600 * 600;
            double Sc = img2.width() * img2.height();
            double k2 = sqrt(Sc / normalS);
            if (k2 < 1) k2 = 1;
            Mat tra2 = new Mat(2, 3, CvType.CV_32FC1);
            tra2.put(0, 0,
                    1 / k2, 0, 0,
                    0, 1 / k2, 0
            );
            Imgproc.warpAffine(img2, img2, tra2, new Size(img2.width() / k2, img2.height() / k2));
            //img2 = Imgcodecs.imread(path2);
        }
        }
        else {
            isImageSet = false;
        }
    }
    }
    static  public Point get(Point a) {
        return new Point((int)((tr[0][0] * a.x + tr[0][1] * a.y + tr[0][2]) / (tr[2][0] * a.x + tr[2][1] * a.y + tr[2][2])), (int)((tr[1][0] * a.x + tr[1][1] * a.y + tr[1][2]) / (tr[2][0] * a.x + tr[2][1] * a.y + tr[2][2])));
    }

    static  public boolean in(Point a) {
        return a.x >= 0 && a.x < img2.width() && a.y >= 0 && a.y < img2.height();
    }

    static public void setValue() {
        Mat grey = img.clone();
        Mat grey2 = img2.clone();
        Imgproc.cvtColor(img, grey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(img2, grey2, Imgproc.COLOR_BGR2GRAY);
        arr = new byte[img.width() * img.height()];
        res = new byte[img.channels() * img.width() * img.height()];
        arr2 = new byte[img2.width() * img2.height()];
        res2 = new byte[img2.channels() * img2.width() * img2.height()];
        grey.get(0, 0, arr);
        img.get(0, 0, res);
        grey2.get(0, 0, arr2);
        img2.get(0, 0, res2);
        value = new int[img.rows()][img.cols()];
        value2 = new int[img2.rows()][img2.cols()];

        for(int i = 0; i < img.rows(); ++i) {
            for(int j = 0; j < img.cols() - 1; ++j) {
                value[i][j] = ((int)arr[i * img.cols() + j] - (int)arr[i * img.cols() + j + 1]) * ((int)arr[i * img.cols() + j] - (int)arr[i * img.cols() + j + 1]);
            }
        }
        for(int i = 0; i < img.rows() - 1; ++i) {
            for(int j = 0; j < img.cols(); ++j) {
                value[i][j] += ((int)arr[i * img.cols() + j] - (int)arr[i * img.cols() + j + img.cols()]) * ((int)arr[i * img.cols() + j] - (int)arr[i * img.cols() + j + img.cols()]);
            }
        }

        for(int i = 0; i < img2.rows(); ++i) {
            for(int j = 0; j < img2.cols() - 1; ++j) {
                value2[i][j] = ((int)arr2[i * img2.cols() + j] - (int)arr2[i * img2.cols() + j + 1]) * ((int)arr2[i * img2.cols() + j] - (int)arr2[i * img2.cols() + j + 1]);
            }
        }
        for(int i = 0; i < img2.rows() - 1; ++i) {
            for(int j = 0; j < img2.cols(); ++j) {
                value2[i][j] += ((int)arr2[i * img2.cols() + j] - (int)arr2[i * img2.cols() + j + img2.cols()]) * ((int)arr2[i * img2.cols() + j] - (int)arr2[i * img2.cols() + j + img2.cols()]);
            }
        }
    }

    static public Mat solve() {
        int cnt = 30, d1 = 0, d2 = 0;
        int dy = img.height() / cnt, dx = img.width() / cnt;

        boolean ok;
        Point b;
        for(int i = 0; i < cnt; ++i) {
            for(int j = 0; j < cnt; ++j) {
                ok = true;
                d1 = 0;
                d2 = 0;
                for(int x = i * dx; x < dx * (i + 1); ++x) {
                    for(int y = j * dy; y < dy * (j + 1); ++y) {
                        b = get(new Point(x, y));
                        if(in(b)) {
                            d1 += value[y][x];
                            d2 += value2[(int)b.y][(int)b.x];
                        }
                        else {
                            ok = false;
                        }
                    }
                }
                //System.out.println(i + " " + j + " " + ok + " " + d1 + " " + d2);
                if(ok && d1 < d2) {
                    for(int x = i * dx; x < dx * (i + 1); ++x) {
                        for(int y = j * dy; y < dy * (j + 1); ++y) {
                            b = get(new Point(x, y));
                            for(int k = 0; k < img.channels(); ++k) {
                                res[(y * img.cols() + x) * img.channels() + k] = res2[(int)((b.y * img2.cols() + b.x) * img.channels() + k)];
                                //res[(y * img.cols() + x) * img.channels() + k] = 0;
                            }
                        }
                    }
                }
            }
        }
        Mat ans = img.clone();
        ans.put(0, 0, res);
        return ans;
    }

    static public  void findTransformMatrix() {
        tr = new double[3][3];
        MatOfKeyPoint kp_img = new MatOfKeyPoint();
        MatOfKeyPoint kp_img2 = new MatOfKeyPoint();
        AKAZE orb = AKAZE.create();
        orb.detect(img, kp_img);
        orb.detect(img2, kp_img2);
        List<KeyPoint> kp = kp_img.toList();
        List<KeyPoint> kp2 = kp_img2.toList();
        // Вычисляем дескрипторы
        Mat descriptors_img = new Mat(); Mat descriptors_img2 = new Mat();
        orb.compute(img, kp_img, descriptors_img);
        orb.compute(img2, kp_img2, descriptors_img2);
        // Сравниваем дескрипторы
        MatOfDMatch matches = new MatOfDMatch();
        DescriptorMatcher dm = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        dm.match(descriptors_img, descriptors_img2, matches);
        // Вычисляем минимальное и максимальное значения
        double max_dist = Double.MIN_VALUE, min_dist = Double.MAX_VALUE;
        float dist = 0;
        List<DMatch> list = matches.toList();
        for (int i = 0, j = list.size(); i < j; i++) {
            dist = list.get(i).distance;
            if (dist == 0) continue;
            if (dist < min_dist) min_dist = dist;
            if (dist > max_dist) max_dist = dist;
        }
        //System.out.println("min = " + min_dist + " max = " + max_dist); // Находим лучшие совпадения
        LinkedList<DMatch> list_good = new LinkedList<DMatch>();
        for (int i = 0, j = list.size(); i < j; i++) {
            if (list.get(i).distance < min_dist * 3) {
                list_good.add(list.get(i));
            }
        }

        MatOfDMatch mat_good = new MatOfDMatch();
        mat_good.fromList(list_good);
        // Отрисовываем результат
     //   Mat outImg = new Mat(img.rows() + img2.rows() + 10, img.cols() + img2.cols() + 10, CvType.CV_8UC3, new Scalar(0, 0, 0));
    //    Features2d.drawMatches(img, kp_img, img2, kp_img2, mat_good, outImg, new Scalar(255, 255, 255), Scalar.all(-1), new MatOfByte(), Features2d.DrawMatchesFlags_NOT_DRAW_SINGLE_POINTS);
        //panel.setImageFromMat(outImg, ".jpg");
        Collections.sort(list_good, new Comparator<DMatch>() {
            @Override
            public int compare(DMatch lhs, DMatch rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return Double.compare(lhs.distance, rhs.distance);
            }
        });
        List<KeyPoint> keys1 = kp_img.toList();
        List<KeyPoint> keys2 = kp_img2.toList();
        LinkedList<Point> list1 = new LinkedList<Point>();
        LinkedList<Point> list2 = new LinkedList<Point>();
        DMatch dmatch = null;
        int count = 50;
        if (list_good.size() < count) {
            count = list_good.size();
        }
        for (int i = 0; i < count; i++) {
            dmatch = list_good.get(i);
            list1.add(keys1.get(dmatch.queryIdx).pt);
            list2.add(keys2.get(dmatch.trainIdx).pt);
        }
        MatOfPoint2f p1 = new MatOfPoint2f();
        MatOfPoint2f p2 = new MatOfPoint2f();
        p1.fromList(list1);
        p2.fromList(list2);
        // Вычисляем матрицу трансформации
        Mat h = Calib3d.findHomography(p1, p2, Calib3d.RANSAC, 3);
        if (h.empty()) {
            System.out.println("Не удалось рассчитать матрицу трансформации");
            // hhjjhjhhjhjhjjhjjhhj
            diffFound = false;
            return;
        }
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                tr[i][j] = h.get(i, j)[0];
            }
        }
    }
}
