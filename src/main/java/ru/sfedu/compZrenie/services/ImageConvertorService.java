package ru.sfedu.compZrenie.services;

import lombok.extern.log4j.Log4j2;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.List;

@Log4j2
public class ImageConvertorService {

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
    Point center = new Point(img.width() >> 1, img.height() >> 2);
    Mat rotationMat = Imgproc.getRotationMatrix2D(
            center,
            angle,
            shaving ? 1 : dImageScale(img)
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

}
