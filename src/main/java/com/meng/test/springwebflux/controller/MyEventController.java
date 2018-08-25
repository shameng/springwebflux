package com.meng.test.springwebflux.controller;

import com.meng.test.springwebflux.model.MyEvent;
import com.meng.test.springwebflux.repository.MyEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author xindemeng
 * @datetime 2018/8/25 21:50
 */
@RestController
@RequestMapping("/events")
public class MyEventController {

    @Autowired
    private MyEventRepository myEventRepository;

    @PostMapping(consumes = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<Void> loadEvents(@RequestBody Flux<MyEvent> events) {
        return myEventRepository.insert(events).then();
    }

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<MyEvent> getEvents() {
        return myEventRepository.findBy();
    }
}
