package com.meng.test.springwebflux.repository;

import com.meng.test.springwebflux.model.MyEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

/**
 * @author xindemeng
 * @datetime 2018/8/25 21:46
 */
public interface MyEventRepository extends ReactiveMongoRepository<MyEvent, Long> {

    @Tailable
    public Flux<MyEvent> findBy();
}
