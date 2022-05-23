package ru.sfedu.compZrenie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import ru.sfedu.compZrenie.services.ImageConvertorService;
import ru.sfedu.compZrenie.services.ImageService;

import java.nio.file.Paths;
import java.util.List;

public class Lab5 {
  private static final String TEST_DATA_PATH = Paths.get("src/test/java/res/assets").toAbsolutePath().toString();
  private static final String TEST_IMAGE_NAME1 = "testImage.jpg";
  private static final String TEST_IMAGE_NAME2 = "testImage2.jpg";
  private static final String TEST_IMAGE_FIG = "fig.jpg";

  private static final String TEST_RESULT_PATH = Paths.get("src/test/java/res/lab5").toAbsolutePath().toString();

  private static ImageService imageService;
  private static ImageConvertorService imageConvertorService;

  private Mat testImage;
  private Mat testImage2;
  private Mat testImageFig;

  @BeforeAll
  public static void config() {
    imageService = ImageService.getInstance();
    imageConvertorService = ImageConvertorService.getInstance();
  }

  @BeforeEach
  public void initTestImage() {
    this.testImage = imageService.readImageMatrix(TEST_DATA_PATH, TEST_IMAGE_NAME1);
    this.testImage2 = imageService.readImageMatrix(TEST_DATA_PATH, TEST_IMAGE_NAME2);
    this.testImageFig = imageService.readImageMatrix(TEST_DATA_PATH, TEST_IMAGE_FIG);
  }

  @Test
  public void task1() {
    double initVal = 20;
    Mat flooded = imageConvertorService.fillFlood(
            this.testImage,
            new Point(0, 0),
            new Scalar(initVal, initVal, initVal),
            new Scalar(initVal, initVal, initVal)
    );
    imageService.writeImageMatrix(TEST_RESULT_PATH, "floood", flooded);
  }

  @Test
  public void task2() {
    Mat stepDst = new Mat();
    for (int i = 1; i <= 3; i++) {
      stepDst = imageConvertorService.pyramidDown(stepDst.size().empty() ? this.testImage2 : stepDst, i);
      imageService.writeImageMatrix(
              TEST_RESULT_PATH,
              "pyramidDown" + i,
              stepDst
      );
    }
    for (int i = 1; i <= 3; i++) {
      stepDst = imageConvertorService.pyramidUp(stepDst, i);
      imageService.writeImageMatrix(
              TEST_RESULT_PATH,
              "pyramidUp " + i,
              stepDst
      );
    }

    Mat cropped = imageConvertorService.resize(this.testImage2, 400, 400);
    Mat smallImage = imageConvertorService.pyramidDown(cropped, 1);
    Mat resized = imageConvertorService.pyramidUp(smallImage, 1);
    Mat dst = new Mat();
    Core.subtract(cropped, resized, dst);
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "substructed",
            dst
    );
  }

  Mat getImgWithFigures() {
    Mat img = new Mat(1000, 1000, CvType.CV_8UC3, new Scalar(255, 255, 255));

    Imgproc.rectangle(img, new Point(0, 0), new Point(100, 100),
            imageConvertorService.getRandomColor(), Core.FILLED);
    Imgproc.rectangle(img, new Point(200, 0), new Point(300, 100),
            imageConvertorService.getRandomColor(), Core.FILLED);
    Imgproc.rectangle(img, new Point(100, 150), new Point(500, 400),
            imageConvertorService.getRandomColor(), Core.FILLED);
    return img;
  }

  @Test
  public void task3() {
    Mat img = getImgWithFigures(); // this.testImageFig;

    imageService.writeImageMatrix(TEST_RESULT_PATH, "rectangleSource", img);

    List<Mat> rectangles = imageConvertorService.findRectangles(img, 400, 250);

    rectangles.forEach(mat -> imageService.writeImageMatrix(TEST_RESULT_PATH, "rectangle" + mat.hashCode(), mat));

    Assertions.assertEquals(1, rectangles.size());
  }

}
