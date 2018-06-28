package daggerok.app;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.time.LocalDateTime.now;
//tag::content[]
@Log4j2
@Service
public class MessageListener {

  @KafkaListener(topics = "messages")
  public void on(final ConsumerRecord<Object, Object> message) {
    log.info("received message: {}", message.value());
    Mono.just(new Message().setAt(now())
                           .setBody(message.value().toString()))
        .subscribe(msg -> log.info("saving message object: '{}'", msg));
  }
}
//end::content[]
