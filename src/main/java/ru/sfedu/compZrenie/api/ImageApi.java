package ru.sfedu.compZrenie.api;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.compZrenie.services.OsService;

@Log4j2
public class ImageApi {

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
}
