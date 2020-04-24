package com.fiveoneofly.cgw.calm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CalmDispatcher {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = 10;//线程池的基本大小
    private static final int MAXIMUM_POOL_SIZE = 10;//线程池核心容量   使用了无界的任务队列这个参数就没什么效果
    private static final int KEEP_ALIVE = 5;//过剩的空闲线程的存活时间
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {//ThreadFactory 线程工厂，通过工厂方法newThread来获取新线程
        private final AtomicInteger mCount = new AtomicInteger(1); //原子整数，可以在超高并发下正常工作

        public Thread newThread(Runnable r) {
            return new Thread(r, "CalmDispatcher #" + mCount.getAndIncrement());
        }
    };
    //    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(10); //静态阻塞式队列，用来存放待执行的任务，初始容量：10个
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(); // 无界队列
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,// 核心线程数
            MAXIMUM_POOL_SIZE,// 线程池所能容纳的最大线程数。超过这个数的线程将被阻塞。当任务队列为没有设置大小的LinkedBlockingDeque时，这个值无效。
            KEEP_ALIVE,// 非核心线程的闲置超时时间，超过这个时间就会被回收。
            TimeUnit.SECONDS,// 指定keepAliveTime的单位，如TimeUnit.SECONDS。当将allowCoreThreadTimeOut设置为true时对corePoolSize生效。
            sPoolWorkQueue,//
            sThreadFactory,
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private ExecutorService mExecutorService;

    CalmDispatcher() {
        THREAD_POOL_EXECUTOR.allowCoreThreadTimeOut(true);
        this.mExecutorService = THREAD_POOL_EXECUTOR;
    }

    public void execute(CalmRunnable runnable) {
        this.mExecutorService.execute(runnable);
    }

    void shutdown() {
        this.mExecutorService.shutdownNow();//立即中断停止线程执行，返回等待执行的任务列表
        this.mExecutorService.shutdown();
    }
}
