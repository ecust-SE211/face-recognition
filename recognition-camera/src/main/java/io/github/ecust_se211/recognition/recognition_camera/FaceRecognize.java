package io.github.ecust_se211.recognition.recognition_camera;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FaceRecognize {
    //相关性阈值，应大于多少，越接近1表示越像，最大为1
    public static double HISTCMP_CORREL_THRESHOLD = 0.6;

    //卡方阈值，应小于多少，越接近0表示越像
    public static double HISTCMP_CHISQR_THRESHOLD = 50;
    //交叉阈值，应大于多少，数值越大表示越像
    public static double HISTCMP_INTERSECT_THRESHOLD = 1.2;
    //巴氏距离阈值，应小于多少，越接近0表示越像
    public static double HISTCMP_BHATTACHARYYA_THRESHOLD = 0.3;

    public static final String FACE_CASCADE_FILE_PATH = "recognition-camera/src/main/java/io/github/ecust_se211/recognition/recognition_camera/haarcascade_frontalface_alt.xml";

    public static boolean ComparePicture(Mat srcPic, Mat refPic) {
        System.out.println("\n==========直方图比较==========");
        try {

            long startTime = System.currentTimeMillis();


            if (srcPic.empty() || refPic.empty()) {
                throw new Exception("no file.");
            }

            org.opencv.core.Mat hsv_src = new org.opencv.core.Mat();
            org.opencv.core.Mat hsv_ref = new org.opencv.core.Mat();

            // 转换成HSV
            Imgproc.cvtColor(srcPic, hsv_src, Imgproc.COLOR_BGR2HSV);
            Imgproc.cvtColor(refPic, hsv_ref, Imgproc.COLOR_BGR2HSV);

            List<Mat> listImg1 = new ArrayList<Mat>();
            List<org.opencv.core.Mat> listImg2 = new ArrayList<org.opencv.core.Mat>();
            listImg1.add(hsv_src);
            listImg2.add(hsv_ref);

            MatOfFloat ranges = new MatOfFloat(0, 255);
            MatOfInt histSize = new MatOfInt(50);
            MatOfInt channels = new MatOfInt(0);

            org.opencv.core.Mat histImg1 = new org.opencv.core.Mat();
            org.opencv.core.Mat histImg2 = new org.opencv.core.Mat();

            //org.bytedeco.javacpp中的方法不太了解参数，所以直接上org.opencv中的方法，所以需要加载一下dll，System.load("D:\\soft\\openCV3\\opencv\\build\\java\\x64\\opencv_java345.dll");
            //opencv_imgproc.calcHist(images, nimages, channels, mask, hist, dims, histSize, ranges, uniform, accumulate);
            Imgproc.calcHist(listImg1, channels, new org.opencv.core.Mat(), histImg1, histSize, ranges);
            Imgproc.calcHist(listImg2, channels, new org.opencv.core.Mat(), histImg2, histSize, ranges);

            org.opencv.core.Core.normalize(histImg1, histImg1, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());
            org.opencv.core.Core.normalize(histImg2, histImg2, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());

            double result0, result1, result2, result3;
            result0 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CORREL);
            result1 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CHISQR);
            result2 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_INTERSECT);
            result3 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_BHATTACHARYYA);

            System.out.println("相关性（度量越高，匹配越准确 [基准：" + HISTCMP_CORREL_THRESHOLD + "]）,当前值:" + result0);
            System.out.println("卡方（度量越低，匹配越准确 [基准：" + HISTCMP_CHISQR_THRESHOLD + "]）,当前值:" + result1);
            System.out.println("交叉核（度量越高，匹配越准确 [基准：" + HISTCMP_INTERSECT_THRESHOLD + "]）,当前值:" + result2);
            System.out.println("巴氏距离（度量越低，匹配越准确 [基准：" + HISTCMP_BHATTACHARYYA_THRESHOLD + "]）,当前值:" + result3);

            //一共四种方式，有三个满足阈值就算匹配成功
            int count = 0;
            if (result0 > HISTCMP_CORREL_THRESHOLD)
                count++;
            if (result1 < HISTCMP_CHISQR_THRESHOLD)
                count++;
            if (result2 > HISTCMP_INTERSECT_THRESHOLD)
                count++;
            if (result3 < HISTCMP_BHATTACHARYYA_THRESHOLD)
                count++;
            int retVal = 0;
            if (count >= 3) {
                //这是相似的图像
                retVal = 1;
            }
            long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("花费时间= " + estimatedTime + "ms");

            if (retVal == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("例外:" + e);
        }
        return false;
    }

    public static boolean ComparePicture(String srcPicPath, String refPicPath) {
        try {

            org.opencv.core.Mat mat_src = Imgcodecs.imread(srcPicPath);
            org.opencv.core.Mat mat_des = Imgcodecs.imread(refPicPath);

            org.opencv.core.Mat hsv_src = new org.opencv.core.Mat();
            org.opencv.core.Mat hsv_ref = new org.opencv.core.Mat();

            Imgproc.cvtColor(mat_src, hsv_src, Imgproc.COLOR_BGR2HSV);
            Imgproc.cvtColor(mat_des, hsv_ref, Imgproc.COLOR_BGR2HSV);

            List<Mat> listImg1 = new ArrayList<Mat>();
            List<org.opencv.core.Mat> listImg2 = new ArrayList<org.opencv.core.Mat>();
            listImg1.add(hsv_src);
            listImg2.add(hsv_ref);

            MatOfFloat ranges = new MatOfFloat(0, 255);
            MatOfInt histSize = new MatOfInt(50);
            MatOfInt channels = new MatOfInt(0);

            org.opencv.core.Mat histImg1 = new org.opencv.core.Mat();
            org.opencv.core.Mat histImg2 = new org.opencv.core.Mat();

            //org.bytedeco.javacpp中的方法不太了解参数，所以直接上org.opencv中的方法，所以需要加载一下dll，System.load("D:\\soft\\openCV3\\opencv\\build\\java\\x64\\opencv_java345.dll");
            //opencv_imgproc.calcHist(images, nimages, channels, mask, hist, dims, histSize, ranges, uniform, accumulate);
            Imgproc.calcHist(listImg1, channels, new org.opencv.core.Mat(), histImg1, histSize, ranges);
            Imgproc.calcHist(listImg2, channels, new org.opencv.core.Mat(), histImg2, histSize, ranges);

            org.opencv.core.Core.normalize(histImg1, histImg1, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());
            org.opencv.core.Core.normalize(histImg2, histImg2, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());

            double result0, result1, result2, result3;
            result0 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CORREL);
            result1 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CHISQR);
            result2 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_INTERSECT);
            result3 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_BHATTACHARYYA);

            System.out.println("相关性（度量越高，匹配越准确 [基准：" + HISTCMP_CORREL_THRESHOLD + "]）,当前值:" + result0);
            System.out.println("卡方（度量越低，匹配越准确 [基准：" + HISTCMP_CHISQR_THRESHOLD + "]）,当前值:" + result1);
            System.out.println("交叉核（度量越高，匹配越准确 [基准：" + HISTCMP_INTERSECT_THRESHOLD + "]）,当前值:" + result2);
            System.out.println("巴氏距离（度量越低，匹配越准确 [基准：" + HISTCMP_BHATTACHARYYA_THRESHOLD + "]）,当前值:" + result3);

            //一共四种方式，有三个满足阈值就算匹配成功
            int count = 0;
            if (result0 > HISTCMP_CORREL_THRESHOLD)
                count++;
            if (result1 < HISTCMP_CHISQR_THRESHOLD)
                count++;
            if (result2 > HISTCMP_INTERSECT_THRESHOLD)
                count++;
            if (result3 < HISTCMP_BHATTACHARYYA_THRESHOLD)
                count++;
            int retVal = 0;
            if (count >= 3) {
                //这是相似的图像
                retVal = 1;
            }

            if (retVal == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("例外:" + e);
        }
        return false;
    }
    public static boolean ComparePicture(Mat srcPic, Mat refPic,double corr_threshold,double chisqr_threshold,double intersect_threshold,double bhattacharyya_threshold) {
        System.out.println("\n==========直方图比较==========");
        try {

            long startTime = System.currentTimeMillis();


            if (srcPic.empty() || refPic.empty()) {
                throw new Exception("no file.");
            }

            org.opencv.core.Mat hsv_src = new org.opencv.core.Mat();
            org.opencv.core.Mat hsv_ref = new org.opencv.core.Mat();

            // 转换成HSV
            Imgproc.cvtColor(srcPic, hsv_src, Imgproc.COLOR_BGR2HSV);
            Imgproc.cvtColor(refPic, hsv_ref, Imgproc.COLOR_BGR2HSV);

            List<Mat> listImg1 = new ArrayList<Mat>();
            List<org.opencv.core.Mat> listImg2 = new ArrayList<org.opencv.core.Mat>();
            listImg1.add(hsv_src);
            listImg2.add(hsv_ref);

            MatOfFloat ranges = new MatOfFloat(0, 255);
            MatOfInt histSize = new MatOfInt(50);
            MatOfInt channels = new MatOfInt(0);

            org.opencv.core.Mat histImg1 = new org.opencv.core.Mat();
            org.opencv.core.Mat histImg2 = new org.opencv.core.Mat();

            //org.bytedeco.javacpp中的方法不太了解参数，所以直接上org.opencv中的方法，所以需要加载一下dll，System.load("D:\\soft\\openCV3\\opencv\\build\\java\\x64\\opencv_java345.dll");
            //opencv_imgproc.calcHist(images, nimages, channels, mask, hist, dims, histSize, ranges, uniform, accumulate);
            Imgproc.calcHist(listImg1, channels, new org.opencv.core.Mat(), histImg1, histSize, ranges);
            Imgproc.calcHist(listImg2, channels, new org.opencv.core.Mat(), histImg2, histSize, ranges);

            org.opencv.core.Core.normalize(histImg1, histImg1, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());
            org.opencv.core.Core.normalize(histImg2, histImg2, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());

            double result0, result1, result2, result3;
            result0 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CORREL);
            result1 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CHISQR);
            result2 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_INTERSECT);
            result3 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_BHATTACHARYYA);

            System.out.println("相关性（度量越高，匹配越准确 [基准：" + corr_threshold + "]）,当前值:" + result0);
            System.out.println("卡方（度量越低，匹配越准确 [基准：" + chisqr_threshold + "]）,当前值:" + result1);
            System.out.println("交叉核（度量越高，匹配越准确 [基准：" + intersect_threshold + "]）,当前值:" + result2);
            System.out.println("巴氏距离（度量越低，匹配越准确 [基准：" + bhattacharyya_threshold + "]）,当前值:" + result3);

            //一共四种方式，有三个满足阈值就算匹配成功
            int count = 0;
            if (result0 > corr_threshold)
                count++;
            if (result1 < chisqr_threshold)
                count++;
            if (result2 > intersect_threshold)
                count++;
            if (result3 < bhattacharyya_threshold)
                count++;
            int retVal = 0;
            if (count >= 3) {
                //这是相似的图像
                retVal = 1;
            }
            long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("花费时间= " + estimatedTime + "ms");

            if (retVal == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("例外:" + e);
        }
        return false;
    }
    public static Mat GetImage(String path) {
        Mat image = new Mat();
        image = Imgcodecs.imread(path);
        return image;
    }

    public static void StoreImage(Mat image, String path, boolean needPreProcess) {
        if (needPreProcess == false) {
            Imgcodecs.imwrite(path, image);
        } else {
            MatOfRect faces = new MatOfRect();
            CascadeClassifier faceCascade = new CascadeClassifier();
            faceCascade.load(FACE_CASCADE_FILE_PATH);
            Mat videoMatGray = new Mat();
            Imgproc.cvtColor(image, videoMatGray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(videoMatGray, videoMatGray);


            faceCascade.detectMultiScale(videoMatGray, faces);
            for (int i = 0; i < faces.toArray().length; i++) {
                Rect r = faces.toArray()[i];
                Mat img_region = new Mat(image, r);
                Imgcodecs.imwrite(path, img_region);
            }
        }

    }

}
