package io.github.ecust_se211.recognition.recognition_camera;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import org.opencv.core.Mat;

/**
 * 视频面板类，继承自JPanel。
 * 用于显示视频图像。
 */
public class VideoPanel extends JPanel {

    private BufferedImage image;  // 显示的图像

    /**
     * 设置图像。
     *
     * @param mat 输入的视频图像，类型为Mat。
     */
    public void setImageWithMat(Mat mat) {
        image = mat2BufferedImage.matToBufferedImage(mat);
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), this);
        }
    }

    /**
     * 显示视频面板。
     *
     * @param title  窗口标题。
     * @param width  窗口宽度。
     * @param height 窗口高度。
     * @param open   是否在关闭窗口时保持窗口激活。
     * @return 返回VideoPanel对象。
     */
    public static VideoPanel show(String title, int width, int height, int open) {
        JFrame frame = new JFrame(title);
        if (open == 0) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        frame.setSize(width, height);
        frame.setBounds(0, 0, width, height);
        VideoPanel videoPanel = new VideoPanel();
        videoPanel.setSize(width, height);
        frame.setContentPane(videoPanel);
        frame.setVisible(true);
        return videoPanel;
    }
}