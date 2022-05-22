package ru.sfedu.compZrenie;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import ru.sfedu.compZrenie.services.ImageConvertorService;
import ru.sfedu.compZrenie.services.ImageService;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


public class Lab4 {

  private static final String TEST_DATA_PATH = Paths.get("src/test/java/res/assets").toAbsolutePath().toString();
  private static final String TEST_IMAGE_NAME = "testImage2.jpg";

  private static final String TEST_RESULT_PATH = Paths.get("src/test/java/res/lab4").toAbsolutePath().toString();

  private static ImageService imageService;
  private static ImageConvertorService imageConvertorService;

  private Mat testImage;

  @BeforeAll
  public static void config() {
    imageService = ImageService.getInstance();
    imageConvertorService = ImageConvertorService.getInstance();
  }

  @BeforeEach
  public void initTestImage() {
    this.testImage = imageService.readImageMatrix(TEST_DATA_PATH, TEST_IMAGE_NAME);
  }

  @Test
  void baseBlur() {
    makeBaseBlur(3);
    makeBaseBlur(5);
    makeBaseBlur(7);
  }

  void makeBaseBlur(int w) {
    Size size = new Size(w, w);
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "baseblur_" + w,
            imageConvertorService.baseBlur(testImage, size)
    );
  }

  @Test
  void gaussianBlur() {
    gaussianBlur(3);
    gaussianBlur(5);
    gaussianBlur(7);
  }

  void gaussianBlur(int x) {
    Size size = new Size(x, x);
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "gaussianblur_" + x,
            imageConvertorService.gaussianBlur(testImage, size, 90, 90, 2)
    );
  }

  @Test
  void medianBlur() {
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "median_5",
            imageConvertorService.medianBlur(testImage, 5)
    );

    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "median_!1",
            imageConvertorService.medianBlur(testImage, 11)
    );
  }

  @Test
  void bilateralFilter() {
    imageService.writeImageMatrix(
            TEST_RESULT_PATH,
            "bilateral",
            imageConvertorService.bilateralFilter(testImage, 15, 80, 80, Core.BORDER_DEFAULT)
    );
  }

  @Test
  void blurAll() {
    Size size = new Size(11, 11);

    Mat baseBlured = imageConvertorService.baseBlur(testImage, size);
    imageService.writeImageMatrix(TEST_RESULT_PATH, "baseBlured", baseBlured);

    Mat gaussianBlured = imageConvertorService.gaussianBlur(baseBlured, size, 90, 90, 2);
    imageService.writeImageMatrix(TEST_RESULT_PATH, "baseBlured_gaussian", gaussianBlured);

    Mat medianBlured = imageConvertorService.medianBlur(gaussianBlured, (int) size.width);
    imageService.writeImageMatrix(TEST_RESULT_PATH, "baseBlured_gaussian_median", medianBlured);

    Mat bilateralBlured = imageConvertorService.bilateralFilter(medianBlured, 15, 80, 80, Core.BORDER_DEFAULT);
    imageService.writeImageMatrix(TEST_RESULT_PATH, "baseBlured_gaussian_median_bilateral", bilateralBlured);
  }


  @Test
  void morfEllipse() {
    List<Double> sizes = Arrays.asList(3.0, 5.0, 7.0, 9.0, 13.0, 15.0);
    sizes.forEach(size -> testMorph(size, testImage.clone(), Imgproc.MORPH_ELLIPSE, "ellipse"));
  }

  @Test
  void morfRect() {
    List<Double> sizes = Arrays.asList(3.0, 5.0, 7.0, 9.0, 13.0, 15.0);
    sizes.forEach(size -> testMorph(size, testImage.clone(), Imgproc.MORPH_RECT, "rectangle"));
  }


  private void testMorph(double size, Mat srcImage, int shape, String suffix) {
    Mat morph = Imgproc.getStructuringElement(shape, new Size(size, size));

    Mat dilated = imageConvertorService.dilate(srcImage, morph);
    imageService.writeImageMatrix(TEST_RESULT_PATH, "mrf_dilate_", dilated);

    Mat gradient = imageConvertorService.morphGradient(srcImage, morph);
    imageService.writeImageMatrix(TEST_RESULT_PATH, "mrf_gradient_", gradient);

    imageConvertorService.morphBlackHat(srcImage, morph);
    imageService.writeImageMatrix(TEST_RESULT_PATH, "mrf_blackhat_", gradient);

  }

}
