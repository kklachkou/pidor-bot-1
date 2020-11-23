package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.DigestUsage;
import by.kobyzau.tg.bot.pbot.model.DigestUsageType;
import by.kobyzau.tg.bot.pbot.repository.digestusage.DigestUsageRepository;
import by.kobyzau.tg.bot.pbot.service.impl.DigestUsageServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class DigestUsageServiceTest {

  private final DigestUsageType type = DigestUsageType.COMMON;

  @Mock private DigestUsageRepository repository;
  @InjectMocks private DigestUsageService service = new DigestUsageServiceImpl();

  @Test
  public void getTopDigestsByType_noDigests_emptyList() {
    // given
    doReturn(Collections.emptyList()).when(repository).findByType(type.name());

    // when
    List<String> result = service.getTopDigestsByType(type, 2);

    // then
    assertEquals(Collections.emptyList(), result);
  }

  @Test
  public void getTopDigestsByType_oneDigests_singleList() {
    // given

    doReturn(Collections.singletonList(getDigest("1", LocalDate.of(2020, 1, 1))))
        .when(repository)
        .findByType(type.name());

    // when
    List<String> result = service.getTopDigestsByType(type, 2);

    // then
    assertEquals(Collections.singletonList("1"), result);
  }

  @Test
  public void getTopDigestsByType_multipleDigests() {
    // given
    DigestUsage oldDigest1 = getDigest("1", LocalDate.of(2020, 1, 1));
    DigestUsage oldDigest2 = getDigest("2", LocalDate.of(2020, 2, 1));
    DigestUsage newDigest1 = getDigest("3", LocalDate.of(2020, 3, 1));
    DigestUsage newDigest2 = getDigest("4", LocalDate.of(2020, 4, 1));
    doReturn(Arrays.asList(oldDigest2, oldDigest1, newDigest2, newDigest1))
        .when(repository)
        .findByType(type.name());

    // when
    List<String> result = service.getTopDigestsByType(type, 3);

    // then
    assertEquals(
        Arrays.asList(oldDigest2.getDigest(), newDigest1.getDigest(), newDigest2.getDigest()),
        result);
  }

  @Test
  public void getTopDigestsByType_sameDate() {
    // given
    DigestUsage digest1 = getDigest("1", LocalDate.of(2020, 2, 1));
    DigestUsage digest2 = getDigest("2", LocalDate.of(2020, 2, 1));
    doReturn(Arrays.asList(digest1, digest2)).when(repository).findByType(type.name());

    // when
    List<String> result = service.getTopDigestsByType(type, 3);

    // then
    assertEquals(Arrays.asList(digest1.getDigest(), digest2.getDigest()), result);
  }

  private DigestUsage getDigest(String digest, LocalDate date) {
    return new DigestUsage(type.name(), digest, date);
  }
}
