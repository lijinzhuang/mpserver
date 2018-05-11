package com.self.wx.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by lijz on 2018/5/11.
 */
public class SyncUtil {
    public static void main(String[] args) {
        MyRunnable mr = new MyRunnable();
        new Thread(mr,"aa").start();
        for (int i=0;i<10;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("wakeup the thread");
            mr.wakeup();
        }
        System.out.println("wakeup is over");

    }
    private static class MyRunnable implements Runnable{
        Sync s = new Sync(1);
        public void wakeup(){
            s.releaseShared(1);
        }
        @Override
        public void run() {
            for(;;){
                System.out.println("Thread is running");
                s.reset();
                try {
                    s.tryAcquireSharedNanos(1, TimeUnit.SECONDS.toNanos(20));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        private final int startCount;

        Sync(int count) {
            this.startCount = count;
            setState(count);
        }

        int getCount() {
            return getState();
        }

        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (; ; ) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c - 1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }

        protected void reset() {
            setState(startCount);
        }
    }
}
