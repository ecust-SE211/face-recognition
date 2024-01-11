package io.github.ecust_se211.recognition.recognition_camera;

import javax.swing.*;

import io.github.ecust_se211.recognition.recognition_camera.VideoPanel;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class CameraCapture {
    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // 新建窗口
        JFrame cameraFrame = new JFrame("camera");
        cameraFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cameraFrame.setSize(640, 480);
        cameraFrame.setBounds(0, 0, cameraFrame.getWidth(), cameraFrame.getHeight());
        VideoPanel videoPanel = new VideoPanel();
        cameraFrame.setContentPane(videoPanel);
        cameraFrame.setVisible(true);

        //TestImage();

        CascadeClassifier faceCascade = new CascadeClassifier();
        // 获取haarcascade_frontalface_alt.xml

        faceCascade.load("recognition-camera/src/main/java/io/github/ecust_se211/recognition/recognition_camera/haarcascade_frontalface_alt.xml");
        // 调用摄像头
        VideoCapture capture = new VideoCapture();
        try {
            capture.open(0);
            if (capture.isOpened()) {
                Mat image = new Mat();
                while(true) {
                    capture.read(image);
                    if (!image.empty()) {
                        detectAndDisplay(image, faceCascade);
                        videoPanel.setImageWithMat(image);
                        cameraFrame.repaint();
                    } else {
                        break;
                    }
                }
            }
        } finally {
            capture.release();
        }
    }

    /**
     * 绘制图形界面
     * @param frame
     * @param faceCascade
     */
    public static void detectAndDisplay(Mat frame, CascadeClassifier faceCascade)
    {
        MatOfRect faces = new MatOfRect();//用来画框的数组

        Mat grayFrame = new Mat();
        //当前帧图片进行灰度和直方均衡
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        int absoluteFaceSize = 0;
        int height = grayFrame.rows();
        if (Math.round(height * 0.2f) > 0) {
            absoluteFaceSize = Math.round(height * 0.2f);
        }

        // detect faces
        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(absoluteFaceSize, absoluteFaceSize), new Size());

        // 所有的
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
        if(facesArray.length>0) {//保存图片
            saveImage(frame, "test", "recognition-camera/src/main/java/io/github/ecust_se211/recognition/recognition_camera/src/Pictures/test.png", facesArray);
        }
        if(CmpPic("E:/EclipseWorkplace/recognition/recognition-camera/src/main/Pictures/test.png", "E:/EclipseWorkplace/recognition/recognition-camera/src/main/Pictures/ref.jpg")==1)
        {
            System.out.println("比对成功");
        }
        else{
            System.out.println("比对失败");
        }

    }
    //将图片保存到本地
    public static void saveImage(Mat frame, String fileName,String dir,Rect[] facesArray) {
        File imagepath = new File(dir);
        //人脸数据保存到图片中
        String path = imagepath.getParent();
        for(int i = 0; i < facesArray.length; i++)
        {
            if(i==0){
                Mat img_region =new Mat(frame, facesArray[i]);
                Imgcodecs.imwrite("E:/EclipseWorkplace/recognition/recognition-camera/src/main/Pictures/test.png", img_region);
                //System.out.println("保存成功");
            }
        }

    }
    public static void TestImage(Mat source,String aimPath) {
        Mat des = Imgcodecs.imread("E:/EclipseWorkplace/recognition/recognition-camera/src/main/Pictures/reference.jpg");
        MatOfRect faces = new MatOfRect();
        CascadeClassifier faceCascade = new CascadeClassifier();
        faceCascade.load("recognition-camera/src/main/java/io/github/ecust_se211/recognition/recognition_camera/haarcascade_frontalface_alt.xml");
        Mat videoMatGray = new Mat();
        Imgproc.cvtColor(des, videoMatGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(videoMatGray, videoMatGray);


        faceCascade.detectMultiScale(videoMatGray, faces);
        for (int i = 0; i < faces.toArray().length; i++) {
            Rect r = faces.toArray()[i];
            Mat img_region = new Mat(des, r);
            Imgcodecs.imwrite("E:/EclipseWorkplace/recognition/recognition-camera/src/main/Pictures/referenceNormalized.png", img_region);
        }
    }

    public static double CmpPic(String src, String des) {
        System.out.println("\n==========直方图比较==========");
        //自定义阈值
        //相关性阈值，应大于多少，越接近1表示越像，最大为1
        double HISTCMP_CORREL_THRESHOLD = 0.7;
        //卡方阈值，应小于多少，越接近0表示越像
        double HISTCMP_CHISQR_THRESHOLD = 2;
        //交叉阈值，应大于多少，数值越大表示越像
        double HISTCMP_INTERSECT_THRESHOLD = 1.2;
        //巴氏距离阈值，应小于多少，越接近0表示越像
        double HISTCMP_BHATTACHARYYA_THRESHOLD = 0.3;

        try {

            long startTime = System.currentTimeMillis();

            org.opencv.core.Mat mat_src = Imgcodecs.imread(src);
            org.opencv.core.Mat mat_des = Imgcodecs.imread(des);
            ;

            if (mat_src.empty() || mat_des.empty()) {
                throw new Exception("no file.");
            }

            org.opencv.core.Mat hsv_src = new org.opencv.core.Mat();
            org.opencv.core.Mat hsv_des = new org.opencv.core.Mat();

            // 转换成HSV
            Imgproc.cvtColor(mat_src, hsv_src, Imgproc.COLOR_BGR2HSV);
            Imgproc.cvtColor(mat_des, hsv_des, Imgproc.COLOR_BGR2HSV);

            List<Mat> listImg1 = new ArrayList<Mat>();
            List<org.opencv.core.Mat> listImg2 = new ArrayList<org.opencv.core.Mat>();
            listImg1.add(hsv_src);
            listImg2.add(hsv_des);

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

            return retVal;
        } catch (Exception e) {
            System.out.println("例外:" + e);
        }
        return 0;
    }

}
