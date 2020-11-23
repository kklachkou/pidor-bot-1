package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.DigestUsage;
import by.kobyzau.tg.bot.pbot.model.DigestUsageType;
import by.kobyzau.tg.bot.pbot.repository.digestusage.DigestUsageRepository;
import by.kobyzau.tg.bot.pbot.service.DigestUsageService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DigestUsageServiceImpl implements DigestUsageService {

  @Autowired private DigestUsageRepository digestUsageRepository;

  @Override
  public void saveDigestUsage(DigestUsageType type, String digest) {
    digestUsageRepository.create(new DigestUsage(type.name(), digest, DateUtil.now()));
  }

  @Override
  public List<String> getTopDigestsByType(DigestUsageType type, int top) {
    return digestUsageRepository.findByType(type.name()).stream()
        .sorted(Comparator.comparing(DigestUsage::getDate).reversed())
        .limit(top)
        .sorted(Comparator.comparing(DigestUsage::getDate))
        .map(DigestUsage::getDigest)
        .collect(Collectors.toList());
  }
}
