package ru.sfedu.compZrenie;

import org.junit.jupiter.api.*;
import org.opencv.core.Mat;
import ru.sfedu.compZrenie.api.ImageApi;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTimeout;

public class Lab2 {

  private static final String TEST_IMAGE_PATH = "D:\\TEST\\";
  private static final String TEST_IMAGE_NAME = "testImage.jpg";

  private static ImageApi imageApi;

  @BeforeAll
  public static void config() {
    imageApi = ImageApi.getInstance();
  }

  @Test
  public void getImageMatrix() {
    Mat imageMatrix = imageApi.readImageMatrix(TEST_IMAGE_PATH, TEST_IMAGE_NAME);
    imageApi.showImage(imageMatrix);
    imageApi.writeImageMatrix(TEST_IMAGE_PATH, "lab2", imageMatrix);
    assertTimeout(Duration.ofSeconds(5), () -> delaySecond(1));
  }

  void delaySecond(int second) {
    try {
      TimeUnit.SECONDS.sleep(second);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
