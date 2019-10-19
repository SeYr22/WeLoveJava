package com.company;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.company.Task23.BufferedImage2Mat;
import static java.lang.Math.sqrt;

public class Task5 {
    public static Mat mat1, mat2;
    public static boolean imageIsSet = false;
    public static void readImages2() throws IOException {
        imageIsSet = true;
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
            if(BuffIMG == null) imageIsSet = false;
            else{
            mat1 = BufferedImage2Mat(BuffIMG);
            double normalS = 500 * 500;
            double Sc = mat1.width() * mat1.height();
            double k2 = sqrt(Sc / normalS);
            if (k2 < 1) k2 = 1;
            Mat tra2 = new Mat(2, 3, CvType.CV_32FC1);
            tra2.put(0, 0,
                    1 / k2, 0, 0,
                    0, 1 / k2, 0
            );
            Imgproc.warpAffine(mat1, mat1, tra2, new Size(mat1.width() / k2, mat1.height() / k2));
            // img = Imgcodecs.imread(path1);
        }}
        else {
            imageIsSet = false;
        }
        if(imageIsSet){
        ret = fileopen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            path2 = file.getPath();
            BufferedImage BuffIMG2 = null;
            try {
                BuffIMG2 = ImageIO.read(new File(path2));
            } catch (IOException a) {

            }
            if(BuffIMG2 == null) imageIsSet = false;
            else{
            mat2 =BufferedImage2Mat(BuffIMG2);
            double normalS = 500 * 500;
            double Sc = mat2.width() * mat2.height();
            double k2 = sqrt(Sc / normalS);
            if (k2 < 1) k2 = 1;
            Mat tra2 = new Mat(2, 3, CvType.CV_32FC1);
            tra2.put(0, 0,
                    1 / k2, 0, 0,
                    0, 1 / k2, 0
            );
            Imgproc.warpAffine(mat2, mat2, tra2, new Size(mat2.width() / k2, mat2.height() / k2));
            //img2 = Imgcodecs.imread(path2);
        }
        }
        else {
            imageIsSet = false;
        }
        }
    }
   public static void solvetask5(){
        Mat greymat1 = new Mat();
        Mat greymat2 = new Mat();
        Imgproc.cvtColor(mat1, greymat1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat2, greymat2, Imgproc.COLOR_BGR2GRAY);
        Mat ssim1 = getSSIM(greymat1, greymat2);


        Mat thresh = new Mat();
        Mat ssim = new Mat();

        ssim1.convertTo(ssim, CvType.CV_8U, 255.0);



        Imgproc.threshold(ssim, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        Mat threshCopy = thresh.clone();
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(threshCopy, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for(int i = 0; i < contours.size(); ++i) {
            MatOfPoint c = contours.get(i);
            Rect rect = Imgproc.boundingRect(c);
            Imgproc.rectangle(mat1, rect.tl(), rect.br(), new Scalar(255, 0, 0, 255), 2);
            Imgproc.rectangle(mat2, rect.tl(), rect.br(), new Scalar(255, 0, 0, 255), 2);
        }
    }
    public static Mat getSSIM(Mat i1, Mat i2) {
        double C1 = 6.5025, C2 = 58.5225;
        /***************************** INITS **********************************/

        int new_rows = Math.min(i1.rows(), i2.rows());
        int new_cols = Math.min(i1.cols(), i2.cols());

        i1 = i1.submat(0, new_rows, 0, new_cols);
        i2 = i2.submat(0, new_rows, 0, new_cols);

        int d     = CvType.CV_32F;

        Mat I1 = new Mat();
        Mat I2 = new Mat();
        i1.convertTo(I1, d);           // cannot calculate on one byte large values
        i2.convertTo(I2, d);

        Mat I2_2   = I2.mul(I2);        // I2^2
        Mat I1_2   = I1.mul(I1);        // I1^2
        Mat I1_I2 = new Mat();
        Core.multiply(I1, I2, I1_I2);


        //Mat I1_I2  = new Mat();         // I1 * I2
        //I1_2.copyTo(I1_I2);

        /*************************** END INITS **********************************/

        Mat mu1 = new Mat();
        Mat mu2 = new Mat();   // PRELIMINARY COMPUTING
        Imgproc.GaussianBlur(I1, mu1, new Size(11, 11), 1.5);
        Imgproc.GaussianBlur(I2, mu2, new Size(11, 11), 1.5);

        Mat mu1_2   =   mu1.mul(mu1);
        Mat mu2_2   =   mu2.mul(mu2);
        Mat mu1_mu2 =  new Mat();
        Core.multiply(mu1, mu2, mu1_mu2);

        Mat sigma1_2 = new Mat();
        Mat sigma2_2 = new Mat();
        Mat sigma12 = new Mat();

        Imgproc.GaussianBlur(I1_2, sigma1_2, new Size(11, 11), 1.5);
        //sigma1_2 -= mu1_2;
        Core.subtract(sigma1_2, mu1_2, sigma1_2);

        Imgproc.GaussianBlur(I2_2, sigma2_2, new  Size(11, 11), 1.5);
        //sigma2_2 -= mu2_2;
        Core.subtract(sigma2_2, mu2_2, sigma2_2);

        Imgproc.GaussianBlur(I1_I2, sigma12, new Size(11, 11), 1.5);
        //sigma12 -= mu1_mu2;
        Core.subtract(sigma12, mu1_mu2, sigma12);

        ///////////////////////////////// FORMULA ////////////////////////////////
        Mat t1 = new Mat(mu1_mu2.size(), CvType.CV_32F);
        Mat t2 = new Mat(sigma12.size(), CvType.CV_32F);
        Mat t3 = new Mat(t1.size(), CvType.CV_32F);

        Scalar scalar = new Scalar(C1);

        mu1_mu2.convertTo(t1, -1, 2, C1); // t1 = 2 * mu1_mu2 + C1;
        sigma12.convertTo(t2, -1, 2, C2); // t2 = 2 * sigma12 + C2;
        Core.multiply(t1, t2, t3);        // t3 = ((2*mu1_mu2 + C1).*(2*sigma12 + C2))

        Core.addWeighted(mu1_2, 1.0, mu2_2, 1.0, C1, t1);       // t1 = mu1_2 + mu2_2 + C1;
        Core.addWeighted(sigma1_2, 1.0, sigma2_2, 1.0, C2, t2); // t2 = sigma1_2 + sigma2_2 + C2;
        Core.multiply(t1, t2, t1);


        Mat ssim_map = new Mat();
        Core.divide(t3, t1, ssim_map);      // ssim_map =  t3./t1;

        Scalar mssim = Core.mean( ssim_map ); // mssim = average of ssim map
        return ssim_map;
    }
}
