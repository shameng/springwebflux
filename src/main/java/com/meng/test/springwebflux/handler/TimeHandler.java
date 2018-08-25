package com.meng.test.springwebflux.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author xindemeng
 * @datetime 2018/8/25 0:15
 */
@Component
public class TimeHandler {

    public Mono<ServerResponse> getTimt(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("Now is " + LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss"))), String.class);
    }

    public Mono<ServerResponse> getDate(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("Now is " + LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd"))), String.class);
    }

    /**
     * MediaType.TEXT_EVENT_STREAM SSE：服务端推送（Server Send Event），在客户端发起一次请求后会保持该连接，服务器端基于该连接持续向客户端发送数据，从HTML5开始加入。
     *
     * 每秒推送一次时间
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> sendTimePerSecond(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(
                Flux.interval(Duration.ofSeconds(1))
                .map(l -> LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))),
                String.class
        );
    }

}
