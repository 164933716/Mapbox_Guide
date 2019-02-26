package com.ksy.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadManager {

    static ThreadPoolProxy threadPool;


    public static ThreadPoolProxy getNormalPool() {
        if (threadPool == null) {
            synchronized (ThreadManager.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolProxy(5, 5, 3000);
                }
            }
        }
        return threadPool;
    }


    public static class ThreadPoolProxy {
        ThreadPoolExecutor mExecutor;
        int mCorePoolSize;
        int mMaximumPoolSize;
        long mKeepAliveTime;

        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize,
                               long keepAliveTime) {
            super();
            mCorePoolSize = corePoolSize;
            mMaximumPoolSize = maximumPoolSize;
            mKeepAliveTime = keepAliveTime;
        }

        private void initThreadPoolExecutor() {
            if (mExecutor == null) {
                synchronized (ThreadPoolProxy.class) {
                    if (mExecutor == null) {
                        TimeUnit unit = TimeUnit.MILLISECONDS;// 毫秒
                        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
                        ThreadFactory threadFactory = Executors
                                .defaultThreadFactory();
                        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();// 任务数量超过时，丢弃任务，抛出异常
                        mExecutor = new ThreadPoolExecutor(//
                                mCorePoolSize, // 核心线程数
                                mMaximumPoolSize,// 最大线程数
                                mKeepAliveTime, // 保持时间
                                unit, // 保持时间对应的单位
                                workQueue,// 缓存队列/阻塞队列
                                threadFactory,// 线程工厂
                                handler// 异常捕获器
                        );
                    }
                }

            }
        }

        public void execute(Runnable task) {
            initThreadPoolExecutor();
            mExecutor.execute(task);
        }

        public void remove(Runnable task) {
            initThreadPoolExecutor();
            mExecutor.remove(task);
        }
    }
}