package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;

@RequiredArgsConstructor
public class SimpleStickerActionChecker implements BotActionChecker {

  private final long chatId;
  private final StickerType stickerType;

  @Override
  public void check(BotAction<?> botAction) {
    Assert.assertNotNull(botAction);
    boolean isMatch =
        stickerType.getStickers().stream()
            .map(s -> "SimpleStickerBotAction{" + "chatId=" + chatId + ", sendSticker=" + s + '}')
            .anyMatch(botAction.toString()::equals);
    Assert.assertTrue("No Sticker " + stickerType + " with chat " + chatId, isMatch);
  }
}
