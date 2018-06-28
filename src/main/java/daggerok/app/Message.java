package daggerok.app;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Message {

  String body;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  LocalDateTime at;
}
