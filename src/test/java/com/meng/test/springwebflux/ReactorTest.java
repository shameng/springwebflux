package com.meng.test.springwebflux;

import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author xindemeng
 * @datetime 2018/9/8 18:55
 */
public class ReactorTest {

    @Test
    public void test1() {
        Flux.just(1, 2, 3, 4, 5, 6).subscribe(System.out::print);
        System.out.println();
        Mono.just(1).subscribe(System.out::print);

        System.out.println();

        Flux.just(1, 2, 3, 4, 5, 6).subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("Completed!"));

        Mono.error(new Exception("some error")).subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("Completed!")
        );
    }

    private Flux<Integer> generateFluxFrom1To6() {
        return Flux.just(1, 2, 3, 4, 5, 6);
    }
    private Mono<Integer> generateMonoWithError() {
        return Mono.error(new Exception("some error"));
    }

    @Test
    public void testViaStepVerifier() {
        StepVerifier.create(generateFluxFrom1To6())
                .expectNext(1, 2, 3, 4, 5, 6, 7)
                .expectComplete()
                .verify();

        StepVerifier.create(generateMonoWithError())
                .expectErrorMessage("some error")
                .verify();
    }

    @Test
    public void testViaStepVerifier2() {
        StepVerifier.create(Flux.range(1, 6)
        .map(i -> i * i))
                .expectNext(1, 4, 9, 16, 25, 36)
                .verifyComplete();
    }

    @Test
    public void testViaStepVerifier3() {
        // 1. StepVerifier对于每一个字符串s，将其拆分为包含一个字符的字符串流；
        // 2. 对每个元素延迟100ms；
        // 3. 对每个元素进行打印（注doOnNext方法是“偷窥式”的方法，不会消费数据流）；
        // 4. 验证是否发出了8个元素。
        // 打印结果为mfolnuox，原因在于各个拆分后的小字符串都是间隔100ms发出的，因此会交叉。ete();
        StepVerifier.create(
                Flux.just("flux", "mono")
                        .flatMap(s -> Flux.fromArray(s.split("\\s*"))   // 1
                                .delayElements(Duration.ofMillis(100))) // 2
                        .doOnNext(System.out::print)) // 3
                .expectNextCount(8) // 4
                .verifyComplete();
    }

    private Flux<String> getZipDescFlux() {
        String desc = "Zip two sources together, that is to say wait for all the sources to emit one element and combine these elements once into a Tuple2.";
        return Flux.fromArray(desc.split("\\s+"));  // 1
    }

    @Test
    public void testSimpleOperators() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);  // 2
        Flux.zip(
                getZipDescFlux(),
                Flux.interval(Duration.ofMillis(2000)))  // 3
                .subscribe(t -> System.out.println(t), null, countDownLatch::countDown);    // 4
        countDownLatch.await(100, TimeUnit.SECONDS);     // 5
    }

    private String getStringSync() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello, Reactor!";
    }
    @Test
    public void testSyncToAsync() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mono.fromCallable(this::getStringSync)    // 1
                .subscribeOn(Schedulers.elastic())  // 2
                .subscribe(System.out::println, null, countDownLatch::countDown);
        System.out.println(666);
        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testErrorHandling() {
        // Flux.range(1, 6)
        //         .map(i -> 10/(i-3)) // 1
        //         .map(i -> i*i)
        //         .subscribe(System.out::println, System.err::println);
        //
        // Flux.range(1, 6)
        //         .map(i -> 10/(i-3))
        //         .onErrorReturn(0)   // 1
        //         .map(i -> i*i)
        //         .subscribe(System.out::println, System.err::println);
        //
        // Flux.range(1, 6)
        //         .map(i -> 10/(i-3))
        //         .doOnError(Throwable::printStackTrace)
        //         .onErrorResume(e -> Mono.just(new Random().nextInt(6))) // 提供新的数据流
        //         .map(i -> i*i)
        //         .subscribe(System.out::println, System.err::println);

        // Flux.range(1, 6)
        //         .map(i -> 10/(i-3))
        //         .onErrorMap(original -> new RuntimeException("SLA exceeded", original)) // 2
        //         .subscribe(System.out::println, System.err::println);

        // 1. 用LongAdder进行统计；
        // 2. doFinally用SignalType检查了终止信号的类型；
        // 3. 如果是取消，那么统计数据自增；
        // 4. take(1)能够在发出1个元素后取消流
        // LongAdder statsCancel = new LongAdder();    // 1
        // Flux.just("foo", "bar")
        //         .doFinally(type -> {
        //             if (type == SignalType.CANCEL)  // 2
        //                 statsCancel.increment();  // 3
        //         })
        //         .take(1)   // 4
        //         .subscribe(System.out::println);
        // System.out.println(statsCancel.sum());

        Flux.range(1, 6)
                .map(i -> 10 / (3 - i))
                .retry(1)
                .subscribe(System.out::println, System.err::println);
        try {
            Thread.sleep(100);  // 确保序列执行完
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 回压
     */
    @Test
    public void testBackpressure() {
        Flux.range(1, 6)    // 1
                .doOnRequest(n -> System.out.println("Request " + n + " values..."))    // 2
                .subscribe(new BaseSubscriber<Integer>() {  // 3
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) { // 4
                        System.out.println("Subscribed and make a request...");
                        request(2); // 5
                    }

                    @Override
                    protected void hookOnNext(Integer value) {  // 6
                        try {
                            TimeUnit.SECONDS.sleep(1);  // 7
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Get value [" + value + "]");    // 8
                        // request(1); // 9
                    }
                });
    }

    @Test
    public void testDelayElements() {
        Flux.range(0, 10)
                .delayElements(Duration.ofMillis(1000))
                .log()
                .blockLast();
    }

    @Test
    public void testParallelFlux() throws InterruptedException {
        Flux.range(1, 10)
                .publishOn(Schedulers.parallel())
                .log().subscribe();
        TimeUnit.MILLISECONDS.sleep(10);
    }
    @Test
    public void testParallelFlux2() throws InterruptedException {
        Flux.range(1, 10)
                .parallel(2)
                .runOn(Schedulers.parallel())
//                .publishOn(Schedulers.parallel())
                .log()
                .subscribe(System.out::println);

        TimeUnit.MILLISECONDS.sleep(10);
    }
}
