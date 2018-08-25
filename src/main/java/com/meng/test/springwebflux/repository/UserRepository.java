package com.meng.test.springwebflux.repository;

import com.meng.test.springwebflux.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @author xindemeng
 * @datetime 2018/8/25 17:23
 */
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<User> findByUsername(String username);

    Mono<Long> deleteByUsername(String username);

}
