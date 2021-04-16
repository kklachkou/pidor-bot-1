package by.kobyzau.tg.bot.pbot.service;

import java.time.LocalDate;

public interface ElectionService {

  boolean isElectionDay(long chatId, LocalDate localDate);

  int getNumToVote(long chatId);


  boolean canUserVote(long chatId, long userId);

  int getNumVotes(long chatId, LocalDate date);

  int getNumVotes(long chatId, LocalDate date, long userId);

  void saveVote(long chatId, long calledUserId, long targetUserId);

}
