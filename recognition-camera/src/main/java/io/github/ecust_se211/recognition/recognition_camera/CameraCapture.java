package io.github.ecust_se211.recognition.recognition_camera;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class CameraCapture {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture camera = new VideoCapture(0); // 0 表示使用默认的摄像头设备
        if (!camera.isOpened()) {
            System.out.println("摄像头无法打开");
        } else {
            Mat frame = new Mat();
            camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
            camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
            JFrame window = new JFrame("摄像头预览");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel label = new JLabel(new ImageIcon(convertMatToBufferedImage(frame)));
            window.add(label);
            window.pack();
            window.setVisible(true);

            while (true) {
                camera.read(frame);
                label.setIcon(new ImageIcon(convertMatToBufferedImage(frame)));
            }
        }
        camera.release();
    }

    public static BufferedImage convertMatToBufferedImage(Mat frame) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (frame.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = frame.channels() * frame.cols() * frame.rows();
        byte[] b = new byte[bufferSize];
        frame.get(0, 0, b);
        BufferedImage image = new BufferedImage(frame.cols(), frame.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
}
