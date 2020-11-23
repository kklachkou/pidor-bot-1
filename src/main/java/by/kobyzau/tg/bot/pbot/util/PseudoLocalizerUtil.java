package by.kobyzau.tg.bot.pbot.util;

import java.util.HashMap;
import java.util.Map;

public class PseudoLocalizerUtil {

  private static Map<String, String> charMap1 = null;

  static {
    charMap1 = new HashMap<>();

    // ENG
    charMap1.put("a", "å");
    charMap1.put("A", "Â");
    charMap1.put("b", "þ");
    charMap1.put("B", "8");
    charMap1.put("c", "ç");
    charMap1.put("C", "Ç");
    charMap1.put("d", "ð");
    charMap1.put("D", "Ð");
    charMap1.put("e", "ë");
    charMap1.put("E", "É");
    charMap1.put("f", "ƒ");
    charMap1.put("F", "ƒ");
    charMap1.put("g", "9");
    charMap1.put("H", "|-|");
    charMap1.put("i", "¡");
    charMap1.put("I", "Ì");
    charMap1.put("l", "|");
    charMap1.put("L", "£");
    charMap1.put("M", "/\\/\\");
    charMap1.put("n", "ñ");
    charMap1.put("N", "Ñ");
    charMap1.put("o", "õ");
    charMap1.put("O", "Ø");
    charMap1.put("p", "Þ");
    charMap1.put("P", "Þ");
    charMap1.put("q", "¶");
    charMap1.put("Q", "¶");
    charMap1.put("s", "š");
    charMap1.put("S", "§");
    charMap1.put("t", "†");
    charMap1.put("T", "†");
    charMap1.put("u", "û");
    charMap1.put("U", "Ü");
    charMap1.put("v", "\\/");
    charMap1.put("V", "\\/");
    charMap1.put("W", "\\/\\/");
    charMap1.put("x", "×");
    charMap1.put("X", "×");
    charMap1.put("y", "ÿ");
    charMap1.put("Y", "¥");
    charMap1.put("z", "ž");
    charMap1.put("Z", "Ž");

    // RU
    charMap1.put("а", "ӑ");
    charMap1.put("б", "6");
    charMap1.put("в", "8");
    charMap1.put("г", "Ӷ");
    charMap1.put("д", "д");
    charMap1.put("е", "ӗ");
    charMap1.put("ё", "ӗ");
    charMap1.put("ж", "җ");
    charMap1.put("з", "ҙ");
    charMap1.put("и", "¡");
    charMap1.put("й", "ҋ");
    charMap1.put("к", "ҝ");
    charMap1.put("л", "љ");
    charMap1.put("м", "м");
    charMap1.put("н", "\u0529");
    charMap1.put("о", "ѻ");
    charMap1.put("п", "ԥ");
    charMap1.put("р", "Þ");
    charMap1.put("с", "ç");
    charMap1.put("т", "†");
    charMap1.put("у", "ӳ");
    charMap1.put("ф", "Ѿ");
    charMap1.put("х", "x");
    charMap1.put("ц", "ҵ");
    charMap1.put("ч", "ӌ");
    charMap1.put("ш", "щ");
    charMap1.put("щ", "щ,");
    charMap1.put("ъ", "ъ");
    charMap1.put("ы", "ъ|");
    charMap1.put("ь", "ъ");
    charMap1.put("э", "ӭ");
    charMap1.put("ю", "|Ө");
    charMap1.put("я", "я");
  }

  public static String transform(String txt) {
    StringBuilder newText = new StringBuilder();
    for (int i = 0; i < txt.length(); i++) {
      String curr = txt.substring(i, i + 1);
      newText.append(charMap1.getOrDefault(curr.toLowerCase(), curr));
    }
    return newText.toString();
  }
}
