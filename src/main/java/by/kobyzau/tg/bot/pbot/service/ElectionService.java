package by.kobyzau.tg.bot.pbot.service;

import java.time.LocalDate;

public interface ElectionService {

  boolean isElectionDay(long chatId, LocalDate localDate);

  int getNumToVote(long chatId);


  boolean canUserVote(long chatId, int userId);

  int getNumVotes(long chatId, LocalDate date);

  int getNumVotes(long chatId, LocalDate date, int userId);

  void saveVote(long chatId, int calledUserId, int targetUserId);

}
