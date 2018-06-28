package daggerok;

//import daggerok.app.EmbeddedKafkaConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

//tag::content[]
@Configuration
@RequiredArgsConstructor
class WebfluxRoutesConfig {

  static final ParameterizedTypeReference<Map<String, String>> sendMessageRequestType
      = new ParameterizedTypeReference<Map<String, String>>() {};

  final KafkaTemplate<Object, Object> kafka;

  @Bean
  HandlerFunction<ServerResponse> sendMessageHandler() {
    return request ->
        ok().body(request.bodyToMono(sendMessageRequestType)
                         .map(it -> it.getOrDefault("message", ""))
                         .filter(it -> !it.trim().isEmpty())
                         .doOnNext(message -> kafka.send("messages", message))
                         .map(s -> "message sent.")
                         .flatMap(Mono::just), Object.class);
  }
  //end::content[]

  @Bean
  HandlerFunction<ServerResponse> fallbackHandler() {
    return request ->
        ok().body(Mono.just(asList(
            "POST api -> /",
            "GET    * -> /**"
        )), List.class);
  }
  //tag::content[]

  @Bean
  RouterFunction routes(final HandlerFunction<ServerResponse> fallbackHandler) {
    return
        route(
            POST("/"),
            sendMessageHandler()
        ).andOther(
            route(
                GET("/**"),
                fallbackHandler
            )
        )
        ;
  }
}
//end::content[]

@SpringBootApplication
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
