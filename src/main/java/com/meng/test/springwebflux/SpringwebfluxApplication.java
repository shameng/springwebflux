package com.meng.test.springwebflux;

import com.meng.test.springwebflux.model.MyEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;

@SpringBootApplication
public class SpringwebfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringwebfluxApplication.class, args);
	}

	/**
	 * 1.对于复杂的Bean只能通过Java Config的方式配置，这也是为什么Spring3之后官方推荐这种配置方式的原因，这段代码可以放到配置类中，本例我们就直接放到启动类WebFluxDemoApplication了；
	 MongoOperations提供对MongoDB的操作方法，由Spring注入的mongo实例已经配置好，直接使用即可；
	 2.CommandLineRunner也是一个函数式接口，其实例可以用lambda表达；
	 3.如果有，先删除collection，生产环境慎用这种操作；
	 4.创建一个记录个数为10的capped的collection，容量满了之后，新增的记录会覆盖最旧的。
	 * @param mongo
	 * @return
	 */
	@Bean   // 1
	public CommandLineRunner initData(MongoOperations mongo) {  // 2
		return (String... args) -> {    // 3
			mongo.dropCollection(MyEvent.class);    // 4
			mongo.createCollection(MyEvent.class, CollectionOptions.empty().size(100000).capped()); // 5
		};
	}
}
