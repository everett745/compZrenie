package ru.sfedu.compZrenie.api;

import lombok.extern.log4j.Log4j2;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import ru.sfedu.compZrenie.services.OsService;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

@Log4j2
public class ImageApi {

  private static final int IMG_FRAME_PADDING = 100;
  private static ImageApi INSTANCE;

  public static ImageApi getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ImageApi();
    }
    return INSTANCE;
  }

  private ImageApi() {
    OsService.load();
  }

  public Mat readImageMatrix(String path, String fileName) {
    return Imgcodecs.imread(path + fileName, Imgcodecs.IMREAD_COLOR);
  }

  public void writeImageMatrix(String path, String fileName, Mat mat) {
    try {
      Imgcodecs.imwrite(getNewImagePath(path + fileName), mat);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  public BufferedImage getBufferedImage(Mat mat) {
    int type = mat.channels() > 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
    BufferedImage bufferedImage = new BufferedImage(mat.cols(), mat.rows(), type);
    mat.get(0, 0, ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData());
    return bufferedImage;
  }

  public void showImage(Mat mat) {
    showImage(getBufferedImage(mat));
  }

  public void showImage(BufferedImage bImage) {
    ImageIcon icon = new ImageIcon(bImage);
    JFrame frame = new JFrame();
    frame.setLayout(new FlowLayout());
    frame.setSize(
            bImage.getWidth() + IMG_FRAME_PADDING,
            bImage.getHeight() + IMG_FRAME_PADDING
    );
    JLabel jLabel = new JLabel(icon);
    frame.add(jLabel);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private String getNewImagePath(String base) {
    return String.format("%s_%d.jpg", base, System.currentTimeMillis());
  }
}
