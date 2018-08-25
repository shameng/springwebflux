package com.meng.test.springwebflux;

import com.meng.test.springwebflux.model.MyEvent;
import com.meng.test.springwebflux.model.User;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

// @RunWith(SpringRunner.class)
// @SpringBootTest
public class SpringwebfluxApplicationTests {

	/**
	 * 1.这次我们使用WebClientBuilder来构建WebClient对象；
	 2.配置请求Header：Content-Type: application/stream+json；
	 3.获取response信息，返回值为ClientResponse，retrive()可以看做是exchange()方法的“快捷版”；
	 4.使用flatMap来将ClientResponse映射为Flux；
	 5.只读地peek每个元素，然后打印出来，它并不是subscribe，所以不会触发流；
	 6.上个例子中sleep的方式有点low，blockLast方法，顾名思义，在收到最后一个元素前会阻塞，响应式业务场景中慎用。
	 * @throws InterruptedException
	 */
	@Test
	public void webClientTest2() throws InterruptedException {
		WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build(); // 1
		webClient
				.get().uri("/user")
				.accept(MediaType.APPLICATION_STREAM_JSON) // 2
				.exchange() // 3
				.flatMapMany(response -> response.bodyToFlux(User.class))   // 4
				.doOnNext(System.out::println)  // 5
				.blockLast();   // 6
	}

	@Test
	public void webClientTest3() throws InterruptedException {
		WebClient webClient = WebClient.create("http://localhost:8080");
		webClient
				.get().uri("/timePerSecond")
				.accept(MediaType.TEXT_EVENT_STREAM)    // 1
				.retrieve()
				.bodyToFlux(String.class)
				.log()  // 这次用log()代替doOnNext(System.out::println)来查看每个元素；
				.take(10)   // 由于/times是一个无限流，这里取前10个，会导致流被取消；
				.blockLast();
	}

	@Test
	public void testPostMyEvents() {
		Flux<MyEvent> eventFlux = Flux.interval(Duration.ofSeconds(1))
				.map(l -> new MyEvent(System.currentTimeMillis(), "message: " + l))
				.take(5);
		WebClient webClient = WebClient.create("http://localhost:8080");
		webClient.post()
				.uri("/events")
				.contentType(MediaType.APPLICATION_STREAM_JSON)
				.body(eventFlux, MyEvent.class)
				.retrieve()
				.bodyToMono(Void.class)
				.log()
				.block();
	}

}
