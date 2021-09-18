package by.kobyzau.tg.bot.pbot.artifacts.entity;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_artifact")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserArtifact {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long userId;
  private long chatId;
  private LocalDate date;

  @Enumerated(EnumType.STRING)
  private ArtifactType artifactType;
}
