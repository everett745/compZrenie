package ru.sfedu.compZrenie.services;

import lombok.extern.log4j.Log4j2;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import java.util.AbstractMap.SimpleEntry;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class ImageConvertorService {

  public static final int MAX_COLOR = 255;
  private static ImageConvertorService INSTANCE;

  public static ImageConvertorService getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ImageConvertorService();
    }
    return INSTANCE;
  }

  private ImageConvertorService() {
    OsService.load();
  }

  private double dImageScale(Mat img) {
    double d = Math.sqrt(img.width()*img.width() + img.height()*img.height());
    return Math.min(img.width(), img.height()) / d;
  }

  private double dImageScale2(Mat img) {
    double d = Math.sqrt(img.width()*img.width() + img.height()*img.height());
    return Math.min(img.width(), img.height()) / d;
  }

  public Mat sobelX(Mat mat) {
    Mat converted = new Mat();
    Imgproc.Sobel(mat, converted, CvType.CV_32F, 1, 0);
    return converted;
  }

  public Mat sobelY(Mat mat) {
    Mat converted = new Mat();
    Imgproc.Sobel(mat, converted, CvType.CV_32F, 0, 1);
    return converted;
  }

  public Mat laplacian(Mat mat) {
    Mat dstLaplacian = new Mat();
    Imgproc.Laplacian(mat, dstLaplacian, CvType.CV_32F);
    Mat absLaplacian = new Mat();
    Core.convertScaleAbs(dstLaplacian, absLaplacian);
    return absLaplacian;
  }

  public Mat mirrorV(Mat mat) {
    Mat converted = new Mat();
    Core.flip(mat, converted,0);
    return converted;
  }

  public Mat mirrorH(Mat mat) {
    Mat converted = new Mat();
    Core.flip(mat, converted,1);
    return converted;
  }

  public Mat mirrorHV(Mat mat) {
    Mat converted = new Mat();
    Core.flip(mat, converted,-1);
    return converted;
  }

  public Mat concatH(List<Mat> images) {
    Mat dst = new Mat();
    Core.hconcat(images, dst);
    return dst;
  }

  public Mat concatV(List<Mat> images) {
    Mat dst = new Mat();
    Core.vconcat(images, dst);
    return dst;
  }

  public Mat repeat(Mat image, int ny, int nx) {
    Mat dst = new Mat();
    Core.repeat(
            image,
            ny > 0 ? ny : 1,
            nx > 0 ? nx : 1,
            dst
    );
    return dst;
  }

  public Mat resize(Mat mat, int xSize, int ySize) {
    Mat dst = new Mat();
    Imgproc.resize(mat, dst, new Size(xSize, ySize));
    return dst;
  }


  /**
   * @param angle image rotation angle
   * @param shaving crop the image if it is larger than the original size
   */
  public Mat rotation(Mat img, int angle, boolean shaving) {
    Point center = new Point(img.width() / 2, img.height() / 2);
    Mat rotationMat = Imgproc.getRotationMatrix2D(
            center,
            angle,
            shaving ? 1 : dImageScale2(img)
    );
    Mat dst = new Mat();

    Imgproc.warpAffine(
            img, dst, rotationMat,
            new Size(img.width(), img.height()),
            Imgproc.INTER_LINEAR,
            Core.BORDER_TRANSPARENT,
            new Scalar(0, 0, 0, 255)
    );

    return dst;
  }

  /**
   * @param topLeft top left corner of the image
   * @param topRight top right corner of the image
   * @param bottomLeft bottom left corner of the image
   * @param bottomRight bottom right corner of the image
   */
  public Mat perspective(Mat img, Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
    Mat dst = new Mat();

    MatOfPoint2f inputCorners = new MatOfPoint2f(
            new Point(0, 0),
            new Point(img.width() - 1,0),
            new Point(0,img.height() - 1),
            new Point(img.width()-1,img.height() - 1)
    );

    MatOfPoint2f outputCorners = new MatOfPoint2f(topLeft, topRight, bottomLeft, bottomRight);

    Mat M = Imgproc.getPerspectiveTransform(inputCorners, outputCorners);

    Imgproc.warpPerspective(img, dst, M, new Size(img.width(), img.height()));
    return dst;
  }

  public Mat baseBlur(Mat src, Size kSize) {
    Mat dst = new Mat();
    Imgproc.blur(src, dst, kSize, new Point(-1, -1));
    return dst;
  }

  public Mat gaussianBlur(Mat src, Size ksize, double sigmaX, double sigmaY, int borderType) {
    Mat dst = new Mat();
    Imgproc.GaussianBlur(src, dst, ksize, sigmaX, sigmaY, borderType);
    return dst;
  }

  public Mat medianBlur(Mat src, int ksize) {
    Mat dst = new Mat();
    Imgproc.medianBlur(src, dst, ksize);
    return dst;
  }

  public Mat bilateralFilter(Mat src, int d, double sigmaColor, double sigmaSpace, int borderType) {
    Mat dst = new Mat();
    Imgproc.bilateralFilter(src, dst, d, sigmaColor, sigmaSpace, borderType);
    return dst;
  }

  public Mat dilate(Mat src, Mat morphEllipse) {
    Mat dst = src.clone();
    Imgproc.dilate(src, dst, morphEllipse);
    return dst;
  }

  public Mat morphGradient(Mat src, Mat morph) {
    Mat dst = src.clone();
    Imgproc.morphologyEx(src, dst, Imgproc.MORPH_GRADIENT, morph);
    return dst;
  }

  public Mat morphBlackHat(Mat src, Mat morph) {
    Mat dst = src.clone();
    Imgproc.morphologyEx(src, dst, Imgproc.MORPH_BLACKHAT, morph);
    return dst;
  }

  public Scalar getRandomColor() {
    Scalar fillColor;
    Random random = new Random();
    fillColor = new Scalar(
            random.nextDouble(0, MAX_COLOR),
            random.nextDouble(0, MAX_COLOR),
            random.nextDouble(0, MAX_COLOR)
    );
    return fillColor;
  }

  public Mat fillFlood(Mat srcImage,
                       Point startPoint,
                       Scalar topColorBorder,
                       Scalar bottomColorBorder) {
    return fillFlood(srcImage, startPoint, null, topColorBorder, bottomColorBorder);
  }

  public Mat fillFlood(Mat srcImage,
                       Point startPoint,
                       Scalar fillColor,
                       Scalar topColorBorder,
                       Scalar bottomColorBorder) {
    if (fillColor == null) {
      fillColor = getRandomColor();
    }
    Imgproc.floodFill(
            srcImage,
            new Mat(),
            startPoint,
            fillColor,
            new Rect(),
            topColorBorder,
            bottomColorBorder,
            Imgproc.FLOODFILL_FIXED_RANGE
    );
    return srcImage;
  }

  public Mat pyramidDown(Mat srcImage, int amount) {
    Mat result = new Mat();
    if (amount > 0) {
      Imgproc.pyrDown(srcImage, result);
    }
    for (int i = 1; i < amount; i++) {
      Imgproc.pyrDown(result, result);
    }
    return result;
  }

  public Mat pyramidUp(Mat srcImage, int amount) {
    Mat result = new Mat();
    if (amount > 0) {
      Imgproc.pyrUp(srcImage, result);
    }
    for (int i = 1; i < amount; i++) {
      Imgproc.pyrUp(result, result);
    }
    return result;
  }

  public Mat makeImageGray(Mat srcImage) {
    Mat grayImage = new Mat();
    Imgproc.cvtColor(srcImage, grayImage, Imgproc.COLOR_BGR2GRAY);
    return grayImage;
  }

  public Mat denoisedImage(Mat srcImage) {
    Mat denoisedImage = new Mat();
    Photo.fastNlMeansDenoising(srcImage, denoisedImage);
    return denoisedImage;
  }

  public Mat histogramEqualization(Mat srcImage) {
    Mat histogramEqualizationImage = new Mat();
    Imgproc.equalizeHist(srcImage, histogramEqualizationImage);
    return histogramEqualizationImage;
  }

  public Mat morphologicalOpening(Mat srcImage, double width, double height) {
    Mat morphologicalOpeningImage = new Mat();
    Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(width, height));
    Imgproc.morphologyEx(srcImage, morphologicalOpeningImage,
            Imgproc.MORPH_RECT, kernel);
    return morphologicalOpeningImage;
  }

  public Mat subtractImages(Mat firstImage, Mat secondImage) {
    Mat subtractImage = new Mat();
    Core.subtract(firstImage, secondImage, subtractImage);
    return subtractImage;
  }

  public SimpleEntry<Mat, Double> thresholdImage(Mat srcImage, double thresh, double maxValue) {
    Mat thresholdImage = new Mat();
    double threshold = Imgproc.threshold(srcImage, thresholdImage, thresh, maxValue,
            Imgproc.THRESH_OTSU);
    return new SimpleEntry<>(thresholdImage, threshold);
  }

  public Mat edgeImage(Mat srcImage, double threshold) {
    Mat edgeImage = new Mat();
    srcImage.convertTo(srcImage, CvType.CV_8U);
    Imgproc.Canny(srcImage, edgeImage, threshold, threshold * 3, 3, true);
    return edgeImage;
  }

  public Mat dilateImage(Mat srcImage) {
    Mat dilatedImage = new Mat();
    Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
    Imgproc.dilate(srcImage, dilatedImage, kernel);
    return dilatedImage;
  }

  public List<MatOfPoint> getContours(Mat srcImage) {
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(srcImage, contours, new Mat(), Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE);
    contours.sort(Collections.reverseOrder(Comparator.comparing(Imgproc::contourArea)));
    return contours;
  }

  public List<Mat> findRectangles(Mat image, double width, double height) {
    int maxIndent = 10;

    Mat grayImage = makeImageGray(image);
    Mat denoisedImage = denoisedImage(grayImage);
    Mat histogramEqualizedImage = histogramEqualization(denoisedImage);
    Mat morphologicalOpenedImage = morphologicalOpening(histogramEqualizedImage, 5, 5);
    Mat subtractedImage = subtractImages(histogramEqualizedImage, morphologicalOpenedImage);
    SimpleEntry<Mat, Double> threscholdedEntry = thresholdImage(subtractedImage, 50, 255);
    Mat thresholdImage = threscholdedEntry.getKey();
    double threshold = threscholdedEntry.getValue();
    thresholdImage.convertTo(thresholdImage, CvType.CV_16SC1);
    Mat edgeImage = edgeImage(thresholdImage, threshold);
    Mat dilatedImage = dilateImage(edgeImage);
    List<MatOfPoint> contours = getContours(dilatedImage);
    return contours.stream().map(contour -> {
              log.debug(Imgproc.contourArea(contour));
              MatOfPoint2f point2f = new MatOfPoint2f();
              MatOfPoint2f approxContour2f = new MatOfPoint2f();
              MatOfPoint approxContour = new MatOfPoint();
              contour.convertTo(point2f, CvType.CV_32FC2);
              double arcLength = Imgproc.arcLength(point2f, true);
              Imgproc.approxPolyDP(point2f, approxContour2f, 0.03 * arcLength, true);
              approxContour2f.convertTo(approxContour, CvType.CV_32S);
              Rect rect = Imgproc.boundingRect(approxContour);
              return image.submat(rect);
            })
            .filter(mat -> Math.abs(mat.height() - height) < maxIndent && Math.abs(mat.width() - width) < maxIndent)
            .collect(Collectors.toList());
  }

}
