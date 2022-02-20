package ru.sfedu.compZrenie;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import ru.sfedu.compZrenie.services.ImageConvertorService;
import ru.sfedu.compZrenie.services.ImageService;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Lab3 {

  private static final String TEST_DATA_PATH = Paths.get("src/test/java/res/assets").toAbsolutePath().toString();
  private static final String TEST_IMAGE_NAME = "testImage2.jpg";

  private static final String TEST_RESULT_PATH = Paths.get("src/test/java/res/lab3").toAbsolutePath().toString();

  private static ImageService imageService;
  private static ImageConvertorService imageConvertorService;

  private Mat testImage;
  private Mat testGrayImage;

  @BeforeAll
  public static void config() {
    imageService = ImageService.getInstance();
    imageConvertorService = ImageConvertorService.getInstance();
  }

  @BeforeEach
  public void initTestImage() {
    this.testImage = imageService.readImageMatrix(TEST_DATA_PATH, TEST_IMAGE_NAME);
    this.testGrayImage = imageService.readGrayImageMatrix(TEST_DATA_PATH, TEST_IMAGE_NAME);
  }

  @Test
  public void sobelTest() {
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "sobelX",
            imageConvertorService.sobelX(this.testGrayImage)
    );
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "sobelY",
            imageConvertorService.sobelY(this.testGrayImage)
    );
  }

  @Test
  public void laplacianTest() {
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "laplacian",
            imageConvertorService.laplacian(this.testGrayImage)
    );
  }

  @Test
  public void mirror() {
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "mirrorV",
            imageConvertorService.mirrorV(this.testImage)
    );
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "mirrorH",
            imageConvertorService.mirrorH(this.testImage)
    );
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "mirrorHV",
            imageConvertorService.mirrorHV(this.testImage)
    );
  }

  @Test
  public void concat() {
    List<Mat> imgList = new ArrayList<>();
    Mat img = imageService.readImageMatrix(TEST_DATA_PATH, TEST_IMAGE_NAME);

    imgList.add(img);
    imgList.add(img);

    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "concatH_2",
            imageConvertorService.concatH(imgList)
    );
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "concatV_2",
            imageConvertorService.concatV(imgList)
    );


    imgList.add(img);

    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "concatH_3",
            imageConvertorService.concatH(imgList)
    );
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "concatV_3",
            imageConvertorService.concatV(imgList)
    );
  }

  @Test
  public void repeat() {
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "repeat_V_3",
            imageConvertorService.repeat(this.testImage, 3, 0)
    );
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "concat_H_3",
            imageConvertorService.repeat(this.testImage, 0, 3)
    );
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "concat_V_H_3",
            imageConvertorService.repeat(this.testImage, 3, 3)
    );
  }

  @Test
  public void resize() {
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "resize_50_50",
            imageConvertorService.resize(this.testImage, 50, 50)
    );
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "resize_500_500",
            imageConvertorService.resize(this.testImage, 500, 500)
    );
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "resize_1000_1000",
            imageConvertorService.resize(this.testImage, 1000, 1000)
    );
  }

  @Test
  public void rotation() {
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "rotation",
            imageConvertorService.rotation(this.testImage, 45, false)
    );
  }

  @Test
  public void perspective() {
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "perspective",
            imageConvertorService.perspective(
                    this.testImage,
                    new Point(0, 0),
                    new Point(500-1,0),
                    new Point(0,500-1),
                    new Point(300-1,1000-1)
            )
    );
  }

}
