package com.meng.test.springwebflux.router;

import com.meng.test.springwebflux.handler.TimeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @author xindemeng
 * @datetime 2018/8/25 0:25
 */
@Configuration
public class RouterConfig {

    @Autowired
    private TimeHandler timeHandler;

    @Bean
    public RouterFunction<ServerResponse> timeRouter() {
        return RouterFunctions.route(RequestPredicates.GET("/time"), req -> timeHandler.getTimt(req))
                .andRoute(RequestPredicates.GET("/date"), timeHandler::getDate)
                .andRoute(RequestPredicates.GET("/timePerSecond"), timeHandler::sendTimePerSecond);
    }
}
