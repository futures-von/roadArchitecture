package org.von.ra.jmm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {


    }

    public static void asyncStart(String[] args) throws ExecutionException, InterruptedException {
        //        CompletableFuture<String> future = CompletableFuture
        CompletableFuture<Void> future = CompletableFuture
                .supplyAsync(() -> "hello" + Thread.currentThread().getName())
                .thenApply(s -> s + " world" + Thread.currentThread().getName())
                .thenApply(s -> {
                    System.out.println(s);
                    return s;
                }).thenAccept((s) -> System.out.println(s + "task is completed"));

//        future.join(); //

        System.out.println("main completed!");
        // 此处只输出了 main completed!
        // 原因是：异步任务的线程启动后，默认是在forkJoinPool线程池中的守护线程执行
        // 守护线程的生命周期依赖于主线程
        // 所以main执行完成，结束后守护线程也就随之结束了，也就不会i执行对应的业务逻辑了

//        System.out.println("result is：" + future.get());
    }
}
