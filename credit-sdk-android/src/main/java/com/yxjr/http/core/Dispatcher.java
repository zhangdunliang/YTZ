package com.yxjr.http.core;

import android.annotation.SuppressLint;

import com.yxjr.http.core.call.AsyncCall;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求分发器，控制并发量
 */

public final class Dispatcher {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    //	private static final int CORE_POOL_SIZE = 5;//线程池的基本大小
    //	private static final int MAXIMUM_POOL_SIZE = 128;//线程池核心容量   使用了无界的任务队列这个参数就没什么效果
    //	private static final int KEEP_ALIVE = 1; //过剩的空闲线程的存活时间
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {//ThreadFactory 线程工厂，通过工厂方法newThread来获取新线程
        private final AtomicInteger mCount = new AtomicInteger(1); //原子整数，可以在超高并发下正常工作

        public Thread newThread(Runnable r) {
            return new Thread(r, "Dispatcher #" + mCount.getAndIncrement());
        }
    };
    //	private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(10); //静态阻塞式队列，用来存放待执行的任务，初始容量：10个
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(); // 修改为无界队列
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            sPoolWorkQueue,
            sThreadFactory,
            new ThreadPoolExecutor.DiscardOldestPolicy());

    @SuppressLint("NewApi")
    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }

    }

    private Executor mExecutorService;

    /**
     * @param isSerial 是否串行
     *                 TODO[true:串行处理；false：并行处理]
     */
    public Dispatcher(boolean isSerial) {
        if (isSerial) {
            this.mExecutorService = new SerialExecutor();
        } else {
            this.mExecutorService = THREAD_POOL_EXECUTOR;
        }
    }

    /**
     * 执行一个异步请求
     *
     * @param call 请求
     */
    public void execute(AsyncCall call) {
        //		this.mExecutorService.submit(call);
        this.mExecutorService.execute(call);
    }

    //	public void shutdown() {
    //		this.mExecutorService.shutdownNow();
    //	}
}
