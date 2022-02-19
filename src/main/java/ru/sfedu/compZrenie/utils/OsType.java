package ru.sfedu.compZrenie.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.sfedu.compZrenie.Constants.OPENCV_PATH_LINUX;
import static ru.sfedu.compZrenie.Constants.OPENCV_PATH_WINDOWS;

@Getter
public enum OsType {
  MACOS(Arrays.asList("mac", "darwin"), ""),
  WINDOWS(List.of("win"), OPENCV_PATH_WINDOWS),
  LINUX(List.of("nux"), OPENCV_PATH_LINUX),
  OTHER(new ArrayList<>(), "");

  private final List<String> classifiers;
  private final String constantPath;

  OsType(List<String> classifiers, String constantPath) {
    this.classifiers = classifiers;
    this.constantPath = constantPath;
  }

  public String getConstantPath() throws Exception {
    if (constantPath.isEmpty()) {
      throw new Exception("OS \"%s\" doesn't supports!".formatted(this.toString()));
    }
    return constantPath;
  }

  public static OsType getOperatingSystemType(String type) {
    return Arrays.stream(values())
            .filter(osType -> osType.getClassifiers().stream().anyMatch(type::contains))
            .findAny()
            .orElse(OTHER);
  }
}
