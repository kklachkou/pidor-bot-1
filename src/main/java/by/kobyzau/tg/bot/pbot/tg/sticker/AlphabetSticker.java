package by.kobyzau.tg.bot.pbot.tg.sticker;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import by.kobyzau.tg.bot.pbot.util.StringUtil;

@Component
public class AlphabetSticker {

  private final Map<String, String> stickers;

  public AlphabetSticker() {
    this.stickers = new LinkedHashMap<>();
    this.stickers.put(" ", "CAACAgIAAxUAAV6ULM5f0j1-Bpf5aRzBm3enCfRnAAILDgACY4tGDBlhUkZAcOODGAQ");
    this.stickers.put("а", "CAACAgIAAxUAAV6ULM4qQfqJmjNIQUU8WPgilpQ3AAJ7DQACY4tGDNajF2mriiNJGAQ");
    this.stickers.put("б", "CAACAgIAAxUAAV6ULM7qydsM7R4ksePBi2Z0QUVSAAJ8DQACY4tGDKmgzlQogqlXGAQ");
    this.stickers.put("в", "CAACAgIAAxUAAV6ULM6DSyI0sGeN83AzRp9fIHTRAAJ9DQACY4tGDLkd3_zoyY95GAQ");
    this.stickers.put("г", "CAACAgIAAxUAAV6ULM6yV_H8ioR336Vo1b408opQAAJ-DQACY4tGDIGI9uNJOZqKGAQ");
    this.stickers.put("д", "CAACAgIAAxUAAV6ULM5sFbJ504z-Qn_BnsDd6LHGAAJ_DQACY4tGDPNZxwIjRYrvGAQ");
    this.stickers.put("е", "CAACAgIAAxUAAV6ULM5gQwbt4QMrXJ8j5kXVFnQ6AAKADQACY4tGDJoTPSWqzrgXGAQ");
    this.stickers.put("ё", "CAACAgIAAxUAAV6ULM6vvJNLHWu2J2KjS7oc5UWqAAKBDQACY4tGDLevbnVx5p8XGAQ");
    this.stickers.put("ж", "CAACAgIAAxUAAV6ULM6eva_0cku0IBNY7x4XhyDiAAKCDQACY4tGDJZR0mty7PIpGAQ");
    this.stickers.put("з", "CAACAgIAAxUAAV6ULM4TXDrmVwe20EDA2rOjqhoSAAKDDQACY4tGDEm4H-bpL8PMGAQ");
    this.stickers.put("и", "CAACAgIAAxUAAV6ULM5yMVG3Tg0y9xEVoHHPtqB5AAKEDQACY4tGDPEWocnDtHmSGAQ");
    this.stickers.put("й", "CAACAgIAAxUAAV6ULM7-WNjJi6WFoQeur54ydbOgAAKFDQACY4tGDEwcVEHE4TKUGAQ");
    this.stickers.put("к", "CAACAgIAAxUAAV6ULM6aiYfaW0Gy9RUTpyi3RFuhAAKGDQACY4tGDBPZFHXs_euyGAQ");
    this.stickers.put("л", "CAACAgIAAxUAAV6ULM6HG83AvGMk1XI8eOC97QNNAAKHDQACY4tGDHN7uWjWvba3GAQ");
    this.stickers.put("м", "CAACAgIAAxUAAV6ULM4G6uHW4Wz55TzcHz6svNBOAAKIDQACY4tGDAjMMJ6v0enXGAQ");
    this.stickers.put("н", "CAACAgIAAxUAAV6ULM7zvGbMiMiVlxRfR-raMylkAAKJDQACY4tGDFLeBYv8CfYdGAQ");
    this.stickers.put("о", "CAACAgIAAxUAAV6ULM5pMM1YbdJlTq4OLGnAnukhAAKKDQACY4tGDEQBrK-n6JdoGAQ");
    this.stickers.put("п", "CAACAgIAAxUAAV6ULM6gkZIs2kU9kPEVp3bWQ_EbAAKLDQACY4tGDLURVqEpy1DFGAQ");
    this.stickers.put("р", "CAACAgIAAxUAAV6ULM6Xift3a1Sfff5L1BONzg60AAKMDQACY4tGDOj_i12o9aFhGAQ");
    this.stickers.put("с", "CAACAgIAAxUAAV6ULM6oFAitNb5Jotq8H2tskd2VAAKNDQACY4tGDKea-h2E9mgLGAQ");
    this.stickers.put("т", "CAACAgIAAxUAAV6ULM51tcKjy_W-XKaxd8-sOID2AAKODQACY4tGDBB24dV3ckCUGAQ");
    this.stickers.put("у", "CAACAgIAAxUAAV6ULM6NMNJJ8m9msr7MPGfWHpgaAAKPDQACY4tGDKiXPSnLlBV9GAQ");
    this.stickers.put("ф", "CAACAgIAAxUAAV6ULM75jFn91h_qQuf4CH7e2uz3AAKQDQACY4tGDD-YIxuszqNFGAQ");
    this.stickers.put("х", "CAACAgIAAxUAAV6ULM6UpLVyrzTR1k7kczFYpCktAAKRDQACY4tGDEQ9aPyaB4duGAQ");
    this.stickers.put("ц", "CAACAgIAAxUAAV6ULM6rjBCelDLf0MFLDNngsJxAAAKSDQACY4tGDKKsWdcBQeymGAQ");
    this.stickers.put("ч", "CAACAgIAAxUAAV6ULM63JsKuUz65YyiWvjpC-GUkAAKTDQACY4tGDLCdA_9OC34tGAQ");
    this.stickers.put("ш", "CAACAgIAAxUAAV6ULM55LuSUw_jVpAJ2euBhmhtVAAKUDQACY4tGDN_rdK1awDcjGAQ");
    this.stickers.put("щ", "CAACAgIAAxUAAV6ULM6I-DP09wNi1pX82_l_uQvjAAKVDQACY4tGDK8EtuBxqArcGAQ");
    this.stickers.put("ъ", "CAACAgIAAxUAAV6ULM6IxCoAAS2usFzNjd9QPz0fagAClg0AAmOLRgxkrjuRFM6f0hgE");
    this.stickers.put("ы", "CAACAgIAAxUAAV6ULM5YU0Vc0KFH3vGvW1E4jks6AAKXDQACY4tGDEkl5QWcRUVsGAQ");
    this.stickers.put("ь", "CAACAgIAAxUAAV6ULM71eoH3cZp98-LPuBfihHB0AAKYDQACY4tGDJoXKiv6FCABGAQ");
    this.stickers.put("э", "CAACAgIAAxUAAV6ULM4Du7O--MwultK8b1LyiQlfAAKiDQACY4tGDLLUU3Y5NDxfGAQ");
    this.stickers.put("ю", "CAACAgIAAxUAAV6ULM7Fxd9vBXE_O9gCk5PXVLIDAAKZDQACY4tGDEMEHkmBHrSMGAQ");
    this.stickers.put("я", "CAACAgIAAxUAAV6ULM59EOOr0kkAAVR51_HDRbB3EQACmg0AAmOLRgymWWhfIJhd8hgE");
    this.stickers.put("u", "CAACAgIAAxUAAV6ULM6ZHQd_oKD6M2pYdENipMrbAAKbDQACY4tGDEYI8DcGENUAARgE");
  }

  public Optional<String> getSticker(String chat) {
    if (StringUtil.isBlank(chat)) {
      return Optional.empty();
    }
    return Optional.ofNullable(stickers.get(chat.trim().toLowerCase()));
  }

  public Set<String> getKeys() {
    return this.stickers.keySet();
  }

}
