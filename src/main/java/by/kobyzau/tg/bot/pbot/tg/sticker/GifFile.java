package by.kobyzau.tg.bot.pbot.tg.sticker;

import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;

import java.util.*;

public enum GifFile {
  CALCULATION(
      new Pair<>(
          "731151614",
          Arrays.asList(
              "CgACAgQAAxkBAAIDSF4wZQRLk2tiubwfVd7m5UsUyN02AAJKAgAC-zB8UQoYI8sbOIKiGAQ",
              "CgACAgQAAxkBAAIDSV4wZQR3C7XWCBHDIOeE8XXJxTsBAAISkQAC2xpkB3JS-jKeEqFSGAQ",
              "CgACAgQAAxkBAAIDSl4wZQSFCvz8ZLGp-kvtEp0C3bLZAAIFAgAC-nB8UceOpEPQRSuDGAQ",
              "CgACAgQAAxkBAAIDS14wZQRdL6pQGaiX1HBYfljG932EAAIvAgACzsN9UWXmA9EKR6U5GAQ",
              "CgACAgQAAxkBAAIDTF4wZQR3X8PTHOAf0kq58ULbn90hAAJZAgACcvF9UXraUG27YDYQGAQ")),
      new Pair<>(
          "824480319",
          Arrays.asList(
              "CgACAgQAAxkBAAICFl4wJhtAoOChEupQMVmuleTg4vk2AAJZAgACcvF9Ue1zNS7v2md_GAQ",
              "CgACAgQAAxkBAAICFV4wJhd8UM28Eg3mvYyKThqZFIHaAAIvAgACzsN9URSUQ7pJicP3GAQ",
              "CgACAgQAAxkBAAICFF4wJhSQO5XUyxNy302G_5oFLKUzAAIFAgAC-nB8UUimqoPHdAAB3BgE",
              "CgACAgQAAxkBAAICE14wJg_ICxumE3njYUkTjs208pBvAAISkQAC2xpkB3qNBro_lvoZGAQ",
              "CgACAgQAAxkBAAICEl4wJg20epqlea3l34C2d5MKV4L6AAJKAgAC-zB8UQbWItMUGWy-GAQ")));

  private final Map<String, List<String>> files;

  private List<String> defaultStickers =
      Collections.singletonList(
          "CAACAgIAAxkBAAIK6V4wJOGPrQXYAtRLNlWC1GhTgRA4AAISAAM7YCQUF_Z7M7pPlcQYBA");

  GifFile(Pair<String, List<String>>... files) {
    Map<String, List<String>> map = new HashMap<>();
    for (Pair<String, List<String>> file : files) {
      map.put(file.getLeft(), file.getRight());
    }

    this.files = map;
  }

  public List<String> getFiles(String botId) {
    return files.getOrDefault(botId, defaultStickers);
  }

  public String getRandom(String botId) {
    return CollectionUtil.getRandomValue(getFiles(botId));
  }

  public static Optional<GifFile> parseGif(String name) {
    final String fileName = StringUtil.trim(name).toUpperCase();
    return EnumSet.allOf(GifFile.class).stream().filter(s -> s.name().equals(fileName)).findFirst();
  }
}
