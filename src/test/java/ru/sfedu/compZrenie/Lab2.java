package ru.sfedu.compZrenie;

import org.junit.jupiter.api.*;
import org.opencv.core.Mat;
import ru.sfedu.compZrenie.services.ImageService;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTimeout;

public class Lab2 {

  private static final String TEST_DATA_PATH = Paths.get("src/test/java/res/assets").toAbsolutePath().toString();
  private static final String TEST_IMAGE_NAME = "testImage.jpg";

  private static final String TEST_RESULT_PATH = Paths.get("src/test/java/res/lab2").toAbsolutePath().toString();

  private static ImageService imageService;

  @BeforeAll
  public static void config() {
    imageService = ImageService.getInstance();
  }

  @Test
  public void getImageMatrix() {
    Mat image = imageService.readImageMatrix(TEST_DATA_PATH, TEST_IMAGE_NAME);
    imageService.showImage(image);
    imageService.dropMatrixChannel(image, 5);
    imageService.showImage(image);
    imageService.writeImageMatrix(TEST_RESULT_PATH, "lab2.jpg", image);

    assertTimeout(Duration.ofSeconds(5), () -> delaySecond(3));
  }

  void delaySecond(int second) {
    try {
      TimeUnit.SECONDS.sleep(second);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
