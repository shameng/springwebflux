package com.meng.test.springwebflux.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author xindemeng
 * @datetime 2018/8/22 22:12
 */
@RestController
public class HelloController {

    // @GetMapping("/hello")
    // public String hello() {
    //     return "Welcome to reactive world ~";
    // }

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Welcome to reactive world ~");
    }
}
