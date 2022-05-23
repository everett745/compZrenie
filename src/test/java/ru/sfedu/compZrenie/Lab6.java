package ru.sfedu.compZrenie;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import ru.sfedu.compZrenie.services.ImageConvertorService;
import ru.sfedu.compZrenie.services.ImageService;

import java.nio.file.Paths;
import java.util.AbstractMap;

public class Lab6 {
  private static final String TEST_DATA_PATH = Paths.get("src/test/java/res/assets").toAbsolutePath().toString();
  private static final String TEST_IMAGE_NAME1 = "fig.png";

  private static final String TEST_RESULT_PATH = Paths.get("src/test/java/res/lab6").toAbsolutePath().toString();

  private static ImageService imageService;
  private static ImageConvertorService imageConvertorService;

  private Mat grayImage;

  @BeforeAll
  public static void config() {
    imageService = ImageService.getInstance();
    imageConvertorService = ImageConvertorService.getInstance();
  }

  @BeforeEach
  public void initTestImage() {
    this.grayImage = imageService.readGrayImageMatrix(TEST_DATA_PATH, TEST_IMAGE_NAME1);
  }

  @Test
  public void task1() {
    Mat detectedEdges = imageConvertorService.baseBlur(grayImage, new Size(3, 3));
    AbstractMap.SimpleEntry<Mat, Double> thresholdResult = imageConvertorService.thresholdImage(detectedEdges, 50, 255);
    double threshold = thresholdResult.getValue();
    Imgproc.Canny(detectedEdges, detectedEdges, threshold, threshold * 3);
    imageService.writeImageMatrix(TEST_RESULT_PATH, "canny", detectedEdges);
  }

}
