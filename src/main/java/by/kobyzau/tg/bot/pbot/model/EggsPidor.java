package by.kobyzau.tg.bot.pbot.model;

import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;

import java.util.Arrays;
import java.util.Optional;

/** Egg Pidor Sticker Set */
public enum EggsPidor {
  NIKITA(451337639, StickerType.PIDOR_NIKITA),
  VOVA(279424529, StickerType.PIDOR_VOVA),
  SANYA(562849728, StickerType.PIDOR_SANYA),
  FIL(396782272, StickerType.PIDOR_FIL),
  SERGEY_SH(306519117, StickerType.PIDOR_SERGEY_JOB),
  SERGEY_J(314157031, StickerType.PIDOR_SHOHAN),
  DIMA(261011580, StickerType.PIDOR_DIMKA),
  MAX(484036310, StickerType.PIDOR_MAX),
  ARTUR(607995172, StickerType.PIDOR_ARTYR);

  private final int userId;
  private final StickerType stickerType;

  EggsPidor(int userId, StickerType stickerType) {
    this.userId = userId;
    this.stickerType = stickerType;
  }

  public static Optional<EggsPidor> parseById(int id) {
    return Arrays.stream(values()).filter(s -> s.getUserId() == id).findFirst();
  }

  public StickerType getStickerType() {
    return stickerType;
  }

  public int getUserId() {
    return userId;
  }
}
